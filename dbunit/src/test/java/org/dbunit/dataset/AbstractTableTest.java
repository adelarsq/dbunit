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

import junit.framework.TestCase;

import org.dbunit.DatabaseEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Manuel Laflamme
 * @version $Revision$
 * @since Feb 17, 2002
 */
public abstract class AbstractTableTest extends TestCase
{
    protected static final int ROW_COUNT = 6;
    protected static final int COLUMN_COUNT = 4;

    protected final Logger logger = LoggerFactory.getLogger(getClass());
    
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

    /**
     * Returns the string converted as an identifier according to the metadata rules of the database environment.
     * Most databases convert all metadata identifiers to uppercase.
     * PostgreSQL converts identifiers to lowercase.
     * MySQL preserves case.
     * @param str The identifier.
     * @return The identifier converted according to database rules.
     */
    protected String convertString(String str) throws Exception
    {
        return str;
    }

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
            String expected = convertString("COLUMN" + i);
            String actual = columns[i].getColumnName();
            assertEquals("column name", expected, actual);
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

    public void testGetValueCaseInsensitive() throws Exception
    {
        ITable table = createTable();
        for (int i = 0; i < ROW_COUNT; i++)
        {
            for (int j = 0; j < COLUMN_COUNT; j++)
            {
                String columnName = "CoLUmN" + j;
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
            fail("Should throw a NoSuchColumnException!");
        }
        catch (NoSuchColumnException e)
        {
        }
    }
    
    /**
     * This method is used so sub-classes can disable the tests according to 
     * some characteristics of the environment
     * @param testName name of the test to be checked
     * @return flag indicating if the test should be executed or not
     */
    protected boolean runTest(String testName) {
      return true;
    }

    protected void runTest() throws Throwable {
      if ( runTest(getName()) ) {
        super.runTest();
      } else { 
        if ( logger.isDebugEnabled() ) {
          logger.debug( "Skipping test " + getClass().getName() + "." + getName() );
        }
      }
    }    
    
}






