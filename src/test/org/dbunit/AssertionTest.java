/*
 * AssertionTest.java   Mar 22, 2002
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

package org.dbunit;

import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.DataSetUtils;
import org.dbunit.dataset.xml.FlatXmlDataSet;

import java.io.FileInputStream;

import junit.framework.TestCase;
import junit.framework.AssertionFailedError;

/**
 * @author Manuel Laflamme
 * @version 1.0
 */
public class AssertionTest extends TestCase
{
    public AssertionTest(String s)
    {
        super(s);
    }

    private IDataSet getDataSet() throws Exception
    {
        return new FlatXmlDataSet(new FileInputStream(
                "src/xml/dataSetUtilsTest.xml"));
    }

    ////////////////////////////////////////////////////////////////////////////
    // Test methods

    public void testAssertTableEquals() throws Exception
    {
        IDataSet dataSet = getDataSet();
        Assertion.assertEquals(dataSet.getTable("TEST_TABLE"),
                dataSet.getTable("TEST_TABLE_WITH_SAME_VALUE"));
    }

    public void testAssertTableNamesNotEquals() throws Exception
    {
        IDataSet dataSet = getDataSet();
        Assertion.assertEquals(dataSet.getTable("TEST_TABLE"),
                dataSet.getTable("TEST_TABLE_WITH_DIFFERENT_NAME"));
    }

    public void testAssertColumnCountNotEquals() throws Exception
    {
        IDataSet dataSet = getDataSet();

        try
        {
            Assertion.assertEquals(dataSet.getTable("TEST_TABLE"),
                    dataSet.getTable("TEST_TABLE_WITH_3_COLUMNS"));
            throw new IllegalStateException("Should throw an AssertionFailedError");
        }
        catch (AssertionFailedError e)
        {
        }
    }

    public void testAssertColumnSequenceNotEquals() throws Exception
    {
        IDataSet dataSet = getDataSet();

        Assertion.assertEquals(dataSet.getTable("TEST_TABLE"),
                dataSet.getTable("TEST_TABLE_WITH_DIFFERENT_COLUMN_SEQUENCE"));
    }

    public void testAssertColumnNamesNotEquals() throws Exception
    {
        IDataSet dataSet = getDataSet();

        try
        {
            Assertion.assertEquals(dataSet.getTable("TEST_TABLE"),
                    dataSet.getTable("TEST_TABLE_WITH_DIFFERENT_COLUMN_NAMES"));
            throw new IllegalStateException("Should throw an AssertionFailedError");
        }
        catch (AssertionFailedError e)
        {
        }
    }

    public void testAssertRowCountNotEquals() throws Exception
    {
        IDataSet dataSet = getDataSet();

        try
        {
            Assertion.assertEquals(dataSet.getTable("TEST_TABLE"),
                    dataSet.getTable("TEST_TABLE_WITH_ONE_ROW"));
            throw new IllegalStateException("Should throw an AssertionFailedError");
        }
        catch (AssertionFailedError e)
        {
        }
    }

    public void testAssertValuesNotEquals() throws Exception
    {
        IDataSet dataSet = getDataSet();

        try
        {
            Assertion.assertEquals(dataSet.getTable("TEST_TABLE"),
                    dataSet.getTable("TEST_TABLE_WITH_WRONG_VALUE"));
            throw new IllegalStateException("Should throw an AssertionFailedError");
        }
        catch (AssertionFailedError e)
        {
        }
    }

}

