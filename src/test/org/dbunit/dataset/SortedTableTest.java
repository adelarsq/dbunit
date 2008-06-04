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
import org.dbunit.dataset.xml.FlatXmlDataSet;

/**
 * @author Manuel Laflamme
 * @version $Revision$
 */
public class SortedTableTest extends AbstractTableTest
{
	private File sortedTableTestFile = new File("src/xml/sortedTableTest.xml");
	
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
    	return new FlatXmlDataSet(sortedTableTestFile);
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
        	assertEquals("Unknown column 'COLUMNXY_UNDEFINED' for table 'MISSING_VALUES'", expected.getMessage());
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
        	assertEquals("Unknown column 'COLUMNXY_UNDEFINED' for table 'MISSING_VALUES'", expected.getMessage());
        }
    }

}









