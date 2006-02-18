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
package org.dbunit.database.search;

import java.io.File;
import java.sql.Connection;
import java.util.Set;

import junit.framework.TestCase;
import junitx.framework.ArrayAssert;

import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;

import org.dbunit.HypersonicEnvironment;
import org.dbunit.util.CollectionsHelper;
import org.dbunit.util.search.DepthFirstSearch;
import org.dbunit.util.search.ISearchCallback;

/**  
 * @author Felipe Leme <dbunit@felipeal.net>
 * @version $Revision$
 * @since Aug 28, 2005
 */
public abstract class AbstractMetaDataBasedSearchCallbackTestCase extends TestCase {
  
  private final String sqlFile;
  
  private Connection jdbcConnection;
  
  private IDatabaseConnection connection;
  
   public AbstractMetaDataBasedSearchCallbackTestCase(String testName, String sqlFile) {
     super(testName );
     this.sqlFile = sqlFile;
   }   
   
   protected void setUp() throws Exception {
     this.jdbcConnection = HypersonicEnvironment.createJdbcConnection("mem:tempdb");
     HypersonicEnvironment.executeDdlFile(new File(
         "src/sql/" + this.sqlFile), this.jdbcConnection);
     this.connection = new DatabaseConnection(jdbcConnection);
   }

   protected void tearDown() throws Exception {
     HypersonicEnvironment.shutdown(this.jdbcConnection);
     this.jdbcConnection.close();
//     HypersonicEnvironment.deleteFiles( "tempdb" );
   }   
   
   protected IDatabaseConnection getConnection() {
     return this.connection;
   }
   
  protected abstract String[][] getInput(); 
  
  protected abstract String[][] getExpectedOutput(); 
  
  protected abstract AbstractMetaDataBasedSearchCallback getCallback(IDatabaseConnection connection2);

  public void testAllInput() throws Exception {
     IDatabaseConnection connection = getConnection();
     
     String[][] allInput = getInput();
     String[][] allExpectedOutput = getExpectedOutput();
     ISearchCallback callback = getCallback(connection);
     for (int i = 0; i < allInput.length; i++) {
       String[] input = allInput[i];
       String[] expectedOutput = allExpectedOutput[i];
       DepthFirstSearch search = new DepthFirstSearch();
       Set result = search.search( input, callback );
       String[] actualOutput = CollectionsHelper.setToStrings( result ); 
       ArrayAssert.assertEquals( "output didn't match for i=" + i, expectedOutput, actualOutput );
     }           
  }

  
  
}
