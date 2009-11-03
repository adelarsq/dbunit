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

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.StringReader;
import java.io.Writer;

import org.dbunit.Assertion;
import org.dbunit.dataset.AbstractDataSetTest;
import org.dbunit.dataset.Column;
import org.dbunit.dataset.DataSetUtils;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.ITableMetaData;

/**
 * @author Manuel Laflamme
 * @version $Revision$
 * @since Mar 13, 2002
 */
public class FlatXmlDataSetTest extends AbstractDataSetTest
{
    public static final File DATASET_FILE =
            new File("src/xml/flatXmlDataSetTest.xml");
    public static final File DUPLICATE_DATASET_FILE =
            new File("src/xml/flatXmlDataSetDuplicateTest.xml");
    public static final File DUPLICATE_DATASET_MULTIPLE_CASE_FILE = 
            new File("src/xml/flatXmlDataSetDuplicateMultipleCaseTest.xml");

    private static final File FLAT_XML_TABLE = 
    		new File("src/xml/flatXmlTableTest.xml");

    private static final File FLAT_XML_DTD_DIFFERENT_CASE_FILE = 
            new File("src/xml/flatXmlDataSetDtdDifferentCaseTest.xml");
    
    public FlatXmlDataSetTest(String s)
    {
        super(s);
    }

    protected IDataSet createDataSet() throws Exception
    {
        return new FlatXmlDataSetBuilder().build(DATASET_FILE);
    }

    protected IDataSet createDuplicateDataSet() throws Exception
    {
        return new FlatXmlDataSetBuilder().build(DUPLICATE_DATASET_FILE);
    }

    protected IDataSet createMultipleCaseDuplicateDataSet() throws Exception 
    {
        return new FlatXmlDataSetBuilder().build(DUPLICATE_DATASET_MULTIPLE_CASE_FILE);
    }

    public void testMissingColumnAndEnableDtdMetadata() throws Exception
    {
        FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder();
        builder.setDtdMetadata(true);
        IDataSet dataSet = builder.build(FLAT_XML_TABLE);

        ITable table = dataSet.getTable("MISSING_VALUES");

        Column[] columns = table.getTableMetaData().getColumns();
        assertEquals("column count", 3, columns.length);
    }

    public void testMissingColumnAndDisableDtdMetadata() throws Exception
    {
        FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder();
        builder.setDtdMetadata(false);
        IDataSet dataSet = builder.build(FLAT_XML_TABLE);
        
        ITable table = dataSet.getTable("MISSING_VALUES");

        Column[] columns = table.getTableMetaData().getColumns();
        assertEquals("column count", 2, columns.length);
    }

    public void testMissingColumnAndDisableDtdMetadataEnableSensing() throws Exception
    {
        FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder();
        builder.setDtdMetadata(false);
        builder.setColumnSensing(true);
        IDataSet dataSet = builder.build(FLAT_XML_TABLE);

        ITable table = dataSet.getTable("MISSING_VALUES_SENSING");

        Column[] columns = table.getTableMetaData().getColumns();
        assertEquals("column count", 3, columns.length);
        assertEquals("COLUMN0", columns[0].getColumnName());
        assertEquals("COLUMN3", columns[1].getColumnName());
        assertEquals("COLUMN1", columns[2].getColumnName());
        assertEquals(3, table.getRowCount());
        assertEquals("row 0 col 0", table.getValue(0, "COLUMN0"));
        assertEquals("row 0 col 3", table.getValue(0, "COLUMN3"));
        assertEquals("row 1 col 0", table.getValue(1, "COLUMN0"));
        assertEquals("row 1 col 1", table.getValue(1, "COLUMN1"));
        assertEquals("row 2 col 3", table.getValue(2, "COLUMN3"));
    }

    
    public void testWrite() throws Exception
    {
        IDataSet expectedDataSet = createDataSet();
        File tempFile = File.createTempFile("flatXmlDataSetTest", ".xml");
        try
        {
            Writer out = new FileWriter(tempFile);

            // write dataset in temp file
            try
            {
                FlatXmlDataSet.write(expectedDataSet, out);
            }
            finally
            {
                out.close();
            }

            // load new dataset from temp file
            FileReader in = new FileReader(tempFile);
            try
            {
                IDataSet actualDataSet = new FlatXmlDataSetBuilder().build(in);

                // verify table count
                assertEquals("table count", expectedDataSet.getTableNames().length,
                        actualDataSet.getTableNames().length);

                // verify each table
                ITable[] expected = DataSetUtils.getTables(expectedDataSet);
                ITable[] actual = DataSetUtils.getTables(actualDataSet);
                assertEquals("table count", expected.length, actual.length);
                for (int i = 0; i < expected.length; i++)
                {
                    String expectedName = expected[i].getTableMetaData().getTableName();
                    String actualName = actual[i].getTableMetaData().getTableName();
                    assertEquals("table name", expectedName, actualName);

                    assertTrue("not same instance", expected[i] != actual[i]);
                    Assertion.assertEquals(expected[i], actual[i]);
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

    
    public void testReadFlatXmlWithDifferentCaseInDtd()throws Exception
    {
        // The creation of such a dataset should work
        IDataSet ds = new FlatXmlDataSetBuilder().build(FLAT_XML_DTD_DIFFERENT_CASE_FILE);
        assertEquals(1, ds.getTableNames().length);
        assertEquals("emp", ds.getTableNames()[0]);
    }

    
    public void testCreateMultipleCaseDuplicateDataSet_CaseSensitive() throws Exception
    {
        FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder();
        builder.setDtdMetadata(false);
        builder.setColumnSensing(false);
        // Create a FlatXmlDataSet having caseSensitivity=true
        builder.setCaseSensitiveTableNames(true);
        IDataSet dataSet = builder.build(DUPLICATE_DATASET_MULTIPLE_CASE_FILE);

        ITable[] tables = dataSet.getTables();
        assertEquals(3, tables.length);
        assertEquals("DUPLICATE_TABLE", tables[0].getTableMetaData().getTableName());
        assertEquals("EMPTY_TABLE", tables[1].getTableMetaData().getTableName());
        assertEquals("duplicate_TABLE", tables[2].getTableMetaData().getTableName());
    }
    
    /**
     * Overridden from parent because FlatXml has different behaviour than other datasets.
     * It allows the occurrence of the same table multiple times in arbitrary locations.
     * @see org.dbunit.dataset.AbstractDataSetTest#testCreateDuplicateDataSet()
     */
    //@Override
    public void testCreateDuplicateDataSet() throws Exception
    {
            IDataSet dataSet = createDuplicateDataSet();
            ITable[] tables = dataSet.getTables();
            assertEquals(2, tables.length);
            assertEquals("DUPLICATE_TABLE", tables[0].getTableMetaData().getTableName());
            assertEquals(3, tables[0].getRowCount());
            assertEquals("EMPTY_TABLE", tables[1].getTableMetaData().getTableName());
            assertEquals(0, tables[1].getRowCount());
    }

    /**
     * Overridden from parent because FlatXml has different behaviour than other datasets.
     * It allows the occurrence of the same table multiple times in arbitrary locations.
     * @see org.dbunit.dataset.AbstractDataSetTest#testCreateMultipleCaseDuplicateDataSet()
     */
    //@Override
    public void testCreateMultipleCaseDuplicateDataSet() throws Exception
    {
        IDataSet dataSet = createMultipleCaseDuplicateDataSet();
        ITable[] tables = dataSet.getTables();
        assertEquals(2, tables.length);
        assertEquals("DUPLICATE_TABLE", tables[0].getTableMetaData().getTableName());
        assertEquals(3, tables[0].getRowCount());
        assertEquals("EMPTY_TABLE", tables[1].getTableMetaData().getTableName());
        assertEquals(0, tables[1].getRowCount());
    }

    public void testCreateDuplicateDataSetWithVaryingColumnsAndColumnSensing() throws Exception
    {
        String xmlString = 
            "<dataset>" +
                "<MISSING_VALUES_SENSING COLUMN0='row 0 col 0' COLUMN3='row 0 col 3'/>"+
                "<MISSING_VALUES         COLUMN0='row 1 col 0' COLUMN2='row 1 col 2'/>"+
                "<MISSING_VALUES_SENSING COLUMN0='row 1 col 0' COLUMN1='row 1 col 1'/>"+
            "</dataset>";
        
        FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder();
        builder.setDtdMetadata(false);
        builder.setColumnSensing(true);
        IDataSet dataSet = builder.build(new StringReader(xmlString));
        ITable[] tables = dataSet.getTables();
        assertEquals(2, tables.length);
        
        ITableMetaData meta1 = tables[0].getTableMetaData();
        assertEquals("MISSING_VALUES_SENSING", meta1.getTableName());
        assertEquals(3, meta1.getColumns().length);
        assertEquals("COLUMN0", meta1.getColumns()[0].getColumnName());
        assertEquals("COLUMN3", meta1.getColumns()[1].getColumnName());
        assertEquals("COLUMN1", meta1.getColumns()[2].getColumnName());
        assertEquals(2, tables[0].getRowCount());
        assertEquals("row 0 col 0", tables[0].getValue(0, "COLUMN0"));
        assertEquals("row 0 col 3", tables[0].getValue(0, "COLUMN3"));
        assertEquals(null,          tables[0].getValue(0, "COLUMN1"));
        assertEquals("row 1 col 0", tables[0].getValue(1, "COLUMN0"));
        assertEquals(null,          tables[0].getValue(1, "COLUMN3"));
        assertEquals("row 1 col 1", tables[0].getValue(1, "COLUMN1"));
        
        assertEquals("MISSING_VALUES", tables[1].getTableMetaData().getTableName());
        assertEquals(1, tables[1].getRowCount());
    }

    
}








