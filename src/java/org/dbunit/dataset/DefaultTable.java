/*
 * DefaultTable.java   Feb 17, 2002
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

package org.dbunit.dataset;

import java.util.Arrays;
import java.util.List;

/**
 * @author Manuel Laflamme
 * @version 1.0
 */
public class DefaultTable extends AbstractTable
{
    private final ITableMetaData _metaData;
    private final List _list;

    public DefaultTable(ITableMetaData metaData, List list)
    {
        _metaData = metaData;
        _list = list;
    }

    /**
     * Creates a new empty table having the specified name.
     */
    public DefaultTable(String tableName)
    {
        _metaData = new DefaultTableMetaData(tableName, new Column[0]);
        _list = Arrays.asList(new Object[0]);
    }

    ////////////////////////////////////////////////////////////////////////////
    // ITable interface

    public ITableMetaData getTableMetaData()
    {
        return _metaData;
    }

    public int getRowCount()
    {
        return _list.size();
    }

    public Object getValue(int row, String column) throws DataSetException
    {
        assertValidRowIndex(row);

        Object[] rowValues = (Object[])_list.get(row);
        return rowValues[getColumnIndex(column)];
    }

}
