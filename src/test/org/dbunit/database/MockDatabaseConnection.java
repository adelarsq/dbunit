/*
 * MockDatabaseConnection.java   Mar 16, 2002
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

package org.dbunit.database;

import java.sql.Connection;
import java.sql.SQLException;

import org.dbunit.dataset.*;
import org.dbunit.database.statement.IStatementFactory;

import javax.swing.*;

import com.mockobjects.ExpectationCounter;
import com.mockobjects.Verifiable;

/**
 * @author Manuel Laflamme
 * @version 1.0
 */
public class MockDatabaseConnection implements IDatabaseConnection, Verifiable
{
    private ExpectationCounter _closeCalls =
            new ExpectationCounter("MockDatabaseConnection.close");;

    private Connection _connection;
    private String _schema;
    private IDataSet _dataSet;
    private IStatementFactory _statementFactory;

    public void setupSchema(String schema)
    {
        _schema = schema;
    }

    public void setupConnection(Connection connection)
    {
        _connection = connection;
    }

    public void setupDataSet(IDataSet dataSet)
    {
        _dataSet = dataSet;
    }

    public void setupDataSet(ITable table)
    {
        _dataSet = new DefaultDataSet(table);
    }

    public void setupDataSet(ITable[] tables)
    {
        _dataSet = new DefaultDataSet(tables);
    }

    public void setupStatementFactory(IStatementFactory statementFactory)
    {
        _statementFactory = statementFactory;
    }

    public void setExpectedCloseCalls(int callsCount)
    {
        _closeCalls.setExpected(callsCount);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Verifiable interface

    public void verify()
    {
        _closeCalls.verify();
    }

    ///////////////////////////////////////////////////////////////////////////
    // IDatabaseConnection interface

    public Connection getConnection() throws SQLException
    {
        return _connection;
    }

    public String getSchema()
    {
        return _schema;
    }

    public void close() throws SQLException
    {
        _closeCalls.inc();
    }

    public IDataSet createDataSet() throws SQLException
    {
        return _dataSet;
    }

    public IDataSet createDataSet(String[] tableNames) throws SQLException
    {
        return new FilteredDataSet(tableNames, createDataSet());
    }

    public ITable createQueryTable(String resultName, String sql)
            throws DataSetException, SQLException
    {
        throw new UnsupportedOperationException();
    }

    public IStatementFactory getStatementFactory()
    {
        return _statementFactory;
    }
}
