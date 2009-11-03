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

package org.dbunit.database.statement;

import com.mockobjects.ExpectationCounter;
import com.mockobjects.Verifiable;
import org.dbunit.database.IDatabaseConnection;

import java.sql.SQLException;

/**
 * @author Manuel Laflamme
 * @version $Revision$
 * @since Mar 16, 2002
 */
public class MockStatementFactory implements IStatementFactory, Verifiable
{
    private IBatchStatement _batchStatement = null;
//    private IPreparedBatchStatement _preparedBatchStatement = null;
    private ExpectationCounter _createStatementCalls =
            new ExpectationCounter("MockStatementFactory.createBatchStatement");;
    private ExpectationCounter _createPreparedStatementCalls =
            new ExpectationCounter("MockStatementFactory.createPreparedBatchStatement");;

    public void setupStatement(IBatchStatement batchStatement)
    {
        _batchStatement = batchStatement;
    }

//    public void setupPreparedStatement(IPreparedBatchStatement preparedBatchStatement)
//    {
//        _preparedBatchStatement = preparedBatchStatement;
//    }

    public void setExpectedCreateStatementCalls(int callsCount)
    {
        _createStatementCalls.setExpected(callsCount);
    }

    public void setExpectedCreatePreparedStatementCalls(int callsCount)
    {
        _createPreparedStatementCalls.setExpected(callsCount);
    }


    ////////////////////////////////////////////////////////////////////////////
    // Verifiable interface

    public void verify()
    {
        _createStatementCalls.verify();
        _createPreparedStatementCalls.verify();
    }

    ////////////////////////////////////////////////////////////////////////////
    // IStatementFactory interface

    public IBatchStatement createBatchStatement(IDatabaseConnection connection)
            throws SQLException
    {
        _createStatementCalls.inc();
        return _batchStatement;
    }

    public IPreparedBatchStatement createPreparedBatchStatement(String sql,
            IDatabaseConnection connection) throws SQLException
    {
        _createPreparedStatementCalls.inc();
        return new BatchStatementDecorator(sql, _batchStatement);
    }
}



