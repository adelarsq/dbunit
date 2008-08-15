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

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.SortedTable;
import org.dbunit.operation.DatabaseOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Manuel Laflamme
 * @version $Revision$
 * @since Feb 18, 2002
 */
public abstract class AbstractDatabaseTest extends DatabaseTestCase
{
    protected IDatabaseConnection _connection;
    
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    public AbstractDatabaseTest(String s)
    {
        super(s);
    }

    protected DatabaseEnvironment getEnvironment() throws Exception
    {
        return DatabaseEnvironment.getInstance();
    }

    protected ITable createOrderedTable(String tableName, String orderByColumn)
            throws Exception
    {
        return new SortedTable(_connection.createDataSet().getTable(tableName),
                new String[]{orderByColumn});
//        String sql = "select * from " + tableName + " order by " + orderByColumn;
//        return _connection.createQueryTable(tableName, sql);
    }

    ////////////////////////////////////////////////////////////////////////////
    // TestCase class

    protected void setUp() throws Exception
    {
        super.setUp();

        _connection = getDatabaseTester().getConnection();
    }

    protected IDatabaseTester getDatabaseTester() throws Exception
    {
       try{
          return getEnvironment().getDatabaseTester();
       }
       catch( Exception e ){
          logger.error("getDatabaseTester()", e );
          // empty
       }
       return super.getDatabaseTester();
    }

    protected void tearDown() throws Exception
    {
        super.tearDown();

        DatabaseOperation.DELETE_ALL.execute(_connection, _connection.createDataSet());

        _connection = null;
    }

    ////////////////////////////////////////////////////////////////////////////
    // DatabaseTestCase class

    protected IDatabaseConnection getConnection() throws Exception
    {
        IDatabaseConnection connection = getEnvironment().getConnection();
        return connection;

//        return new DatabaseEnvironment(getEnvironment().getProfile()).getConnection();
//        return new DatabaseConnection(connection.getConnection(), connection.getSchema());
    }

    protected IDataSet getDataSet() throws Exception
    {
        return getEnvironment().getInitDataSet();
    }

    protected void closeConnection(IDatabaseConnection connection) throws Exception
    {
//        getEnvironment().closeConnection();
    }
//
//    protected DatabaseOperation getTearDownOperation() throws Exception
//    {
//        return DatabaseOperation.DELETE_ALL;
//    }

    /**
     * This method is used so sub-classes can disable the tests according to 
     * some characteristics of the environment
     * @param testName name of the test to be checked
     * @return flag indicating if the test should be executed or not
     */
    protected boolean runTest(String testName) {
      return true;
    }

    protected void runTest() throws Throwable {
      if ( runTest(getName()) ) {
        super.runTest();
      } else { 
        if ( logger.isDebugEnabled() ) {
          logger.debug( "Skipping test " + getClass().getName() + "." + getName() );
        }
      }
    }
    
    public static boolean environmentHasFeature(TestFeature feature) {
      try {
        final DatabaseEnvironment environment = DatabaseEnvironment.getInstance();
        final boolean runIt = environment.support(feature);
        return runIt;
      } catch ( Exception e ) {
        throw new DatabaseUnitRuntimeException(e);
      }
    }
    
}






