/*
 * XmlDataSetWriteTest.java   Feb 18, 2002
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

import org.dbunit.dataset.*;

/**
 * @author Manuel Laflamme
 * @version 1.0
 */
public class XmlDataSetWriteTest extends XmlTableTest
{
    public XmlDataSetWriteTest(String s)
    {
        super(s);
    }

//    protected ITable createTable() throws Exception
//    {
//        ITable table = super.createTable();
//        IDataSet dataSet = new DefaultDataSet(table);
//
//        File tempFile = File.createTempFile("xmlDataSetWriteTest", "xml");
//        OutputStream out = new FileOutputStream(tempFile);
//        try
//        {
//            // write DefaultTable in temp file
//            XmlDataSet.write(dataSet, out);
//
//            // load new dataset from temp file
//            XmlDataSet xmlDataSet2 = new XmlDataSet(new FileInputStream(tempFile));
//            return xmlDataSet2.getTable(xmlDataSet2.getTableNames()[0]);
//        }
//        finally
//        {
//            out.close();
//            tempFile.delete();
//        }
//    }

    protected IDataSet createDataSet() throws Exception
    {
        File tempFile = File.createTempFile("xmlDataSetWriteTest", "xml");
        OutputStream out = new FileOutputStream(tempFile);
        try
        {
            // write DefaultTable in temp file
            XmlDataSet.write(super.createDataSet(), out);

            // load new dataset from temp file
            return new XmlDataSet(new FileInputStream(tempFile));
        }
        finally
        {
            out.close();
            tempFile.delete();
        }

    }

    public void testWriteMultipleTable() throws Exception
    {
        int tableCount = 5;
        ITable sourceTable = super.createTable();

        ITable[] tables = new ITable[tableCount];
        for (int i = 0; i < tables.length; i++)
        {
            ITableMetaData metaData = new DefaultTableMetaData("table" + i,
                    sourceTable.getTableMetaData().getColumns());
            tables[i] = new CompositeTable(metaData, sourceTable);
        }

        IDataSet dataSet = new DefaultDataSet(tables);
        File tempFile = File.createTempFile("xmlDataSetWriteTest", "xml");
        OutputStream out = new FileOutputStream(tempFile);
        try
        {
            // write DefaultTable in temp file
            XmlDataSet.write(dataSet, out);

            // load new dataset from temp file
            XmlDataSet xmlDataSet2 = new XmlDataSet(new FileInputStream(tempFile));

            // verify each table
            for (int i = 0; i < tables.length; i++)
            {
                ITable table = tables[i];
                assertEquals(table.toString(), xmlDataSet2.getTable(xmlDataSet2.getTableNames()[i]).toString());
            }
        }
        finally
        {
            out.close();
            tempFile.delete();
        }

    }

}
