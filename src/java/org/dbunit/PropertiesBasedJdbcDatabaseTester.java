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

/**
 * DatabaseTester that configures a DriverManager from environment properties.<br>
 * This class defines a set of keys for system properties that need to be
 * present in the environment before using it. Example:
 * <xmp>
 * System.setProperty( PropertiesBasedJdbcDatabaseTester.DBUNIT_DRIVER_CLASS,
 *             "com.mycompany.myDriver" );
 * System.setProperty( PropertiesBasedJdbcDatabaseTester.DBUNIT_CONNECTION_URL,
 *             "jdbc:mydb://host/dbname" );
 * System.setProperty( PropertiesBasedJdbcDatabaseTester.DBUNIT_USERNAME,
 *             "myuser" );
 * System.setProperty( PropertiesBasedJdbcDatabaseTester.DBUNIT_PASSWORD,
 *             "mypasswd" );
 * System.setProperty( PropertiesBasedJdbcDatabaseTester.DBUNIT_SCHEMA,
 *             "myschema" );
 * </xmp>
 *
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 * @author Felipe Leme <dbunit@felipeal.net>
 */
public class PropertiesBasedJdbcDatabaseTester extends JdbcDatabaseTester
{

    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(PropertiesBasedJdbcDatabaseTester.class);

   /** A key for property that defines the connection url */
   public static final String DBUNIT_CONNECTION_URL = "dbunit.connectionUrl";
   /** A key for property that defines the driver classname */
   public static final String DBUNIT_DRIVER_CLASS = "dbunit.driverClass";
   /** A key for property that defines the user's password */
   public static final String DBUNIT_PASSWORD = "dbunit.password";
   /** A key for property that defines the username */
   public static final String DBUNIT_USERNAME = "dbunit.username";
   /** A key for property that defines the database schema */
   public static final String DBUNIT_SCHEMA = "dbunit.schema";

   /** A key for property that defines the connection url */

   public PropertiesBasedJdbcDatabaseTester()
   {
      super( null, null, null, null );
   }

   protected void initialize() throws Exception
   {
        logger.debug("initialize() - start");

      setDriverClass( System.getProperty( DBUNIT_DRIVER_CLASS ) );
      setConnectionUrl( System.getProperty( DBUNIT_CONNECTION_URL ) );
      setUsername( System.getProperty( DBUNIT_USERNAME ) );
      setPassword( System.getProperty( DBUNIT_PASSWORD ) );
      setSchema( System.getProperty( DBUNIT_SCHEMA ) );
      super.initialize();
   }
}
