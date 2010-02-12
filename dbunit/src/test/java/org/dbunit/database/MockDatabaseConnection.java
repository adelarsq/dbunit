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

package org.dbunit.database;

import com.mockobjects.ExpectationCounter;
import com.mockobjects.Verifiable;
import org.dbunit.database.statement.IStatementFactory;
import org.dbunit.dataset.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author Manuel Laflamme
 * @version $Revision$
 * @since Mar 16, 2002
 */
public class MockDatabaseConnection implements IDatabaseConnection, Verifiable
{
    private ExpectationCounter _closeCalls =
            new ExpectationCounter("MockDatabaseConnection.close");;

    private Connection _connection;
    private String _schema;
    private IDataSet _dataSet;
//    private IStatementFactory _statementFactory;
    private DatabaseConfig _databaseConfig = new DatabaseConfig();

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

    public void setupDataSet(ITable table) throws AmbiguousTableNameException
    {
        _dataSet = new DefaultDataSet(table);
    }

    public void setupDataSet(ITable[] tables) throws AmbiguousTableNameException
    {
        _dataSet = new DefaultDataSet(tables);
    }

    public void setupStatementFactory(IStatementFactory statementFactory)
    {
        _databaseConfig.setProperty(DatabaseConfig.PROPERTY_STATEMENT_FACTORY, statementFactory);
    }

//    public void setupEscapePattern(String escapePattern)
//    {
//        _databaseConfig.setProperty(DatabaseConfig.PROPERTY_ESCAPE_PATTERN, escapePattern);
//    }
//
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

    public IDataSet createDataSet(String[] tableNames) throws SQLException, AmbiguousTableNameException
    {
        return new FilteredDataSet(tableNames, createDataSet());
    }

    public ITable createQueryTable(String resultName, String sql)
            throws DataSetException, SQLException
    {
        throw new UnsupportedOperationException();
    }

    public ITable createTable(String tableName,
            PreparedStatement preparedStatement) throws DataSetException,
            SQLException 
    {
        throw new UnsupportedOperationException();
    }

    public ITable createTable(String tableName) throws DataSetException,
            SQLException 
    {
        throw new UnsupportedOperationException();
    }

    public int getRowCount(String tableName) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public int getRowCount(String tableName, String whereClause) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public IStatementFactory getStatementFactory()
    {
        return (IStatementFactory)_databaseConfig.getProperty(
                DatabaseConfig.PROPERTY_STATEMENT_FACTORY);
    }

    public DatabaseConfig getConfig()
    {
        return _databaseConfig;
    }
}





