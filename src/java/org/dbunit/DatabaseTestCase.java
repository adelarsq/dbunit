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

package org.dbunit;

import junit.framework.TestCase;

import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Manuel Laflamme
 * @version $Revision$
 * @since Feb 17, 2002
 * 
 * @see DBTestCase
 */
public abstract class DatabaseTestCase extends TestCase
{

    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(DatabaseTestCase.class);

    private IDatabaseTester tester;

    public DatabaseTestCase()
    {
    }

    public DatabaseTestCase(String name)
    {
        super(name);
    }

    /**
     * Returns the test database connection. It is retrieved from the 
     * configured database tester via {@link IDatabaseTester#getConnection()}.
     * <p>
     * Note that this method was <i>abstract</i> until dbunit 2.2. Since dbunit 2.3 it
     * is declared <i>final</i> and implemented as described above. 
     * </p>
     * @return The test database connection
     * @throws Exception
     */
    protected final IDatabaseConnection getConnection() throws Exception
    {
        logger.debug("getConnection() - start");

        final IDatabaseTester databaseTester = getDatabaseTester();
        assertNotNull("DatabaseTester is not set", databaseTester);
        IDatabaseConnection databaseConnection = databaseTester.getConnection();
        assertNotNull("IDatabaseTester.getConnection() must not return null: " + databaseTester, databaseConnection);
        setUpConnectionConfig(databaseConnection.getConfig());
        return databaseConnection;
    }

    /**
     * Method to initialize the configuration of a {@link IDatabaseConnection}'s configuration.
     * <p>
     * This method is designed to be overridden and is implemented empty here.
     * For example you can set a data type factory on the configuration as follows:
     * <code>
     * databaseConfig.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new OracleDataTypeFactory());
     * </code>
     * </p>
     * @param databaseConfig The configuration of the current database connection 
     * that has been created for the current test execution
     * @since 2.3
     */
    protected void setUpConnectionConfig(DatabaseConfig databaseConfig) {
    	// empty implementation.
	}

	/**
     * Returns the test dataset.
     */
    protected abstract IDataSet getDataSet() throws Exception;

    /**
     * Creates a IDatabaseTester for this testCase.<br>
     * <p>
     * Note that this method was implemented here until dbunit 2.2 using the
     * {@link DefaultDatabaseTester}. Since dbunit 2.3 it
     * is declared <i>abstract</i> and must be implemented by subclasses. 
     * </p>
     * @throws Exception
     */
    protected abstract IDatabaseTester newDatabaseTester() throws Exception;

    /**
     * Gets the IDatabaseTester for this testCase.<br>
     * If the IDatabaseTester is not set yet, this method calls
     * newDatabaseTester() to obtain a new instance.
     * @throws Exception
     */
    protected IDatabaseTester getDatabaseTester() throws Exception {
    	if ( this.tester == null ) {
    		this.tester = newDatabaseTester();
    	}
    	return this.tester;
    }

    /**
     * Close the specified connection. Override this method of you want to
     * keep your connection alive between tests.
     */
    protected void closeConnection(IDatabaseConnection connection) throws Exception
    {
        logger.debug("closeConnection(connection={}) - start", connection);

        assertNotNull( "DatabaseTester is not set", getDatabaseTester() );
        getDatabaseTester().closeConnection( connection );
    }

    /**
     * Returns the database operation executed in test setup.
     */
    protected DatabaseOperation getSetUpOperation() throws Exception
    {
        return DatabaseOperation.CLEAN_INSERT;
    }

    /**
     * Returns the database operation executed in test cleanup.
     */
    protected DatabaseOperation getTearDownOperation() throws Exception
    {
        return DatabaseOperation.NONE;
    }

    ////////////////////////////////////////////////////////////////////////////
    // TestCase class

    protected void setUp() throws Exception
    {
    	logger.debug("setUp() - start");

    	super.setUp();
    	final IDatabaseTester databaseTester = getDatabaseTester();
    	assertNotNull( "DatabaseTester is not set", databaseTester );
    	databaseTester.setSetUpOperation( getSetUpOperation() );
    	databaseTester.setDataSet( getDataSet() );
    	databaseTester.onSetup();
    }

    protected void tearDown() throws Exception
    {
    	logger.debug("tearDown() - start");

    	try {
    		final IDatabaseTester databaseTester = getDatabaseTester();
    		assertNotNull( "DatabaseTester is not set", databaseTester );
    		databaseTester.setTearDownOperation( getTearDownOperation() );
    		databaseTester.setDataSet( getDataSet() );
    		databaseTester.onTearDown();
    	} finally {
    		tester = null;
    		super.tearDown();
    	}
    }
}
