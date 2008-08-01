/*
 *
 * The DbUnit Database Testing Framework
 * Copyright (C)2002-2008, DbUnit.org
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
package org.dbunit;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;
import junitx.framework.StringAssert;

import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.database.MockDatabaseConnection;
import org.dbunit.dataset.IDataSet;

/**
 * @author gommma
 * @author Last changed by: $Author$
 * @version $Revision$ $Date$
 * @since 2.3.0
 */
public class DatabaseTestCaseTest extends TestCase
{

	public void testSetUpConnectionConfig_HappyPath() throws Exception
	{
		final Object dummyDataTypeFactory = new String("DUMMY_DATA_TYPE_FACTORY");
		
		TestSubject dbTestCase = new TestSubject() {
			protected void setUpConnectionConfig(DatabaseConfig databaseConfig) 
			{
				databaseConfig.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, dummyDataTypeFactory);
			}
		};
		
		// The setUpConnectionConfig is invoked when the "getConnection" method is called
		DatabaseConfig configOfConnection = dbTestCase.getConnection().getConfig();
		Object actualDataTypeFactory = configOfConnection.getProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY);
		// Our specific dummy dataTypeFactory should be set
		assertEquals(dummyDataTypeFactory, actualDataTypeFactory);
	}
	
	public void testGetConnection_ConnectionIsNull() throws Exception
	{
		// Create test case with "null" connection
		TestSubject dbTestCase = new TestSubject(null);
		
		try {
			dbTestCase.getConnection();
			fail("Should not be able to get connection from dbTestCase that has 'null' connection");
		}
		catch(AssertionFailedError expected) {
			String expectedMsgStart = "IDatabaseTester.getConnection() must not return null: ";
			String actualMsg = expected.getMessage();
			StringAssert.assertStartsWith(expectedMsgStart, actualMsg);
		}
	}

	public void testGetConnection_TesterIsNull() throws Exception
	{
		// Create test case with "null" databaseTester
		TestSubject dbTestCase = new TestSubject(null) {
			protected IDatabaseTester newDatabaseTester() throws Exception {
				return null;
			}			
		};
		
		try {
			dbTestCase.getConnection();
			fail("Should not be able to get connection from dbTestCase that has 'null' IDatabaseTester");
		}
		catch(AssertionFailedError expected) {
			String expectedMsgStart = "DatabaseTester is not set";
			String actualMsg = expected.getMessage();
			StringAssert.assertStartsWith(expectedMsgStart, actualMsg);
		}
	}
	
	
	/**
	 * Utility implementation of database test case
	 * @author gommma
	 * @author Last changed by: $Author$
	 * @version $Revision$ $Date$
	 * @since 2.3.0
	 */
	private static class TestSubject extends DatabaseTestCase
	{
		private IDatabaseConnection connection;
		
		public TestSubject() {
			this.connection = new MockDatabaseConnection();			
		}
		
		public TestSubject(IDatabaseConnection connection) {
			this.connection = connection;
		}
		
		protected IDataSet getDataSet() throws Exception {
			// TODO Auto-generated method stub
			return null;
		}

		protected IDatabaseTester newDatabaseTester() throws Exception {
			return new DefaultDatabaseTester(this.connection);
		}

	}
}
