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

import org.dbunit.AbstractDatabaseTest;
import org.dbunit.Assertion;
import org.dbunit.database.MockDatabaseConnection;
import org.dbunit.database.statement.MockStatementFactory;
import org.dbunit.dataset.Column;
import org.dbunit.dataset.DefaultDataSet;
import org.dbunit.dataset.DefaultTable;
import org.dbunit.dataset.DefaultTableMetaData;
import org.dbunit.dataset.ForwardOnlyDataSet;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.LowerCaseDataSet;
import org.dbunit.dataset.NoPrimaryKeyException;
import org.dbunit.dataset.datatype.DataType;
import org.dbunit.dataset.xml.FlatXmlDataSet;

import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;

/**
 * @author Manuel Laflamme
 * @version $Revision$
 * @since Feb 19, 2002
 */
public class RefreshOperationTest extends AbstractDatabaseTest
{
    public RefreshOperationTest(String s)
    {
        super(s);
    }

    public void testExecute() throws Exception
    {
        Reader reader = new FileReader("src/xml/refreshOperationTest.xml");
        IDataSet dataSet = new FlatXmlDataSet(reader);

        testExecute(dataSet);
    }

    public void testExecuteCaseInsensitive() throws Exception
    {
        Reader reader = new FileReader("src/xml/refreshOperationTest.xml");
        IDataSet dataSet = new FlatXmlDataSet(reader);

        testExecute(new LowerCaseDataSet(dataSet));
    }

    public void testExecuteForwardOnly() throws Exception
    {
        Reader reader = new FileReader("src/xml/refreshOperationTest.xml");
        IDataSet dataSet = new FlatXmlDataSet(reader);

        testExecute(new ForwardOnlyDataSet(dataSet));
    }

    private void testExecute(IDataSet dataSet) throws Exception
    {
        String[] tableNames = {"PK_TABLE", "ONLY_PK_TABLE"};
        int[] tableRowCount = {3, 1};
        String primaryKey = "PK0";

        // verify table before
        assertEquals("array lenght", tableNames.length, tableRowCount.length);
        for (int i = 0; i < tableNames.length; i++)
        {
            ITable tableBefore = createOrderedTable(tableNames[i], primaryKey);
            assertEquals("row count before", tableRowCount[i], tableBefore.getRowCount());
        }

        DatabaseOperation.REFRESH.execute(_connection, dataSet);

        // verify table after
        IDataSet expectedDataSet = new FlatXmlDataSet(
                new FileReader("src/xml/refreshOperationTestExpected.xml"));

        for (int i = 0; i < tableNames.length; i++)
        {
            ITable expectedTable = expectedDataSet.getTable(tableNames[i]);
            ITable tableAfter = createOrderedTable(tableNames[i], primaryKey);
            Assertion.assertEquals(expectedTable, tableAfter);
        }
    }

    public void testExecuteAndNoPrimaryKeys() throws Exception
    {
        String tableName = "test_table";

        Reader reader = new FileReader("src/xml/refreshOperationNoPKTest.xml");
        IDataSet dataSet = new FlatXmlDataSet(reader);

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

    public void testExecuteWithDuplicateTables() throws Exception
    {
        String[] tableNames = {"PK_TABLE", "ONLY_PK_TABLE"};
        int[] tableRowCount = {3, 1};
        String primaryKey = "PK0";

        IDataSet xmlDataSet = new FlatXmlDataSet(
                new FileReader("src/xml/refreshOperationDuplicateTest.xml"));
        assertEquals("table count", xmlDataSet.getTableNames().length, 4);

        // verify table before
        assertEquals("array lenght", tableNames.length, tableRowCount.length);
        for (int i = 0; i < tableNames.length; i++)
        {
            ITable tableBefore = createOrderedTable(tableNames[i], primaryKey);
            assertEquals("row count before", tableRowCount[i], tableBefore.getRowCount());
        }

        DatabaseOperation.REFRESH.execute(_connection, xmlDataSet);

        // verify table after
        IDataSet expectedDataSet = new FlatXmlDataSet(
                new FileReader("src/xml/refreshOperationTestExpected.xml"));

        for (int i = 0; i < tableNames.length; i++)
        {
            ITable expectedTable = expectedDataSet.getTable(tableNames[i]);
            ITable tableAfter = createOrderedTable(tableNames[i], primaryKey);
            Assertion.assertEquals(expectedTable, tableAfter);
        }
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
        DatabaseOperation.REFRESH.execute(connection, dataSet);

        factory.verify();
        connection.verify();
    }

}







