/*
 * DatabaseTableMetaData.java   Mar 8, 2002
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

import org.dbunit.dataset.AbstractTableMetaData;
import org.dbunit.dataset.Column;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.NoColumnsFoundException;
import org.dbunit.dataset.datatype.DataType;
import org.dbunit.dataset.datatype.IDataTypeFactory;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Manuel Laflamme
 * @version $Revision$
 */
public class DatabaseTableMetaData extends AbstractTableMetaData
{
    private final String _tableName;
    private final IDatabaseConnection _connection;
    private Column[] _columns;
    private Column[] _primaryKeys;

    DatabaseTableMetaData(String tableName, IDatabaseConnection connection
            )
    {
        _tableName = tableName;
        _connection = connection;
    }

    private String[] getPrimaryKeyNames() throws SQLException
    {
        // qualified names support
        String schemaName = _connection.getSchema();
        String tableName = _tableName;
        int index = tableName.indexOf(".");
        if (index >= 0)
        {
            schemaName = tableName.substring(0, index);
            tableName = tableName.substring(index + 1);
        }

        Connection connection = _connection.getConnection();
        DatabaseMetaData databaseMetaData = connection.getMetaData();
        ResultSet resultSet = databaseMetaData.getPrimaryKeys(
                null, schemaName, tableName);

        List list = new ArrayList();
        try
        {
            while (resultSet.next())
            {
                String name = resultSet.getString(4);
                int sequence = resultSet.getInt(5);
                list.add(new PrimaryKeyData(name, sequence));
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
            return getIndex() - data.getIndex();
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    // ITableMetaData interface

    public String getTableName()
    {
        return _tableName;
    }

    public Column[] getColumns() throws DataSetException
    {
        if (_columns == null)
        {
            try
            {
                // qualified names support
                String schemaName = _connection.getSchema();
                String tableName = _tableName;
                int index = tableName.indexOf(".");
                if (index >= 0)
                {
                    schemaName = tableName.substring(0, index);
                    tableName = tableName.substring(index + 1);
                }

                Connection jdbcConnection = _connection.getConnection();
                DatabaseMetaData databaseMetaData = jdbcConnection.getMetaData();
                ResultSet resultSet = databaseMetaData.getColumns(
                        null, schemaName, tableName, null);

                try
                {
                    DatabaseConfig config = _connection.getConfig();
                    IDataTypeFactory dataTypeFactory = (IDataTypeFactory)config.getProperty(
                            DatabaseConfig.PROPERTY_DATATYPE_FACTORY);

                    List columnList = new ArrayList();
                    while (resultSet.next())
                    {
                        String columnName = resultSet.getString(4);
                        int sqlType = resultSet.getInt(5);
                        String sqlTypeName = resultSet.getString(6);
//                        int columnSize = resultSet.getInt(7);
                        int nullable = resultSet.getInt(11);

                        // Convert SQL type to DataType
                        DataType dataType =
                                dataTypeFactory.createDataType(sqlType, sqlTypeName);
                        if (dataType != DataType.UNKNOWN)
                        {
                            Column column = new Column(columnName, dataType,
                                    sqlTypeName, Column.nullableValue(nullable));
                            columnList.add(column);
                        }
                    }

                    if (columnList.size() == 0)
                    {
                        throw new NoColumnsFoundException(tableName);
                    }

                    _columns = (Column[])columnList.toArray(new Column[0]);
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
        return _columns;
    }

    public Column[] getPrimaryKeys() throws DataSetException
    {
        if (_primaryKeys == null)
        {
            try
            {
                _primaryKeys = getPrimaryKeys(getColumns(), getPrimaryKeyNames());
            }
            catch (SQLException e)
            {
                throw new DataSetException(e);
            }
        }
        return _primaryKeys;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Object class
    public String toString()
    {
        try
        {
            String tableName = getTableName();
            String columns = Arrays.asList(getColumns()).toString();
            String primaryKeys = Arrays.asList(getPrimaryKeys()).toString();
            return "table=" + tableName + ", cols=" + columns + ", pk=" + primaryKeys + "";
        }
        catch (DataSetException e)
        {
            return super.toString();
        }
    }
}










