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

import org.dbunit.DatabaseEnvironment;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.*;
import org.dbunit.util.FileAsserts;

import java.io.*;

/**
 * @author Manuel Laflamme
 * @version $Revision$
 * @since Apr 4, 2002
 */
public class FlatDtdDataSetTest extends AbstractDataSetTest
{
    private static final File DTD_FILE =
            new File("src/dtd/flatDtdDataSetTest.dtd");
    private static final File DUPLICATE_FILE =
            new File("src/dtd/flatDtdDataSetDuplicateTest.dtd");

    public FlatDtdDataSetTest(String s)
    {
        super(s);
    }

    ////////////////////////////////////////////////////////////////////////////
    // AbstractDataSetTest class

    protected IDataSet createDataSet() throws Exception
    {
        return new FlatDtdDataSet(new FileReader(DTD_FILE));
    }

    protected IDataSet createDuplicateDataSet() throws Exception
    {
        return new FlatDtdDataSet(new FileReader(DUPLICATE_FILE));
    }

    protected int[] getExpectedDuplicateRows()
    {
        return new int[] {0, 0, 0};
    }

    ////////////////////////////////////////////////////////////////////////////
    // Test methods

    public void testGetDuplicateTable() throws Exception
    {
        String expectedTableName = getDuplicateTableName();

        IDataSet dataSet = createDuplicateDataSet();
        ITable table = dataSet.getTable(expectedTableName);
        String actualTableName = table.getTableMetaData().getTableName();
        assertEquals("table name", expectedTableName, actualTableName);
    }

    public void testGetDuplicateTableMetaData() throws Exception
    {
        String expectedTableName = getDuplicateTableName();

        IDataSet dataSet = createDuplicateDataSet();
        ITableMetaData metaData = dataSet.getTableMetaData(expectedTableName);
        String actualTableName = metaData.getTableName();
        assertEquals("table name", expectedTableName, actualTableName);
    }

    public void testWriteFromDtd() throws Exception
    {
        IDataSet dataSet = new FlatDtdDataSet(new FileReader(DTD_FILE));

        File tempFile = File.createTempFile("flatXmlDocType", ".dtd");

        try
        {
            Writer out = new FileWriter(tempFile);

            try
            {
                // write DTD in temp file
                FlatDtdDataSet.write(dataSet, out);
            }
            finally
            {
                out.close();
            }

            FileAsserts.assertEquals(
                    new BufferedReader(new FileReader(DTD_FILE)),
                    new BufferedReader(new FileReader(tempFile)));
        }
        finally
        {
            tempFile.delete();
        }

    }

    public void testWriteFromDatabase() throws Exception
    {
        IDatabaseConnection connection =
                DatabaseEnvironment.getInstance().getConnection();
        IDataSet dataSet = connection.createDataSet();

        File tempFile = File.createTempFile("flatXmlDocType", ".dtd");

        try
        {
            Writer out = new FileWriter(tempFile);

            try
            {
                // write DTD in temp file
                String[] tableNames = getExpectedNames();
                FlatDtdDataSet.write(new FilteredDataSet(
                        tableNames, dataSet), out);
            }
            finally
            {
                out.close();
            }

            FileAsserts.assertEquals(
                    new BufferedReader(new FileReader(DTD_FILE)),
                    new BufferedReader(new FileReader(tempFile)));
        }
        finally
        {
            tempFile.delete();
        }
    }


}





