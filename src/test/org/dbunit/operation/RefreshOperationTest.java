/*
 * UpdateOperationTest.java   Feb 19, 2002
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
import org.dbunit.Assertion;
import org.dbunit.database.MockDatabaseConnection;
import org.dbunit.database.statement.MockStatementFactory;
import org.dbunit.dataset.*;
import org.dbunit.dataset.datatype.DataType;
import org.dbunit.dataset.xml.FlatXmlDataSet;

import java.io.FileInputStream;
import java.io.FileReader;
import java.util.ArrayList;

/**
 * @author Manuel Laflamme
 * @version $Revision$
 */
public class RefreshOperationTest extends AbstractDatabaseTest
{
    public RefreshOperationTest(String s)
    {
        super(s);
    }

    public void testExecute() throws Exception
    {
        String[] tableNames = {"PK_TABLE", "ONLY_PK_TABLE"};
        int[] tableRowCount = {3, 1};
        String primaryKey = "PK0";

        IDataSet xmlDataSet = new FlatXmlDataSet(
                new FileReader("src/xml/refreshOperationTest.xml"));

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







