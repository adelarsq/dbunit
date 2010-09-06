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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.dbunit.dataset.AbstractTable;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.ITableMetaData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Manuel Laflamme
 * @since Apr 10, 2003
 * @version $Revision$
 */
public abstract class AbstractResultSetTable extends AbstractTable
        implements IResultSetTable
{

    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(AbstractResultSetTable.class);

    protected ITableMetaData _metaData;
    private Statement _statement;
    protected ResultSet _resultSet;

    public AbstractResultSetTable(ITableMetaData metaData, ResultSet resultSet)
            throws SQLException, DataSetException
    {
        _metaData = metaData;
        _resultSet = resultSet;
    }

    public AbstractResultSetTable(String tableName, String selectStatement,
            IDatabaseConnection connection)
            throws DataSetException, SQLException
    {
        this(tableName, selectStatement, connection, false);
    }
    
    /**
     * @param tableName
     * @param selectStatement
     * @param connection
     * @param caseSensitiveTableNames
     * @throws DataSetException
     * @throws SQLException
     * @since 2.4.1
     */
    public AbstractResultSetTable(String tableName, String selectStatement,
            IDatabaseConnection connection, boolean caseSensitiveTableNames)
            throws DataSetException, SQLException
    {
    	_statement = createStatement(connection);

        try
        {
            _resultSet = _statement.executeQuery(selectStatement);
            _metaData = new ResultSetTableMetaData(tableName, _resultSet, connection, caseSensitiveTableNames);
        }
        catch (SQLException e)
        {
            _statement.close();
            _statement = null;
            throw e;
        }
    }

	public AbstractResultSetTable(ITableMetaData metaData,
            IDatabaseConnection connection) throws DataSetException, SQLException
    {
		_statement = createStatement(connection);
		
        String escapePattern = (String)connection.getConfig().getProperty(
                DatabaseConfig.PROPERTY_ESCAPE_PATTERN);

        try
        {
            String schema = connection.getSchema();
            String selectStatement = getSelectStatement(schema, metaData, escapePattern);

            if(logger.isDebugEnabled())
                logger.debug("Query: {}", selectStatement);
            
            _resultSet = _statement.executeQuery(selectStatement);
            _metaData = metaData;
        }
        catch (SQLException e)
        {
            _statement.close();
            _statement = null;
            throw e;
        }
    }

    private Statement createStatement(IDatabaseConnection connection) throws SQLException 
    {
        logger.trace("createStatement() - start");

        Connection jdbcConnection = connection.getConnection();
        Statement stmt = jdbcConnection.createStatement();
        connection.getConfig().getConfigurator().configureStatement(stmt);
        return stmt;
    }

    static String getSelectStatement(String schema, ITableMetaData metaData, String escapePattern)
            throws DataSetException
    {
        return DatabaseDataSet.getSelectStatement(schema, metaData, escapePattern);
    }

    ////////////////////////////////////////////////////////////////////////////
    // ITable interface

    public ITableMetaData getTableMetaData()
    {
        return _metaData;
    }

    ////////////////////////////////////////////////////////////////////////////
    // IResultSetTable interface

    public void close() throws DataSetException
    {
        logger.trace("close() - start");

        try
        {
            if (_resultSet != null)
            {
                _resultSet.close();
                _resultSet = null;
            }

            if (_statement != null)
            {
                _statement.close();
                _statement = null;
            }
        }
        catch (SQLException e)
        {
            throw new DataSetException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public String toString()
    {
        StringBuilder sb = new StringBuilder(2000);

        sb.append(getClass().getName()).append("[");
        sb.append("_metaData=[").append(_metaData).append("], ");
        sb.append("_resultSet=[").append(_resultSet).append("], ");
        sb.append("_statement=[").append(_statement).append("]");
        sb.append("]");

        return sb.toString();
    }
}
