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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.dbunit.database.statement.IStatementFactory;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.FilteredDataSet;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Manuel Laflamme
 * @version $Revision$
 * @since Mar 6, 2002
 */
public abstract class AbstractDatabaseConnection implements IDatabaseConnection
{

    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(AbstractDatabaseConnection.class);

    private IDataSet _dataSet = null;
    private DatabaseConfig _databaseConfig;

    public AbstractDatabaseConnection()
    {
        _databaseConfig = new DatabaseConfig();
    }

    ////////////////////////////////////////////////////////////////////////////
    // IDatabaseConnection interface

    public IDataSet createDataSet() throws SQLException
    {
        logger.debug("createDataSet() - start");

        if (_dataSet == null)
        {
            _dataSet = new DatabaseDataSet(this);
        }

        return _dataSet;
    }

    public IDataSet createDataSet(String[] tableNames) throws SQLException
    {
        logger.debug("createDataSet(tableNames={}) - start", tableNames);

        return new FilteredDataSet(tableNames, createDataSet());
    }

    public ITable createQueryTable(String resultName, String sql)
            throws DataSetException, SQLException
    {
        logger.debug("createQueryTable(resultName={}, sql={}) - start", resultName, sql);

        ForwardOnlyResultSetTable rsTable = new ForwardOnlyResultSetTable(resultName, sql, this);
        return new CachedResultSetTable(rsTable);
    }

    public int getRowCount(String tableName) throws SQLException
    {
        logger.debug("getRowCount(tableName={}) - start", tableName);

        return getRowCount(tableName, null);
    }

    public int getRowCount(String tableName, String whereClause) throws SQLException
    {
        logger.debug("getRowCount(tableName={}, whereClause={}) - start", tableName, whereClause);

        StringBuffer sqlBuffer = new StringBuffer(128);
        sqlBuffer.append("select count(*) from ");
        sqlBuffer.append(tableName);
        if (whereClause != null)
        {
            sqlBuffer.append(" ");
            sqlBuffer.append(whereClause);
        }

        Statement statement = getConnection().createStatement();
        try
        {
            ResultSet resultSet = statement.executeQuery(sqlBuffer.toString());
            try
            {
                resultSet.next();
                return resultSet.getInt(1);
            }
            finally
            {
                resultSet.close();
            }
        }
        finally
        {
            statement.close();
        }
    }

    public DatabaseConfig getConfig()
    {
        return _databaseConfig;
    }

    public IStatementFactory getStatementFactory()
    {
        return (IStatementFactory)_databaseConfig.getProperty(DatabaseConfig.PROPERTY_STATEMENT_FACTORY);
    }

    public String toString()
    {
    	StringBuffer sb = new StringBuffer();
    	sb.append("_databaseConfig=").append(_databaseConfig);
    	sb.append(", _dataSet=").append(_dataSet);
    	return sb.toString();
    }
}
