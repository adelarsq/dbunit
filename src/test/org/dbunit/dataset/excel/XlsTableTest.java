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
package org.dbunit.dataset.excel;

import java.io.File;
import java.math.BigDecimal;
import java.util.TimeZone;

import org.dbunit.dataset.AbstractTableTest;
import org.dbunit.dataset.Column;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.datatype.DataType;

/**
 * @author Manuel Laflamme
 * @author Last changed by: $Author$
 * @version $Revision$ $Date$
 * @since Feb 21, 2003
 */
public class XlsTableTest extends AbstractTableTest
{
//    private static final long ONE_SECOND_IN_MILLIS = 1000;
//    private static final long ONE_MINUTE_IN_MILLIS = 60 * 1000;
//    private static final long ONE_HOUR_IN_MILLIS = 60 * ONE_MINUTE_IN_MILLIS;
//    private static final long ONE_DAY_IN_MILLIS = 24 * ONE_HOUR_IN_MILLIS;
    
    
    public XlsTableTest(String s)
    {
        super(s);
    }

    protected ITable createTable() throws Exception
    {
        return createDataSet().getTable("TEST_TABLE");
    }

    protected IDataSet createDataSet() throws Exception
    {
        return new XlsDataSet(new File("src/xml/tableTest.xls"));
    }

    public void testGetMissingValue() throws Exception
    {
        int row = 0;
        Object[] expected = {"row 0 col 0", null, "row 0 col 2"};

        ITable table = createDataSet().getTable("MISSING_VALUES");

        Column[] columns = table.getTableMetaData().getColumns();
        assertEquals("column count", expected.length, columns.length);
        assertEquals("row count", 1, table.getRowCount());
        for (int i = 0; i < columns.length; i++)
        {
            assertEquals("value " + i, expected[i],
                    table.getValue(row, columns[i].getColumnName()));
        }
    }
    
    public void testEmptyTableColumns() throws Exception
    {
    	Column[] expectedColumns = new Column[] {
    			new Column("COLUMN0", DataType.UNKNOWN),
    			new Column("COLUMN1", DataType.UNKNOWN),
    			new Column("COLUMN2", DataType.UNKNOWN),
    			new Column("COLUMN3", DataType.UNKNOWN)
    	};
        ITable table = createDataSet().getTable("EMPTY_TABLE");

        Column[] columns = table.getTableMetaData().getColumns();
    	assertEquals("Column count", expectedColumns.length, columns.length);
    	for (int i = 0; i < columns.length; i++) {
			assertEquals("Column " + i, expectedColumns[i], columns[i]);
		}
    }
    
    public void testEmptySheet() throws Exception
    {
        ITable table = createDataSet().getTable("EMPTY_SHEET");

        Column[] columns = table.getTableMetaData().getColumns();
    	assertEquals("Column count", 0, columns.length);
    }

    
    public void testDifferentDatatypes() throws Exception
    {
        int row = 0;
        ITable table = createDataSet().getTable("TABLE_DIFFERENT_DATATYPES");
        
//        long tzOffset = TimeZone.getDefault().getOffset(0);
        Object[] expected = {
//                new Date(0-tzOffset), 
//                new Date(0-tzOffset + (10*ONE_HOUR_IN_MILLIS + 45*ONE_MINUTE_IN_MILLIS)),
//                new Date(0-tzOffset + (13*ONE_HOUR_IN_MILLIS + 30*ONE_MINUTE_IN_MILLIS + 55*ONE_SECOND_IN_MILLIS) ),
//                new Long(25569),// Dates stored as Long numbers
//                new Long(25569447916666668L),
//                new Long(563136574074074L),
                new Long(0),// Dates stored as Long numbers
                new Long(38700000),
                new Long(-2209026545000L),
                new BigDecimal("10000.00"), 
                new BigDecimal("-200"), 
                new BigDecimal("12345.123456789000"),
                new Long(1233398764000L),
                new Long(1233332866000L) // The last column is a dbunit-date-formatted column in the excel sheet
                };

        Column[] columns = table.getTableMetaData().getColumns();
        assertEquals("column count", expected.length, columns.length);
        for (int i = 0; i < columns.length; i++)
        {
            Object actual = table.getValue(row, columns[i].getColumnName());
            String typesResult = " expected=" + (expected[i]!=null ? expected[i].getClass().getName() : "null") + " - actual=" 
                                    + (actual!=null ? actual.getClass().getName() : "null");
            assertEquals("value " + i + " (" + typesResult + ")", expected[i], actual);
        }
    }

    public void testNumberAsText() throws Exception
    {
        int row = 0;
        ITable table = createDataSet().getTable("TABLE_NUMBER_AS_TEXT");
        
        String[] expected = {
        		"0",
        		"666",
        		"66.6",
        		"66.6",
        		"-6.66"
                };

        Column[] columns = table.getTableMetaData().getColumns();
        assertEquals("column count", expected.length, columns.length);
        for (int i = 0; i < columns.length; i++)
        {
        	String columnName = columns[i].getColumnName();
            Object actual = table.getValue(row, columnName).toString();
            assertEquals(columns[i].getColumnName(),expected[i],actual);
        }
    }
}
