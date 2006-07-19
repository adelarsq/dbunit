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

/**
 * Basic implementation of IDatabaseTester.<br>
 * Implementations of IDatabaseTester may use this class as a starting point.
 * 
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public abstract class AbstractDatabaseTester implements IDatabaseTester
{
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
      connection.close();
   }

   public IDataSet getDataSet()
   {
      return dataSet;
   }

   public void onSetup() throws Exception
   {
      executeOperation( getSetUpOperation() );
   }

   public void onTearDown() throws Exception
   {
      executeOperation( getTearDownOperation() );
   }

   public void setDataSet( IDataSet dataSet )
   {
      this.dataSet = dataSet;
   }

   public void setSchema( String schema )
   {
      this.schema = schema;
   }

   public void setSetUpOperation( DatabaseOperation setUpOperation )
   {
      this.setUpOperation = setUpOperation;
   }

   public void setTearDownOperation( DatabaseOperation tearDownOperation )
   {
      this.tearDownOperation = tearDownOperation;
   }

   protected void assertNotNullNorEmpty( String propertyName, String property )
   {
      Assert.assertNotNull( propertyName + " is null", property );
      Assert.assertTrue( "Invalid " + propertyName, property.trim()
            .length() > 0 );
   }

   protected String getSchema()
   {
      return schema;
   }

   protected DatabaseOperation getSetUpOperation()
   {
      return setUpOperation;
   }

   protected DatabaseOperation getTearDownOperation()
   {
      return tearDownOperation;
   }

   private void executeOperation( DatabaseOperation operation ) throws Exception
   {
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
