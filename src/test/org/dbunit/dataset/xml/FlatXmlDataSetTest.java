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

package org.dbunit.dataset.xml;

import org.dbunit.Assertion;
import org.dbunit.dataset.*;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Writer;

/**
 * @author Manuel Laflamme
 * @version $Revision$
 * @since Mar 13, 2002
 */
public class FlatXmlDataSetTest extends AbstractDataSetTest
{
    protected static final File DATASET_FILE =
            new File("src/xml/flatXmlDataSetTest.xml");
    protected static final File DUPLICATE_DATASET_FILE =
            new File("src/xml/flatXmlDataSetDuplicateTest.xml");

    public FlatXmlDataSetTest(String s)
    {
        super(s);
    }

    protected IDataSet createDataSet() throws Exception
    {
        return new FlatXmlDataSet(DATASET_FILE);
    }

    protected IDataSet createDuplicateDataSet() throws Exception
    {
        return new FlatXmlDataSet(
                DUPLICATE_DATASET_FILE);
    }

    public void testMissingColumnAndEnableDtdMetadata() throws Exception
    {
        IDataSet dataSet = new FlatXmlDataSet(
                new File("src/xml/flatXmlTableTest.xml"), true);

        ITable table = dataSet.getTable("MISSING_VALUES");

        Column[] columns = table.getTableMetaData().getColumns();
        assertEquals("column count", 3, columns.length);
    }

    public void testMissingColumnAndDisableDtdMetadata() throws Exception
    {
        IDataSet dataSet = new FlatXmlDataSet(
                new File("src/xml/flatXmlTableTest.xml"), false);

        ITable table = dataSet.getTable("MISSING_VALUES");

        Column[] columns = table.getTableMetaData().getColumns();
        assertEquals("column count", 2, columns.length);
    }

    public void testWrite() throws Exception
    {
        IDataSet expectedDataSet = createDataSet();
        File tempFile = File.createTempFile("flatXmlDataSetTest", ".xml");
        try
        {
            Writer out = new FileWriter(tempFile);

            // write dataset in temp file
            try
            {
                FlatXmlDataSet.write(expectedDataSet, out);
            }
            finally
            {
                out.close();
            }

            // load new dataset from temp file
            FileReader in = new FileReader(tempFile);
            try
            {
                IDataSet actualDataSet = new FlatXmlDataSet(in);

                // verify table count
                assertEquals("table count", expectedDataSet.getTableNames().length,
                        actualDataSet.getTableNames().length);

                // verify each table
                ITable[] expected = DataSetUtils.getTables(expectedDataSet);
                ITable[] actual = DataSetUtils.getTables(actualDataSet);
                assertEquals("table count", expected.length, actual.length);
                for (int i = 0; i < expected.length; i++)
                {
                    String expectedName = expected[i].getTableMetaData().getTableName();
                    String actualName = actual[i].getTableMetaData().getTableName();
                    assertEquals("table name", expectedName, actualName);

                    assertTrue("not same instance", expected[i] != actual[i]);
                    Assertion.assertEquals(expected[i], actual[i]);
                }
            }
            finally
            {
                in.close();
            }
        }
        finally
        {
            tempFile.delete();
        }
    }

    public void testDuplicateWrite() throws Exception
    {
        IDataSet expectedDataSet = createDuplicateDataSet();
        File tempFile = File.createTempFile("flatXmlDataSetDuplicateTest", ".xml");
        try
        {
            Writer out = new FileWriter(tempFile);

            // write dataset in temp file
            try
            {
                FlatXmlDataSet.write(expectedDataSet, out);
            }
            finally
            {
                out.close();
            }

            // load new dataset from temp file
            FileReader in = new FileReader(tempFile);
            try
            {
                IDataSet actualDataSet = new FlatXmlDataSet(in);

                // verify table count
                assertEquals("table count", expectedDataSet.getTableNames().length,
                        actualDataSet.getTableNames().length);

                // verify each table
                ITable[] expected = DataSetUtils.getTables(expectedDataSet);
                ITable[] actual = DataSetUtils.getTables(actualDataSet);
                assertEquals("table count", expected.length, actual.length);
                for (int i = 0; i < expected.length; i++)
                {
                    String expectedName = expected[i].getTableMetaData().getTableName();
                    String actualName = actual[i].getTableMetaData().getTableName();
                    assertEquals("table name", expectedName, actualName);

                    assertTrue("not same instance", expected[i] != actual[i]);
                    Assertion.assertEquals(expected[i], actual[i]);
                }
            }
            finally
            {
                in.close();
            }
        }
        finally
        {
            tempFile.delete();
        }
    }

}







