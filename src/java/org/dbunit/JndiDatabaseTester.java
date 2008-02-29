/*
 * The DbUnit Database Testing Framework Copyright (C)2002-2004, DbUnit.org This
 * library is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with this library; if not, write
 * to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 * 02111-1307 USA
 */
package org.dbunit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;

/**
 * DatabaseTester that pulls a DataSource from a JNDI location.
 *
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class JndiDatabaseTester extends AbstractDatabaseTester
{

    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(JndiDatabaseTester.class);

   private DataSource dataSource;
   private Properties environment;
   private boolean initialized = false;
   private String lookupName;

   /**
    * Creates a JndiDatabaseTester with specific JNDI properties.
    *
    * @param environment A Properties object with JNDI properties
    * @param lookupName the name of the resource in the JNDI context
    */
   public JndiDatabaseTester( Properties environment, String lookupName )
   {
      super();
      this.environment = environment;
      this.lookupName = lookupName;
   }

   /**
    * Creates a JndiDatabaseTester with default JNDI properties.
    *
    * @param lookupName the name of the resource in the JNDI context
    */
   public JndiDatabaseTester( String lookupName )
   {
      this( null, lookupName );
   }

   public IDatabaseConnection getConnection() throws Exception
   {
        logger.debug("getConnection() - start");

      if( !initialized ){
         initialize();
      }

      if( getSchema() != null ){
         return new DatabaseConnection( dataSource.getConnection(), getSchema() );
      }else{
         return new DatabaseConnection( dataSource.getConnection() );
      }
   }

   /**
    * Verifies the configured properties and locates the Datasource through
    * JNDI.<br>
    * This method is called by {@link getConnection} if the tester has not been
    * initialized yet.
    */
   private void initialize() throws NamingException
   {
        logger.debug("initialize() - start");

      Context context = new InitialContext( environment );
      assertNotNullNorEmpty( "lookupName", lookupName );
      Object obj = context.lookup( lookupName );
      assertTrue( "JNDI object with [" + lookupName + "] not found", obj!=null );
      assertTrue( "Object [" + obj + "] at JNDI location [" + lookupName
            + "] is not of type [" + DataSource.class.getName() + "]", obj instanceof DataSource );
      dataSource = (DataSource) obj;
      assertTrue( "DataSource is not set", dataSource!=null );
      initialized = true;
   }
}
