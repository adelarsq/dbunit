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
package org.dbunit.dataset;

import java.math.BigDecimal;

/**
 * Decorator that exposes Boolean value of the decorated table to specified
 * replacement values.
 *
 * @author Manuel Laflamme
 * @since Mar 14, 2003
 * @version $Revision$
 */
public class BooleanTable implements ITable
{
    static final Number DEFAULT_TRUE_VALUE = new BigDecimal(1);
    static final Number DEFAULT_FALSE_VALUE = new BigDecimal(0);

    private final ITable _table;
    private final Object _trueValue;
    private final Object _falseValue;

    /**
     * Creates a BooleanTable that decorates the specified table using numbers
     * 0 and 1 as replacement values.
     *
     * @param table the decorated table
     */
    public BooleanTable(ITable table)
    {
        this(table, DEFAULT_TRUE_VALUE, DEFAULT_FALSE_VALUE);
    }

    /**
     * Creates a BooleanTable that decorates the specified table and using the
     * specified replacement values.
     *
     * @param table the decorated table
     * @param trueValue the replacement value for {@link Boolean#TRUE}
     * @param falseValue the replacement value for {@link Boolean#FALSE}
     */
    BooleanTable(ITable table, Object trueValue, Object falseValue)
    {
        _table = table;
        _trueValue = trueValue;
        _falseValue = falseValue;
    }

    ////////////////////////////////////////////////////////////////////////
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
        Object value = _table.getValue(row, column);
        if (value instanceof Boolean)
        {
            return value.equals(Boolean.TRUE) ? _trueValue : _falseValue;
        }
        return value;
    }
}

