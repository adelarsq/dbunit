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

import javax.sql.DataSource;

import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;

/**
 * DatabaseTester that uses a DataSource to create connections.
 *
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 * @author Felipe Leme <dbunit@felipeal.net>
 */
public class DataSourceDatabaseTester extends AbstractDatabaseTester
{

    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(DataSourceDatabaseTester.class);

   private DataSource dataSource;

   /**
    * Creates a new DataSourceDatabaseTester with the specified DataSource.
    *
    * @param dataSource the DataSource to pull connections from
    */
   public DataSourceDatabaseTester( DataSource dataSource )
   {
      super();
      this.dataSource = dataSource;
   }

   public IDatabaseConnection getConnection() throws Exception
   {
        logger.debug("getConnection() - start");

      assertTrue( "DataSource is not set", dataSource!=null );
      if( getSchema() != null ){
         return new DatabaseConnection( dataSource.getConnection(), getSchema() );
      }else{
         return new DatabaseConnection( dataSource.getConnection() );
      }
   }
}
