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

import org.dbunit.dataset.*;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * provides access
      to a database instance as a dataset.

 * @author Manuel Laflamme
 * @version $Revision$
 * @since Feb 17, 2002
 */
public class DatabaseDataSet extends AbstractDataSet
{

    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(DatabaseDataSet.class);

    private final IDatabaseConnection _connection;
    private final Map _tableMap = new HashMap();
    private List _nameList = null;

    DatabaseDataSet(IDatabaseConnection connection) throws SQLException
    {
        _connection = connection;
    }

    static String getSelectStatement(String schema, ITableMetaData metaData, String escapePattern)
            throws DataSetException
    {
        logger.debug("getSelectStatement(schema=" + schema + ", metaData=" + metaData + ", escapePattern="
                + escapePattern + ") - start");

        Column[] columns = metaData.getColumns();
        Column[] primaryKeys = metaData.getPrimaryKeys();

        // select
        StringBuffer sqlBuffer = new StringBuffer(128);
        sqlBuffer.append("select ");
        for (int i = 0; i < columns.length; i++)
        {
            if (i > 0)
            {
                sqlBuffer.append(", ");
            }
            String columnName = DataSetUtils.getQualifiedName(null,
                    columns[i].getColumnName(), escapePattern);
            sqlBuffer.append(columnName);
        }

        // from
        sqlBuffer.append(" from ");
        sqlBuffer.append(DataSetUtils.getQualifiedName(schema,
                metaData.getTableName(), escapePattern));

        // order by
        for (int i = 0; i < primaryKeys.length; i++)
        {
            if (i == 0)
            {
                sqlBuffer.append(" order by ");
            }
            else
            {
                sqlBuffer.append(", ");
            }
            sqlBuffer.append(DataSetUtils.getQualifiedName(null, primaryKeys[i]
					.getColumnName(), escapePattern));

        }

        return sqlBuffer.toString();
    }

    private String getQualifiedName(String prefix, String name)
    {
        logger.debug("getQualifiedName(prefix=" + prefix + ", name=" + name + ") - start");

        DatabaseConfig config = _connection.getConfig();
        boolean feature = config.getFeature(DatabaseConfig.FEATURE_QUALIFIED_TABLE_NAMES);
        if (feature)
        {
            return DataSetUtils.getQualifiedName(prefix, name);
        }
        return name;
    }

    /**
     * Get all the table names form the database that are not system tables.
     */
    private void initialize() throws DataSetException
    {
        logger.debug("initialize() - start");

        if (_nameList != null)
        {
            return;
        }

        try
        {
            Connection jdbcConnection = _connection.getConnection();
            String schema = _connection.getSchema();
            String[] tableType = (String[])_connection.getConfig().getProperty(
                    DatabaseConfig.PROPERTY_TABLE_TYPE);

            DatabaseMetaData databaseMetaData = jdbcConnection.getMetaData();
            ResultSet resultSet = databaseMetaData.getTables(
                    null, schema, "%", tableType);

            try
            {
                List nameList = new ArrayList();
                while (resultSet.next())
                {
                    String schemaName = resultSet.getString(2);
                    String tableName = resultSet.getString(3);

                    // skip oracle 10g recycle bin system tables if enabled
                    if(_connection.getConfig().getFeature(DatabaseConfig.FEATURE_SKIP_ORACLE_RECYCLEBIN_TABLES)) {
                        // Oracle 10g workaround
                        // don't process system tables (oracle recycle bin tables) which
                        // are reported to the application due a bug in the oracle JDBC driver
                        if (tableName.startsWith("BIN$")) continue;	
                    }
                    tableName = getQualifiedName(schemaName, tableName);

                    // prevent table name conflict
                    if (_tableMap.containsKey(tableName.toUpperCase()))
                    {
                        throw new AmbiguousTableNameException(tableName);
                    }
                    nameList.add(tableName);
                    _tableMap.put(tableName.toUpperCase(), null);
                }

                _nameList = nameList;
            }
            finally
            {
                resultSet.close();
            }
        }
        catch (SQLException e)
        {
            logger.error("initialize()", e);

            throw new DataSetException(e);
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    // AbstractDataSet class

    protected ITableIterator createIterator(boolean reversed)
            throws DataSetException
    {
        logger.debug("createIterator(reversed=" + reversed + ") - start");

        String[] names = getTableNames();
        if (reversed)
        {
            names = DataSetUtils.reverseStringArray(names);
        }

        return new DatabaseTableIterator(names, this);
    }

    ////////////////////////////////////////////////////////////////////////////
    // IDataSet interface

    public String[] getTableNames() throws DataSetException
    {
        logger.debug("getTableNames() - start");

        initialize();

        return (String[])_nameList.toArray(new String[0]);
    }

    public ITableMetaData getTableMetaData(String tableName) throws DataSetException
    {
        logger.debug("getTableMetaData(tableName=" + tableName + ") - start");

        initialize();

        // Verify if table exist in the database
        String upperTableName = tableName.toUpperCase();
        if (!_tableMap.containsKey(upperTableName))
        {
            throw new NoSuchTableException(tableName);
        }

        // Try to find cached metadata
        ITableMetaData metaData = (ITableMetaData)_tableMap.get(upperTableName);
        if (metaData != null)
        {
            return metaData;
        }

        // Search for original database table name
        for (Iterator it = _nameList.iterator(); it.hasNext();)
        {
            String databaseTableName = (String)it.next();
            if (databaseTableName.equalsIgnoreCase(tableName))
            {
                // Create metadata and cache it
                metaData = new DatabaseTableMetaData(
                        databaseTableName, _connection);
                _tableMap.put(upperTableName, metaData);
                break;
            }
        }

        return metaData;
    }

    public ITable getTable(String tableName) throws DataSetException
    {
        logger.debug("getTable(tableName=" + tableName + ") - start");

        initialize();

        try
        {
            ITableMetaData metaData = getTableMetaData(tableName);

            DatabaseConfig config = _connection.getConfig();
            IResultSetTableFactory factory = (IResultSetTableFactory)config.getProperty(
                    DatabaseConfig.PROPERTY_RESULTSET_TABLE_FACTORY);
            return factory.createTable(metaData, _connection);
        }
        catch (SQLException e)
        {
            logger.error("getTable()", e);

            throw new DataSetException(e);
        }
    }
}













