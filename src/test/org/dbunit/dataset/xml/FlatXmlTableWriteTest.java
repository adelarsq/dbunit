/*
 * XmlTableWriteTest.java   Mar 13, 2002
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

import java.io.*;

import org.dbunit.dataset.*;

/**
 * @author Manuel Laflamme
 * @version 1.0
 */
public class FlatXmlTableWriteTest extends FlatXmlTableTest
{
    public FlatXmlTableWriteTest(String s)
    {
        super(s);
    }

    protected IDataSet createDataSet() throws Exception
    {
        File tempFile = File.createTempFile("flatXmlTableWriteTest", ".xml");
        OutputStream out = new FileOutputStream(tempFile);
        try
        {
            // write DefaultTable in temp file
            FlatXmlDataSet.write(super.createDataSet(true), out);

            // load new dataset from temp file
            return new FlatXmlDataSet(new FileInputStream(tempFile));
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
        File tempFile = File.createTempFile("flatXmlTableWriteTest", "xml");
        OutputStream out = new FileOutputStream(tempFile);
        try
        {
            // write DefaultTable in temp file
            FlatXmlDataSet.write(dataSet, out);

            // load new dataset from temp file
            FlatXmlDataSet xmlDataSet2 = new FlatXmlDataSet(
                    new FileInputStream(tempFile));

            // verify each table
            for (int i = 0; i < tables.length; i++)
            {
                ITable table = tables[i];
                DataSetUtils.assertEquals(table, xmlDataSet2.getTable(xmlDataSet2.getTableNames()[i]));
            }
        }
        finally
        {
            out.close();
            tempFile.delete();
        }

    }

}






