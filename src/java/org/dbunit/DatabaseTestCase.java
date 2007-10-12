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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import junit.framework.TestCase;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.operation.DatabaseOperation;

/**
 * @author Manuel Laflamme
 * @version $Revision$
 * @since Feb 17, 2002
 * TODO: mark it as deprecated use #{DBTestCase} instead.
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
     * Returns the test database connection.
     */
    protected abstract IDatabaseConnection getConnection() throws Exception;

    /**
     * Returns the test dataset.
     */
    protected abstract IDataSet getDataSet() throws Exception;

    /**
     * Creates a IDatabaseTester for this testCase.<br>
     *
     * A {@link DefaultDatabaseTester} is used by default.
     * @throws Exception
     */
    protected IDatabaseTester newDatabaseTester() throws Exception{
        logger.debug("newDatabaseTester() - start");

      final IDatabaseConnection connection = getConnection();
      final IDatabaseTester tester = new DefaultDatabaseTester(connection);
      return tester;
    }

    /**
     * Gets the IDatabaseTester for this testCase.<br>
     * If the IDatabaseTester is not set yet, this method calls
     * newDatabaseTester() to obtain a new instance.
     * @throws Exception
     */
    protected IDatabaseTester getDatabaseTester() throws Exception {
        logger.debug("getDatabaseTester() - start");

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
        logger.debug("closeConnection(connection=" + connection + ") - start");

        assertNotNull( "DatabaseTester is not set", getDatabaseTester() );
        getDatabaseTester().closeConnection( connection );
    }

    /**
     * Returns the database operation executed in test setup.
     */
    protected DatabaseOperation getSetUpOperation() throws Exception
    {
        logger.debug("getSetUpOperation() - start");

        return DatabaseOperation.CLEAN_INSERT;
    }

    /**
     * Returns the database operation executed in test cleanup.
     */
    protected DatabaseOperation getTearDownOperation() throws Exception
    {
        logger.debug("getTearDownOperation() - start");

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
        super.tearDown();
      }
    }
}





