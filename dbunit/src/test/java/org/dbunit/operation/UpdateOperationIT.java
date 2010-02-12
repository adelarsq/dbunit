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

import java.io.File;
import java.io.FileReader;
import java.io.Reader;

import org.dbunit.AbstractDatabaseIT;
import org.dbunit.Assertion;
import org.dbunit.DatabaseEnvironment;
import org.dbunit.TestFeature;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.MockDatabaseConnection;
import org.dbunit.database.statement.MockBatchStatement;
import org.dbunit.database.statement.MockStatementFactory;
import org.dbunit.dataset.Column;
import org.dbunit.dataset.CompositeDataSet;
import org.dbunit.dataset.DefaultDataSet;
import org.dbunit.dataset.DefaultTable;
import org.dbunit.dataset.DefaultTableMetaData;
import org.dbunit.dataset.ForwardOnlyDataSet;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.LowerCaseDataSet;
import org.dbunit.dataset.NoPrimaryKeyException;
import org.dbunit.dataset.datatype.DataType;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.dataset.xml.XmlDataSet;
import org.dbunit.testutil.TestUtils;

/**
 * @author Manuel Laflamme
 * @version $Revision$
 * @since Feb 19, 2002
 */
public class UpdateOperationIT extends AbstractDatabaseIT
{
    public UpdateOperationIT(String s)
    {
        super(s);
    }

    ////////////////////////////////////////////////////////////////////////////
    //

    protected IDataSet getDataSet() throws Exception
    {
        IDataSet dataSet = super.getDataSet();

        DatabaseEnvironment environment = DatabaseEnvironment.getInstance();
        if (environment.support(TestFeature.BLOB))
        {
            dataSet = new CompositeDataSet(
                    new FlatXmlDataSetBuilder().build(TestUtils.getFile("xml/blobInsertTest.xml")),
                    dataSet);
        }

        if (environment.support(TestFeature.CLOB))
        {
            dataSet = new CompositeDataSet(
                    new FlatXmlDataSetBuilder().build(TestUtils.getFile("xml/clobInsertTest.xml")),
                    dataSet);
        }

        if (environment.support(TestFeature.SDO_GEOMETRY))
        {
            dataSet = new CompositeDataSet(
                    new FlatXmlDataSetBuilder().build(TestUtils.getFile("xml/sdoGeometryInsertTest.xml")),
                    dataSet
            );
        }

        return dataSet;
    }

    ////////////////////////////////////////////////////////////////////////////
    //

    public void testMockExecute() throws Exception
    {
        String schemaName = "schema";
        String tableName = "table";
        String[] expected = {
            "update schema.table set c2 = 1234, c3 = 'false' where c4 = 0 and c1 = 'toto'",
            "update schema.table set c2 = 123.45, c3 = NULL where c4 = 0 and c1 = 'qwerty'",
        };

        Column[] columns = new Column[]{
            new Column("c1", DataType.VARCHAR),
            new Column("c2", DataType.NUMERIC),
            new Column("c3", DataType.VARCHAR),
            new Column("c4", DataType.NUMERIC),
        };
        String[] primaryKeys = {"c4", "c1"};
        DefaultTable table = new DefaultTable(new DefaultTableMetaData(
                tableName, columns, primaryKeys));
        table.addRow(new Object[]{"toto", "1234", "false", "0"});
        table.addRow(new Object[]{"qwerty", new Double("123.45"), null, "0"});
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
        new UpdateOperation().execute(connection, dataSet);

        statement.verify();
        factory.verify();
        connection.verify();
    }

    public void testExecuteWithEscapedName() throws Exception
    {
        String schemaName = "schema";
        String tableName = "table";
        String[] expected = {
            "update [schema].[table] set [c2] = 1234, [c3] = 'false' where [c4] = 0 and [c1] = 'toto'",
            "update [schema].[table] set [c2] = 123.45, [c3] = NULL where [c4] = 0 and [c1] = 'qwerty'",
        };

        Column[] columns = new Column[]{
            new Column("c1", DataType.VARCHAR),
            new Column("c2", DataType.NUMERIC),
            new Column("c3", DataType.VARCHAR),
            new Column("c4", DataType.NUMERIC),
        };
        String[] primaryKeys = {"c4", "c1"};
        DefaultTable table = new DefaultTable(new DefaultTableMetaData(
                tableName, columns, primaryKeys));
        table.addRow(new Object[]{"toto", "1234", "false", "0"});
        table.addRow(new Object[]{"qwerty", new Double("123.45"), null, "0"});
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
        new UpdateOperation().execute(connection, dataSet);

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
        new UpdateOperation().execute(connection, dataSet);

        factory.verify();
        connection.verify();
    }

    public void testUpdateClob() throws Exception
    {
        // execute this test only if the target database support CLOB
        DatabaseEnvironment environment = DatabaseEnvironment.getInstance();
        if (environment.support(TestFeature.CLOB))
        {
            String tableName = "CLOB_TABLE";

            {
                IDataSet beforeDataSet = new FlatXmlDataSetBuilder().build(
                        TestUtils.getFile("xml/clobInsertTest.xml"));

                ITable tableBefore = _connection.createDataSet().getTable(tableName);
                assertEquals("count before", 3, _connection.getRowCount(tableName));
                Assertion.assertEquals(beforeDataSet.getTable(tableName), tableBefore);
            }

            IDataSet afterDataSet = new FlatXmlDataSetBuilder().build(
                    TestUtils.getFile("xml/clobUpdateTest.xml"));
            DatabaseOperation.REFRESH.execute(_connection, afterDataSet);

            {
                ITable tableAfter = _connection.createDataSet().getTable(tableName);
                assertEquals("count after", 4, tableAfter.getRowCount());
                Assertion.assertEquals(afterDataSet.getTable(tableName), tableAfter);
            }
        }
    }

    public void testUpdateBlob() throws Exception
    {
        // execute this test only if the target database support BLOB
        DatabaseEnvironment environment = DatabaseEnvironment.getInstance();
        if (environment.support(TestFeature.BLOB))
        {
            String tableName = "BLOB_TABLE";

            {
                IDataSet beforeDataSet = new FlatXmlDataSetBuilder().build(
                        TestUtils.getFile("xml/blobInsertTest.xml"));

                ITable tableBefore = _connection.createDataSet().getTable(tableName);
                assertEquals("count before", 1, _connection.getRowCount(tableName));
                Assertion.assertEquals(beforeDataSet.getTable(tableName), tableBefore);

//                System.out.println("****** BEFORE *******");
//                FlatXmlDataSet.write(_connection.createDataSet(), System.out);
            }

            IDataSet afterDataSet = new FlatXmlDataSetBuilder().build(
                    TestUtils.getFile("xml/blobUpdateTest.xml"));
            DatabaseOperation.REFRESH.execute(_connection, afterDataSet);

            {
                ITable tableAfter = _connection.createDataSet().getTable(tableName);
                assertEquals("count after", 2, tableAfter.getRowCount());
                Assertion.assertEquals(afterDataSet.getTable(tableName), tableAfter);

//                System.out.println("****** AFTER *******");
//                FlatXmlDataSet.write(_connection.createDataSet(), System.out);
            }
        }
    }

    public void testUpdateSdoGeometry() throws Exception
    {
        // execute this test only if the target database supports SDO_GEOMETRY
        DatabaseEnvironment environment = DatabaseEnvironment.getInstance();
        if (environment.support(TestFeature.SDO_GEOMETRY))
        {
            String tableName = "SDO_GEOMETRY_TABLE";

            {
                IDataSet beforeDataSet = new FlatXmlDataSetBuilder().build(
                        TestUtils.getFile("xml/sdoGeometryInsertTest.xml"));

                ITable tableBefore = _connection.createDataSet().getTable(tableName);
                assertEquals("count before", 1, _connection.getRowCount(tableName));
                Assertion.assertEquals(beforeDataSet.getTable(tableName), tableBefore);
            }

            IDataSet afterDataSet = new FlatXmlDataSetBuilder().build(
                    TestUtils.getFile("xml/sdoGeometryUpdateTest.xml"));
            DatabaseOperation.REFRESH.execute(_connection, afterDataSet);

            {
                ITable tableAfter = _connection.createDataSet().getTable(tableName);
                assertEquals("count after", 8, tableAfter.getRowCount());
                Assertion.assertEquals(afterDataSet.getTable(tableName), tableAfter);
            }
        }
    }

    public void testExecute() throws Exception
    {
        Reader in = new FileReader(
                TestUtils.getFile("xml/updateOperationTest.xml"));
        IDataSet dataSet = new XmlDataSet(in);

        testExecute(dataSet);

    }

    public void testExecuteCaseInsensitive() throws Exception
    {
        Reader in = new FileReader(
                TestUtils.getFile("xml/updateOperationTest.xml"));
        IDataSet dataSet = new XmlDataSet(in);

        testExecute(new LowerCaseDataSet(dataSet));
    }

    public void testExecuteForwardOnly() throws Exception
    {
        Reader in = new FileReader(
                TestUtils.getFile("xml/updateOperationTest.xml"));
        IDataSet dataSet = new XmlDataSet(in);

        testExecute(new ForwardOnlyDataSet(dataSet));
    }

    public void testExecuteAndNoPrimaryKeys() throws Exception
    {
        String tableName = "TEST_TABLE";

        Reader reader = TestUtils.getFileReader("xml/updateOperationNoPKTest.xml");
        IDataSet dataSet = new FlatXmlDataSetBuilder().build(reader);

        // verify table before
        assertEquals("row count before", 6, _connection.getRowCount(tableName));

        try
        {
            DatabaseOperation.REFRESH.execute(_connection, dataSet);
            fail("Should not be here!");
        }
        catch (NoPrimaryKeyException e)
        {

        }

        // verify table after
        assertEquals("row count before", 6, _connection.getRowCount(tableName));
    }

    private void testExecute(IDataSet dataSet) throws Exception
    {
        String tableName = "PK_TABLE";
        String[] columnNames = {"PK0", "PK1", "PK2", "NORMAL0", "NORMAL1"};
        int modifiedRow = 1;

        // verify table before
        ITable tableBefore = createOrderedTable(tableName, columnNames[0]);
        assertEquals("row count before", 3, tableBefore.getRowCount());

        DatabaseOperation.UPDATE.execute(_connection, dataSet);

        ITable tableAfter = createOrderedTable(tableName, columnNames[0]);
        assertEquals("row count after", 3, tableAfter.getRowCount());
        for (int i = 0; i < tableAfter.getRowCount(); i++)
        {
            // verify modified row
            if (i == modifiedRow)
            {
                assertEquals("PK0", "1",
                        tableAfter.getValue(i, "PK0").toString());
                assertEquals("PK1", "1",
                        tableAfter.getValue(i, "PK1").toString());
                assertEquals("PK2", "1",
                        tableAfter.getValue(i, "PK2").toString());
                assertEquals("NORMAL0", "toto",
                        tableAfter.getValue(i, "NORMAL0").toString());
                assertEquals("NORMAL1", "qwerty",
                        tableAfter.getValue(i, "NORMAL1").toString());
            }
            // all other row must be equals than before update
            else
            {
                for (int j = 0; j < columnNames.length; j++)
                {
                    String name = columnNames[j];
                    Object valueAfter = tableAfter.getValue(i, name);
                    Object valueBefore = tableBefore.getValue(i, name);
                    assertEquals("c=" + name + ",r=" + j, valueBefore, valueAfter);
                }
            }
        }
    }


}








