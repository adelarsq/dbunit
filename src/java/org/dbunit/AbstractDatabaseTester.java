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

import junit.framework.Assert;

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
        logger.debug("closeConnection(connection=" + connection + ") - start");

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
        logger.debug("setDataSet(dataSet=" + dataSet + ") - start");

      this.dataSet = dataSet;
   }

   public void setSchema( String schema )
   {
        logger.debug("setSchema(schema=" + schema + ") - start");

      this.schema = schema;
   }

   public void setSetUpOperation( DatabaseOperation setUpOperation )
   {
        logger.debug("setSetUpOperation(setUpOperation=" + setUpOperation + ") - start");

      this.setUpOperation = setUpOperation;
   }

   public void setTearDownOperation( DatabaseOperation tearDownOperation )
   {
        logger.debug("setTearDownOperation(tearDownOperation=" + tearDownOperation + ") - start");

      this.tearDownOperation = tearDownOperation;
   }

   /**
    * Asserts that propertyName is not a null String and has a length greater
    * than zero.
    */
   protected void assertNotNullNorEmpty( String propertyName, String property )
   {
        logger.debug("assertNotNullNorEmpty(propertyName=" + propertyName + ", property=" + property + ") - start");

      Assert.assertNotNull( propertyName + " is null", property );
      Assert.assertTrue( "Invalid " + propertyName, property.trim()
            .length() > 0 );
   }

   /**
    * Returs the schema value.
    */
   protected String getSchema()
   {
        logger.debug("getSchema() - start");

      return schema;
   }

   /**
    * Returns the DatabaseOperation to call when starting the test.
    */
   protected DatabaseOperation getSetUpOperation()
   {
        logger.debug("getSetUpOperation() - start");

      return setUpOperation;
   }

   /**
    * Returns the DatabaseOperation to call when ending the test.
    */
   protected DatabaseOperation getTearDownOperation()
   {
        logger.debug("getTearDownOperation() - start");

      return tearDownOperation;
   }

   /**
    * Executes a DatabaseOperation with a IDatabaseConnection supplied by
    * {@link getConnection()} and the test dataset.
    */
   private void executeOperation( DatabaseOperation operation ) throws Exception
   {
        logger.debug("executeOperation(operation=" + operation + ") - start");

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
}
