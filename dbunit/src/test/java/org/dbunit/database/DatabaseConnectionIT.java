/*
 * DatabaseConnectionTest.java   Mar 26, 2002
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

import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.util.Locale;

import org.dbunit.DatabaseEnvironment;
import org.dbunit.DatabaseUnitException;
import org.dbunit.dataset.ITable;


/**
 * @author Manuel Laflamme
 * @version $Revision$
 * @since Mar 26, 2002
 */
public class DatabaseConnectionIT extends AbstractDatabaseConnectionIT
{
    public DatabaseConnectionIT(String s)
    {
        super(s);
    }

    protected String convertString(String str) throws Exception
    {
        return getEnvironment().convertString(str);
    }


    public void testCreateNullConnection() throws Exception
    {
        try
        {
            new DatabaseConnection(null);
            fail("Should not be able to create a database connection without a JDBC connection");
        }
        catch(NullPointerException expected)
        {
            // all right
        }
    }

    public void testCreateConnectionWithNonExistingSchemaAndStrictValidation() throws Exception
    {
        DatabaseEnvironment environment = getEnvironment();
        String schema = environment.convertString("XYZ_INVALID_SCHEMA_1642344539");
        IDatabaseConnection validConnection = super.getConnection();
        // Try to create a database connection with an invalid schema
        try
        {
        	boolean validate = true;
            new DatabaseConnection(validConnection.getConnection(), schema, validate);
            fail("Should not be able to create a database connection object with an unknown schema.");
        }
        catch(DatabaseUnitException expected)
        {
            String expectedMsg = "The given schema '" + convertString(schema) + "' does not exist.";
            assertEquals(expectedMsg, expected.getMessage());
        }
    }
    
    public void testCreateConnectionWithNonExistingSchemaAndLenientValidation() throws Exception
    {
        DatabaseEnvironment environment = getEnvironment();
        String schema = environment.convertString("XYZ_INVALID_SCHEMA_1642344539");
        IDatabaseConnection validConnection = super.getConnection();
        // Try to create a database connection with an invalid schema
    	boolean validate = false;
        DatabaseConnection dbConnection = new DatabaseConnection(validConnection.getConnection(), schema, validate);
        assertNotNull(dbConnection);
    }

    
    public void testCreateConnectionWithSchemaDbStoresUpperCaseIdentifiers() throws Exception
    {
        IDatabaseConnection validConnection = super.getConnection();
        String schema = validConnection.getSchema();
        assertNotNull("Precondition: schema of connection must not be null", schema);
        
        
        DatabaseMetaData metaData = validConnection.getConnection().getMetaData();
        if(metaData.storesUpperCaseIdentifiers())
        {
            boolean validate = true;
            DatabaseConnection dbConnection = new DatabaseConnection(validConnection.getConnection(), schema.toLowerCase(Locale.ENGLISH), validate);
            assertNotNull(dbConnection);
            assertEquals(schema.toUpperCase(Locale.ENGLISH), dbConnection.getSchema());
        }
        else
        {
            // skip this test
            assertTrue(true);
        }
    }

    
    public void testCreateConnectionWithSchemaDbStoresLowerCaseIdentifiers() throws Exception
    {
        IDatabaseConnection validConnection = super.getConnection();
        String schema = validConnection.getSchema();
        assertNotNull("Precondition: schema of connection must not be null", schema);
        
        
        DatabaseMetaData metaData = validConnection.getConnection().getMetaData();
        if(metaData.storesLowerCaseIdentifiers())
        {
            boolean validate = true;
            DatabaseConnection dbConnection = new DatabaseConnection(validConnection.getConnection(), schema.toUpperCase(Locale.ENGLISH), validate);
            assertNotNull(dbConnection);
            assertEquals(schema.toLowerCase(Locale.ENGLISH), dbConnection.getSchema());
        }
        else
        {
            // skip this test
            assertTrue(true);
        }
    }

    public void testCreateQueryWithPreparedStatement() throws Exception
    {
        IDatabaseConnection connection = super.getConnection();
        PreparedStatement pstmt = connection.getConnection().prepareStatement("select * from TEST_TABLE where COLUMN0=?");

        try{
            pstmt.setString(1, "row 1 col 0");
            ITable table = connection.createTable("MY_TABLE", pstmt);
            assertEquals(1, table.getRowCount());
            assertEquals(4, table.getTableMetaData().getColumns().length);
            assertEquals("row 1 col 1", table.getValue(0, "COLUMN1"));
            
            // Now reuse the prepared statement
            pstmt.setString(1, "row 2 col 0");
            ITable table2 = connection.createTable("MY_TABLE", pstmt);
            assertEquals(1, table2.getRowCount());
            assertEquals(4, table2.getTableMetaData().getColumns().length);
            assertEquals("row 2 col 1", table2.getValue(0, "COLUMN1"));
        }
        finally{
            pstmt.close();
        }
    }

}


