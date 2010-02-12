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

package org.dbunit.operation;

import org.dbunit.AbstractDatabaseIT;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.MockDatabaseConnection;
import org.dbunit.database.statement.MockBatchStatement;
import org.dbunit.database.statement.MockStatementFactory;
import org.dbunit.dataset.AbstractDataSetTest;
import org.dbunit.dataset.DataSetUtils;
import org.dbunit.dataset.DefaultDataSet;
import org.dbunit.dataset.DefaultTable;
import org.dbunit.dataset.EmptyTableDataSet;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.LowerCaseDataSet;

/**
 * @author Manuel Laflamme
 * @author Eric Pugh
 * TODO Refactor all the references to AbstractDataSetTest.removeExtraTestTables() to something better.
 * @version $Revision$
 * @since Feb 18, 2002
 */
public class DeleteAllOperationIT extends AbstractDatabaseIT
{
    public DeleteAllOperationIT(String s)
    {
        super(s);
    }
    
    protected void setUp() throws Exception
    {
        super.setUp();

        DatabaseOperation.CLEAN_INSERT.execute(_connection,
                getEnvironment().getInitDataSet());
    }

    protected DatabaseOperation getDeleteAllOperation()
    {
        return new DeleteAllOperation();
    }

    protected String getExpectedStament(String tableName)
    {
        return "delete from " + tableName;
    }

    public void testMockExecute() throws Exception
    {
        String schemaName = "schema";
        String tableName = "table";
        String expected = getExpectedStament(schemaName + "." + tableName);

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
        getDeleteAllOperation().execute(connection, dataSet);

        statement.verify();
        factory.verify();
        connection.verify();
    }

    public void testExecuteWithEscapedNames() throws Exception
    {
        String schemaName = "schema";
        String tableName = "table";
        String expected = getExpectedStament("'" + schemaName + "'.'" + tableName +"'");

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
        connection.getConfig().setProperty(
                DatabaseConfig.PROPERTY_ESCAPE_PATTERN, "'?'");
        getDeleteAllOperation().execute(connection, dataSet);

        statement.verify();
        factory.verify();
        connection.verify();
    }

    public void testExecute() throws Exception
    {
        IDataSet databaseDataSet = _connection.createDataSet();
        IDataSet dataSet = AbstractDataSetTest.removeExtraTestTables(
                databaseDataSet);

        testExecute(dataSet);
    }

    public void testExecuteEmpty() throws Exception
    {
        IDataSet databaseDataSet = _connection.createDataSet();
        IDataSet dataSet = AbstractDataSetTest.removeExtraTestTables(
                databaseDataSet);

        testExecute(new EmptyTableDataSet(dataSet));
    }

    public void testExecuteCaseInsentive() throws Exception
    {
        IDataSet dataSet = AbstractDataSetTest.removeExtraTestTables(
                _connection.createDataSet());

        testExecute(new LowerCaseDataSet(dataSet));
    }

    /* The AbstractDataSetTest.removeExtraTestTables() is required when you
    run on something besides hypersone (like mssql or oracle) to deal with
    the extra tables that may not have data.

    Need something like getDefaultTables or something that is totally cross dbms.
    */
    private void testExecute(IDataSet dataSet) throws Exception
    {
        //dataSet = dataSet);
        ITable[] tablesBefore = DataSetUtils.getTables(AbstractDataSetTest.removeExtraTestTables(_connection.createDataSet()));
        getDeleteAllOperation().execute(_connection, dataSet);
        ITable[] tablesAfter = DataSetUtils.getTables(AbstractDataSetTest.removeExtraTestTables(_connection.createDataSet()));

        assertTrue("table count > 0", tablesBefore.length > 0);
        assertEquals("table count", tablesBefore.length, tablesAfter.length);
        for (int i = 0; i < tablesBefore.length; i++)
        {
            ITable table = tablesBefore[i];
            String name = table.getTableMetaData().getTableName();

            if (!name.toUpperCase().startsWith("EMPTY"))
            {
                assertTrue(name + " before", table.getRowCount() > 0);
            }
        }

        for (int i = 0; i < tablesAfter.length; i++)
        {
            ITable table = tablesAfter[i];
            String name = table.getTableMetaData().getTableName();
            assertEquals(name + " after " + i, 0, table.getRowCount());
        }
    }

    public void testExecuteWithEmptyDataset() throws Exception
    {
        getDeleteAllOperation().execute(
                _connection, new DefaultDataSet(new ITable[0]));
    }
}






