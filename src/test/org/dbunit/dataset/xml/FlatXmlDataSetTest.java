/*
 * FlatXmlDataSetTest.java   Mar 13, 2002
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

package org.dbunit.dataset.xml;

import org.dbunit.Assertion;
import org.dbunit.dataset.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Manuel Laflamme
 * @version $Revision$
 */
public class FlatXmlDataSetTest extends AbstractDataSetTest
{
    public FlatXmlDataSetTest(String s)
    {
        super(s);
    }

    protected IDataSet createDataSet() throws Exception
    {
        return new FlatXmlDataSet(new File("src/xml/flatXmlDataSetTest.xml"));
    }

    protected IDataSet createDuplicateDataSet() throws Exception
    {
        return new FlatXmlDataSet(
                new File("src/xml/flatXmlDataSetDuplicateTest.xml"));
    }

//    public void testWrite() throws Exception
//    {
//        List tableList = new ArrayList();
//
//        IDataSet dataSet = createDataSet();
//        File tempFile = File.createTempFile("flatXmlDataSetTest", ".xml");
//        try
//        {
//            OutputStream out = new FileOutputStream(tempFile);
//            try
//            {
//                // write dataset in temp file
//                FlatXmlDataSet.write(dataSet, out);
//
//                // load new dataset from temp file
//                FlatXmlDataSet xmlDataSet2 = new FlatXmlDataSet(
//                        new FileInputStream(tempFile));
//
//                // verify table count
//                assertEquals("table count", dataSet.getTableNames().length,
//                        xmlDataSet2.getTableNames().length);
//
//                // verify each table
//                String[] tableNames = dataSet.getTableNames();
//                for (int i = 0; i < tableNames.length; i++)
//                {
//                    String name = tableNames[i];
//                    ITable table1 = dataSet.getTable(name);
//                    ITable table2 = xmlDataSet2.getTable(name);
//                    assertTrue("not same instance", table1 != table2);
//                    Assertion.assertEquals(table1, table2);
//                }
//            }
//            finally
//            {
//                out.close();
//            }
//        }
//        finally
//        {
//            tempFile.delete();
//        }
//    }

    public void testWrite() throws Exception
    {
        List tableList = new ArrayList();

        IDataSet expectedDataSet = (FlatXmlDataSet)createDataSet();
        File tempFile = File.createTempFile("flatXmlDataSetTest", ".xml");
        try
        {
            OutputStream out = new FileOutputStream(tempFile);

            try
            {
                // write dataset in temp file
                FlatXmlDataSet.write(expectedDataSet, out);

                // load new dataset from temp file
                IDataSet actualDataSet = new FlatXmlDataSet(new FileReader(tempFile));

                // verify table count
                assertEquals("table count", expectedDataSet.getTableNames().length,
                        actualDataSet.getTableNames().length);

                // verify each table
                ITable[] expected = expectedDataSet.getTables();
                ITable[] actual = actualDataSet.getTables();
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
                out.close();
            }
        }
        finally
        {
            tempFile.delete();
        }
    }

    public void testDuplicateWrite() throws Exception
    {
        List tableList = new ArrayList();

        IDataSet expectedDataSet = createDuplicateDataSet();
        File tempFile = File.createTempFile("flatXmlDataSetDuplicateTest", ".xml");
        try
        {
            OutputStream out = new FileOutputStream(tempFile);

            try
            {
                // write dataset in temp file
                FlatXmlDataSet.write(expectedDataSet, out);

                // load new dataset from temp file
                IDataSet actualDataSet = new FlatXmlDataSet(new FileReader(tempFile));

                // verify table count
                assertEquals("table count", expectedDataSet.getTableNames().length,
                        actualDataSet.getTableNames().length);

                // verify each table
                ITable[] expected = expectedDataSet.getTables();
                ITable[] actual = actualDataSet.getTables();
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
                out.close();
            }
        }
        finally
        {
            tempFile.delete();
        }
    }

}







