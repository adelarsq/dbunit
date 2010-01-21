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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.dbunit.AbstractDatabaseIT;
import org.dbunit.DatabaseProfile;
import org.dbunit.IDatabaseTester;

/**
 * @author Manuel Laflamme
 * @version $Revision$
 * @since Mar 26, 2002
 */
public abstract class AbstractDatabaseConnectionIT extends AbstractDatabaseIT
{
	private String schema;
	private DatabaseProfile profile;
	
	public AbstractDatabaseConnectionIT(String s)
    {
        super(s);
    }

    
	protected void setUp() throws Exception {
		super.setUp();
    	this.profile = super.getEnvironment().getProfile();
		this.schema = this.profile.getSchema();
	}


	public final void testGetRowCount() throws Exception
    {
        assertEquals("EMPTY_TABLE", 0, _connection.getRowCount("EMPTY_TABLE", null));
        assertEquals("EMPTY_TABLE", 0, _connection.getRowCount("EMPTY_TABLE"));

        assertEquals("TEST_TABLE", 6, _connection.getRowCount("TEST_TABLE", null));
        assertEquals("TEST_TABLE", 6, _connection.getRowCount("TEST_TABLE"));

        assertEquals("PK_TABLE", 1, _connection.getRowCount("PK_TABLE", "where PK0 = 0"));
    }

    public final void testGetRowCount_NonexistingSchema() throws Exception
    {
    	DatabaseProfile profile = super.getEnvironment().getProfile();
    	String nonexistingSchema = profile.getSchema() + "_444_XYZ_TEST";
    	this.schema = nonexistingSchema;

    	IDatabaseTester dbTester = this.newDatabaseTester(nonexistingSchema);
    	try {
			IDatabaseConnection dbConnection = dbTester.getConnection();
			
			assertEquals(convertString(nonexistingSchema), dbConnection.getSchema());
			try {
				dbConnection.getRowCount("TEST_TABLE");
				fail("Should not be able to retrieve row count for non-existing schema " + nonexistingSchema);
			}
			catch(SQLException expected)
			{
				// All right
			}
    	}
    	finally {
    		// Reset the testers schema for subsequent tests (environment.dbTester is a singleton)
    		dbTester.setSchema(profile.getSchema());    		
    	}
    }

    public final void testGetRowCount_NoSchemaSpecified() throws Exception
    {
    	DatabaseProfile profile = super.getEnvironment().getProfile();
    	this.schema = null;
    	IDatabaseTester dbTester = this.newDatabaseTester(this.schema);
    	try {
			IDatabaseConnection dbConnection = dbTester.getConnection();
			
			assertEquals(null, dbConnection.getSchema());
	        assertEquals("TEST_TABLE", 6, _connection.getRowCount("TEST_TABLE", null));
    	}
    	finally {
    		// Reset the testers schema for subsequent tests (environment.dbTester is a singleton)
    		dbTester.setSchema(profile.getSchema());    		
    	}
    }

    
    private IDatabaseTester newDatabaseTester(String schema) throws Exception {
    	IDatabaseTester tester = super.newDatabaseTester();
    	tester.setSchema(schema);
    	return tester;
	}


	protected IDatabaseConnection getConnection() throws Exception {
        String name = profile.getDriverClass();
        Class.forName(name);
        Connection connection = DriverManager.getConnection(
                profile.getConnectionUrl(), profile.getUser(),
                profile.getPassword());
        _connection = new DatabaseConnection(connection,
                profile.getSchema());
		
        IDatabaseConnection dbunitConnection = new DatabaseConnection(connection,
                this.schema);
        return dbunitConnection;
	}
    
    

}


