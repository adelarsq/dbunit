/*
 * XmlTableTest.java   Mar 12, 2002
 *
 * DbUnit Database Testing Framework
 * Copyright (C)2002, Manuel Laflamme
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

import java.io.*;

import org.dbunit.dataset.*;

/**
 * @author Manuel Laflamme
 * @version 1.0
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
        InputStream in = new FileInputStream(
                new File("src/xml/flatXmlTableTest.xml"));
        return new FlatXmlDataSet(in);
    }

    public void testGetMissingValue() throws Exception
    {
        int row = 1;
        Object[] expected = {"row 1 col 0", null, "row 1 col 2"};

        ITable table = createDataSet(false).getTable("MISSING_VALUES");

        Column[] columns = table.getTableMetaData().getColumns();
        assertEquals("column count", expected.length, columns.length);
        assertEquals("row count", 2, table.getRowCount());
        for (int i = 0; i < columns.length; i++)
        {
            assertEquals("value " + i, expected[i],
                    table.getValue(row, columns[i].getColumnName()));
        }
    }

    public void testGetValueAndNoSuchColumn() throws Exception
    {
        ITable table = createTable();
        String columnName = "Unknown";

        Object value = table.getValue(0, columnName);
        assertEquals("no value", null, value);
    }

}






