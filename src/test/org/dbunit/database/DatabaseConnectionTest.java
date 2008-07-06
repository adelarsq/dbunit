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

import org.dbunit.DatabaseUnitException;


/**
 * @author Manuel Laflamme
 * @version $Revision$
 * @since Mar 26, 2002
 */
public class DatabaseConnectionTest extends AbstractDatabaseConnectionTest
{
    public DatabaseConnectionTest(String s)
    {
        super(s);
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
        IDatabaseConnection validConnection = super.getConnection();
        String schema = "XYZ_INVALID_SCHEMA_1642344539";
        // Try to create a database connection with an invalid schema
        try
        {
        	boolean validate = true;
            new DatabaseConnection(validConnection.getConnection(), schema, validate);
            fail("Should not be able to create a database connection object with an unknown schema.");
        }
        catch(DatabaseUnitException expected)
        {
            String expectedMsg = "The given schema 'XYZ_INVALID_SCHEMA_1642344539' does not exist.";
            assertEquals(expectedMsg, expected.getMessage());
        }
    }
    
    public void testCreateConnectionWithNonExistingSchemaAndLenientValidation() throws Exception
    {
        IDatabaseConnection validConnection = super.getConnection();
        String schema = "XYZ_INVALID_SCHEMA_1642344539";
        // Try to create a database connection with an invalid schema
    	boolean validate = false;
        DatabaseConnection dbConnection = new DatabaseConnection(validConnection.getConnection(), schema, validate);
        assertNotNull(dbConnection);
    }

}


