/*
 * FlatDtdDataSetTest.java   Apr 4, 2002
 *
 * Copyright (c)2002 Manuel Laflamme. All Rights Reserved.
 *
 * This software is the proprietary information of Manuel Laflamme.
 * Use is subject to license terms.
 *
 */

package org.dbunit.dataset.xml;

import org.dbunit.DatabaseEnvironment;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.*;

import java.io.*;
import java.util.Arrays;

import FileAsserts;

/**
 * @author Manuel Laflamme
 * @version $Revision$
 */
public class FlatDtdDataSetTest extends AbstractDataSetTest
{
    private static final File DTD_FILE = new File("src/dtd/test.dtd");

    public FlatDtdDataSetTest(String s)
    {
        super(s);
    }

    ////////////////////////////////////////////////////////////////////////////
    // AbstractDataSetTest class

    protected IDataSet createDataSet() throws Exception
    {
        return new FlatDtdDataSet(new FileInputStream(DTD_FILE));
    }

    protected void sort(Object[] array)
    {
        Arrays.sort(array);
    }

    ////////////////////////////////////////////////////////////////////////////
    // Test methods

    public void testWriteFromDtd() throws Exception
    {
        IDataSet dataSet = new FlatDtdDataSet(new FileInputStream(DTD_FILE));

        File tempFile = File.createTempFile("flatXmlDocType", ".dtd");

        try
        {
            OutputStream out = new FileOutputStream(tempFile);

            try
            {
                // write DTD in temp file
                String[] tableNames = dataSet.getTableNames();
                Arrays.sort(tableNames);
                FlatDtdDataSet.write(new FilteredDataSet(
                        tableNames, dataSet), out);
            }
            finally
            {
                out.close();
            }

            FileAsserts.assertEquals(new FileInputStream(DTD_FILE), tempFile);
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
        IDataSet dataSet = removeExtraTestTables(connection.createDataSet());

        File tempFile = File.createTempFile("flatXmlDocType", ".dtd");

        try
        {
            OutputStream out = new FileOutputStream(tempFile);

            try
            {
                // write DTD in temp file
                String[] tableNames = dataSet.getTableNames();
                Arrays.sort(tableNames);
                FlatDtdDataSet.write(new FilteredDataSet(
                        tableNames, dataSet), out);
            }
            finally
            {
                out.close();
            }

            FileAsserts.assertEquals(new FileInputStream(DTD_FILE), tempFile);
        }
        finally
        {
            tempFile.delete();
        }
    }


}





