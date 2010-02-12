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
package org.dbunit.dataset;

import org.dbunit.dataset.datatype.DataType;

/**
 * @author Manuel Laflamme
 * @since Apr 12, 2003
 * @version $Revision$
 */
public class MockTableMetaData extends AbstractTableMetaData
{
    private String _tableName;
    private Column[] _columns = new Column[0];
    private String[] _keyNames = new String[0];

    public MockTableMetaData()
    {
    }

    public MockTableMetaData(String tableName, String[] columnNames)
    {
        _tableName = tableName;
        setupColumns(columnNames);
    }

    public void setTableName(String tableName)
    {
        _tableName = tableName;
    }

    public void setupColumns(Column[] columns)
    {
        _columns = columns;
    }

    public void setupColumns(String[] columnNames)
    {
        Column[] columns = new Column[columnNames.length];
        for (int i = 0; i < columnNames.length; i++)
        {
            String columnName = columnNames[i];
            columns[i] = new Column(columnName, DataType.UNKNOWN);
        }
        _columns = columns;
    }

    public void setupPrimaryKeys(String[] keyNames)
    {
        _keyNames = keyNames;
    }

    ////////////////////////////////////////////////////////////////////////////
    // ITableMetaData interface

    public String getTableName()
    {
        return _tableName;
    }

    public Column[] getColumns() throws DataSetException
    {
        return _columns;
    }

    public Column[] getPrimaryKeys() throws DataSetException
    {
        return Columns.getColumns(_keyNames, _columns);
    }
}
