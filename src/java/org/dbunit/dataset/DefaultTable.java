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

import java.util.ArrayList;
import java.util.List;

/**
 * @author Manuel Laflamme
 * @version $Revision$
 * @since Feb 17, 2002
 */
public class DefaultTable extends AbstractTable
{
    private final ITableMetaData _metaData;
    protected final List _rowList;

    public DefaultTable(ITableMetaData metaData, List list)
    {
        _metaData = metaData;
        _rowList = list;
    }

    /**
     * Creates a new empty table having the specified name.
     */
    public DefaultTable(String tableName)
    {
        _metaData = new DefaultTableMetaData(tableName, new Column[0]);
        _rowList = new ArrayList();
    }

    public DefaultTable(String tableName, Column[] columns, List list)
    {
        _metaData = new DefaultTableMetaData(tableName, columns);
        _rowList = list;
    }

    public DefaultTable(String tableName, Column[] columns)
    {
        _metaData = new DefaultTableMetaData(tableName, columns);
        _rowList = new ArrayList();
    }

    protected DefaultTable(ITableMetaData metaData)
    {
        _metaData = metaData;
        _rowList = new ArrayList();
    }

    public void addRow() throws DataSetException
    {
        int columnCount = _metaData.getColumns().length;
        _rowList.add(new Object[columnCount]);
    }

    public void addRow(Object[] values) throws DataSetException
    {
        _rowList.add(values);
    }

    public void setValue(int row, String column, Object value) throws DataSetException
    {
        assertValidRowIndex(row);

        Object[] rowValues = (Object[])_rowList.get(row);
        rowValues[getColumnIndex(column)] = value;
    }

    ////////////////////////////////////////////////////////////////////////////
    // ITable interface

    public ITableMetaData getTableMetaData()
    {
        return _metaData;
    }

    public int getRowCount()
    {
        return _rowList.size();
    }

    public Object getValue(int row, String column) throws DataSetException
    {
        assertValidRowIndex(row);

        Object[] rowValues = (Object[])_rowList.get(row);
        return rowValues[getColumnIndex(column)];
    }

}





