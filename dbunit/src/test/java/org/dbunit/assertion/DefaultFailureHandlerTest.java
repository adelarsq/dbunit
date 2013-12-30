/*
 *
 *  The DbUnit Database Testing Framework
 *  Copyright (C)2002-2004, DbUnit.org
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package org.dbunit.assertion;

import junit.framework.TestCase;

import org.dbunit.dataset.Column;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.DefaultTable;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.datatype.DataType;
import org.dbunit.dataset.filter.DefaultColumnFilter;

/**
 * @author gommma (gommma AT users.sourceforge.net)
 * @since 2.4.0
 */
public class DefaultFailureHandlerTest extends TestCase
{
    private static final String MY_TABLE = "MY_TABLE";

    private static final String COL_NAME_1 = "COL_1";
    private static final String COL_NAME_2 = "COL_2";

    private static final String COL_VALUE_1 = "value1";
    private static final String COL_VALUE_2 = "value2";

    public void testGetColumn() throws Exception
    {
        Column[] cols = new Column[]{
                new Column(COL_NAME_1, DataType.UNKNOWN),
                new Column(COL_NAME_2, DataType.UNKNOWN)
        };
        DefaultTable table = new DefaultTable(MY_TABLE, cols);
        table.addRow(new Object[] {COL_VALUE_1, COL_VALUE_2});

        // Filter COL_NAME_1
        ITable tableFiltered = DefaultColumnFilter.excludedColumnsTable(table, new String[]{COL_NAME_1});

        DefaultFailureHandler failureHandler = new DefaultFailureHandler(cols);
        String info = failureHandler.getAdditionalInfo(tableFiltered, tableFiltered, 0, COL_NAME_1);

        String expectedInfo =
                "Additional row info: ('"
                        + COL_NAME_1
                        + "': expected=<"
                        + COL_VALUE_1
                        + ">, actual=<"
                        + COL_VALUE_1
                        + ">) ('"
                        + COL_NAME_2 + "': expected=<" + COL_VALUE_2
                        + ">, actual=<" + COL_VALUE_2 + ">)";
        assertEquals(expectedInfo, info);
    }

    public void testMakeAdditionalColumnInfoErrorMessage()
    {
        DefaultFailureHandler defaultFailureHandler = new DefaultFailureHandler();

        String columnName = "testColumnName";
        DataSetException e = new DataSetException("test exception message");
        String actual =
                defaultFailureHandler.makeAdditionalColumnInfoErrorMessage(
                        columnName,
                        e);
        assertNotNull("Error message is null.", actual);

        // manually review log for acceptable message content
    }

    public void testGetColumnValue_Found() throws DataSetException
    {
        Column[] cols =
                new Column[] {new Column(COL_NAME_1, DataType.UNKNOWN),
                new Column(COL_NAME_2, DataType.UNKNOWN)};
        DefaultTable table = new DefaultTable(MY_TABLE, cols);
        table.addRow(new Object[] {COL_VALUE_1, COL_VALUE_2});

        DefaultColumnFilter.excludedColumnsTable(table,
                new String[] {COL_NAME_1});
        DefaultFailureHandler defaultFailureHandler =
                new DefaultFailureHandler();

        Object expected = COL_VALUE_1;

        int rowIndex = 0;
        String columnName = COL_NAME_1;
        Object actual = defaultFailureHandler.getColumnValue(table, rowIndex, columnName);

        assertEquals("Wrong column value found.", expected, actual);
    }

    public void testGetColumnValue_NotFound() throws DataSetException
    {
        Column[] cols =
                new Column[] {new Column(COL_NAME_1, DataType.UNKNOWN),
                new Column(COL_NAME_2, DataType.UNKNOWN)};
        DefaultTable table = new DefaultTable(MY_TABLE, cols);
        table.addRow(new Object[] {COL_VALUE_1, COL_VALUE_2});

        DefaultColumnFilter.excludedColumnsTable(table,
                new String[] {COL_NAME_1});
        DefaultFailureHandler defaultFailureHandler =
                new DefaultFailureHandler();

        Object expected = COL_VALUE_1;

        int rowIndex = 0;
        String columnName = "NonExistingColumnName";
        Object actual =
                defaultFailureHandler.getColumnValue(table, rowIndex,
                        columnName);

        assertNotSame("Wrong column value found.", expected, actual);
    }
}
