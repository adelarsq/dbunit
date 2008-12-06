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
import java.util.ArrayList;
import java.util.List;

import org.dbunit.AbstractDatabaseTest;
import org.dbunit.DatabaseEnvironment;
import org.dbunit.HypersonicEnvironment;
import org.dbunit.TestFeature;
import org.dbunit.dataset.Column;
import org.dbunit.dataset.Columns;
import org.dbunit.dataset.IDataSet;
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
    public DatabaseTableMetaDataTest(String s)
    {
        super(s);
    }

    protected IDataSet createDataSet() throws Exception
    {
        return _connection.createDataSet();
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
        	String msg = "Did not find table 'UNKNOWN_TABLE' in schema '" + schema + "'";
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
            assertEquals("name", (String)expectedNames.get(i), column.getColumnName());
            assertEquals("datatype", (DataType)expectedTypes.get(i), column.getDataType());
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
}







