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
package org.dbunit.dataset.excel;

import org.dbunit.Assertion;
import org.dbunit.dataset.AbstractDataSetTest;
import org.dbunit.dataset.DataSetUtils;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;

import java.io.*;

/**
 * @author Manuel Laflamme
 * @since Feb 22, 2003
 * @version $Revision$
 */
public class XlsDataSetTest extends AbstractDataSetTest
{
    public XlsDataSetTest(String s)
    {
        super(s);
    }

    protected IDataSet createDataSet() throws Exception
    {
        return new XlsDataSet(new File("src/xml/dataSetTest.xls"));
    }

    protected IDataSet createDuplicateDataSet() throws Exception
    {
        return new XlsDataSet(
                new File("src/xml/dataSetDuplicateTest.xls"));
    }

    public void testWrite() throws Exception
    {
        IDataSet expectedDataSet = createDataSet();
        File tempFile = File.createTempFile("xlsDataSetTest", ".xls");
        try
        {
            OutputStream out = new FileOutputStream(tempFile);

            // write dataset in temp file
            try
            {
                XlsDataSet.write(expectedDataSet, out);
            }
            finally
            {
                out.close();
            }

            // load new dataset from temp file
            InputStream in = new FileInputStream(tempFile);
            try
            {
                IDataSet actualDataSet = new XlsDataSet(in);

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
        File tempFile = File.createTempFile("xlsDataSetDuplicateTest", ".xls");
        try
        {
            OutputStream out = new FileOutputStream(tempFile);

            // write dataset in temp file
            try
            {
                XlsDataSet.write(expectedDataSet, out);
            }
            finally
            {
                out.close();
            }

            // load new dataset from temp file
            InputStream in = new FileInputStream(tempFile);
            try
            {
                IDataSet actualDataSet = new XlsDataSet(in);

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
