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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Writer;

import org.dbunit.DatabaseEnvironment;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.AbstractDataSetTest;
import org.dbunit.dataset.FilteredDataSet;
import org.dbunit.dataset.IDataSet;
import org.dbunit.testutil.FileAsserts;
import org.dbunit.testutil.TestUtils;

/**
 * @author Manuel Laflamme
 * @version $Revision$
 * @since Apr 4, 2002
 */
public class FlatDtdDataSetIT extends AbstractDataSetTest
{
    private static final String DTD_FILE =
        "dtd/flatDtdDataSetTest.dtd";
    private static final String DUPLICATE_FILE =
        "dtd/flatDtdDataSetDuplicateTest.dtd";
    private static final String DUPLICATE_MULTIPLE_CASE_FILE =
        "dtd/flatDtdDataSetDuplicateMultipleCaseTest.dtd";

    public FlatDtdDataSetIT(String s)
    {
        super(s);
    }

    ////////////////////////////////////////////////////////////////////////////
    // AbstractDataSetTest class

    private File getFile(String fileName) throws Exception
    {
        return TestUtils.getFileForDatabaseEnvironment(TestUtils.getFileName(fileName));
    }

    protected IDataSet createDataSet() throws Exception
    {
        return new FlatDtdDataSet(TestUtils.getFileReader(DTD_FILE));
    }

    protected IDataSet createDuplicateDataSet() throws Exception
    {
        return new FlatDtdDataSet(TestUtils.getFileReader(DUPLICATE_FILE));
    }

    protected IDataSet createMultipleCaseDuplicateDataSet() throws Exception 
    {
        return new FlatDtdDataSet(TestUtils.getFileReader(DUPLICATE_MULTIPLE_CASE_FILE));
    }

    protected int[] getExpectedDuplicateRows()
    {
        return new int[] {0, 0, 0};
    }

    ////////////////////////////////////////////////////////////////////////////
    // Test methods

    public void testWriteFromDtd() throws Exception
    {
        IDataSet dataSet = new FlatDtdDataSet(TestUtils.getFileReader(DTD_FILE));

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
                    new BufferedReader(TestUtils.getFileReader(DTD_FILE)),
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
                    new BufferedReader(new FileReader(getFile(DTD_FILE))),
                    new BufferedReader(new FileReader(tempFile)));
        }
        finally
        {
            tempFile.delete();
        }
    }


}





