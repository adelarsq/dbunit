/*
 * XmlDataSetTest.java   Feb 17, 2002
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
import java.util.ArrayList;
import java.util.List;

import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.AbstractDataSetTest;

/**
 * @author Manuel Laflamme
 * @version 1.0
 */
public class XmlDataSetTest extends AbstractDataSetTest
{
    public XmlDataSetTest(String s)
    {
        super(s);
    }

    protected IDataSet createDataSet() throws Exception
    {
        InputStream in = new FileInputStream(
                new File("src/xml/dataSetTest.xml"));
        return new XmlDataSet(in);
    }

    public void testWriteTable() throws Exception
    {
        List tableList = new ArrayList();

        XmlDataSet xmlDataSet1 = (XmlDataSet)createDataSet();
        File tempFile = File.createTempFile("dataSetTest", "xml");
        OutputStream out = new FileOutputStream(tempFile);
        try
        {
            // write dataset in temp file
            XmlDataSet.write(xmlDataSet1, out);

            // load new dataset from temp file
            XmlDataSet xmlDataSet2 = new XmlDataSet(new FileInputStream(tempFile));

            // verify table count
            assertEquals("table count", xmlDataSet1.getTableNames().length,
                    xmlDataSet2.getTableNames().length);

            // verify each table
            String[] tableNames = xmlDataSet1.getTableNames();
            for (int i = 0; i < tableNames.length; i++)
            {
                String name = tableNames[i];
                ITable table1 = xmlDataSet1.getTable(name);
                ITable table2 = xmlDataSet2.getTable(name);
                assertTrue("not same instance", table1 != table2);
                assertEquals("table", table1.toString(), table2.toString());
            }
        }
        finally
        {
            out.close();
            tempFile.delete();
        }
    }

}
