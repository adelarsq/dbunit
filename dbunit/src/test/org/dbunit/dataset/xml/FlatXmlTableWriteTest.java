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
public class FlatXmlTableWriteTest extends FlatXmlTableTest
{
    public FlatXmlTableWriteTest(String s)
    {
        super(s);
    }

    protected IDataSet createDataSet() throws Exception
    {
        File tempFile = File.createTempFile("flatXmlTableWriteTest", ".xml");
        Writer out = new FileWriter(tempFile);
        try
        {
            // write DefaultTable in temp file
            try
            {
                FlatXmlDataSet.write(super.createDataSet(true), out);
            }
            finally
            {
                out.close();
            }

            // load new dataset from temp file
            FileReader in = new FileReader(tempFile);
            try
            {
                return new FlatXmlDataSetBuilder().build(in);
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
        Writer out = new FileWriter(tempFile);
        try
        {
            // write DefaultTable in temp file
            try
            {
                FlatXmlDataSet.write(dataSet, out);
            }
            finally
            {
                out.close();
            }

            // load new dataset from temp file
            FileReader in = new FileReader(tempFile);
            try
            {
                FlatXmlDataSet xmlDataSet2 = new FlatXmlDataSetBuilder().build(in);

                // verify each table
                for (int i = 0; i < tables.length; i++)
                {
                    ITable table = tables[i];
                    Assertion.assertEquals(table, xmlDataSet2.getTable(xmlDataSet2.getTableNames()[i]));
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







