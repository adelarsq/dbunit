/*
 * XmlDataSet.java   Feb 17, 2002
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

import java.sql.*;
import java.util.*;

import org.dbunit.dataset.*;
import org.dbunit.dataset.datatype.DataType;
import org.dbunit.dataset.datatype.DataTypeException;

/**
 * @author Manuel Laflamme
 * @version 1.0
 */
public class DatabaseDataSet extends AbstractDataSet
{
    private final Connection _connection;
    private final String _schema;
    private Map _tableMap = null;

    DatabaseDataSet(DatabaseConnection connection) throws SQLException
    {
        _connection = connection.getConnection();
        _schema = connection.getSchema();
    }

    static String getSelectStatement(String schema, ITableMetaData metaData)
            throws DataSetException
    {
        Column[] columns = metaData.getColumns();

        // select
        String sql = "select";
        for (int i = 0; i < columns.length; i++)
        {
            Column column = columns[i];
//            sql += " " + getAbsoluteName(schema, column.getColumnName());
            sql += " " + column.getColumnName();
            if (i + 1 < columns.length)
            {
                sql += ",";
            }
        }

        // from
        sql += " from " + DataSetUtils.getAbsoluteName(schema, metaData.getTableName());

        return sql;
    }

    private String[] getPrimaryKeys(String tableName) throws SQLException
    {
        DatabaseMetaData databaseMetaData = _connection.getMetaData();
        ResultSet resultSet = databaseMetaData.getPrimaryKeys("", _schema, tableName);

        List list = new ArrayList();
        try
        {
            while (resultSet.next())
            {
                String name = resultSet.getString(4);
                int index = resultSet.getInt(5);
                list.add(new PrimaryKeyData(name, index));
            }
        }
        finally
        {
            resultSet.close();
        }

        Collections.sort(list);
        String[] keys = new String[list.size()];
        for (int i = 0; i < keys.length; i++)
        {
            PrimaryKeyData data = (PrimaryKeyData)list.get(i);
            keys[i] = data.getName();
        }

        return keys;
    }

    private class PrimaryKeyData implements Comparable
    {
        private final String _name;
        private final int _index;

        public PrimaryKeyData(String name, int index)
        {
            _name = name;
            _index = index;
        }

        public String getName()
        {
            return _name;
        }

        public int getIndex()
        {
            return _index;
        }

        ////////////////////////////////////////////////////////////////////////
        // Comparable interface

        public int compareTo(Object o)
        {
            PrimaryKeyData data = (PrimaryKeyData)o;
            return data.getIndex() - getIndex();
        }
    }

    /**
     * Get all the table names in the current database that are not
     * system tables.
     */
    private Map getTableMap() throws DataSetException
    {
        if (_tableMap != null)
        {
            return _tableMap;
        }

        try
        {
            DatabaseMetaData databaseMetaData = _connection.getMetaData();
            ResultSet resultSet = databaseMetaData.getTables(
                    "", _schema, "%", null);

            try
            {
                Map tableMap = new HashMap();
                while (resultSet.next())
                {
                    String name = resultSet.getString(3);
                    String type = resultSet.getString(4);
                    if (type.equals("TABLE"))
                    {
                        tableMap.put(name, null);
                    }
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
        ITableMetaData tableMetaData = (ITableMetaData)getTableMap().get(tableName);
        if (tableMetaData != null)
        {
            return tableMetaData;
        }
        else if (!getTableMap().containsKey(tableName))
        {
            throw new NoSuchTableException(tableName);
        }

        try
        {
            DatabaseMetaData databaseMetaData = _connection.getMetaData();
            ResultSet resultSet = databaseMetaData.getColumns("", _schema, tableName, null);

            try
            {
                List columnList = new ArrayList();
                while (resultSet.next())
                {
                    String columnName = resultSet.getString(4);
                    int sqlType = resultSet.getInt(5);
                    String sqlTypeName = resultSet.getString(6);
                    int columnSize = resultSet.getInt(7);
                    int nullable = resultSet.getInt(11);

                    Column column = new Column(columnName, DataType.forSqlType(sqlType));
                    columnList.add(column);
                }

                Column[] columns = (Column[])columnList.toArray(new Column[0]);
                tableMetaData = new DefaultTableMetaData(tableName, columns,
                        getPrimaryKeys(tableName));
                getTableMap().put(tableName, tableMetaData);
                return tableMetaData;
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
        catch (DataTypeException e)
        {
            throw new DataSetException(e);
        }
    }

    public ITable getTable(String tableName) throws DataSetException
    {
        try
        {
            ITableMetaData metaData = getTableMetaData(tableName);
            Statement statement = _connection.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet resultSet = statement.executeQuery(getSelectStatement(
                    _schema, metaData));

            if (resultSet.getType() == resultSet.TYPE_FORWARD_ONLY)
            {
                try
                {
                    return new CachedResultSetTable(metaData, resultSet);
                }
                finally
                {
                    resultSet.close();
                }
            }
            return new ResultSetTable(metaData, resultSet);
        }
        catch (SQLException e)
        {
            throw new DataSetException(e);
        }
    }

}
