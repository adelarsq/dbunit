/*
 *
 * The DbUnit Database Testing Framework
 * Copyright (C)2002-2004, DbUnit.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

package org.dbunit.operation;

import com.mockobjects.ExpectationCounter;
import com.mockobjects.Verifiable;
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;

import java.sql.SQLException;

/**
 * @author Manuel Laflamme
 * @version $Revision$
 * @since Mar 16, 2002
 */
public class MockDatabaseOperation extends DatabaseOperation implements Verifiable
{
    private ExpectationCounter _executeCalls =
            new ExpectationCounter("MockDatabaseOperation.execute");
    private Exception _executeException = null;

    public void setExpectedExecuteCalls(int callsCount)
    {
        _executeCalls.setExpected(callsCount);
    }

    public void setupThrowExceptionOnExecute(Exception exception)
    {
        _executeException = exception;
    }


    ///////////////////////////////////////////////////////////////////////////
    // Verifiable interface

    public void verify()
    {
        _executeCalls.verify();
    }

    ///////////////////////////////////////////////////////////////////////////
    // DatabaseOperation class

    public void execute(IDatabaseConnection connection, IDataSet dataSet)
            throws DatabaseUnitException, SQLException
    {
        _executeCalls.inc();

        if (_executeException instanceof SQLException)
        {
            throw (SQLException)_executeException;
        }
        else if (_executeException instanceof DatabaseUnitException)
        {
            throw (DatabaseUnitException)_executeException;
        }
        else if (_executeException instanceof RuntimeException)
        {
            throw (RuntimeException)_executeException;
        }
    }
}


