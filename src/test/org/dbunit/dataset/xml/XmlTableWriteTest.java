/*
 * XmlTableWriteTest.java   Feb 18, 2002
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

import org.dbunit.dataset.*;
import org.dbunit.Assertion;

import java.io.*;

/**
 * @author Manuel Laflamme
 * @version $Revision$
 */
public class XmlTableWriteTest extends XmlTableTest
{
    public XmlTableWriteTest(String s)
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
        File tempFile = File.createTempFile("xmlDataSetWriteTest", ".xml");
        OutputStream out = new FileOutputStream(tempFile);
        try
        {
            // write DefaultTable in temp file
            XmlDataSet.write(super.createDataSet(), out);

            // load new dataset from temp file
            return new XmlDataSet(new FileReader(tempFile));
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
            XmlDataSet xmlDataSet2 = new XmlDataSet(new FileReader(tempFile));

            // verify each table
            for (int i = 0; i < tables.length; i++)
            {
                ITable table = tables[i];
                Assertion.assertEquals(table, xmlDataSet2.getTable(xmlDataSet2.getTableNames()[i]));
            }
        }
        finally
        {
            out.close();
            tempFile.delete();
        }

    }

}




