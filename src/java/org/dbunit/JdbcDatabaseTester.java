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

import java.sql.Connection;
import java.sql.DriverManager;

import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;

/**
 * DatabaseTester that uses JDBC's Driver Manager to create connections.<br>
 *
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 * @author Felipe Leme <dbunit@felipeal.net>
 */
public class JdbcDatabaseTester extends AbstractDatabaseTester
{

    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(JdbcDatabaseTester.class);

   private String connectionUrl;
   private String driverClass;
   private boolean initialized = false;
   private String password;
   private String username;

   /**
    * Creates a new JdbcDatabaseTester with the specified properties.<br>
    * Username and Password are set to null.
    *
    * @param driverClass the classname of the JDBC driver to use
    * @param connectionUrl the connection url
    */
   public JdbcDatabaseTester( String driverClass, String connectionUrl )
   {
      this( driverClass, connectionUrl, null, null );
   }

   /**
    * Creates a new JdbcDatabaseTester with the specified properties.
    *
    * @param driverClass the classname of the JDBC driver to use
    * @param connectionUrl the connection url
    * @param username a username that can has access to the database
    * @param password the user's password
    */
   public JdbcDatabaseTester( String driverClass, String connectionUrl, String username,
         String password )
   {
      super();
      this.driverClass = driverClass;
      this.connectionUrl = connectionUrl;
      this.username = username;
      this.password = password;
   }

   public IDatabaseConnection getConnection() throws Exception
   {
        logger.debug("getConnection() - start");

      if( !initialized ){
         initialize();
      }
      assertNotNullNorEmpty( "connectionUrl", connectionUrl );
      Connection conn = null;
      if( username == null && password == null ){
         conn = DriverManager.getConnection( connectionUrl );
      }else{
         conn = DriverManager.getConnection( connectionUrl, username, password );
      }
      if( getSchema() != null ){
         return new DatabaseConnection( conn, getSchema() );
      }
      return new DatabaseConnection( conn );
   }

   /**
    * Sets the value of the user's password.
    */
   public void setPassword( String password )
   {
        logger.debug("setPassword(password=" + password + ") - start");

      this.password = password;
   }

   /**
    * Sets the value of the username from the connection.
    */
   public void setUsername( String username )
   {
        logger.debug("setUsername(username=" + username + ") - start");

      this.username = username;
   }

   /**
    * Verifies the configured properties and initializes the driver.<br>
    * This method is called by {@link getConnection} if the tester has not been
    * initialized yet.
    */
   protected void initialize() throws Exception
   {
        logger.debug("initialize() - start");

      assertNotNullNorEmpty( "driverClass", driverClass );
      Class.forName( driverClass );
      initialized = true;
   }

   /**
    * Sets the value of the connection url.
    */
   protected void setConnectionUrl( String connectionUrl )
   {
        logger.debug("setConnectionUrl(connectionUrl=" + connectionUrl + ") - start");

      this.connectionUrl = connectionUrl;
   }

   /**
    * Sets the value of the JDBC driver classname.
    */
   protected void setDriverClass( String driverClass )
   {
        logger.debug("setDriverClass(driverClass=" + driverClass + ") - start");

      this.driverClass = driverClass;
   }
}
