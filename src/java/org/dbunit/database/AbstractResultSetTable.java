/*
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

import org.dbunit.dataset.AbstractTable;
import org.dbunit.dataset.ITableMetaData;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.Column;
import org.dbunit.dataset.DefaultTableMetaData;
import org.dbunit.dataset.datatype.DataType;
import org.dbunit.dataset.datatype.IDataTypeFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.sql.Connection;

/**
 * @author Manuel Laflamme
 * @since Apr 10, 2003
 * @version $Revision$
 */
public abstract class AbstractResultSetTable extends AbstractTable
        implements IResultSetTable
{
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
            IDatabaseConnection connection, IDataTypeFactory dataTypeFactory)
            throws DataSetException, SQLException
    {
        Connection jdbcConnection = connection.getConnection();
        _statement = jdbcConnection.createStatement();
//        _statement.setFetchDirection(ResultSet.FETCH_FORWARD);

        try
        {
            _resultSet = _statement.executeQuery(selectStatement);
            _metaData = createTableMetaData(tableName, _resultSet, dataTypeFactory);
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
        Connection jdbcConnection = connection.getConnection();
        _statement = jdbcConnection.createStatement();
//        _statement.setFetchDirection(ResultSet.FETCH_FORWARD);

        try
        {
            String schema = connection.getSchema();
            String selectStatement = getSelectStatement(schema, metaData);
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

    static String getSelectStatement(String schema, ITableMetaData metaData)
            throws DataSetException
    {
        return DatabaseDataSet.getSelectStatement(schema, metaData);
    }

    static ITableMetaData createTableMetaData(String name,
            ResultSet resultSet, IDataTypeFactory dataTypeFactory) throws DataSetException, SQLException
    {
        ResultSetMetaData metaData = resultSet.getMetaData();
        Column[] columns = new Column[metaData.getColumnCount()];
        for (int i = 0; i < columns.length; i++)
        {
            int columnType = metaData.getColumnType(i + 1);
            String columnTypeName = metaData.getColumnTypeName(i + 1);
            DataType dataType = dataTypeFactory.createDataType(
                    columnType, columnTypeName);
            columns[i] = new Column(
                    metaData.getColumnName(i + 1),
                    dataType,
                    columnTypeName,
                    Column.nullableValue(metaData.isNullable(i + 1)));
        }

        return new DefaultTableMetaData(name, columns);
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
        try
        {
            if (_statement != null)
            {
                _statement.close();
                _statement = null;
            }

            if (_resultSet != null)
            {
                _resultSet.close();
                _resultSet = null;
            }
        }
        catch (SQLException e)
        {
            throw new DataSetException(e);
        }
    }
}
