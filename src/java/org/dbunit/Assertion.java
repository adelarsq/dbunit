/*
 * Assertion.java   Mar 22, 2002
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

package org.dbunit;

import org.dbunit.dataset.*;
import org.dbunit.dataset.datatype.DataType;

import java.util.Arrays;
import java.util.Comparator;

import junit.framework.Assert;

/**
 * @author Manuel Laflamme
 * @version $Revision$
 */
public class Assertion
{
    private Assertion()
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

        String[] expectedNames = getSortedUpperTableNames(expectedDataSet);
        String[] actualNames = getSortedUpperTableNames(actualDataSet);

        // tables count
        Assert.assertEquals("table count", expectedNames.length, actualNames.length);


        // table names in no specific order
        for (int i = 0; i < expectedNames.length; i++)
        {
            if (!actualNames[i].equals(expectedNames[i]))
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
        String[] expectedNames = getSortedUpperColumnNames(expectedMetaData);
        String[] actualNames = getSortedUpperColumnNames(actualMetaData);
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

    private static String[] getSortedUpperColumnNames(ITableMetaData metaData)
            throws DataSetException
    {
        Column[] columns = metaData.getColumns();
        String[] names = new String[columns.length];
        for (int i = 0; i < columns.length; i++)
        {
            names[i] = columns[i].getColumnName().toUpperCase();
        }
        Arrays.sort(names);
        return names;
    }

    private static String[] getSortedUpperTableNames(IDataSet dataSet)
            throws DataSetException
    {
        String[] names = dataSet.getTableNames();
        for (int i = 0; i < names.length; i++)
        {
            names[i] = names[i].toUpperCase();
        }
        Arrays.sort(names);
        return names;
    }

}




