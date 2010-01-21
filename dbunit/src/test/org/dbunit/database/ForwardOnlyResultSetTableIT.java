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
package org.dbunit.database;

import org.dbunit.DatabaseEnvironment;
import org.dbunit.dataset.Column;
import org.dbunit.dataset.ForwardOnlyTableTest;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.MockTableMetaData;
import org.dbunit.dataset.RowOutOfBoundsException;
import org.dbunit.operation.DatabaseOperation;

/**
 * @author Manuel Laflamme
 * @since Apr 11, 2003
 * @version $Revision$
 */
public class ForwardOnlyResultSetTableIT extends ForwardOnlyTableTest
{
    public ForwardOnlyResultSetTableIT(String s)
    {
        super(s);
    }

    protected ITable createTable() throws Exception
    {
        DatabaseEnvironment env = DatabaseEnvironment.getInstance();
        IDatabaseConnection connection = env.getConnection();

        DatabaseOperation.CLEAN_INSERT.execute(connection, env.getInitDataSet());

        String selectStatement = "select * from TEST_TABLE order by COLUMN0";
        return new ForwardOnlyResultSetTable("TEST_TABLE", selectStatement, connection);
    }

    protected String convertString(String str) throws Exception
    {
        return DatabaseEnvironment.getInstance().convertString(str);
    }

    public void testGetMissingValue() throws Exception
    {
        // Do not test this!
    }

    public void testGetValueOnLastRowIsClosingResultSet() throws Exception
    {
        String tableName = "TABLE";
        String[] columnNames = {"C0"};
//        String[] columnNames = {"C0", "C1", "C2"};
        Object[][] expectedValues = new Object[][]{
            new Object[]{"1", "2", "3"},
            new Object[]{"4", "5", "6"},
            new Object[]{"7", "8", "9"},
        };

        // Setup resultset
        ExtendedMockMultiRowResultSet resultSet = new ExtendedMockMultiRowResultSet();
        resultSet.setExpectedCloseCalls(1);
        resultSet.setupColumnNames(columnNames);
        resultSet.setupRows(expectedValues);

        // Create table
        MockTableMetaData metaData = new MockTableMetaData(tableName, columnNames);
        ForwardOnlyResultSetTable table =
                new ForwardOnlyResultSetTable(metaData, resultSet);

        // Exercise getValue()
        try
        {
            Column[] columns = table.getTableMetaData().getColumns();

            for (int i = 0; ; i++)
            {
                for (int j = 0; j < columns.length; j++)
                {
                    String columnName = columns[j].getColumnName();
                    Object actualValue = table.getValue(i, columnName);
                    Object expectedValue = expectedValues[i][j];
                    assertEquals("row=" + i + ", col=" + columnName,
                            expectedValue, actualValue);

                }
            }
        }
        catch(RowOutOfBoundsException e)
        {
            // end of table
        }

        // Verify that ResultSet have been closed
        resultSet.verify();
    }

}
