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

import org.dbunit.dataset.AbstractTableTest;
import org.dbunit.dataset.Column;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.testutil.TestUtils;

import java.io.File;

/**
 * @author Manuel Laflamme
 * @version $Revision$
 * @since Mar 12, 2002
 */
public class FlatXmlTableTest extends AbstractTableTest
{
    public FlatXmlTableTest(String s)
    {
        super(s);
    }

    protected ITable createTable() throws Exception
    {
        return createDataSet(true).getTable("TEST_TABLE");
    }

    protected IDataSet createDataSet(boolean noneAsNull) throws Exception
    {
        return new FlatXmlDataSetBuilder().build(TestUtils.getFile("xml/flatXmlTableTest.xml"));
    }

    public void testGetMissingValue() throws Exception
    {
        int row = 0;
        Object[] expected = {"row 1 col 0", null, "row 1 col 2"};

        ITable table = createDataSet(false).getTable("MISSING_VALUES");

        Column[] columns = table.getTableMetaData().getColumns();
        assertEquals("column count", expected.length, columns.length);
        assertEquals("row count", 1, table.getRowCount());
        for (int i = 0; i < columns.length; i++)
        {
            assertEquals("value " + i, expected[i],
                    table.getValue(row, columns[i].getColumnName()));
        }
    }

    public void testLoadCRLF() throws Exception
    {
        int row = 0;
        Object[] expected = {"row 0 \n col 0 \r"}; // in the expected result the &#xA; and &#xD; should be replaced by \n and \r

        ITable table = createDataSet(false).getTable("TABLE_VALUE_METACHARS");

        Column[] columns = table.getTableMetaData().getColumns();
        assertEquals("column count", expected.length, columns.length);
        assertEquals("row count", 1, table.getRowCount());
        for (int i = 0; i < columns.length; i++)
        {
            assertEquals("value " + i, expected[i],
                    table.getValue(row, columns[i].getColumnName()));
        }
    }
    
    
//    public void testGetValueAndNoSuchColumn() throws Exception
//    {
//        ITable table = createTable();
//        String columnName = "Unknown";
//
//        Object value = table.getValue(0, columnName);
//        assertEquals("no value", null, value);
//    }

}









