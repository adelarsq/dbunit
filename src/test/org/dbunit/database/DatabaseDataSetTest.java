/*
 * DatabaseDataSetTest.java   Feb 18, 2002
 *
 * The DbUnit Database Testing Framework
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

package org.dbunit.database;

import java.util.Arrays;
import java.sql.DatabaseMetaData;

import org.dbunit.DatabaseEnvironment;
import org.dbunit.dataset.*;
import org.dbunit.dataset.datatype.DataType;

/**
 * @author Manuel Laflamme
 * @version $Revision$
 */
public class DatabaseDataSetTest extends AbstractDataSetTest
{
    private IDatabaseConnection _connection;

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

    public void testGetSelectStatement() throws Exception
    {
        String schemaName = "schema";
        String tableName = "table";
        Column[] columns = new Column[]{
            new Column("c1", DataType.UNKNOWN),
            new Column("c2", DataType.UNKNOWN),
            new Column("c3", DataType.UNKNOWN),
        };
        String expected = "select c1, c2, c3 from schema.table";

        ITableMetaData metaData = new DefaultTableMetaData(tableName, columns);
        String sql = DatabaseDataSet.getSelectStatement(schemaName, metaData);
        assertEquals("select statement", expected, sql);
    }

    public void testGetSelectStatementWithPrimaryKeys() throws Exception
    {
        String schemaName = "schema";
        String tableName = "table";
        Column[] columns = new Column[]{
            new Column("c1", DataType.UNKNOWN),
            new Column("c2", DataType.UNKNOWN),
            new Column("c3", DataType.UNKNOWN),
        };
        String expected = "select c1, c2, c3 from schema.table order by c1, c2, c3";

        ITableMetaData metaData = new DefaultTableMetaData(tableName, columns, columns);
        String sql = DatabaseDataSet.getSelectStatement(schemaName, metaData);
        assertEquals("select statement", expected, sql);
    }

    public void testGetQualifiedTableNames() throws Exception
    {
        String[] expectedNames = getExpectedNames();
        sort(expectedNames);

        try
        {
            System.setProperty(DatabaseDataSet.QUALIFIED_TABLE_NAMES, "true");

            IDatabaseConnection connection = new DatabaseConnection(
                    _connection.getConnection(), _connection.getSchema());

            IDataSet dataSet = removeExtraTestTables(connection.createDataSet());
            String[] actualNames = dataSet.getTableNames();
            sort(actualNames);

            assertEquals("name count", expectedNames.length, actualNames.length);
            for (int i = 0; i < actualNames.length; i++)
            {
                String expected = DataSetUtils.getQualifiedName(
                        _connection.getSchema(), expectedNames[i]);
                String actual = actualNames[i];
                assertEquals("name", expected, actual);
            }
        }
        finally
        {
            System.setProperty(DatabaseDataSet.QUALIFIED_TABLE_NAMES, "false");
        }
    }

    public void testGetColumnsAndQualifiedNamesEnabled() throws Exception
    {
        String tableName = DataSetUtils.getQualifiedName(
                _connection.getSchema(), "TEST_TABLE");
        String[] expected = {"COLUMN0", "COLUMN1", "COLUMN2", "COLUMN3"};

        try
        {
            System.setProperty(DatabaseDataSet.QUALIFIED_TABLE_NAMES, "true");

            IDatabaseConnection connection = new DatabaseConnection(
                    _connection.getConnection(), _connection.getSchema());

            ITableMetaData metaData = connection.createDataSet().getTableMetaData(tableName);
            Column[] columns = metaData.getColumns();

            assertEquals("column count", expected.length, columns.length);
            for (int i = 0; i < columns.length; i++)
            {
                assertEquals("column name", expected[i], columns[i].getColumnName());
            }
        }
        finally
        {
            System.setProperty(DatabaseDataSet.QUALIFIED_TABLE_NAMES, "false");
        }
    }

    public void testGetPrimaryKeysAndQualifiedNamesEnabled() throws Exception
    {
        String tableName = DataSetUtils.getQualifiedName(
                _connection.getSchema(), "PK_TABLE");
        String[] expected = {"PK0"};

        try
        {
            System.setProperty(DatabaseDataSet.QUALIFIED_TABLE_NAMES, "true");

            IDatabaseConnection connection = new DatabaseConnection(
                    _connection.getConnection(), _connection.getSchema());

            ITableMetaData metaData = connection.createDataSet().getTableMetaData(tableName);
            Column[] columns = metaData.getPrimaryKeys();

            assertEquals("column count", expected.length, columns.length);
            for (int i = 0; i < columns.length; i++)
            {
                assertEquals("column name", expected[i], columns[i].getColumnName());
            }
        }
        finally
        {
            System.setProperty(DatabaseDataSet.QUALIFIED_TABLE_NAMES, "false");
        }
    }

//    public void testGetTableNamesAndCaseSensitive() throws Exception
//    {
//        DatabaseMetaData metaData = _connection.getConnection().getMetaData();
//        metaData.
//    }

}










