/*
 * XmlTable.java   Feb 17, 2002
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

import org.dbunit.dataset.*;
import org.dbunit.dataset.datatype.DataType;

/**
 * @author Manuel Laflamme
 * @version 1.0
 */
public class ResultSetTable extends AbstractTable
{
    private final ITableMetaData _metaData;
    private final ResultSet _resultSet;
    private final int _rowCount;

    public ResultSetTable(ITableMetaData metaData, ResultSet resultSet)
            throws SQLException
    {
        if (resultSet.getType() == resultSet.TYPE_FORWARD_ONLY)
        {
            throw new SQLException("Forward only ResultSet not supported");
        }

        _metaData = metaData;
        _resultSet = resultSet;
        _resultSet.last();
        _rowCount = _resultSet.getRow();
    }

    public static ITableMetaData createTableMetaData(String name,
            ResultSet resultSet) throws DataSetException, SQLException
    {
        ResultSetMetaData metaData = resultSet.getMetaData();
        Column[] columns = new Column[metaData.getColumnCount()];
        for (int i = 0; i < columns.length; i++)
        {
            columns[i] = new Column(metaData.getColumnName(i + 1),
                    DataType.forSqlType(metaData.getColumnType(i + 1)));
        }

        return new DefaultTableMetaData(name, columns);
    }

    ////////////////////////////////////////////////////////////////////////////
    // ITable interface

    public ITableMetaData getTableMetaData()
    {
        return _metaData;
    }

    public int getRowCount()
    {
        return _rowCount;
    }

    public Object getValue(int row, String column) throws DataSetException
    {
        assertValidRowIndex(row);

        try
        {
            _resultSet.absolute(row + 1);
            return _resultSet.getObject(column);
        }
        catch (SQLException e)
        {
            throw new DataSetException(e);
        }
    }
}
