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
   private String connectionUrl;
   private String driverClass;
   private boolean initialized = false;
   private String password;
   private String username;

   public JdbcDatabaseTester( String driverClass, String connectionUrl )
   {
      this( driverClass, connectionUrl, null, null );
   }

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

   public void setPassword( String password )
   {
      this.password = password;
   }

   public void setUsername( String username )
   {
      this.username = username;
   }

   protected void initialize() throws Exception
   {
      assertNotNullNorEmpty( "driverClass", driverClass );
      Class.forName( driverClass );
      initialized = true;
   }

   protected void setConnectionUrl( String connectionUrl )
   {
      this.connectionUrl = connectionUrl;
   }

   protected void setDriverClass( String driverClass )
   {
      this.driverClass = driverClass;
   }
}
