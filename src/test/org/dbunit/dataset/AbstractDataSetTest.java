/*
 * AbstractDataSetTest.java   Feb 22, 2002
 *
 * DbUnit Database Testing Framework
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

import java.util.*;

import junit.framework.TestCase;

/**
 * @author Manuel Laflamme
 * @version $Revision$
 */
public abstract class AbstractDataSetTest extends TestCase
{
    private static final String[] TABLE_NAMES = {
        "TEST_TABLE",
        "SECOND_TABLE",
        "EMPTY_TABLE",
        "PK_TABLE",
        "ONLY_PK_TABLE",
        "EMPTY_MULTITYPE_TABLE",
    };

    public AbstractDataSetTest(String s)
    {
        super(s);
    }

    protected static String[] getExpectedNames()
    {
        return (String[])TABLE_NAMES.clone();
    }

    /**
     * This method exclude BLOB_TABLE and CLOB_TABLE from the specified dataset
     * because BLOB and CLOB are not supported by all database vendor
     */
    public static IDataSet removeExtraTestTables(IDataSet dataSet) throws Exception
    {
        String[] names = dataSet.getTableNames();

        // exclude BLOB_TABLE and CLOB_TABLE from test since not supported by
        // all database vendor
        List nameList = new ArrayList(Arrays.asList(names));
        nameList.remove("BLOB_TABLE");
        nameList.remove("CLOB_TABLE");
        nameList.remove("DBUNIT.BLOB_TABLE");
        nameList.remove("DBUNIT.CLOB_TABLE");
        names = (String[])nameList.toArray(new String[0]);

        return new FilteredDataSet(names, dataSet);
    }

    protected abstract IDataSet createDataSet() throws Exception;

    /**
     * Many tests in this class assume a known sequence of table. For some
     * IDataSet implemntation (like OldDatabaseDataSet) we can't predict
     * any specific order. For supporting them, this method is called for both
     * the expected names and dataset names before comparing them.
     * <p>
     * This method should do nothing for implemntation supporting ordered names.
     * Others should sort the specified array.
     */
    protected void sort(Object[] array)
    {
    }

    protected void assertEqualsTableName(String mesage, String expected,
            String actual)
    {
        assertEquals(mesage, expected, actual);
    }

    public void testGetTableNames() throws Exception
    {
        String[] expected = getExpectedNames();
        sort(expected);

        IDataSet dataSet = removeExtraTestTables(createDataSet());
        String[] names = dataSet.getTableNames();
        sort(names);

        assertEquals("table count", expected.length, names.length);
        for (int i = 0; i < expected.length; i++)
        {
            assertEqualsTableName("name " + i, expected[i], names[i]);
        }
    }

    public void testGetTableNamesDefensiveCopy() throws Exception
    {
        IDataSet dataSet = createDataSet();
        assertTrue("Should not be same intance",
                dataSet.getTableNames() != dataSet.getTableNames());
    }

    public void testGetTable() throws Exception
    {
        String[] expected = getExpectedNames();
//        sort(expected);

        IDataSet dataSet = createDataSet();
//        String[] names = dataSet.getTableNames();
//        sort(names);
//        assertEquals("table count",
//                expected.length, dataSet.getTableNames().length);
        for (int i = 0; i < expected.length; i++)
        {
            ITable table = dataSet.getTable(expected[i]);
            assertEqualsTableName("name " + i, expected[i], table.getTableMetaData().getTableName());
        }
    }

    public void testGetUnknownTable() throws Exception
    {
        IDataSet dataSet = createDataSet();
        try
        {
            dataSet.getTable("UNKNOWN_TABLE");
            fail("Should throw a NoSuchTableException");
        }
        catch (NoSuchTableException e)
        {
        }
    }

    public void testGetTableMetaData() throws Exception
    {
        String[] expected = getExpectedNames();
//        sort(expected);

        IDataSet dataSet = createDataSet();
//        String[] names = dataSet.getTableNames();
//        sort(names);
//        assertEquals("table count",
//                expected.length, dataSet.getTableNames().length);
        for (int i = 0; i < expected.length; i++)
        {
            ITableMetaData metaData = dataSet.getTableMetaData(expected[i]);
            assertEqualsTableName("name " + i, expected[i], metaData.getTableName());
        }
    }

    public void testGetUnknownTableMetaData() throws Exception
    {
        IDataSet dataSet = createDataSet();
        try
        {
            dataSet.getTableMetaData("UNKNOWN_TABLE");
            fail("Should throw a NoSuchTableException");
        }
        catch (NoSuchTableException e)
        {
        }
    }

}










