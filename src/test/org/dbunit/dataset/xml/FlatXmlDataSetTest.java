/*
 * FlatXmlDataSetTest.java   Mar 13, 2002
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

package org.dbunit.dataset.xml;

import java.io.*;
import java.util.*;

import org.dbunit.DatabaseEnvironment;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.*;
import FileAsserts;

/**
 * @author Manuel Laflamme
 * @version 1.0
 */
public class FlatXmlDataSetTest extends AbstractDataSetTest
{
    public FlatXmlDataSetTest(String s)
    {
        super(s);
    }

    protected IDataSet createDataSet() throws Exception
    {
        InputStream in = new FileInputStream(
                new File("src/xml/flatXmlDataSetTest.xml"));
        return new FlatXmlDataSet(in);
    }

    public void testWrite() throws Exception
    {
        List tableList = new ArrayList();

        IDataSet dataSet = createDataSet();
        File tempFile = File.createTempFile("flatXmlDataSetTest", ".xml");
        try
        {
            OutputStream out = new FileOutputStream(tempFile);
            try
            {
                // write dataset in temp file
                FlatXmlDataSet.write(dataSet, out);

                // load new dataset from temp file
                FlatXmlDataSet xmlDataSet2 = new FlatXmlDataSet(
                        new FileInputStream(tempFile));

                // verify table count
                assertEquals("table count", dataSet.getTableNames().length,
                        xmlDataSet2.getTableNames().length);

                // verify each table
                String[] tableNames = dataSet.getTableNames();
                for (int i = 0; i < tableNames.length; i++)
                {
                    String name = tableNames[i];
                    ITable table1 = dataSet.getTable(name);
                    ITable table2 = xmlDataSet2.getTable(name);
                    assertTrue("not same instance", table1 != table2);
                    DataSetUtils.assertEquals(table1, table2);
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

    public void testWriteDtd() throws Exception
    {
        IDatabaseConnection connection =
                DatabaseEnvironment.getInstance().getConnection();
        IDataSet dataSet = connection.createDataSet();

        File tempFile = File.createTempFile("flatXmlDataSetTest", ".dtd");

        try
        {
            OutputStream out = new FileOutputStream(tempFile);

            try
            {
                // write DTD in temp file
                String[] tableNames = dataSet.getTableNames();
                Arrays.sort(tableNames);
                FlatXmlDataSet.writeDtd(new FilteredDataSet(
                        tableNames, dataSet), out);
            }
            finally
            {
                out.close();
            }

            FileAsserts.assertEquals(new FileInputStream("src/dtd/test.dtd"), tempFile);
        }
        finally
        {
            tempFile.delete();
        }
    }

}




