/*
 * DeleteAllOperationTest.java   Feb 18, 2002
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

package org.dbunit.operation;

import org.dbunit.dataset.*;
import org.dbunit.AbstractDatabaseTest;
import org.dbunit.database.statement.MockBatchStatement;
import org.dbunit.database.statement.MockStatementFactory;
import org.dbunit.database.MockDatabaseConnection;

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

    public void testMockExecute() throws Exception
    {
        String schemaName = "schema";
        String tableName = "table";
        String expected = "delete from schema.table";

        IDataSet dataSet = new DefaultDataSet(new DefaultTable(tableName));

        // setup mock objects
        MockBatchStatement statement = new MockBatchStatement();
        statement.addExpectedBatchString(expected);
        statement.setExpectedExecuteBatchCalls(1);
        statement.setExpectedClearBatchCalls(1);
        statement.setExpectedCloseCalls(1);

        MockStatementFactory factory = new MockStatementFactory();
        factory.setExpectedCreateStatementCalls(1);
        factory.setupStatement(statement);

        MockDatabaseConnection connection = new MockDatabaseConnection();
        connection.setupDataSet(dataSet);
        connection.setupSchema(schemaName);
        connection.setupStatementFactory(factory);
        connection.setExpectedCloseCalls(0);

        // execute operation
        new DeleteAllOperation().execute(connection, dataSet);

        statement.verify();
        factory.verify();
        connection.verify();
    }

    public void testExecute() throws Exception
    {
        IDataSet databaseDataSet = AbstractDataSetTest.removeExtraTestTables(
                _connection.createDataSet());

        ITable[] tablesBefore = DataSetUtils.getTables(databaseDataSet);
        DatabaseOperation.DELETE_ALL.execute(_connection, databaseDataSet);
        ITable[] tablesAfter = DataSetUtils.getTables(databaseDataSet);


//        ITable[] tablesBefore = DataSetUtils.getTables(_connection.createDataSet());
//        DatabaseOperation.DELETE_ALL.execute(_connection,
//                _connection.createDataSet());
//        ITable[] tablesAfter = DataSetUtils.getTables(_connection.createDataSet());

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

    public void testExecuteWithEmptyDataset() throws Exception
    {
        DatabaseOperation.DELETE_ALL.execute(_connection, new DefaultDataSet(new ITable[0]));
    }

}





