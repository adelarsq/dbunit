/*
 * UpdateOperationTest.java   Feb 19, 2002
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
public class UpdateOperationTest extends AbstractDatabaseTest
{
    public UpdateOperationTest(String s)
    {
        super(s);
    }

    public void testGetOperationStatements() throws Exception
    {
        String schemaName = "schema";
        String tableName = "table";
        String[] expected = {
            "update schema.table set c2 = 1234, c3 = 'false' where c4 = 0 and c1 = 'toto'",
            "update schema.table set c2 = 123.45, c3 = NULL where c4 = 0 and c1 = 'qwerty'",
        };

        List valueList = new ArrayList();
        valueList.add(new Object[]{"toto", "1234", "false", "0"});
        valueList.add(new Object[]{"qwerty", new Double("123.45"), null, "0"});
        Column[] columns = new Column[]{
            new Column("c1", DataType.STRING),
            new Column("c2", DataType.NUMBER),
            new Column("c3", DataType.STRING),
            new Column("c4", DataType.NUMBER),
        };
        String[] primaryKeys = {"c4", "c1"};

        ITable table = new DefaultTable(new DefaultTableMetaData(
                tableName, columns, primaryKeys), valueList);

        String[] sql = new UpdateOperation().getOperationStatements(schemaName, table);
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
            "update schema.table set c1 = 'toto', c2 = 1234 where c4 = 0",
            "update schema.table set c2 = 123.45, c3 = NULL where c4 = 0",
        };

        List valueList = new ArrayList();
        valueList.add(new Object[]{"toto", "1234", ITable.NO_VALUE, "0"});
        valueList.add(new Object[]{ITable.NO_VALUE, new Double("123.45"), null, "0"});
        Column[] columns = new Column[]{
            new Column("c1", DataType.STRING),
            new Column("c2", DataType.NUMBER),
            new Column("c3", DataType.STRING),
            new Column("c4", DataType.NUMBER),
        };
        String[] primaryKeys = {"c4"};

        ITable table = new DefaultTable(new DefaultTableMetaData(
                tableName, columns, primaryKeys), valueList);

        String[] sql = new UpdateOperation().getOperationStatements(schemaName, table);
        assertEquals("statement count", valueList.size(), sql.length);
        for (int i = 0; i < sql.length; i++)
        {
            String s = sql[i];
            assertEquals("statement", expected[i], sql[i]);
        }
    }

    public void testGetOperationStatementsAndNoPrimaryKey() throws Exception
    {
        ITable table = _connection.createDataSet().getTable("TEST_TABLE");
        try
        {
            new UpdateOperation().getOperationStatements(_connection.getSchema(), table);
            fail("Should throw a NoPrimaryKeyException");
        }
        catch (NoPrimaryKeyException e)
        {
        }
    }

    public void testExecute() throws Exception
    {
        String tableName = "PK_TABLE";
        String[] columnNames = {"PK0", "PK1", "PK2", "NORMAL0", "NORMAL1"};
        int modifiedRow = 1;

        InputStream in = new FileInputStream(
                new File("src/xml/updateOperationTest.xml"));
        IDataSet xmlDataSet = new XmlDataSet(in);

        // verify table before
        ITable tableBefore = createOrderedTable(tableName, columnNames[0]);
        assertEquals("row count before", 3, tableBefore.getRowCount());

        DatabaseOperation.UPDATE.execute(_connection, xmlDataSet);

        ITable tableAfter = createOrderedTable(tableName, columnNames[0]);
        assertEquals("row count after", 3, tableAfter.getRowCount());
        for (int i = 0; i < tableAfter.getRowCount(); i++)
        {
            // verify modified row
            if (i == modifiedRow)
            {
                assertEquals("PK0", "1",
                        tableAfter.getValue(i, "PK0").toString());
                assertEquals("PK1", "11",
                        tableAfter.getValue(i, "PK1").toString());
                assertEquals("PK2", "111",
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
