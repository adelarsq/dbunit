/*
 * DataSetUtils.java   Feb 19, 2002
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
import org.dbunit.dataset.datatype.TypeCastException;

/**
 * @author Manuel Laflamme
 * @version 1.0
 */
public class DataSetUtils
{
    private DataSetUtils()
    {
    }

    public static String getAbsoluteName(String schema, String name)
    {
        if (schema == null)
        {
            return name;
        }

        return schema + "." + name;
    }

    public static String getSqlValueString(Object value, DataType dataType)
            throws TypeCastException
    {
        if (value == null)
        {
            return "NULL";
        }

        String stringValue = (String)DataType.STRING.typeCast(value);
        if (!dataType.isNumber())
        {
            stringValue = "'" + stringValue + "'";
        }

        return stringValue;
    }

    public static Column getColumn(String columnName, Column[] columns)
    {
        for (int i = 0; i < columns.length; i++)
        {
            Column column = columns[i];
            if (columnName.equals(columns[i].getColumnName()))
            {
                return column;
            }
        }

        return null;
    }

    public static ITable[] getTables(String[] names, IDataSet dataSet)
            throws DataSetException
    {
        ITable[] tables = new ITable[names.length];
        for (int i = 0; i < names.length; i++)
        {
            String name = names[i];
            tables[i] = dataSet.getTable(name);
        }

        return tables;
    }

    public static ITable[] getTables(IDataSet dataSet) throws DataSetException
    {
        return getTables(dataSet.getTableNames(), dataSet);
    }

    public static String[] getReverseTableNames(IDataSet dataSet)
            throws DataSetException
    {
        return reverseStringArray(dataSet.getTableNames());
    }

    private static String[] reverseStringArray(String[] array)
    {
        String[] newArray = new String[array.length];
        for (int i = 0; i < array.length; i++)
        {
            newArray[array.length - 1 - i] = array[i];
        }
        return newArray;
    }

}
