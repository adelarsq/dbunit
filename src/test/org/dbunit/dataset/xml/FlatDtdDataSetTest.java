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
import org.dbunit.Assertion;
import org.dbunit.database.*;
import org.dbunit.dataset.*;

import java.io.*;
import java.util.Arrays;
import java.util.Comparator;

import org.dbunit.util.FileAsserts;

/**
 * @author Manuel Laflamme
 * @version $Revision$
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

    protected void sort(Object[] array)
    {
        if (ITable[].class.isInstance(array))
        {
            Arrays.sort(array, new TableComparator());
        }
        else
        {
            Arrays.sort(array);
        }
    }

    private class TableComparator implements Comparator
    {
        public int compare(Object o1, Object o2)
        {
            String name1 = ((ITable)o1).getTableMetaData().getTableName();
            String name2 = ((ITable)o2).getTableMetaData().getTableName();

            return name1.compareTo(name2);
        }
    }


    protected int[] getExpectedDuplicateRows()
    {
        return new int[] {0, 0, 0};
    }

    ////////////////////////////////////////////////////////////////////////////
    // Test methods

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
                String[] tableNames = dataSet.getTableNames();
                Arrays.sort(tableNames);
                FlatDtdDataSet.write(new FilteredDataSet(
                        tableNames, dataSet), out);
            }
            finally
            {
                out.close();
            }

            org.dbunit.util.FileAsserts.assertEquals(
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
        IDataSet dataSet = removeExtraTestTables(connection.createDataSet());

        File tempFile = File.createTempFile("flatXmlDocType", ".dtd");

        try
        {
            Writer out = new FileWriter(tempFile);

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

            org.dbunit.util.FileAsserts.assertEquals(
                    new BufferedReader(new FileReader(DTD_FILE)),
                    new BufferedReader(new FileReader(tempFile)));
        }
        finally
        {
            tempFile.delete();
        }
    }


}





