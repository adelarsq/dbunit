/*
 * DeleteOperationTest.java   Feb 19, 2002
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

import org.dbunit.AbstractDatabaseTest;
import org.dbunit.database.MockDatabaseConnection;
import org.dbunit.database.statement.MockBatchStatement;
import org.dbunit.database.statement.MockStatementFactory;
import org.dbunit.dataset.*;
import org.dbunit.dataset.datatype.DataType;
import org.dbunit.dataset.xml.XmlDataSet;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Manuel Laflamme
 * @version $Revision$
 */
public class DeleteOperationTest extends AbstractDatabaseTest
{
    public DeleteOperationTest(String s)
    {
        super(s);
    }

    public void testMockExecute() throws Exception
    {
        String schemaName = "schema";
        String tableName = "table";
        String[] expected = {
            "delete from schema.table where c2 = 123.45 and c1 = 'qwerty'",
            "delete from schema.table where c2 = 1234 and c1 = 'toto'",
        };

        List valueList = new ArrayList();
        valueList.add(new Object[]{"toto", "1234", Boolean.FALSE});
        valueList.add(new Object[]{"qwerty", new Double("123.45"), "true"});
        Column[] columns = new Column[]{
            new Column("c1", DataType.VARCHAR),
            new Column("c2", DataType.NUMERIC),
            new Column("c3", DataType.BOOLEAN),
        };
        String[] primaryKeys = {"c2", "c1"};

        ITable table = new DefaultTable(new DefaultTableMetaData(
                tableName, columns, primaryKeys), valueList);
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

        List valueList = new ArrayList();
        valueList.add(new Object[]{"toto", "1234", Boolean.FALSE});
        valueList.add(new Object[]{"qwerty", new Double("123.45"), "true"});
        Column[] columns = new Column[]{
            new Column("c1", DataType.VARCHAR),
            new Column("c2", DataType.NUMERIC),
            new Column("c3", DataType.BOOLEAN),
        };
        String[] primaryKeys = {"c2", "c1"};

        ITable table = new DefaultTable(new DefaultTableMetaData(
                tableName, columns, primaryKeys), valueList);
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
        setEscapePattern("[?]");
        try
        {
            new DeleteOperation().execute(connection, dataSet);
        }
        finally
        {
            setEscapePattern(null);
        }

        statement.verify();
        factory.verify();
        connection.verify();
    }

    public void testExecuteWithDuplicateTables() throws Exception
    {
        String schemaName = "schema";
        String tableName = "table";
        String[] expected = {
            "delete from schema.table where c2 = 123.45 and c1 = 'qwerty'",
            "delete from schema.table where c2 = 1234 and c1 = 'toto'",
            "delete from schema.table where c2 = 123.45 and c1 = 'qwerty'",
            "delete from schema.table where c2 = 1234 and c1 = 'toto'",
        };

        List valueList = new ArrayList();
        valueList.add(new Object[]{"toto", "1234", Boolean.FALSE});
        valueList.add(new Object[]{"qwerty", new Double("123.45"), "true"});
        Column[] columns = new Column[]{
            new Column("c1", DataType.VARCHAR),
            new Column("c2", DataType.NUMERIC),
            new Column("c3", DataType.BOOLEAN),
        };
        String[] primaryKeys = {"c2", "c1"};

        ITable table = new DefaultTable(new DefaultTableMetaData(
                tableName, columns, primaryKeys), valueList);
        IDataSet dataSet = new DefaultDataSet(new ITable[]{table, table});

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
        connection.setupDataSet(new DefaultDataSet(table));
        connection.setupSchema(schemaName);
        connection.setupStatementFactory(factory);
        connection.setExpectedCloseCalls(0);

        // execute operation
        new DeleteOperation().execute(connection, dataSet);

        statement.verify();
        factory.verify();
        connection.verify();
    }

    public void testExecuteWithEmptyTable() throws Exception
    {
        Column[] columns = {new Column("c1", DataType.VARCHAR)};
        ITable table = new DefaultTable(new DefaultTableMetaData(
                "name", columns, columns), new ArrayList());
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
                    _connection.getSchema(), metaData);
            fail("Should throw a NoPrimaryKeyException");
        }
        catch (NoPrimaryKeyException e)
        {
        }
    }

    public void testExecute() throws Exception
    {
        String tableName = "PK_TABLE";
        String columnName = "PK0";
        InputStream in = new FileInputStream(
                new File("src/xml/deleteOperationTest.xml"));
        IDataSet xmlDataSet = new XmlDataSet(in);

        // verify table before
        ITable tableBefore = createOrderedTable(tableName, columnName);
        assertEquals("row count before", 3, tableBefore.getRowCount());
        assertEquals("before", "0", tableBefore.getValue(0, columnName).toString());
        assertEquals("before", "1", tableBefore.getValue(1, columnName).toString());
        assertEquals("before", "2", tableBefore.getValue(2, columnName).toString());

        DatabaseOperation.DELETE.execute(_connection, xmlDataSet);

        ITable tableAfter = createOrderedTable(tableName, columnName);
        assertEquals("row count after", 2, tableAfter.getRowCount());
        assertEquals("after", "0", tableAfter.getValue(0, columnName).toString());
        assertEquals("after", "2", tableAfter.getValue(1, columnName).toString());
    }
}








