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
import org.dbunit.operation.DatabaseOperation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Basic implementation of IDatabaseTester.<br>
 * Implementations of IDatabaseTester may use this class as a starting point.
 *
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public abstract class AbstractDatabaseTester implements IDatabaseTester
{

   /**
    * Logger for this class
    */
   private static final Logger logger = LoggerFactory.getLogger(AbstractDatabaseTester.class);

   private IDataSet dataSet;
   private String schema;
   private DatabaseOperation setUpOperation = DatabaseOperation.CLEAN_INSERT;
   private DatabaseOperation tearDownOperation = DatabaseOperation.NONE;

   public AbstractDatabaseTester()
   {
      super();
   }

   public void closeConnection( IDatabaseConnection connection ) throws Exception
   {
        logger.debug("closeConnection(connection={}) - start",connection);

      connection.close();
   }

   public IDataSet getDataSet()
   {
        logger.debug("getDataSet() - start");

      return dataSet;
   }

   public void onSetup() throws Exception
   {
        logger.debug("onSetup() - start");

      executeOperation( getSetUpOperation() );
   }

   public void onTearDown() throws Exception
   {
        logger.debug("onTearDown() - start");
      executeOperation( getTearDownOperation() );
   }

   public void setDataSet( IDataSet dataSet )
   {
        logger.debug("setDataSet(dataSet={}) - start", dataSet);

      this.dataSet = dataSet;
   }

   public void setSchema( String schema )
   {
        logger.debug("setSchema(schema={}) - start", schema);

      this.schema = schema;
   }

   public void setSetUpOperation( DatabaseOperation setUpOperation )
   {
        logger.debug("setSetUpOperation(setUpOperation={}) - start", setUpOperation);

      this.setUpOperation = setUpOperation;
   }

   public void setTearDownOperation( DatabaseOperation tearDownOperation )
   {
        logger.debug("setTearDownOperation(tearDownOperation={}) - start", tearDownOperation);

      this.tearDownOperation = tearDownOperation;
   }

   /**
    * Asserts that propertyName is not a null String and has a length greater
    * than zero.
    */
   protected void assertNotNullNorEmpty( String propertyName, String property )
   {
        logger.debug("assertNotNullNorEmpty(propertyName={}, property={}) - start", propertyName, property);

      assertTrue( propertyName + " is null", property != null );
      assertTrue( "Invalid " + propertyName, property.trim()
            .length() > 0 );
   }

   /**
    * Method used to avoid JUnit dependency
    * @param message message displayed if assertion is false
    * @param condition condition to be tested
    */
   protected void assertTrue(String message, boolean condition) {
     if (!condition) {
       throw new AssertionFailedError( message );
     }
    
  }

  /**
    * Returs the schema value.
    */
   protected String getSchema()
   {
        logger.trace("getSchema() - start");

      return schema;
   }

   /**
    * Returns the DatabaseOperation to call when starting the test.
    */
   protected DatabaseOperation getSetUpOperation()
   {
        logger.trace("getSetUpOperation() - start");

      return setUpOperation;
   }

   /**
    * Returns the DatabaseOperation to call when ending the test.
    */
   protected DatabaseOperation getTearDownOperation()
   {
        logger.trace("getTearDownOperation() - start");

      return tearDownOperation;
   }

   /**
    * Executes a DatabaseOperation with a IDatabaseConnection supplied by
    * {@link getConnection()} and the test dataset.
    */
   private void executeOperation( DatabaseOperation operation ) throws Exception
   {
        logger.debug("executeOperation(operation={}) - start", operation);

      if( operation != DatabaseOperation.NONE ){
         IDatabaseConnection connection = getConnection();
         try{
            operation.execute( connection, getDataSet() );
         }
         finally{
            closeConnection( connection );
         }
      }
   }

   /**
    * Exception used to avoid JUnit dependency.
    * @author Felipe Leme
    *
    */
   public static class AssertionFailedError extends Error {

     private static final long serialVersionUID= 1L;
     
     public AssertionFailedError () {
     }
     public AssertionFailedError (String message) {
       super (message);
     }
   }
   
}

