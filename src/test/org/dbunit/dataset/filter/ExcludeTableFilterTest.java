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
package org.dbunit.dataset.filter;

import org.dbunit.database.AmbiguousTableNameException;
import org.dbunit.dataset.*;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

/**
 * @author Manuel Laflamme
 * @since Mar 18, 2003
 * @version $Revision$
 */
public class ExcludeTableFilterTest extends AbstractTableFilterTest
{
    public ExcludeTableFilterTest(String s)
    {
        super(s);
    }

    public void testIsValidName() throws Exception
    {
        String[] validNames = getExpectedNames();
        ExcludeTableFilter filter = new ExcludeTableFilter();
        filter.excludeTable(getExtraTableName());

        for (int i = 0; i < validNames.length; i++)
        {
            String validName = validNames[i];
            assertEquals(validName, true, filter.isValidName(validName));
        }
    }

    public void testIsCaseInsensitiveValidName() throws Exception
    {
        String[] validNames = getExpectedNames();
        ExcludeTableFilter filter = new ExcludeTableFilter();
        filter.excludeTable(getExtraTableName());

        for (int i = 0; i < validNames.length; i++)
        {
            String validName = validNames[i];
            assertEquals(validName, true, filter.isValidName(validName));
        }
    }

    public void testIsValidNameAndInvalid() throws Exception
    {
        String[] invalidNames = new String[] {
            "INVALID_TABLE",
            "UNKNOWN_TABLE",
        };
        ITableFilter filter = new ExcludeTableFilter(invalidNames);

        for (int i = 0; i < invalidNames.length; i++)
        {
            String invalidName = invalidNames[i];
            assertEquals(invalidName, false, filter.isValidName(invalidName));
        }
    }

    public void testGetTableNames() throws Exception
    {
        String[] expectedNames = getExpectedNames();
        ExcludeTableFilter filter = new ExcludeTableFilter();
        filter.excludeTable(getExtraTableName());

        IDataSet dataSet = createDataSet();
        assertTrue("dataset names count",
                dataSet.getTableNames().length > expectedNames.length);

        String[] actualNames = filter.getTableNames(dataSet);
        assertEquals("name count", expectedNames.length, actualNames.length);
        assertEquals("names",
                Arrays.asList(expectedNames), Arrays.asList(actualNames));
    }

    public void testGetTableNamesAndTableNotInDecoratedDataSet() throws Exception
    {
        String[] expectedNames = getExpectedNames();
        ExcludeTableFilter filter = new ExcludeTableFilter();
        filter.excludeTable(getExtraTableName());
        filter.excludeTable("UNKNOWN_TABLE");

        IDataSet dataSet = createDataSet();
        assertTrue("dataset names count",
                dataSet.getTableNames().length > expectedNames.length);

        String[] actualNames = filter.getTableNames(dataSet);
        assertEquals("name count", expectedNames.length, actualNames.length);
        assertEquals("names",
                Arrays.asList(expectedNames), Arrays.asList(actualNames));
    }

    public void testGetDuplicateTableNames() throws Exception
    {
        String[] expectedNames = getExpectedDuplicateNames();
        ExcludeTableFilter filter = new ExcludeTableFilter();
        filter.excludeTable(getExtraTableName());

        IDataSet dataSet = createDuplicateDataSet();
        assertTrue("dataset names count",
                dataSet.getTableNames().length > expectedNames.length);

        String[] actualNames = filter.getTableNames(dataSet);
        assertEquals("name count", expectedNames.length, actualNames.length);
        assertEquals("names",
                Arrays.asList(expectedNames), Arrays.asList(actualNames));
    }

    public void testGetCaseInsensitiveTableNames() throws Exception
    {
        ExcludeTableFilter filter = new ExcludeTableFilter();
        filter.excludeTable(getExtraTableName());

        String[] expectedNames = getExpectedLowerNames();
        IDataSet dataSet = new LowerCaseDataSet(createDataSet());
        assertTrue("dataset names count",
                dataSet.getTableNames().length > expectedNames.length);

        String[] actualNames = filter.getTableNames(dataSet);
        assertEquals("name count", expectedNames.length, actualNames.length);
        assertEquals("names",
                Arrays.asList(expectedNames), Arrays.asList(actualNames));
    }

    public void testGetReverseTableNames() throws Exception
    {
        // Cannot test!
    }

    public void testGetTables() throws Exception
    {
        String[] expectedNames = getExpectedNames();
        ExcludeTableFilter filter = new ExcludeTableFilter();
        filter.excludeTable(getExtraTableName());

        IDataSet dataSet = createDataSet();
        assertTrue("dataset names count",
                dataSet.getTableNames().length > expectedNames.length);

        ITable[] actualTables = filter.getTables(dataSet);
        String[] actualNames = new DefaultDataSet(actualTables).getTableNames();
        assertEquals("table count", expectedNames.length, actualTables.length);
        assertEquals("table names",
                Arrays.asList(expectedNames), Arrays.asList(actualNames));
    }

    public void testGetDuplicateTables() throws Exception
    {
        String[] expectedNames = getExpectedDuplicateNames();
        ExcludeTableFilter filter = new ExcludeTableFilter();
        filter.excludeTable(getExtraTableName());

        IDataSet dataSet = createDuplicateDataSet();
        assertTrue("dataset names count",
                dataSet.getTableNames().length > expectedNames.length);

        ITable[] actualTables = filter.getTables(dataSet);
        String[] actualNames = new DefaultDataSet(actualTables).getTableNames();
        assertEquals("table count", expectedNames.length, actualTables.length);
        assertEquals("table names",
                Arrays.asList(expectedNames), Arrays.asList(actualNames));
    }

    public void testGetCaseInsensitiveTables() throws Exception
    {
        ExcludeTableFilter filter = new ExcludeTableFilter();
        filter.excludeTable(getExtraTableName());

        String[] expectedNames = getExpectedLowerNames();
        IDataSet dataSet = new LowerCaseDataSet(createDataSet());
        assertTrue("dataset names count",
                dataSet.getTableNames().length > expectedNames.length);

        ITable[] actualTables = filter.getTables(dataSet);
        String[] actualNames = new DefaultDataSet(actualTables).getTableNames();
        assertEquals("table count", expectedNames.length, actualTables.length);
        assertEquals("table names",
                Arrays.asList(expectedNames), Arrays.asList(actualNames));
    }

    public void testGetReverseTables() throws Exception
    {
        // Cannot test!
    }

    public void testGetTablesAndTableNotInDecoratedDataSet() throws Exception
    {
        String[] expectedNames = getExpectedNames();
        ExcludeTableFilter filter = new ExcludeTableFilter();
        filter.excludeTable(getExtraTableName());
        filter.excludeTable("UNKNOWN_TABLE");

        IDataSet dataSet = createDataSet();
        assertTrue("dataset names count",
                dataSet.getTableNames().length > expectedNames.length);

        ITable[] actualTables = filter.getTables(dataSet);
        String[] actualNames = new DefaultDataSet(actualTables).getTableNames();
        assertEquals("table count", expectedNames.length, actualTables.length);
        assertEquals("table names",
                Arrays.asList(expectedNames), Arrays.asList(actualNames));
    }
}
