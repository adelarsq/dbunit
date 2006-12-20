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

/**
 * This interface defines the behavior of a DatabaseTester, which is responsible
 * for adding DBUnit features as composition on existing test cases (instead of
 * extending DBTestCase directly).
 *
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public interface IDatabaseTester
{
   /**
    * Close the specified connection.
    */
   void closeConnection( IDatabaseConnection connection ) throws Exception;

   /**
    * Returns the test database connection.
    */
   IDatabaseConnection getConnection() throws Exception;

   /**
    * Returns the test dataset.
    */
   IDataSet getDataSet();

   /**
    * Sets the test dataset to use.
    */
   void setDataSet( IDataSet dataSet );

   /**
    * Sets the schema value.
    */
   void setSchema( String schema );

   /**
    * Sets the DatabaseOperation to call when starting the test.
    */
   void setSetUpOperation( DatabaseOperation setUpOperation );

   /**
    * Sets the DatabaseOperation to call when ending the test.
    */
   void setTearDownOperation( DatabaseOperation tearDownOperation );

   /**
    * TestCases must call this method inside setUp()
    */
   void onSetup() throws Exception;

   /**
    * TestCases must call this method inside tearDown()
    */
   void onTearDown() throws Exception;
}