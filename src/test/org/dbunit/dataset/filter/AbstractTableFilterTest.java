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

import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.CompositeDataSet;
import org.dbunit.dataset.DefaultDataSet;
import org.dbunit.dataset.DefaultTable;
import org.dbunit.dataset.xml.XmlDataSet;

import junit.framework.TestCase;

import java.io.FileReader;

/**
 * @author Manuel Laflamme
 * @since Mar 9, 2003
 * @version $Revision$
 */
public abstract class AbstractTableFilterTest extends TestCase
{
    private static final String[] TABLE_NAMES = {
        "TEST_TABLE",
        "SECOND_TABLE",
        "EMPTY_TABLE",
        "PK_TABLE",
        "ONLY_PK_TABLE",
        "EMPTY_MULTITYPE_TABLE",
    };

    private static final String[] DUPLICATE_TABLE_NAMES = {
        "DUPLICATE_TABLE",
        "EMPTY_TABLE",
        "DUPLICATE_TABLE",
    };

    private static final String EXTRA_TABLE_NAME = "EXTRA_TABLE";

    public AbstractTableFilterTest(String s)
    {
        super(s);
    }

    protected String[] getExpectedNames() throws Exception
    {
        return (String[])TABLE_NAMES.clone();
    }

    protected String[] getExpectedLowerNames() throws Exception
    {
        String[] names = (String[])TABLE_NAMES.clone();
        for (int i = 0; i < names.length; i++)
        {
            names[i] = names[i].toLowerCase();
        }

        return names;
    }

    protected String[] getExpectedDuplicateNames()
    {
        return (String[])DUPLICATE_TABLE_NAMES.clone();
    }

    public String getExtraTableName()
    {
        return EXTRA_TABLE_NAME;
    }

    protected IDataSet createDataSet() throws Exception
    {
        IDataSet dataSet1 = new XmlDataSet(
                new FileReader("src/xml/dataSetTest.xml"));
        IDataSet dataSet2 = new DefaultDataSet(
                new DefaultTable(getExtraTableName()));

        IDataSet dataSet = new CompositeDataSet(dataSet1, dataSet2);
        assertEquals("count before filter", getExpectedNames().length + 1,
                dataSet.getTableNames().length);
        return dataSet;
    }

    protected IDataSet createDuplicateDataSet() throws Exception
    {
        IDataSet dataSet1 = new XmlDataSet(
                new FileReader("src/xml/xmlDataSetDuplicateTest.xml"));
        IDataSet dataSet2 = new DefaultDataSet(
                new DefaultTable(getExtraTableName()));

        IDataSet dataSet = new CompositeDataSet(dataSet1, dataSet2, false);
        assertEquals("count before filter", getExpectedDuplicateNames().length + 1,
                dataSet.getTableNames().length);
        return dataSet;
    }

    public abstract void testIsValidName() throws Exception;

    public abstract void testIsCaseInsensitiveValidName() throws Exception;

    public abstract void testIsValidNameAndInvalid() throws Exception;

    public abstract void testGetTableNames() throws Exception;

    public abstract void testGetDuplicateTableNames() throws Exception;

    public abstract void testGetCaseInsensitiveTableNames() throws Exception;

    public abstract void testGetReverseTableNames() throws Exception;

    public abstract void testGetTableNamesAndTableNotInDecoratedDataSet() throws Exception;

    public abstract void testGetTables() throws Exception;

    public abstract void testGetDuplicateTables() throws Exception;

    public abstract void testGetCaseInsensitiveTables() throws Exception;

    public abstract void testGetReverseTables() throws Exception;

    public abstract void testGetTablesAndTableNotInDecoratedDataSet() throws Exception;

}
