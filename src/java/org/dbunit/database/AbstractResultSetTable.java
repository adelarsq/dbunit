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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.dbunit.dataset.AbstractTable;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.ITableMetaData;
import org.dbunit.dataset.datatype.IDataTypeFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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
        Connection jdbcConnection = connection.getConnection();
        _statement = jdbcConnection.createStatement();
//        _statement.setFetchDirection(ResultSet.FETCH_FORWARD);

        DatabaseConfig config = connection.getConfig();
        IDataTypeFactory dataTypeFactory = (IDataTypeFactory)config.getProperty(
                DatabaseConfig.PROPERTY_DATATYPE_FACTORY);

        try
        {
            _resultSet = _statement.executeQuery(selectStatement);
            _metaData = DatabaseTableMetaData.createMetaData(tableName,
                    _resultSet, dataTypeFactory);
        }
        catch (SQLException e)
        {
            logger.error("AbstractResultSetTable()", e);

            _statement.close();
            _statement = null;
            throw e;
        }
    }

    public AbstractResultSetTable(ITableMetaData metaData,
            IDatabaseConnection connection) throws DataSetException, SQLException
    {
        Connection jdbcConnection = connection.getConnection();
        String escapePattern = (String)connection.getConfig().getProperty(
                DatabaseConfig.PROPERTY_ESCAPE_PATTERN);
        _statement = jdbcConnection.createStatement();
//        _statement.setFetchDirection(ResultSet.FETCH_FORWARD);

        try
        {
            String schema = connection.getSchema();
            String selectStatement = getSelectStatement(schema, metaData, escapePattern);
            _resultSet = _statement.executeQuery(selectStatement);
            _metaData = metaData;
        }
        catch (SQLException e)
        {
            logger.error("AbstractResultSetTable()", e);

            _statement.close();
            _statement = null;
            throw e;
        }
    }

    static String getSelectStatement(String schema, ITableMetaData metaData, String escapePattern)
            throws DataSetException
    {
        logger.debug("getSelectStatement(schema=" + schema + ", metaData=" + metaData + ", escapePattern="
                + escapePattern + ") - start");

        return DatabaseDataSet.getSelectStatement(schema, metaData, escapePattern);
    }

    ////////////////////////////////////////////////////////////////////////////
    // ITable interface

    public ITableMetaData getTableMetaData()
    {
        logger.debug("getTableMetaData() - start");

        return _metaData;
    }

    ////////////////////////////////////////////////////////////////////////////
    // IResultSetTable interface

    public void close() throws DataSetException
    {
        logger.debug("close() - start");

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
            logger.error("close()", e);

            throw new DataSetException(e);
        }
    }
}
