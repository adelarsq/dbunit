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

import java.util.Arrays;
import java.util.Comparator;

import junit.framework.Assert;

import org.dbunit.dataset.datatype.DataType;
import org.dbunit.dataset.datatype.TypeCastException;

/**
 * This class contains various methods for manipulating datasets.
 *
 * @author Manuel Laflamme
 * @version 1.0
 */
public class DataSetUtils
{
    private DataSetUtils()
    {
    }

    /**
     * Asserts that the two specified dataset are equals. This method ignore
     * the tables order.
     */
    public static void assertEquals(IDataSet expectedDataSet,
            IDataSet actualDataSet) throws Exception
    {
        // do not continue if same instance
        if (expectedDataSet == actualDataSet)
        {
            return;
        }

        String[] expectedNames = expectedDataSet.getTableNames();
        String[] actualNames = actualDataSet.getTableNames();

        // tables count
        Assert.assertEquals("table count", expectedNames.length, actualNames.length);


        // table names in no specific order
        Arrays.sort(expectedNames);
        Arrays.sort(actualNames);
        for (int i = 0; i < expectedNames.length; i++)
        {
            if (actualNames[i].equals(expectedNames[i]))
            {
                Assert.fail("expected tables " + Arrays.asList(expectedNames) +
                        " but was " + Arrays.asList(actualNames));
            }

        }

        // tables
        for (int i = 0; i < expectedNames.length; i++)
        {
            String name = expectedNames[i];
            assertEquals(expectedDataSet.getTable(name),
                    actualDataSet.getTable(name));
        }

    }


    /**
     * Asserts that the two specified tables are equals. This method ignore the
     * table names, the columns order, the columns data type and the primary
     * keys.
     */
    public static void assertEquals(ITable expectedTable, ITable actualTable)
            throws Exception
    {
        // do not continue if same instance
        if (expectedTable == actualTable)
        {
            return;
        }

        ITableMetaData expectedMetaData = expectedTable.getTableMetaData();
        ITableMetaData actualMetaData = actualTable.getTableMetaData();
        String expectedTableName = expectedMetaData.getTableName();

//        // verify table name
//        Assert.assertEquals("table name", expectedMetaData.getTableName(),
//                actualMetaData.getTableName());

        // column count
        String[] expectedNames = getSortedColumnNames(expectedMetaData);
        String[] actualNames = getSortedColumnNames(actualMetaData);
        Assert.assertEquals("column count (table=" + expectedTableName + ")",
                expectedNames.length, actualNames.length);

        // columns names in no specific order
        for (int i = 0; i < expectedNames.length; i++)
        {
            String expectedName = expectedNames[i];
            String actualName = actualNames[i];
            if (!expectedName.equals(actualName))
            {
                Assert.fail("expected columns " + Arrays.asList(expectedNames) +
                        " but was " + Arrays.asList(actualNames) + " (table=" +
                        expectedTableName + ")");
            }

        }

        // row count
        Assert.assertEquals("row count (table=" + expectedTableName + ")",
                expectedTable.getRowCount(), actualTable.getRowCount());

        // values as strings
        for (int i = 0; i < expectedTable.getRowCount(); i++)
        {
            for (int j = 0; j < expectedNames.length; j++)
            {
                String columnName = expectedNames[j];

                Object expectedValue = expectedTable.getValue(i, columnName);
                Object actualValue = actualTable.getValue(i, columnName);
                Assert.assertEquals("value (table=" + expectedTableName +
                        ", row=" + i + ", col=" + columnName + ")",
                        DataType.asString(expectedValue),
                        DataType.asString(actualValue));

            }
        }
    }

    private static String[] getSortedColumnNames(ITableMetaData metaData)
            throws DataSetException
    {
        Column[] columns = metaData.getColumns();
        String[] names = new String[columns.length];
        for (int i = 0; i < columns.length; i++)
        {
            names[i] = columns[i].getColumnName();
        }
        Arrays.sort(names);
        return names;
    }


    /**
     * Returns the specified name qualified with the specified prefix. The name
     * is not modified if the prefix is <code>null</code> or if the name is
     * already qualified.
     * <p>
     * Example: <br>
     * <code>getQualifiedName(null, "NAME")</code> returns
     * <code>"NAME"</code>. <code>getQualifiedName("PREFIX", "NAME")</code>
     * returns <code>"PREFIX.NAME"</code> and
     * <code>getQualifiedName("PREFIX2", "PREFIX1.NAME")</code>
     * returns <code>"PREFIX1.NAME"</code>.
     *
     * @param schema the prefix
     * @param name the name
     * @returns the qualified name
     */
    public static String getQualifiedName(String prefix, String name)
    {
        if (prefix == null || prefix.equals("") || name.indexOf(".") >= 0)
        {
            return name;
        }

        return prefix + "." + name;
    }

    /**
     * Returns the specified value as a string to be use in an SQL Statement.
     * For example the string <code>myValue</code> is returned as
     * <code>'myValue'</code>.
     *
     * @param value the value
     * @param dataType the value data type
     * @returns the SQL string value
     */
    public static String getSqlValueString(Object value, DataType dataType)
            throws TypeCastException
    {
        if (value == null)
        {
            return "NULL";
        }

        String stringValue = DataType.asString(value);
        if (dataType == DataType.DATE)
        {
            return "{d '" + stringValue + "'}";
        }

        if (dataType == DataType.TIME)
        {
            return "{t '" + stringValue + "'}";
        }

        if (dataType == DataType.TIMESTAMP)
        {
            return "{ts '" + stringValue + "'}";
        }

        if (!dataType.isNumber())
        {
            stringValue = "'" + stringValue + "'";
        }

        return stringValue;
    }

    /**
     * Search and returns the specified column from the specified column array.
     *
     * @param columnName the name of the column to search.
     * @param columns the array of columns from which the column must be searched.
     * @return the column or <code>null</code> if the column is not found
     */
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

    /**
     * Search and returns the specified tables from the specified dataSet.
     *
     * @param names the names of the tables to search.
     * @param dataSet the dataset from which the tables must be searched.
     * @return the tables or an empty array if no tables are found.
     */
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

    /**
     * Returns the tables from the specified dataset.
     */
    public static ITable[] getTables(IDataSet dataSet) throws DataSetException
    {
        return getTables(dataSet.getTableNames(), dataSet);
    }

    /**
     * Returns the table names from the specified dataset in reverse order.
     */
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




