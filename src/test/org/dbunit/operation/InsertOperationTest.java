/*
 * InsertOperationTest.java   Feb 19, 2002
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

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import org.dbunit.dataset.*;
import org.dbunit.dataset.datatype.DataType;
import org.dbunit.dataset.xml.XmlDataSet;
import org.dbunit.AbstractDatabaseTest;

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


    public void testGetOperationStatements() throws Exception
    {
        String schemaName = "schema";
        String tableName = "table";
        String[] expected = {
            "insert into schema.table (c1, c2, c3) values (NULL, 1234, 'false')",
            "insert into schema.table (c1, c2, c3) values ('qwerty', 123.45, 'true')",
        };

        List valueList = new ArrayList();
        valueList.add(new Object[]{null, "1234", Boolean.FALSE});
        valueList.add(new Object[]{"qwerty", new Double("123.45"), "true"});
        Column[] columns = new Column[]{
            new Column("c1", DataType.STRING),
            new Column("c2", DataType.NUMBER),
            new Column("c3", DataType.BOOLEAN),
        };

        ITable table = new DefaultTable(new DefaultTableMetaData(tableName, columns),
                valueList);

        String[] sql = new InsertOperation().getOperationStatements(schemaName, table);
        assertEquals("statement count", valueList.size(), sql.length);
        for (int i = 0; i < sql.length; i++)
        {
            String s = sql[i];
            assertEquals("statement", expected[i], sql[i]);
        }
    }

    public void testGetOperationStatementsAndMissingValue() throws Exception
    {
        String schemaName = "schema";
        String tableName = "table";
        String[] expected = {
            "insert into schema.table (c1, c2) values (NULL, 1234)",
            "insert into schema.table (c2, c3) values (123.45, 'true')",
        };

        List valueList = new ArrayList();
        valueList.add(new Object[]{null, "1234", ITable.NO_VALUE});
        valueList.add(new Object[]{ITable.NO_VALUE, new Double("123.45"), "true"});
        Column[] columns = new Column[]{
            new Column("c1", DataType.STRING),
            new Column("c2", DataType.NUMBER),
            new Column("c3", DataType.BOOLEAN),
        };

        ITable table = new DefaultTable(
                new DefaultTableMetaData(tableName, columns), valueList);

        String[] sql = new InsertOperation().getOperationStatements(schemaName, table);
        assertEquals("statement count", valueList.size(), sql.length);
        for (int i = 0; i < sql.length; i++)
        {
            String s = sql[i];
            assertEquals("statement", expected[i], sql[i]);
        }
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
                assertTrue(name + " after", table.getRowCount() > 0);
            }
        }

    }
}
