/*
 * AbstractTableTest.java   Feb 17, 2002
 *
 * The dbUnit database testing framework.
 * Copyright (C) 2002   Manuel Laflamme
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

import junit.framework.TestCase;

/**
 * @author Manuel Laflamme
 * @version 1.0
 */
public abstract class AbstractTableTest extends TestCase
{
    protected static final int ROW_COUNT = 6;
    protected static final int COLUMN_COUNT = 4;

    public AbstractTableTest(String s)
    {
        super(s);
    }

    /**
     * Creates a table having 6 row and 4 column where columns are named
     * "COLUMN1, COLUMN2, COLUMN3, COLUMN4" and values are string follwing this
     * template "row ? col ?"
     */
    protected abstract ITable createTable() throws Exception;

    ////////////////////////////////////////////////////////////////////////////
    // Test methods

    public void testGetRowCount() throws Exception
    {
        assertEquals("row count", ROW_COUNT, createTable().getRowCount());
    }

    public void testTableMetaData() throws Exception
    {
        Column[] columns = createTable().getTableMetaData().getColumns();
        assertEquals("column count", COLUMN_COUNT, columns.length);
        for (int i = 0; i < columns.length; i++)
        {
            Column column = columns[i];
            assertEquals("column name", "COLUMN" + i, columns[i].getColumnName());
        }
    }

    public void testGetValue() throws Exception
    {
        ITable table = createTable();
        for (int i = 0; i < ROW_COUNT; i++)
        {
            for (int j = 0; j < COLUMN_COUNT; j++)
            {
                String columnName = "COLUMN" + j;
                String expected = "row " + i + " col " + j;
                Object value = table.getValue(i, columnName);
                assertEquals("value", expected, value);
            }
        }
    }

    public abstract void testGetMissingValue() throws Exception;

    public void testGetValueRowBounds() throws Exception
    {
        int[] rows = new int[]{-2, -1, -ROW_COUNT, ROW_COUNT, ROW_COUNT + 1};
        ITable table = createTable();
        String columnName = table.getTableMetaData().getColumns()[0].getColumnName();

        for (int i = 0; i < rows.length; i++)
        {
            try
            {
                table.getValue(rows[i], columnName);
                fail("Should throw a RowOutOfBoundsException!");
            }
            catch (RowOutOfBoundsException e)
            {
            }
        }
    }

    public void testGetValueAndNoSuchColumn() throws Exception
    {
        ITable table = createTable();
        String columnName = "Unknown";

        try
        {
            table.getValue(0, columnName);
            fail("Should throw a RowOutOfBoundsException!");
        }
        catch (NoSuchColumnException e)
        {
        }
    }
}
