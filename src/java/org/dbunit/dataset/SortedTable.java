/*
 * DefaultTable.java   Feb 19, 2003
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

package org.dbunit.dataset;

import org.dbunit.DatabaseUnitRuntimeException;
import org.dbunit.dataset.datatype.DataType;

import java.util.Arrays;
import java.util.Comparator;

/**
 * This is a ITable decorator that provide a sorted view of the decorated table.
 * This implementation does not keep a separate copy of the decorated table data.
 *
 * @author Manuel Laflamme
 * @version $Revision$
 */
public class SortedTable extends AbstractTable
{
    private final ITable _table;
    private final Column[] _columns;
    private Integer[] _indexes;

    /**
     * Sort the decorated table by specified columns order.
     */
    public SortedTable(ITable table, Column[] columns)
    {
        _table = table;
        _columns = columns;
    }

    /**
     * Sort the decorated table by specified columns order.
     */
    public SortedTable(ITable table, String[] columnNames) throws DataSetException
    {
        _table = table;
        _columns = new Column[columnNames.length];

        Column[] columns = table.getTableMetaData().getColumns();
        for (int i = 0; i < columnNames.length; i++)
        {
            String columnName = columnNames[i];
            _columns[i] = DataSetUtils.getColumn(columnName, columns);
        }
    }

    /**
     * Sort the decorated table by specified metadata columns order. All
     * metadata columns will be used.
     */
    public SortedTable(ITable table, ITableMetaData metaData) throws DataSetException
    {
        this(table, metaData.getColumns());
    }

    /**
     * Sort the decorated table by its own columns order. All
     * table columns will be used.
     */
    public SortedTable(ITable table) throws DataSetException
    {
        this(table, table.getTableMetaData());
    }

    private int getOriginalRowIndex(int row) throws DataSetException
    {
        if (_indexes == null)
        {
            Integer[] indexes = new Integer[getRowCount()];
            for (int i = 0; i < indexes.length; i++)
            {
                indexes[i] = new Integer(i);
            }

            try
            {
                Arrays.sort(indexes, new RowComparator());
            }
            catch (DatabaseUnitRuntimeException e)
            {
                throw (DataSetException)e.getException();
            }

            _indexes = indexes;
        }

        return _indexes[row].intValue();
    }

    ////////////////////////////////////////////////////////////////////////////
    // ITable interface

    public ITableMetaData getTableMetaData()
    {
        return _table.getTableMetaData();
    }

    public int getRowCount()
    {
        return _table.getRowCount();
    }

    public Object getValue(int row, String column) throws DataSetException
    {
        assertValidRowIndex(row);

        return _table.getValue(getOriginalRowIndex(row), column);
    }

    ////////////////////////////////////////////////////////////////////////////
    // Comparator interface

    private class RowComparator implements Comparator
    {
        public int compare(Object o1, Object o2)
        {
            Integer i1 = (Integer)o1;
            Integer i2 = (Integer)o2;

            try
            {
                for (int i = 0; i < _columns.length; i++)
                {
                    String columnName = _columns[i].getColumnName();
                    Object value1 = _table.getValue(i1.intValue(), columnName);
                    Object value2 = _table.getValue(i2.intValue(), columnName);

                    if (value1 == null && value2 == null)
                    {
                        continue;
                    }

                    if (value1 == null && value2 != null)
                    {
                        return -1;
                    }

                    if (value1 != null && value2 == null)
                    {
                        return 1;
                    }

                    String stringValue1 = DataType.asString(value1);
                    String stringValue2 = DataType.asString(value2);
                    int result = stringValue1.compareTo(stringValue2);
                    if (result != 0)
                    {
                        return result;
                    }
                }
            }
            catch (DataSetException e)
            {
                throw new DatabaseUnitRuntimeException(e);
            }

            return 0;
        }
    }
}





