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
import org.dbunit.dataset.Column;
import org.dbunit.dataset.DefaultDataSet;
import org.dbunit.dataset.DefaultTable;
import org.dbunit.dataset.DefaultTableMetaData;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.ITableMetaData;
import org.dbunit.dataset.LowerCaseDataSet;
import org.dbunit.dataset.NoPrimaryKeyException;
import org.dbunit.dataset.datatype.DataType;
import org.dbunit.dataset.xml.XmlDataSet;
import org.dbunit.testutil.TestUtils;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;

/**
 * @author Manuel Laflamme
 * @version $Revision$
 * @since Feb 19, 2002
 */
public class DeleteOperationIT extends AbstractDatabaseIT
{
    public DeleteOperationIT(String s)
    {
        super(s);
    }

    public void testMockExecute() throws Exception
    {
        String schemaName = "schema";
        String tableName1 = "table1";
        String tableName2 = "table2";
        String[] expected = {
            "delete from schema.table2 where c2 = 1234 and c1 = 'toto'",
            "delete from schema.table2 where c2 = 123.45 and c1 = 'qwerty'",
            "delete from schema.table1 where c2 = 1234 and c1 = 'toto'",
            "delete from schema.table1 where c2 = 123.45 and c1 = 'qwerty'",
        };

        Column[] columns = new Column[]{
            new Column("c1", DataType.VARCHAR),
            new Column("c2", DataType.NUMERIC),
            new Column("c3", DataType.BOOLEAN),
        };
        String[] primaryKeys = {"c2", "c1"};

        DefaultTable table1 = new DefaultTable(new DefaultTableMetaData(
                tableName1, columns, primaryKeys));
        table1.addRow(new Object[]{"qwerty", new Double("123.45"), "true"});
        table1.addRow(new Object[]{"toto", "1234", Boolean.FALSE});
        DefaultTable table2 = new DefaultTable(new DefaultTableMetaData(
                tableName2, columns, primaryKeys));
        table2.addTableRows(table1);
        IDataSet dataSet = new DefaultDataSet(table1, table2);

        // setup mock objects
        MockBatchStatement statement = new MockBatchStatement();
        statement.addExpectedBatchStrings(expected);
        statement.setExpectedExecuteBatchCalls(2);
        statement.setExpectedClearBatchCalls(2);
        statement.setExpectedCloseCalls(2);

        MockStatementFactory factory = new MockStatementFactory();
        factory.setExpectedCreatePreparedStatementCalls(2);
        factory.setupStatement(statement);

        MockDatabaseConnection connection = new MockDatabaseConnection();
        connection.setupDataSet(dataSet);
        connection.setupSchema(schemaName);
        connection.setupStatementFactory(factory);
        connection.setExpectedCloseCalls(0);

        // execute operation
        new DeleteOperation().execute(connection, dataSet);

        statement.verify();
        factory.verify();
        connection.verify();
    }

    public void testExecuteWithEscapedNames() throws Exception
    {
        String schemaName = "schema";
        String tableName = "table";
        String[] expected = {
            "delete from [schema].[table] where [c2] = 123.45 and [c1] = 'qwerty'",
            "delete from [schema].[table] where [c2] = 1234 and [c1] = 'toto'",
        };

        Column[] columns = new Column[]{
            new Column("c1", DataType.VARCHAR),
            new Column("c2", DataType.NUMERIC),
            new Column("c3", DataType.BOOLEAN),
        };
        String[] primaryKeys = {"c2", "c1"};

        DefaultTable table = new DefaultTable(new DefaultTableMetaData(
                tableName, columns, primaryKeys));
        table.addRow(new Object[]{"toto", "1234", Boolean.FALSE});
        table.addRow(new Object[]{"qwerty", new Double("123.45"), "true"});
        IDataSet dataSet = new DefaultDataSet(table);

        // setup mock objects
        MockBatchStatement statement = new MockBatchStatement();
        statement.addExpectedBatchStrings(expected);
        statement.setExpectedExecuteBatchCalls(1);
        statement.setExpectedClearBatchCalls(1);
        statement.setExpectedCloseCalls(1);

        MockStatementFactory factory = new MockStatementFactory();
        factory.setExpectedCreatePreparedStatementCalls(1);
        factory.setupStatement(statement);

        MockDatabaseConnection connection = new MockDatabaseConnection();
        connection.setupDataSet(dataSet);
        connection.setupSchema(schemaName);
        connection.setupStatementFactory(factory);
        connection.setExpectedCloseCalls(0);

        // execute operation
        connection.getConfig().setProperty(
                DatabaseConfig.PROPERTY_ESCAPE_PATTERN, "[?]");
        new DeleteOperation().execute(connection, dataSet);

        statement.verify();
        factory.verify();
        connection.verify();
    }

    public void testExecuteWithEmptyTable() throws Exception
    {
        Column[] columns = {new Column("c1", DataType.VARCHAR)};
        ITable table = new DefaultTable(new DefaultTableMetaData(
                "name", columns, columns));
        IDataSet dataSet = new DefaultDataSet(table);

        // setup mock objects
        MockStatementFactory factory = new MockStatementFactory();
        factory.setExpectedCreatePreparedStatementCalls(0);

        MockDatabaseConnection connection = new MockDatabaseConnection();
        connection.setupDataSet(dataSet);
        connection.setupStatementFactory(factory);
        connection.setExpectedCloseCalls(0);

        // execute operation
        new DeleteOperation().execute(connection, dataSet);

        factory.verify();
        connection.verify();
    }

    public void testExecuteAndNoPrimaryKey() throws Exception
    {
        IDataSet dataSet = _connection.createDataSet();
        ITableMetaData metaData = dataSet.getTableMetaData("TEST_TABLE");
        try
        {
            new DeleteOperation().getOperationData(
                    metaData, null, _connection);
            fail("Should throw a NoPrimaryKeyException");
        }
        catch (NoPrimaryKeyException e)
        {
        }
    }

    public void testExecute() throws Exception
    {
        Reader in = new FileReader(
                TestUtils.getFile("xml/deleteOperationTest.xml"));
        IDataSet dataSet = new XmlDataSet(in);

        testExecute(dataSet);

    }

    public void testExecuteCaseInsensitive() throws Exception
    {
        Reader in = new FileReader(
                TestUtils.getFile("xml/deleteOperationTest.xml"));
        IDataSet dataSet = new XmlDataSet(in);

        testExecute(new LowerCaseDataSet(dataSet));
    }

    private void testExecute(IDataSet dataSet) throws Exception
    {
        String tableName = "PK_TABLE";
        String columnName = "PK0";

        // verify table before
        ITable tableBefore = createOrderedTable(tableName, columnName);
        assertEquals("row count before", 3, tableBefore.getRowCount());
        assertEquals("before", "0", tableBefore.getValue(0, columnName).toString());
        assertEquals("before", "1", tableBefore.getValue(1, columnName).toString());
        assertEquals("before", "2", tableBefore.getValue(2, columnName).toString());

        DatabaseOperation.DELETE.execute(_connection, dataSet);

        ITable tableAfter = createOrderedTable(tableName, columnName);
        assertEquals("row count after", 2, tableAfter.getRowCount());
        assertEquals("after", "0", tableAfter.getValue(0, columnName).toString());
        assertEquals("after", "2", tableAfter.getValue(1, columnName).toString());
    }
}








