/*
 * AbstractDataSetTest.java   Feb 22, 2002
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

import junit.framework.TestCase;

/**
 * @author Manuel Laflamme
 * @version 1.0
 */
public abstract class AbstractDataSetTest extends TestCase
{
    private static final String[] TABLE_NAMES = {
        "TEST_TABLE",
        "SECOND_TABLE",
        "EMPTY_TABLE",
        "PK_TABLE",
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

    public void testGetTableNames() throws Exception
    {
        String[] expected = getExpectedNames();
        sort(expected);

        IDataSet dataSet = createDataSet();
        String[] names = dataSet.getTableNames();
        sort(names);

        assertEquals("table count", expected.length, names.length);
        for (int i = 0; i < expected.length; i++)
        {
            assertEquals("name " + i, expected[i], names[i]);
        }
    }

    public void testGetTable() throws Exception
    {
        String[] expected = getExpectedNames();
        sort(expected);

        IDataSet dataSet = createDataSet();
        String[] names = dataSet.getTableNames();
        sort(names);
        assertEquals("table count", expected.length, names.length);
        for (int i = 0; i < expected.length; i++)
        {
            ITable table = dataSet.getTable(names[i]);
            assertEquals("name", expected[i], table.getTableMetaData().getTableName());
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
        sort(expected);

        IDataSet dataSet = createDataSet();
        String[] names = dataSet.getTableNames();
        sort(names);
        assertEquals("table count", expected.length, names.length);
        for (int i = 0; i < expected.length; i++)
        {
            ITableMetaData metaData = dataSet.getTableMetaData(names[i]);
            assertEquals("name", expected[i], metaData.getTableName());
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
