/*
 *
 * The DbUnit Database Testing Framework
 * Copyright (C)2005, DbUnit.org
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

package org.dbunit.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Helper for SQL-related stuff.
 * <br>
 * TODO: testcases
 * @author Felipe Leme <dbunit@felipeal.net>
 * @version $Revision$
 * @since Nov 5, 2005
 * 
 */

public class SQLHelper {

    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(SQLHelper.class);
  
  // class is "static"
  private SQLHelper() {}

  /**
   * Gets the primary column for a table.
   * @param conn connection with the database
   * @param table table name
   * @return name of primary column for a table (assuming it's just 1 column).
   * @throws SQLException raised while getting the meta data
   */
  public static String getPrimaryKeyColumn( Connection conn, String table ) throws SQLException {
        logger.debug("getPrimaryKeyColumn(conn=" + conn + ", table=" + table + ") - start");

    DatabaseMetaData metadata = conn.getMetaData();
    ResultSet rs = metadata.getPrimaryKeys( null, null, table );
    rs.next();
    String pkColumn = rs.getString(4);
    return pkColumn;    
  }

  /**
   * Close a result set and a prepared statement, checking for null references.
   * @param rs result set to be closed
   * @param stmt prepared statement to be closed
   * @throws SQLException exception raised in either close() method
   */
  public static void close(ResultSet rs, Statement stmt) throws SQLException {
        logger.debug("close(rs=" + rs + ", stmt=" + stmt + ") - start");

    try {
      if ( rs != null ) {
        rs.close();
      }
    } finally { 
      close( stmt );
    }    
  }

  /**
   * Close a preparement statement, checking for null references.
   * @param rs result set to be closed
   * @param stmt statement to be closed
   * @throws SQLException exception raised while closing the statement
   */
  public static void close(Statement stmt) throws SQLException {
        logger.debug("close(stmt=" + stmt + ") - start");

    if ( stmt != null ) { 
      stmt.close();
    }
  }


}
