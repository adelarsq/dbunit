/*
 * DatabaseDataSetTest.java   Feb 18, 2002
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

package org.dbunit.database;

import java.util.Arrays;

import org.dbunit.dataset.AbstractDataSetTest;
import org.dbunit.DatabaseEnvironment;
import org.dbunit.dataset.*;

/**
 * @author Manuel Laflamme
 * @version 1.0
 */
public class DatabaseDataSetTest extends AbstractDataSetTest
{
    private DatabaseConnection _connection;

    public DatabaseDataSetTest(String s)
    {
        super(s);
    }

    ////////////////////////////////////////////////////////////////////////////
    // TestCase class

    protected void setUp() throws Exception
    {
        super.setUp();

        _connection = DatabaseEnvironment.getInstance().getConnection();
    }

    protected void tearDown() throws Exception
    {
        super.tearDown();

        _connection = null;
    }

    ////////////////////////////////////////////////////////////////////////////
    // AbstractDataSetTest class

    protected IDataSet createDataSet() throws Exception
    {
        return _connection.createDataSet();
    }

    protected void sort(Object[] array)
    {
        Arrays.sort(array);
    }

    ////////////////////////////////////////////////////////////////////////////
    // Test methods

    public void testGetPrimaryKeys() throws Exception
    {
        String tableName = "PK_TABLE";
        String[] expected = {"PK0"};
//        String[] expected = {"PK0", "PK1", "PK2"};

        ITableMetaData metaData = createDataSet().getTableMetaData(tableName);
        Column[] columns = metaData.getPrimaryKeys();

        assertEquals("pk count", expected.length, columns.length);
        for (int i = 0; i < columns.length; i++)
        {
            Column column = columns[i];
            assertEquals("name", expected[i], column.getColumnName());
        }
    }

    public void testGetNoPrimaryKeys() throws Exception
    {
        String tableName = "TEST_TABLE";

        ITableMetaData metaData = createDataSet().getTableMetaData(tableName);
        Column[] columns = metaData.getPrimaryKeys();

        assertEquals("pk count", 0, columns.length);
    }


    public void testGetSelectStatement() throws Exception
    {
        String schemaName = "schema";
        String tableName = "table";
        Column[] columns = new Column[]{
            new Column("c1", null),
            new Column("c2", null),
            new Column("c3", null),
        };
        String expected = "select c1, c2, c3 from schema.table";

        ITableMetaData metaData = new DefaultTableMetaData(tableName, columns);
        String sql = DatabaseDataSet.getSelectStatement(schemaName, metaData);
        assertEquals("select statement", expected, sql);
    }

}
