/*
 * DeleteAllOperationTest.java   Feb 18, 2002
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

package org.dbunit.operation;

import org.dbunit.dataset.*;
import org.dbunit.AbstractDatabaseTest;

/**
 * @author Manuel Laflamme
 * @version 1.0
 */
public class DeleteAllOperationTest extends AbstractDatabaseTest
{
    public DeleteAllOperationTest(String s)
    {
        super(s);
    }

    public void testGetDeleteStatement() throws Exception
    {
        String schemaName = "schema";
        String tableName = "table";
        String expected = "delete from schema.table";

        ITableMetaData metaData = new DefaultTableMetaData(tableName, new Column[0]);
        String sql = new DeleteAllOperation().getDeleteStatement(schemaName, metaData);
        assertEquals("delete statement", expected, sql);
    }

    public void testExecute() throws Exception
    {

        ITable[] tablesBefore = DataSetUtils.getTables(_connection.createDataSet());
        DatabaseOperation.DELETE_ALL.execute(_connection,
                _connection.createDataSet());
        ITable[] tablesAfter = DataSetUtils.getTables(_connection.createDataSet());

        assertTrue("table count > 0", tablesBefore.length > 0);
        assertEquals("table count", tablesBefore.length, tablesAfter.length);
        for (int i = 0; i < tablesBefore.length; i++)
        {
            ITable table = tablesBefore[i];
            String name = table.getTableMetaData().getTableName();

            if (!name.startsWith("EMPTY"))
            {
                assertTrue(name + " before", table.getRowCount() > 0);
            }
        }

        for (int i = 0; i < tablesAfter.length; i++)
        {
            ITable table = tablesAfter[i];
            String name = table.getTableMetaData().getTableName();
            assertEquals(name + "after", 0, table.getRowCount());
        }

    }

}
