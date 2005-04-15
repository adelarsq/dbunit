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

import org.dbunit.database.AmbiguousTableNameException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Manuel Laflamme
 * @version $Revision$
 * @since Feb 22, 2002
 */
public abstract class AbstractDataSetTest extends AbstractTest
{
    public AbstractDataSetTest(String s)
    {
        super(s);
    }

    protected int[] getExpectedDuplicateRows()
    {
        return new int[] {1, 0, 2};
    }

    /**
     * This method exclude BLOB_TABLE and CLOB_TABLE from the specified dataset
     * because BLOB and CLOB are not supported by all database vendor.  It also excludes
     * tables with Identity columns (MSSQL) becasuse they are specific to MSSQL.
     * TODO : should be refactored into thee various DatabaseEnvironments!
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
        /*
        this table shows up on MSSQLServer.  It is a user table for storing diagram information
        that really should be considered a system table.
        */
        nameList.remove("DBUNIT.dtproperties");
        nameList.remove("dtproperties");
        /*
        These tables are created specifically for testing identity columns on MSSQL server.
        They should be ignored on other platforms.
        */
        nameList.remove("DBUNIT.IDENTITY_TABLE");
        nameList.remove("IDENTITY_TABLE");
        nameList.remove("DBUNIT.TEST_IDENTITY_NOT_PK");
        nameList.remove("TEST_IDENTITY_NOT_PK");

        names = (String[])nameList.toArray(new String[0]);

        return new FilteredDataSet(names, dataSet);
    }

    /**
     * Create a dataset with duplicate tables having different char case in name
     * @return
     */
    protected IDataSet createMultipleCaseDuplicateDataSet() throws Exception
    {
        IDataSet dataSet = createDuplicateDataSet();
        ITable[] tables = DataSetUtils.getTables(dataSet.iterator());
        ITable lowerTable = tables[0];
        dataSet = new DefaultDataSet(new ITable[]{
            new CompositeTable(getDuplicateTableName().toLowerCase(), lowerTable),
            tables[1],
            tables[2],
        });
        return dataSet;
    }

    protected abstract IDataSet createDataSet() throws Exception;

    protected abstract IDataSet createDuplicateDataSet() throws Exception;

    protected void assertEqualsTableName(String mesage, String expected,
            String actual)
    {
        assertEquals(mesage, expected, actual);
    }

    public void testGetTableNames() throws Exception
    {
        String[] expected = getExpectedNames();
        assertContainsIgnoreCase("minimal names subset",
                super.getExpectedNames(), expected);

        IDataSet dataSet = createDataSet();
        String[] names = dataSet.getTableNames();

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

        IDataSet dataSet = createDataSet();
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

        IDataSet dataSet = createDataSet();
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

    public void testGetTables() throws Exception
    {
        String[] expected = getExpectedNames();
        assertContainsIgnoreCase("minimal names subset",
                super.getExpectedNames(), expected);

        IDataSet dataSet = createDataSet();
        ITable[] tables = dataSet.getTables();

        assertEquals("table count", expected.length, tables.length);
        for (int i = 0; i < expected.length; i++)
        {
            assertEqualsTableName("name " + i, expected[i],
                    tables[i].getTableMetaData().getTableName());
        }
    }

    public void testGetTablesDefensiveCopy() throws Exception
    {
        IDataSet dataSet = createDataSet();
        assertTrue("Should not be same instance",
                dataSet.getTables() != dataSet.getTables());
    }

    public void testGetDuplicateTables() throws Exception
    {
        String[] expectedNames = getExpectedDuplicateNames();
        int[] expectedRows = getExpectedDuplicateRows();
        assertEquals(expectedNames.length, expectedRows.length);

        IDataSet dataSet = createDuplicateDataSet();
        ITable[] tables = dataSet.getTables();

        assertEquals("table count", expectedNames.length, tables.length);
        for (int i = 0; i < expectedNames.length; i++)
        {
            ITable table = tables[i];
            String name = table.getTableMetaData().getTableName();
            assertEqualsTableName("name " + i, expectedNames[i], name);
            assertEquals("row count", expectedRows[i], table.getRowCount());
        }
    }

    public void testGetDuplicateTableNames() throws Exception
    {
        String[] expected = getExpectedDuplicateNames();

        IDataSet dataSet = createDuplicateDataSet();
        String[] names = dataSet.getTableNames();

        assertEquals("table count", expected.length, names.length);
        for (int i = 0; i < expected.length; i++)
        {
            assertEqualsTableName("name " + i, expected[i], names[i]);
        }
    }

    public void testGetDuplicateTable() throws Exception
    {
        IDataSet dataSet = createDuplicateDataSet();
        try
        {
            dataSet.getTable(getDuplicateTableName());
            fail("Should throw AmbiguousTableNameException");
        }
        catch (AmbiguousTableNameException e)
        {
        }
    }

    public void testGetDuplicateTableMetaData() throws Exception
    {
        IDataSet dataSet = createDuplicateDataSet();
        try
        {
            dataSet.getTableMetaData(getDuplicateTableName());
            fail("Should throw AmbiguousTableNameException");
        }
        catch (AmbiguousTableNameException e)
        {
        }
    }

    public void testGetCaseInsensitiveTable() throws Exception
    {
        String[] expectedNames = getExpectedLowerNames();

        IDataSet dataSet = createDataSet();
        for (int i = 0; i < expectedNames.length; i++)
        {
            String expected = expectedNames[i];
            ITable table = dataSet.getTable(expected);
            String actual = table.getTableMetaData().getTableName();

            if (!expected.equalsIgnoreCase(actual))
            {
                assertEquals("name " + i, expected, actual);
            }
        }
    }

    public void testGetCaseInsensitiveTableMetaData() throws Exception
    {
        String[] expectedNames = getExpectedLowerNames();
        IDataSet dataSet = createDataSet();

        for (int i = 0; i < expectedNames.length; i++)
        {
            String expected = expectedNames[i];
            ITableMetaData metaData = dataSet.getTableMetaData(expected);
            String actual = metaData.getTableName();

            if (!expected.equalsIgnoreCase(actual))
            {
                assertEquals("name " + i, expected, actual);
            }
        }
    }

    public void testGetCaseInsensitiveDuplicateTable() throws Exception
    {
        IDataSet dataSet = createMultipleCaseDuplicateDataSet();

        try
        {
            dataSet.getTable(getDuplicateTableName().toLowerCase());
            fail("Should throw AmbiguousTableNameException");
        }
        catch (AmbiguousTableNameException e)
        {
        }
    }

    public void testGetCaseInsensitiveDuplicateTableMetaData() throws Exception
    {
        IDataSet dataSet = createMultipleCaseDuplicateDataSet();
        try
        {
            dataSet.getTableMetaData(getDuplicateTableName().toLowerCase());
            fail("Should throw AmbiguousTableNameException");
        }
        catch (AmbiguousTableNameException e)
        {
        }
    }

    public void testIterator() throws Exception
    {
        String[] expected = getExpectedNames();
        assertContainsIgnoreCase("minimal names subset",
                super.getExpectedNames(), expected);

        int i = 0;
        ITableIterator iterator = createDataSet().iterator();
        while(iterator.next())
        {
            assertEqualsTableName("name " + i, expected[i],
                    iterator.getTableMetaData().getTableName());
            i++;
        }

        assertEquals("table count", expected.length, i);
    }

    public void testIteratorAndDuplicateTable() throws Exception
    {
        String[] expectedNames = getExpectedDuplicateNames();
//        int[] expectedRows = getExpectedDuplicateRows();

        int i = 0;
        ITableIterator iterator = createDuplicateDataSet().iterator();
        while(iterator.next())
        {
            ITable table = iterator.getTable();
            String name = table.getTableMetaData().getTableName();
            assertEqualsTableName("name " + i, expectedNames[i], name);
//            assertEquals("row count", expectedRows[i], table.getRowCount());
            i++;
        }

        assertEquals("table count", expectedNames.length, i);
    }

    public void testReverseIterator() throws Exception
    {
        String[] expected = DataSetUtils.reverseStringArray(getExpectedNames());
        assertContainsIgnoreCase("minimal names subset",
                super.getExpectedNames(), expected);

        int i = 0;
        ITableIterator iterator = createDataSet().reverseIterator();
        while(iterator.next())
        {
            assertEqualsTableName("name " + i, expected[i],
                    iterator.getTableMetaData().getTableName());
            i++;
        }

        assertEquals("table count", expected.length, i);
    }

    public void testReverseIteratorAndDuplicateTable() throws Exception
    {
        String[] expectedNames = getExpectedDuplicateNames();
        int[] expectedRows = getExpectedDuplicateRows();

        int i = expectedRows.length - 1;
        int count = 0;
        ITableIterator iterator = createDuplicateDataSet().reverseIterator();
        while(iterator.next())
        {
            ITable table = iterator.getTable();
            String name = table.getTableMetaData().getTableName();
            assertEqualsTableName("name " + i, expectedNames[i], name);
            assertEquals("row count", expectedRows[i], table.getRowCount());
            i--;
            count++;
        }

        assertEquals("table count", expectedNames.length, count);
    }
}










