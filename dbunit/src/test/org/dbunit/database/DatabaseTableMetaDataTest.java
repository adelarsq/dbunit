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

import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.dbunit.AbstractDatabaseTest;
import org.dbunit.DatabaseEnvironment;
import org.dbunit.HypersonicEnvironment;
import org.dbunit.TestFeature;
import org.dbunit.dataset.Column;
import org.dbunit.dataset.Columns;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.ITableMetaData;
import org.dbunit.dataset.NoSuchTableException;
import org.dbunit.dataset.datatype.DataType;
import org.dbunit.dataset.datatype.DataTypeException;
import org.dbunit.dataset.datatype.DefaultDataTypeFactory;
import org.dbunit.dataset.datatype.IDataTypeFactory;

/**
 * @author Manuel Laflamme
 * @version $Revision$
 * @since Mar 14, 2002
 */
public class DatabaseTableMetaDataTest extends AbstractDatabaseTest
{
    
    public static final String TEST_TABLE = "TEST_TABLE";
    
    public DatabaseTableMetaDataTest(String s)
    {
        super(s);
    }

    protected IDataSet createDataSet() throws Exception
    {
        return _connection.createDataSet();
    }

    protected String convertString(String str) throws Exception
    {
        return DatabaseEnvironment.getInstance().convertString(str);
    }

    public void testGetPrimaryKeys() throws Exception
    {
        String tableName = "PK_TABLE";
//        String[] expected = {"PK0"};
        String[] expected = {"PK0", "PK1", "PK2"};

        ITableMetaData metaData = createDataSet().getTableMetaData(tableName);
        Column[] columns = metaData.getPrimaryKeys();

        assertEquals("pk count", expected.length, columns.length);
        for (int i = 0; i < columns.length; i++)
        {
            Column column = columns[i];
            assertEquals("name", convertString(expected[i]), column.getColumnName());
        }
    }

    public void testGetNoPrimaryKeys() throws Exception
    {
        String tableName = TEST_TABLE;

        ITableMetaData metaData = createDataSet().getTableMetaData(tableName);
        Column[] columns = metaData.getPrimaryKeys();

        assertEquals("pk count", 0, columns.length);
    }

    
    
    
    public void testCreation_UnknownTable() throws Exception
    {
        String tableName = "UNKNOWN_TABLE";
        IDatabaseConnection connection = getConnection();
        String schema = connection.getSchema();
        try
        {
        	new DatabaseTableMetaData(tableName, getConnection());
        	fail("Should not be able to create a DatabaseTableMetaData for an unknown table");
        }
        catch (NoSuchTableException expected)
        {
        	String msg = "Did not find table '" + convertString("UNKNOWN_TABLE") + "' in schema '" + schema + "'";
        	assertEquals(msg, expected.getMessage());
        }
    }

    public void testGetNoColumns() throws Exception
    {
    	// Since the "unknown_table" does not exist it also does not have any columns
        String tableName = "UNKNOWN_TABLE";
        boolean validate = false;

        ITableMetaData metaData = new DatabaseTableMetaData(tableName,
                getConnection(), validate);
        
        Column[] columns = metaData.getColumns();
        assertEquals(0, columns.length);
    }

    public void testColumnIsNullable() throws Exception
    {
        String tableName = "PK_TABLE";
        String[] notNullable = {"PK0", "PK1", "PK2"};
        String[] nullable = {"NORMAL0", "NORMAL1"};

        ITableMetaData metaData = createDataSet().getTableMetaData(tableName);
        Column[] columns = metaData.getColumns();

        assertEquals("column count", nullable.length + notNullable.length,
                columns.length);

        // not nullable
        for (int i = 0; i < notNullable.length; i++)
        {
            Column column = Columns.getColumn(notNullable[i], columns);
            assertEquals(notNullable[i], Column.NO_NULLS, column.getNullable());
        }

        // nullable
        for (int i = 0; i < nullable.length; i++)
        {
            Column column = Columns.getColumn(nullable[i], columns);
            assertEquals(nullable[i], Column.NULLABLE, column.getNullable());
        }
    }

    public void testUnsupportedColumnDataType() throws Exception
    {
        IDataTypeFactory dataTypeFactory = new DefaultDataTypeFactory() {
			public DataType createDataType(int sqlType, String sqlTypeName,
					String tableName, String columnName)
					throws DataTypeException {
				return DataType.UNKNOWN;
			}
        };
        this._connection.getConfig().setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, dataTypeFactory);
    	
        String tableName = "EMPTY_MULTITYPE_TABLE";
        ITableMetaData metaData = createDataSet().getTableMetaData(tableName);
        Column[] columns = metaData.getColumns();
        // No columns recognized -> should not provide any columns here
        assertEquals("Should be an empty column array", 0, columns.length);
    }
    
    public void testColumnDataType() throws Exception
    {
    	String tableName = "EMPTY_MULTITYPE_TABLE";

        List expectedNames = new ArrayList();
        expectedNames.add("VARCHAR_COL");
        expectedNames.add("NUMERIC_COL");
        expectedNames.add("TIMESTAMP_COL");

        List expectedTypes = new ArrayList();
        expectedTypes.add(DataType.VARCHAR);
        expectedTypes.add(DataType.NUMERIC);
        expectedTypes.add(DataType.TIMESTAMP);

    	DatabaseEnvironment environment = DatabaseEnvironment.getInstance();
        if (environment.support(TestFeature.VARBINARY)) {
            expectedNames.add("VARBINARY_COL");
            expectedTypes.add(DataType.VARBINARY);
        }

        // Check correct setup
        assertEquals("expected columns", expectedNames.size(), expectedTypes.size());

        ITableMetaData metaData = createDataSet().getTableMetaData(tableName);
        Column[] columns = metaData.getColumns();

        assertEquals("column count", 4, columns.length);

        for (int i = 0; i < expectedNames.size(); i++)
        {
            Column column = columns[i];
            assertEquals("name", convertString((String)expectedNames.get(i)), column.getColumnName());
            if (expectedTypes.get(i).equals(DataType.NUMERIC))
            {
                // 2009-10-10 TODO John Hurst: hack for Oracle, returns java.sql.Types.DECIMAL for this column
                assertTrue("Expected numeric datatype, got [" + column.getDataType() + "]",
                        column.getDataType().equals(DataType.NUMERIC) ||
                        column.getDataType().equals(DataType.DECIMAL)
                );
            }
            else if (expectedTypes.get(i).equals(DataType.TIMESTAMP) && column.getDataType().equals(DataType.DATE))
            {
                // 2009-10-22 TODO John Hurst: hack for Postgresql, returns DATE for TIMESTAMP.
                // Need to move DataType comparison to DatabaseEnvironment.
                assertTrue(true);
            }
            else if (expectedTypes.get(i).equals(DataType.VARBINARY) && column.getDataType().equals(DataType.VARCHAR))
            {
                // 2009-10-22 TODO John Hurst: hack for Postgresql, returns VARCHAR for VARBINARY.
                // Need to move DataType comparison to DatabaseEnvironment.
                assertTrue(true);
            }
            else
            {
                assertEquals("datatype", (DataType)expectedTypes.get(i), column.getDataType());
            }
        }
    }
 
    /**
     * Tests whether dbunit works correctly when the local machine has a specific locale set while having
     * case sensitivity=false (so that the "toUpperCase()" is internally invoked on table names)
     * @throws Exception
     */
    public void testCaseInsensitiveAndI18n() throws Exception
    {
        // To test bug report #1537894 where the user has a turkish locale set on his box
        
        // Change the locale for this test
        Locale oldLocale = Locale.getDefault();
        // Set the locale to turkish where "i".toUpperCase() produces an "\u0131" ("I" with dot above) which is not equal to "I". 
        Locale.setDefault(new Locale("tr", "TR"));
        
        try {
            // Use the "EMPTY_MULTITYPE_TABLE" because it has an "I" in the name.
            // Use as input a completely lower-case string so that the internal "toUpperCase()" has effect
            String tableName = "empty_multitype_table";

            IDataSet dataSet = this._connection.createDataSet();
            ITable table = dataSet.getTable(tableName);
            // Should now find the table, regardless that we gave the tableName in lowerCase
            assertNotNull("Table '" + tableName + "' was not found", table);
        }
        finally {
            //Reset locale
            Locale.setDefault(oldLocale);
        }
    }


    /**
     * Tests the pattern-like column retrieval from the database. DbUnit
     * should not interpret any table names as regex patterns. 
     * @throws Exception
     */
    public void testGetColumnsForTablesMatchingSamePattern() throws Exception
    {
        Connection jdbcConnection = HypersonicEnvironment.createJdbcConnection("tempdb");
        HypersonicEnvironment.executeDdlFile(new File("src/sql/hypersonic_dataset_pattern_test.sql"),
                jdbcConnection);
        IDatabaseConnection connection = new DatabaseConnection(jdbcConnection);

        try {
            String tableName = "PATTERN_LIKE_TABLE_X_";
            String[] columnNames = {"VARCHAR_COL_XUNDERSCORE"};
    
            ITableMetaData metaData = connection.createDataSet().getTableMetaData(tableName);
            Column[] columns = metaData.getColumns();
    
            assertEquals("column count", columnNames.length, columns.length);
    
            for (int i = 0; i < columnNames.length; i++)
            {
                Column column = Columns.getColumn(columnNames[i], columns);
                assertEquals(columnNames[i], columnNames[i], column.getColumnName());
            }
        }
        finally {
            HypersonicEnvironment.shutdown(jdbcConnection);
            jdbcConnection.close();
            HypersonicEnvironment.deleteFiles("tempdb");
        }
    }

    public void testCaseSensitive() throws Exception
    {
        Connection jdbcConnection = HypersonicEnvironment.createJdbcConnection("tempdb");
        HypersonicEnvironment.executeDdlFile(new File("src/sql/hypersonic_case_sensitive_test.sql"),
                jdbcConnection);
        IDatabaseConnection connection = new DatabaseConnection(jdbcConnection);

        try {
            String tableName = "MixedCaseTable";
            String tableNameWrongCase = "MIXEDCASETABLE";
            boolean validate = true;
            boolean caseSensitive = true;

            ITableMetaData metaData = new DatabaseTableMetaData(tableName,
                    connection, validate, caseSensitive);
            Column[] columns = metaData.getColumns();
            assertEquals(1, columns.length);
            assertEquals("COL1", columns[0].getColumnName());
            
            // Now test with same table name but wrong case
            try {
                ITableMetaData metaDataWrongCase = new DatabaseTableMetaData(tableNameWrongCase,
                        connection, validate, caseSensitive);
                fail("Should not be able to create DatabaseTableMetaData with non-existing table name " + tableNameWrongCase + 
                        ". Created "+ metaDataWrongCase);
            }
            catch(NoSuchTableException expected){
                assertTrue(expected.getMessage().indexOf(tableNameWrongCase) != -1);
            }
        }
        finally {
            HypersonicEnvironment.shutdown(jdbcConnection);
            jdbcConnection.close();
            HypersonicEnvironment.deleteFiles("tempdb");
        }
    }
    
    /**
     * Ensure that the same table name is returned by {@link DatabaseTableMetaData#getTableName()}
     * as the specified by the input parameter.
     * @throws Exception
     */
    public void testFullyQualifiedTableName() throws Exception
    {
        DatabaseEnvironment environment = DatabaseEnvironment.getInstance();
        String schema = environment.getProfile().getSchema();
        
        assertNotNull("Precondition: db environment 'schema' must not be null", schema);
//        Connection jdbcConn = _connection.getConnection();
//        String schema = SQLHelper.getSchema(jdbcConn);
        DatabaseTableMetaData metaData = new DatabaseTableMetaData(schema + "." + TEST_TABLE, _connection);
        assertEquals(schema + "." + convertString(TEST_TABLE), metaData.getTableName());
    }
    
    public void testDbStoresUpperCaseTableNames() throws Exception
    {
        IDatabaseConnection connection = getConnection();
        DatabaseMetaData metaData = connection.getConnection().getMetaData();
        if(metaData.storesUpperCaseIdentifiers())
        {
            DatabaseTableMetaData dbTableMetaData = new DatabaseTableMetaData(TEST_TABLE.toLowerCase(Locale.ENGLISH), _connection);
            // Table name should have been "toUpperCase'd"
            assertEquals(TEST_TABLE.toUpperCase(Locale.ENGLISH), dbTableMetaData.getTableName());
        }
        else
        {
            // skip the test
            assertTrue(true);
        }
    }

    public void testDbStoresLowerCaseTableNames() throws Exception
    {
        IDatabaseConnection connection = getConnection();
        DatabaseMetaData metaData = connection.getConnection().getMetaData();
        if(metaData.storesLowerCaseIdentifiers())
        {
            DatabaseTableMetaData dbTableMetaData = new DatabaseTableMetaData(TEST_TABLE.toUpperCase(Locale.ENGLISH), _connection);
            // Table name should have been "toUpperCase'd"
            assertEquals(TEST_TABLE.toLowerCase(Locale.ENGLISH), dbTableMetaData.getTableName());
        }
        else
        {
            // skip the test
            assertTrue(true);
        }
    }
}







