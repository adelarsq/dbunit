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

import java.util.ArrayList;
import java.util.List;

import org.dbunit.AbstractDatabaseTest;
import org.dbunit.DatabaseEnvironment;
import org.dbunit.DatabaseUnitRuntimeException;
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
 
    public void testGetDataTypeFactory_InvalidDataTypeFactory() throws Exception
    {
        String tableName = "TEST_TABLE";
        Object nonDataTypeFactoryObject = new Object();
        this._connection.getConfig().setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, nonDataTypeFactoryObject);
        DatabaseTableMetaData metadata = new DatabaseTableMetaData(tableName, this._connection);
        try
        {
            metadata.getDataTypeFactory(_connection);
            fail("Should not obtain datatype factory from object that does not implement corresponding interface");
        }
        catch(DatabaseUnitRuntimeException expected)
        {
            String expectedMsg = "Invalid datatype factory configured. Class 'class java.lang.Object' " +
            		"does not implement 'interface org.dbunit.dataset.datatype.IDataTypeFactory'.";
            assertEquals(expectedMsg, expected.getMessage());
        }
    }

    public void testGetDataTypeFactory_InvalidDataTypeFactoryAsString() throws Exception
    {
        String tableName = "TEST_TABLE";
        this._connection.getConfig().setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, "org.dbunit.ext.oracle.OracleDataTypeFactory");
        DatabaseTableMetaData metadata = new DatabaseTableMetaData(tableName, this._connection);
        try
        {
            metadata.getDataTypeFactory(_connection);
            fail("Should not obtain datatype factory from object that does not implement corresponding interface");
        }
        catch(DatabaseUnitRuntimeException expected)
        {
            String expectedMsg = "Invalid datatype factory configured. Class 'class java.lang.String' " +
                    "does not implement 'interface org.dbunit.dataset.datatype.IDataTypeFactory'." +
                    " Ensure not to specify the fully qualified class name as String but the concrete " +
                    "instance of the datatype factory (for example 'new OracleDataTypeFactory()').";
            assertEquals(expectedMsg, expected.getMessage());
        }
    }


}







