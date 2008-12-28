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

import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.operation.DatabaseOperation;

import junit.framework.TestCase;

/**
 * @author gommma
 * @author Last changed by: $Author: gommma $
 * @version $Revision: 789 $ $Date: 2008-08-15 16:45:18 +0200 (Fr, 15. Aug 2008) $
 * @since 2.4.3
 */
public class DBTestCaseTest extends TestCase 
{

	/**
	 * Tests whether the user can simply change the {@link DatabaseConfig} by
	 * overriding the method {@link DatabaseTestCase#setUpDatabaseConfig(DatabaseConfig)}.
	 * @throws Exception
	 */
	public void testConfigureConnection() throws Exception
	{
	    DatabaseEnvironment dbEnv = DatabaseEnvironment.getInstance();
	    final IDatabaseConnection conn = dbEnv.getConnection();
	    final DefaultDatabaseTester tester = new DefaultDatabaseTester(conn);
	    
	    DBTestCase testSubject = new DBTestCase() {
	        
            /**
             * method under test
             */
            protected void setUpDatabaseConfig(DatabaseConfig config) {
                config.setProperty(DatabaseConfig.PROPERTY_BATCH_SIZE, new Integer(97));
                config.setFeature(DatabaseConfig.FEATURE_BATCHED_STATEMENTS, true);
            }

            protected IDatabaseTester newDatabaseTester() throws Exception {
                return tester;
            }

            protected DatabaseOperation getSetUpOperation() throws Exception {
                return DatabaseOperation.NONE;
            }

            protected DatabaseOperation getTearDownOperation() throws Exception {
                return DatabaseOperation.NONE;
            }

            protected IDataSet getDataSet() throws Exception {
                return null;
            }
        };
        
        // Simulate JUnit which first of all calls the "setUp" method
        testSubject.setUp();
        
        IDatabaseConnection actualConn = testSubject.getConnection();
        assertEquals(new Integer(97), actualConn.getConfig().getProperty(DatabaseConfig.PROPERTY_BATCH_SIZE));
        assertSame(conn, actualConn);
        
        IDatabaseConnection actualConn2 = testSubject.getDatabaseTester().getConnection();
        assertEquals(new Integer(97), actualConn2.getConfig().getProperty(DatabaseConfig.PROPERTY_BATCH_SIZE));
        assertSame(tester, testSubject.getDatabaseTester());
        assertSame(conn, testSubject.getDatabaseTester().getConnection());
	}
}
