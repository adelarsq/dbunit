/*
 * DatabaseDataSet.java   Feb 17, 2002
 *
 * The DbUnit Database Testing Framework
 * Copyright (C)2002, Manuel Laflamme
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

import org.dbunit.dataset.*;

import java.sql.*;
import java.util.*;

/**
 * @author Manuel Laflamme
 * @version $Revision$
 */
public class DatabaseDataSet implements IDataSet
{
    static final String QUALIFIED_TABLE_NAMES =
            "dbunit.qualified.table.names";
    private static final String[] TABLE_TYPE = {"TABLE"};

    private final IDatabaseConnection _connection;
    private Map _tableMap = null;

    DatabaseDataSet(IDatabaseConnection connection) throws SQLException
    {
        _connection = connection;
    }

    static String getSelectStatement(String schema, ITableMetaData metaData)
            throws DataSetException
    {
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
                    columns[i].getColumnName(), true);
            sqlBuffer.append(columnName);
        }

        // from
        sqlBuffer.append(" from ");
        sqlBuffer.append(DataSetUtils.getQualifiedName(schema,
                metaData.getTableName(), true));

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
            sqlBuffer.append(primaryKeys[i].getColumnName());

        }

        return sqlBuffer.toString();
    }

    private String getQualifiedName(String prefix, String name)
    {
        if (System.getProperty(QUALIFIED_TABLE_NAMES, "false").equals("true"))
        {
            return DataSetUtils.getQualifiedName(prefix, name);
        }
        return name;
    }

    /**
     * Get all the table names form the database that are not system tables.
     */
    private Map getTableMap() throws DataSetException
    {
        if (_tableMap != null)
        {
            return _tableMap;
        }

        try
        {
            Connection jdbcConnection = _connection.getConnection();
            String schema = _connection.getSchema();

            DatabaseMetaData databaseMetaData = jdbcConnection.getMetaData();
            ResultSet resultSet = databaseMetaData.getTables(
                    null, schema, "%", TABLE_TYPE);

            try
            {
                Map tableMap = new HashMap();
                while (resultSet.next())
                {
                    String schemaName = resultSet.getString(2);
                    String tableName = resultSet.getString(3);
                    String tableType = resultSet.getString(4);
//                    System.out.println("schema=" + schemaName + ", table=" + tableName + ", type=" + tableType + "");
                    tableName = getQualifiedName(schemaName, tableName);

                    // prevent table name conflict
                    if (tableMap.containsKey(tableName))
                    {
                        throw new AmbiguousTableNameException(tableName);
                    }
                    tableMap.put(tableName, null);
                }

                _tableMap = tableMap;
                return _tableMap;
            }
            finally
            {
                resultSet.close();
            }
        }
        catch (SQLException e)
        {
            throw new DataSetException(e);
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    // IDataSet interface

    public String[] getTableNames() throws DataSetException
    {
        return (String[])getTableMap().keySet().toArray(new String[0]);
    }

    public ITableMetaData getTableMetaData(String tableName) throws DataSetException
    {
        ITableMetaData metaData = (ITableMetaData)getTableMap().get(tableName);
        if (metaData != null)
        {
            return metaData;
        }

        if (!getTableMap().containsKey(tableName))
        {
            throw new NoSuchTableException(tableName);
        }

        metaData = new DatabaseTableMetaData(tableName, _connection);
        getTableMap().put(tableName, metaData);
        return metaData;
    }

    public ITable getTable(String tableName) throws DataSetException
    {
        try
        {
            ITableMetaData metaData = getTableMetaData(tableName);

            Connection jdbcConnection = _connection.getConnection();
            String schema = _connection.getSchema();
            Statement statement = jdbcConnection.createStatement();

            try
            {
                String sql = getSelectStatement(schema, metaData);
                ResultSet resultSet = statement.executeQuery(sql);
                try
                {
                    return new CachedResultSetTable(metaData, resultSet);
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
        catch (SQLException e)
        {
            throw new DataSetException(e);
        }
    }

    public ITable[] getTables() throws DataSetException
    {
        String[] names = getTableNames();
        List tableList = new ArrayList(names.length);
        for (int i = 0; i < names.length; i++)
        {
            tableList.add(getTable(names[i]));
        }
        return (ITable[])tableList.toArray(new ITable[0]);
    }
}











