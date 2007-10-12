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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
 * @since Feb 19, 2003
 */
public class SortedTable extends AbstractTable
{

    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(SortedTable.class);

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
        logger.debug("getOriginalRowIndex(row=" + row + ") - start");

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
                logger.error("getOriginalRowIndex()", e);

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
        logger.debug("getTableMetaData() - start");

        return _table.getTableMetaData();
    }

    public int getRowCount()
    {
        logger.debug("getRowCount() - start");

        return _table.getRowCount();
    }

    public Object getValue(int row, String column) throws DataSetException
    {
        logger.debug("getValue(row=" + row + ", column=" + column + ") - start");

        assertValidRowIndex(row);

        return _table.getValue(getOriginalRowIndex(row), column);
    }

    ////////////////////////////////////////////////////////////////////////////
    // Comparator interface

    private class RowComparator implements Comparator
    {

        /**
         * Logger for this class
         */
        private final Logger logger = LoggerFactory.getLogger(RowComparator.class);

        public int compare(Object o1, Object o2)
        {
            logger.debug("compare(o1=" + o1 + ", o2=" + o2 + ") - start");

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
                logger.error("compare()", e);

                throw new DatabaseUnitRuntimeException(e);
            }

            return 0;
        }
    }
}





