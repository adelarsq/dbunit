/*
 * Column.java   Feb 17, 2002
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

import org.dbunit.dataset.datatype.DataType;

/**
 * Represents a table column.
 *
 * @author Manuel Laflamme
 * @version 1.0
 */
public class Column
{
    private final String _columnName;
    private final DataType _dataType;
    private final boolean _nullable;

    /**
     * Creates a Column object. This contructor set nullable to true.
     *
     * @param columnName the column name
     * @param dataType the data type
     */
    public Column(String columnName, DataType dataType)
    {
        _columnName = columnName;
        _dataType = dataType;
        _nullable = true;
    }

    /**
     * Creates a Column object.
     */
    public Column(String columnName, DataType dataType, boolean nullable)
    {
        _columnName = columnName;
        _dataType = dataType;
        _nullable = nullable;
    }

    /**
     * Returns this column name.
     */
    public String getColumnName()
    {
        return _columnName;
    }

    /**
     * Returns this column data type.
     */
    public DataType getDataType()
    {
        return _dataType;
    }

    /**
     * Returns <code>true</code> if this column is nullable.
     */
    public boolean isNullable()
    {
        return _nullable;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Object class

    public String toString()
    {
        return _columnName;
    }

}

