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

package org.dbunit.dataset;

import java.io.File;
import java.io.IOException;

import org.dbunit.dataset.datatype.DataType;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.testutil.TestUtils;

/**
 * @author Manuel Laflamme
 * @version $Revision$
 */
public class SortedTableTest extends AbstractTableTest
{
	private File sortedTableTestFile = TestUtils.getFile("xml/sortedTableTest.xml");
	
    public SortedTableTest(String s)
    {
        super(s);
    }

    protected ITable createTable() throws Exception
    {
        return createDataSet().getTable("TEST_TABLE");
    }

    protected IDataSet createDataSet() throws Exception
    {
        return new SortedDataSet(createUnsortedDataSet());
    }

    private IDataSet createUnsortedDataSet() throws DataSetException, IOException 
    {
    	return new FlatXmlDataSetBuilder().build(sortedTableTestFile);
    }

    private ITable createNumericTable() throws Exception
    {
    	// Create a table that has numeric values in the first column
    	Column[] columns = new Column[]{
    			new Column("COLUMN0", DataType.NUMERIC),
    			new Column("COLUMN1", DataType.VARCHAR)
    	};
    	DefaultTable table = new DefaultTable("TEST_TABLE", columns);
    	Object[] row1 = new Object[]{new Integer(9), "row 9"};
    	Object[] row2 = new Object[]{new Integer(10), "row 10"};
    	Object[] row3 = new Object[]{new Integer(11), "row 11"};
    	table.addRow(row1);
    	table.addRow(row2);
    	table.addRow(row3);
    	return table;
    }
    
    
    public void testSetUseComparableTooLate() throws Exception
    {
    	ITable table = createTable();
    	SortedTable sortedTable = new SortedTable(table);
    	// access a value to initialize the array
    	sortedTable.getValue(0, "COLUMN0");
    	// now set the "useComparable" flag which should fail
    	try
    	{
        	sortedTable.setUseComparable(true);
        	fail("Should not be able to set 'useComparable' after table has already been in use");
    	}
    	catch(IllegalStateException expected)
    	{
    		String msgStart = "Do not use this method after the table has been used";
    		assertTrue("Msg should start with: " + msgStart, expected.getMessage().startsWith(msgStart));
    	}
    }
    
    
    public void testSortByComparable() throws Exception
    {
    	// Sort by column0 which is a numeric column
        String columnName = "COLUMN0";

        ITable table = createNumericTable();
        SortedTable sortedTable = new SortedTable(table, new String[]{columnName});
        sortedTable.setUseComparable(true);

        Column[] columns = sortedTable.getTableMetaData().getColumns();
        assertEquals("column count", 2, columns.length);
        assertEquals("row count", 3, sortedTable.getRowCount());

        Object[] expected = {new Integer(9), new Integer(10), new Integer(11)};
        for (int i = 0; i < sortedTable.getRowCount(); i++)
        {
            assertEquals("value row " + i, expected[i],
            		sortedTable.getValue(i, columnName));
        }
    }
    
    /**
     * Tests the sort by string which is the default behavior
     * @throws Exception
     */
    public void testSortByString() throws Exception
    {
    	// Sort by column0 which is a numeric column
        String columnName = "COLUMN0";

        ITable table = createNumericTable();
        SortedTable sortedTable = new SortedTable(table, new String[]{columnName});

        Column[] columns = sortedTable.getTableMetaData().getColumns();
        assertEquals("column count", 2, columns.length);
        assertEquals("row count", 3, sortedTable.getRowCount());

        Object[] expected = {new Integer(10), new Integer(11), new Integer(9)};
        for (int i = 0; i < sortedTable.getRowCount(); i++)
        {
            assertEquals("value row " + i, expected[i],
            		sortedTable.getValue(i, columnName));
        }
    }

    
	public void testGetMissingValue() throws Exception
    {
        String columnName = "COLUMN2";
        Object[] expected = {null, null, null, "0", "1"};

        ITable table = createDataSet().getTable("MISSING_VALUES");

        Column[] columns = table.getTableMetaData().getColumns();
        assertEquals("column count", 3, columns.length);
        assertEquals("row count", 5, table.getRowCount());
        for (int i = 0; i < table.getRowCount(); i++)
        {
            assertEquals("value row " + i, expected[i],
                    table.getValue(i, columnName));
        }
    }

    public void testCustomColumnsWithUnknownColumnName() throws Exception
    {
    	String[] sortColumnNames = new String[] {"COLUMN2", "COLUMNXY_UNDEFINED"};
    	
        ITable unsortedTable = createUnsortedDataSet().getTable("MISSING_VALUES");
        try {
	        new SortedTable(unsortedTable, sortColumnNames);
	        fail("Should not be able to create a SortedTable with unexisting columns");
        }catch(NoSuchColumnException expected) {
            assertTrue(expected.getMessage().startsWith("MISSING_VALUES.COLUMNXY_UNDEFINED"));
        }
    }

    public void testCustomColumnsWithUnknownColumn() throws Exception
    {
    	Column[] sortColumns = new Column[] {
    			new Column("COLUMN2", DataType.UNKNOWN, Column.NULLABLE),
    			new Column("COLUMNXY_UNDEFINED", DataType.UNKNOWN, Column.NULLABLE) 
		};
    	
        ITable unsortedTable = createUnsortedDataSet().getTable("MISSING_VALUES");
        try {
	        new SortedTable(unsortedTable, sortColumns);
	        fail("Should not be able to create a SortedTable with unexisting columns");
        }catch(NoSuchColumnException expected) {
        	assertTrue(expected.getMessage().startsWith("MISSING_VALUES.COLUMNXY_UNDEFINED"));
        }
    }

    public void testCustomColumnsWithDifferentColumnTypesButSameName() throws Exception
    {
        Column sortColumn = new Column("COLUMN2", DataType.CHAR, Column.NO_NULLS);
        Column[] sortColumns = new Column[] { sortColumn };
        // Use different columns (different datatype) in ITableMetaData that have valid column names
        ITable unsortedTable = createUnsortedDataSet().getTable("MISSING_VALUES");
        SortedTable sortedTable = new SortedTable(unsortedTable, sortColumns);
        // Check the results
        Column actualSortColumn = sortedTable.getSortColumns()[0];
        // The column actually used for sorting must has some different attributes than the one passed in (dataType, nullable) 
        assertNotSame(sortColumn, actualSortColumn);
        assertEquals(DataType.UNKNOWN, actualSortColumn.getDataType());
        assertEquals("COLUMN2", actualSortColumn.getColumnName());
        assertEquals(Column.NULLABLE, actualSortColumn.getNullable());
    }

}









