/*
 * InsertOperationTest.java   Feb 19, 2002
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

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import org.dbunit.dataset.*;
import org.dbunit.dataset.datatype.DataType;
import org.dbunit.dataset.xml.XmlDataSet;
import org.dbunit.AbstractDatabaseTest;
import org.dbunit.Assertion;
import org.dbunit.database.MockDatabaseConnection;
import org.dbunit.database.statement.*;

/**
 * @author Manuel Laflamme
 * @version 1.0
 */
public class InsertOperationTest extends AbstractDatabaseTest
{
    public InsertOperationTest(String s)
    {
        super(s);
    }

    public void testMockExecute() throws Exception
    {
        String schemaName = "schema";
        String tableName = "table";
        String[] expected = {
            "insert into schema.table (c1, c2, c3) values (NULL, 1234, 'false')",
            "insert into schema.table (c1, c2, c3) values ('qwerty', 123.45, 'true')",
        };

        // setup table
        List valueList = new ArrayList();
        valueList.add(new Object[]{null, "1234", Boolean.FALSE});
        valueList.add(new Object[]{"qwerty", new Double("123.45"), "true"});
        Column[] columns = new Column[]{
            new Column("c1", DataType.VARCHAR),
            new Column("c2", DataType.NUMERIC),
            new Column("c3", DataType.BOOLEAN),
        };
        DefaultTable table = new DefaultTable(tableName, columns, valueList);
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
        new InsertOperation().execute(connection, dataSet);

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
        new InsertOperation().execute(connection, dataSet);

        factory.verify();
        connection.verify();
    }

    public void testExecute() throws Exception
    {
        InputStream in = new FileInputStream(
                new File("src/xml/insertOperationTest.xml"));
        IDataSet xmlDataSet = new XmlDataSet(in);

        ITable[] tablesBefore = DataSetUtils.getTables(_connection.createDataSet());
        DatabaseOperation.INSERT.execute(_connection, xmlDataSet);
        ITable[] tablesAfter = DataSetUtils.getTables(_connection.createDataSet());

        assertEquals("table count", tablesBefore.length, tablesAfter.length);
        for (int i = 0; i < tablesBefore.length; i++)
        {
            ITable table = tablesBefore[i];
            String name = table.getTableMetaData().getTableName();


            if (name.startsWith("EMPTY"))
            {
                assertEquals(name + "before", 0, table.getRowCount());
            }
        }

        for (int i = 0; i < tablesAfter.length; i++)
        {
            ITable table = tablesAfter[i];
            String name = table.getTableMetaData().getTableName();

            if (name.startsWith("EMPTY"))
            {
                Assertion.assertEquals(xmlDataSet.getTable(name), table);
            }
        }

    }
}






