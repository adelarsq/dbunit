/*
 * CompositeTable.java   Feb 17, 2002
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

/**
 * @author Manuel Laflamme
 * @version 1.0
 */
public class CompositeTable extends AbstractTable
{
    private final ITableMetaData _metaData;
    private final ITable[] _tables;

    public CompositeTable(ITableMetaData metaData, ITable table)
    {
        _metaData = metaData;
        _tables = new ITable[]{table};
    }

    public CompositeTable(ITableMetaData metaData, ITable[] tables)
    {
        _metaData = metaData;
        _tables = tables;
    }

    public CompositeTable(ITable table1, ITable table2)
    {
        _metaData = table1.getTableMetaData();
        _tables = new ITable[]{table1, table2};
    }

    ////////////////////////////////////////////////////////////////////////////
    // ITable interface

    public ITableMetaData getTableMetaData()
    {
        return _metaData;
    }

    public int getRowCount()
    {
        int totalCount = 0;
        for (int i = 0; i < _tables.length; i++)
        {
            ITable table = _tables[i];
            totalCount += table.getRowCount();
        }

        return totalCount;
    }

    public Object getValue(int row, String column) throws DataSetException
    {
        if (row < 0)
        {
            throw new RowOutOfBoundsException(row + " < 0 ");
        }

        int totalCount = 0;
        for (int i = 0; i < _tables.length; i++)
        {
            ITable table = _tables[i];

            int count = table.getRowCount();
            if (totalCount + count > row)
            {
                return table.getValue(row - totalCount, column);
            }
            totalCount += count;
        }

        throw new RowOutOfBoundsException(row + " > " + totalCount);
    }
}
