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

/**
 * TestCase that uses a JdbcDatabaseTester.
 * 
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 * @author Felipe Leme <dbunit@felipeal.net>
 */
public abstract class JdbcBasedDBTestCase extends DBTestCase
{
   public JdbcBasedDBTestCase()
   {
      super();
   }

   public JdbcBasedDBTestCase( String name )
   {
      super( name );
   }

   protected IDatabaseTester newDatabaseTester()
   {
      JdbcDatabaseTester databaseTester = new JdbcDatabaseTester( getDriverClass(), getConnectionUrl() );
      databaseTester.setUsername( getUsername() );
      databaseTester.setPassword( getPassword() );
      return databaseTester;
   }

   protected abstract String getConnectionUrl();

   protected abstract String getDriverClass();

   /**
    * Returns the password for the connection.<br>
    * Subclasses may override this method to provide a custom password.<br>
    * Default implementations returns null;
    */
   protected String getPassword()
   {
      return null;
   }

   /**
    * Returns the username for the connection.<br>
    * Subclasses may override this method to provide a custom username.<br>
    * Default implementations returns null;
    */
   protected String getUsername()
   {
      return null;
   }
}
