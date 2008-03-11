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

package org.dbunit.database;

import org.dbunit.DatabaseEnvironment;
import org.dbunit.dataset.*;
import org.dbunit.dataset.filter.DefaultColumnFilter;
import org.dbunit.dataset.datatype.DataType;

/**
 * @author Manuel Laflamme
 * @version $Revision$
 * @since Feb 18, 2002
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

    protected String[] getExpectedNames() throws Exception
    {
        return _connection.createDataSet().getTableNames();
    }

    protected IDataSet createDuplicateDataSet() throws Exception
    {
        throw new UnsupportedOperationException();
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
        String sql = DatabaseDataSet.getSelectStatement(schemaName, metaData, null);
        assertEquals("select statement", expected, sql);
    }

    public void testGetSelectStatementWithEscapedNames() throws Exception
    {
        String schemaName = "schema";
        String tableName = "table";
        Column[] columns = new Column[]{
            new Column("c1", DataType.UNKNOWN),
            new Column("c2", DataType.UNKNOWN),
            new Column("c3", DataType.UNKNOWN),
        };
        String expected = "select 'c1', 'c2', 'c3' from 'schema'.'table'";

        ITableMetaData metaData = new DefaultTableMetaData(tableName, columns);
        String sql = DatabaseDataSet.getSelectStatement(schemaName, metaData, "'?'");
        assertEquals("select statement", expected, sql);
    }
    
    public void testGetSelectStatementWithEscapedNamesAndOrderBy() throws Exception
    {
        String schemaName = "schema";
        String tableName = "table";
        Column[] columns = new Column[]{
            new Column("c1", DataType.UNKNOWN),
            new Column("c2", DataType.UNKNOWN),
            new Column("c3", DataType.UNKNOWN),
        };
        String expected = "select 'c1', 'c2', 'c3' from 'schema'.'table' order by 'c1', 'c2'";
        
        String[] primaryKeys = { "c1", "c2" };

        ITableMetaData metaData = new DefaultTableMetaData(tableName, columns, primaryKeys);
        String sql = DatabaseDataSet.getSelectStatement(schemaName, metaData, "'?'");
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
        String sql = DatabaseDataSet.getSelectStatement(schemaName, metaData, null);
        assertEquals("select statement", expected, sql);
    }

    public void testGetQualifiedTableNames() throws Exception
    {
        String[] expectedNames = getExpectedNames();

        IDatabaseConnection connection = new DatabaseConnection(
                _connection.getConnection(), _connection.getSchema());
        connection.getConfig().setFeature(DatabaseConfig.FEATURE_QUALIFIED_TABLE_NAMES, true);

        IDataSet dataSet = connection.createDataSet();
        String[] actualNames = dataSet.getTableNames();

        assertEquals("name count", expectedNames.length, actualNames.length);
        for (int i = 0; i < actualNames.length; i++)
        {
            String expected = DataSetUtils.getQualifiedName(
                    _connection.getSchema(), expectedNames[i]);
            String actual = actualNames[i];
            assertEquals("name", expected, actual);
        }
    }

    public void testGetColumnsAndQualifiedNamesEnabled() throws Exception
    {
        String tableName = DataSetUtils.getQualifiedName(
                _connection.getSchema(), "TEST_TABLE");
        String[] expected = {"COLUMN0", "COLUMN1", "COLUMN2", "COLUMN3"};

        IDatabaseConnection connection = new DatabaseConnection(
                _connection.getConnection(), _connection.getSchema());
        connection.getConfig().setFeature(
                DatabaseConfig.FEATURE_QUALIFIED_TABLE_NAMES, true);

        ITableMetaData metaData = connection.createDataSet().getTableMetaData(tableName);
        Column[] columns = metaData.getColumns();

        assertEquals("column count", expected.length, columns.length);
        for (int i = 0; i < columns.length; i++)
        {
            assertEquals("column name", expected[i], columns[i].getColumnName());
        }
    }

    public void testGetPrimaryKeysAndQualifiedNamesEnabled() throws Exception
    {
        String tableName = DataSetUtils.getQualifiedName(
                _connection.getSchema(), "PK_TABLE");
        String[] expected = {"PK0", "PK1", "PK2"};

        IDatabaseConnection connection = new DatabaseConnection(
                _connection.getConnection(), _connection.getSchema());
        connection.getConfig().setFeature(
                DatabaseConfig.FEATURE_QUALIFIED_TABLE_NAMES, true);

        ITableMetaData metaData = connection.createDataSet().getTableMetaData(tableName);
        Column[] columns = metaData.getPrimaryKeys();

        assertEquals("column count", expected.length, columns.length);
        for (int i = 0; i < columns.length; i++)
        {
            assertEquals("column name", expected[i], columns[i].getColumnName());
        }
    }

    public void testGetPrimaryKeysWithColumnFilters() throws Exception
    {
      
      // TODO (felipeal): I don't know if PK_TABLE is a standard JDBC name or if
      // it's HSQLDB specific. Anyway, now that HSQLDB's schema is set on property,
      // we cannot add it as prefix here....
      String tableName = "PK_TABLE";
//        String tableName = DataSetUtils.getQualifiedName(
//                _connection.getSchema(), "PK_TABLE");
       
        String[] expected = {"PK0", "PK2"};

        DefaultColumnFilter filter = new DefaultColumnFilter();
        filter.includeColumn("PK0");
        filter.includeColumn("PK2");

        IDatabaseConnection connection = new DatabaseConnection(
                _connection.getConnection(), _connection.getSchema());
        connection.getConfig().setProperty(
                DatabaseConfig.PROPERTY_PRIMARY_KEY_FILTER, filter);

        ITableMetaData metaData = connection.createDataSet().getTableMetaData(tableName);
        Column[] columns = metaData.getPrimaryKeys();

        assertEquals("column count", expected.length, columns.length);
        for (int i = 0; i < columns.length; i++)
        {
            assertEquals("column name", expected[i], columns[i].getColumnName());
        }
    }

//    public void testGetTableNamesAndCaseSensitive() throws Exception
//    {
//        DatabaseMetaData metaData = _connection.getConnection().getMetaData();
//        metaData.
//    }

    public void testGetDuplicateTable() throws Exception
    {
        // Cannot test! Unsupported feature.
    }

    public void testGetDuplicateTableMetaData() throws Exception
    {
        // Cannot test! Unsupported feature.
    }

    public void testGetDuplicateTableNames() throws Exception
    {
        // Cannot test! Unsupported feature.
    }

    public void testGetDuplicateTables() throws Exception
    {
        // Cannot test! Unsupported feature.
    }

    public void testGetCaseInsensitiveDuplicateTable() throws Exception
    {
        // Cannot test! Unsupported feature.
    }

    public void testGetCaseInsensitiveDuplicateTableMetaData() throws Exception
    {
        // Cannot test! Unsupported feature.
    }

    public void testReverseIteratorAndDuplicateTable() throws Exception
    {
        // Cannot test! Unsupported feature.
    }

    public void testIteratorAndDuplicateTable() throws Exception
    {
        // Cannot test! Unsupported feature.
    }
}












