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
import org.dbunit.dataset.AbstractDataSetTest;
import org.dbunit.dataset.Column;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.DefaultTableMetaData;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITableMetaData;
import org.dbunit.dataset.NoSuchTableException;
import org.dbunit.dataset.datatype.DataType;
import org.dbunit.dataset.filter.DefaultColumnFilter;
import org.dbunit.dataset.filter.ITableFilterSimple;
import org.dbunit.util.QualifiedTableName;


/**
 * @author Manuel Laflamme
 * @version $Revision$
 * @since Feb 18, 2002
 */
public class DatabaseDataSetIT extends AbstractDataSetTest
{
    private IDatabaseConnection _connection;

    public DatabaseDataSetIT(String s)
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


    protected String convertString(String str) throws Exception
    {
        return DatabaseEnvironment.getInstance().convertString(str);
    }

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
    
    protected IDataSet createMultipleCaseDuplicateDataSet() throws Exception 
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
            String expected = new QualifiedTableName(
            		expectedNames[i], _connection.getSchema()).getQualifiedName();
            String actual = actualNames[i];
            assertEquals("name", expected, actual);
        }
    }

    public void testGetColumnsAndQualifiedNamesEnabled() throws Exception
    {
        String tableName = new QualifiedTableName(
                "TEST_TABLE", _connection.getSchema()).getQualifiedName();
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
            assertEquals("column name", convertString(expected[i]), columns[i].getColumnName());
        }
    }

    public void testGetPrimaryKeysAndQualifiedNamesEnabled() throws Exception
    {
        String tableName = new QualifiedTableName(
                "PK_TABLE", _connection.getSchema()).getQualifiedName();
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
            assertEquals("column name", convertString(expected[i]), columns[i].getColumnName());
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
            assertEquals("column name", convertString(expected[i]), columns[i].getColumnName());
        }
    }

//    public void testGetTableNamesAndCaseSensitive() throws Exception
//    {
//        DatabaseMetaData metaData = _connection.getConnection().getMetaData();
//        metaData.
//    }

    public void testCreateDuplicateDataSet() throws Exception 
    {
        // Cannot test! Unsupported feature.
    }

    public void testCreateMultipleCaseDuplicateDataSet() throws Exception 
    {
        // Cannot test! Unsupported feature.
    }

    public void testGetTableThatIsFiltered() throws Exception
    {
        final String existingTableToFilter = convertString("TEST_TABLE");
        ITableFilterSimple tableFilter = new ITableFilterSimple(){
            public boolean accept(String tableName) throws DataSetException {
                if(tableName.equals(existingTableToFilter))
                    return false;
                return true;
            }
        };
        IDataSet dataSet = new DatabaseDataSet(_connection, false, tableFilter);
        try
        {
            dataSet.getTable(existingTableToFilter);
            fail("Should not be able to retrieve table from dataset that has not been loaded - expected an exception");
        }
        catch(NoSuchTableException expected)
        {
            assertEquals(existingTableToFilter, expected.getMessage());
        }
    }

}












