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
import com.mockobjects.ExpectationList;
import com.mockobjects.Verifiable;

import java.sql.SQLException;

/**
 * @author Manuel Laflamme
 * @version $Revision$
 * @since Mar 16, 2002
 */
public class MockBatchStatement implements IBatchStatement, Verifiable
{
    private ExpectationCounter _executeBatchCalls =
            new ExpectationCounter("MockBatchStatement.executeBatch");;
    private ExpectationCounter _clearBatchCalls =
            new ExpectationCounter("MockBatchStatement.clearBatch");;
    private ExpectationCounter _closeCalls =
            new ExpectationCounter("MockBatchStatement.close");;
    private ExpectationList _batchStrings =
            new ExpectationList("MockBatchStatement.batchStrings");
    private int _addBatchCalls = 0;

    public MockBatchStatement()
    {
    }

    public void addExpectedBatchString(String sql)
    {
        _batchStrings.addExpected(sql);
    }

    public void addExpectedBatchStrings(String[] sql)
    {
        _batchStrings.addExpectedMany(sql);
    }

    public void setExpectedExecuteBatchCalls(int callsCount)
    {
        _executeBatchCalls.setExpected(callsCount);
    }

    public void setExpectedClearBatchCalls(int callsCount)
    {
        _clearBatchCalls.setExpected(callsCount);
    }

    public void setExpectedCloseCalls(int callsCount)
    {
        _closeCalls.setExpected(callsCount);
    }

    ////////////////////////////////////////////////////////////////////////////
    // Verifiable interface

    public void verify()
    {
        _executeBatchCalls.verify();
        _clearBatchCalls.verify();
        _closeCalls.verify();
        _batchStrings.verify();
    }

    ////////////////////////////////////////////////////////////////////////////
    // IBatchStatement interface

    public void addBatch(String sql) throws SQLException
    {
        _batchStrings.addActual(sql);
        _addBatchCalls++;
    }

    public int executeBatch() throws SQLException
    {
        _executeBatchCalls.inc();
        return _addBatchCalls;
    }

    public void clearBatch() throws SQLException
    {
        _clearBatchCalls.inc();
        _addBatchCalls = 0;
    }

    public void close() throws SQLException
    {
        _closeCalls.inc();
    }
}



