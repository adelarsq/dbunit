/*
 * CloseConnectionOperation.java   Mar 6, 2002
 *
 * The dbUnit database testing framework.
 * Copyright (C) 2002   Manuel Laflamme
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

import java.sql.SQLException;

import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.DatabaseUnitException;

/**
 * This class is a decorator that close the database connection after executing
 * the decorated operation.
 *
 * @author Manuel Laflamme
 * @version 1.0
 */
public class CloseConnectionOperation extends DatabaseOperation
{
    private final DatabaseOperation _operation;

    /**
     * Creates a new CloseConnectionOperation object.
     * @param the decorated operation
     */
    public CloseConnectionOperation(DatabaseOperation operation)
    {
        _operation = operation;
    }

    ////////////////////////////////////////////////////////////////////////////
    // DatabaseOperation class

    public void execute(IDatabaseConnection connection,
            IDataSet dataSet) throws DatabaseUnitException, SQLException
    {
        try
        {
            _operation.execute(connection, dataSet);
        }
        finally
        {
            connection.close();
        }
    }
}
