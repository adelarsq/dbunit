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

import org.dbunit.dataset.*;
import org.dbunit.dataset.xml.XmlDataSet;
import org.dbunit.database.AmbiguousTableNameException;

import junit.framework.TestCase;

import java.io.FileReader;
import java.util.Arrays;

/**
 * @author Manuel Laflamme
 * @since Mar 8, 2003
 * @version $Revision$
 */
public class SequenceTableFilterTest extends AbstractTableFilterTest
{

    public SequenceTableFilterTest(String s)
    {
        super(s);
    }

    public void testIsValidName() throws Exception
    {
        String[] validNames = getExpectedNames();
        ITableFilter filter = new SequenceTableFilter(validNames);

        for (int i = 0; i < validNames.length; i++)
        {
            String validName = validNames[i];
            assertEquals(validName, true, filter.isValidName(validName));
        }
    }

    public void testIsValidName2() throws Exception
    {
//        String[] validNames = getExpectedNames();
//        ITableFilter filter = new SequenceTableFilter(validNames);
//
//        for (int i = 0; i < validNames.length; i++)
//        {
//            String validName = validNames[i];
//            assertEquals(validName, true, filter.isValidName(validName));
//        }
    }

    public void testIsCaseInsensitiveValidName() throws Exception
    {
        String[] validNames = getExpectedNames();
        ITableFilter filter = new SequenceTableFilter(validNames);

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
        String[] validNames = getExpectedNames();
        ITableFilter filter = new SequenceTableFilter(validNames);

        for (int i = 0; i < invalidNames.length; i++)
        {
            String invalidName = invalidNames[i];
            assertEquals(invalidName, false, filter.isValidName(invalidName));
        }
    }

    public void testGetTableNames() throws Exception
    {
        String[] expectedNames = getExpectedNames();
        ITableFilter filter = new SequenceTableFilter(expectedNames);

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
        ITableFilter filter = new SequenceTableFilter(expectedNames);

        IDataSet dataSet = createDuplicateDataSet();
        assertTrue("dataset names count",
                dataSet.getTableNames().length > expectedNames.length);

        try
        {
            filter.getTableNames(dataSet);
            fail("Should not be here!");
        }
        catch (AmbiguousTableNameException e)
        {

        }
    }

    public void testGetCaseInsensitiveTableNames() throws Exception
    {
        String[] filterNames = getExpectedNames();
        ITableFilter filter = new SequenceTableFilter(filterNames);

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
        String[] expectedNames = DataSetUtils.reverseStringArray(getExpectedNames());
        ITableFilter filter = new SequenceTableFilter(expectedNames);

        IDataSet dataSet = createDataSet();
        assertTrue("dataset names count",
                dataSet.getTableNames().length > expectedNames.length);

        String[] actualNames = filter.getTableNames(dataSet);
        assertEquals("name count", expectedNames.length, actualNames.length);
        assertEquals("names",
                Arrays.asList(expectedNames), Arrays.asList(actualNames));
    }

    public void testGetTables() throws Exception
    {
        String[] expectedNames = getExpectedNames();
        ITableFilter filter = new SequenceTableFilter(expectedNames);

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
        ITableFilter filter = new SequenceTableFilter(expectedNames);

        IDataSet dataSet = createDuplicateDataSet();
        assertTrue("dataset names count",
                dataSet.getTableNames().length > expectedNames.length);

        try
        {
            filter.getTables(dataSet);
            fail("Should not be here!");
        }
        catch (AmbiguousTableNameException e)
        {
        }
    }

    public void testGetCaseInsensitiveTables() throws Exception
    {
        ITableFilter filter = new SequenceTableFilter(getExpectedNames());
        String[] lowerNames = getExpectedLowerNames();

        IDataSet dataSet = new LowerCaseDataSet(createDataSet());
        assertTrue("dataset names count",
                dataSet.getTableNames().length > lowerNames.length);

        ITable[] actualTables = filter.getTables(dataSet);
        String[] actualNames = new DefaultDataSet(actualTables).getTableNames();
        assertEquals("table count", lowerNames.length, actualTables.length);
        assertEquals("table names",
                Arrays.asList(lowerNames), Arrays.asList(actualNames));
    }

    public void testGetReverseTables() throws Exception
    {
        String[] expectedNames = DataSetUtils.reverseStringArray(getExpectedNames());
        ITableFilter filter = new SequenceTableFilter(expectedNames);

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
