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

import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import junit.framework.Assert;

import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;

/**
 * DatabaseTester that pulls a DataSource from a JNDI location.
 * 
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class JndiDatabaseTester extends AbstractDatabaseTester
{
   private DataSource dataSource;
   private Properties environment;
   private boolean initialized = false;
   private String lookupName;

   public JndiDatabaseTester( Properties environment, String lookupName )
   {
      super();
      this.environment = environment;
      this.lookupName = lookupName;
   }

   public JndiDatabaseTester( String lookupName )
   {
      this( null, lookupName );
   }

   public IDatabaseConnection getConnection() throws Exception
   {
      if( !initialized ){
         initialize();
      }

      return new DatabaseConnection( dataSource.getConnection() );
   }

   private void initialize() throws NamingException
   {
      Context context = new InitialContext( environment );
      assertNotNullNorEmpty( "lookupName", lookupName );
      Object obj = context.lookup( lookupName );
      Assert.assertNotNull( "JNDI object with [" + lookupName + "] not found", obj );
      Assert.assertTrue( "Object [" + obj + "] at JNDI location [" + lookupName
            + "] is not of type [" + DataSource.class.getName() + "]", obj instanceof DataSource );
      dataSource = (DataSource) obj;
      Assert.assertNotNull( "DataSource is not set", dataSource );
      initialized = true;
   }
}
