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

import org.dbunit.dataset.AbstractTableTest;
import org.dbunit.dataset.Column;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.datatype.DataType;

import java.io.File;

/**
 * @author Manuel Laflamme
 * @author Last changed by: $Author$
 * @version $Revision$ $Date$
 * @since Feb 21, 2003
 */
public class XlsTableTest extends AbstractTableTest
{
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

}
