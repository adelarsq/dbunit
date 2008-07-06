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
        logger.debug("getPrimaryKeyColumn(conn={}, table={}) - start", conn, table);

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
        logger.debug("close(rs={}, stmt={}) - start", rs, stmt);

        try {
            if ( rs != null ) {
                rs.close();
            }
        } finally { 
            close( stmt );
        }    
    }

    /**
     * Close a SQL statement, checking for null references.
     * @param rs result set to be closed
     * @param stmt statement to be closed
     * @throws SQLException exception raised while closing the statement
     */
    public static void close(Statement stmt) throws SQLException {
        logger.debug("close(stmt={}) - start", stmt);

        if ( stmt != null ) { 
            stmt.close();
        }
    }

    /**
     * Returns <code>true</code> if the given schema exists for the given connection.
     * @param connection The connection to a database
     * @param schema The schema to be searched
     * @return Returns <code>true</code> if the given schema exists for the given connection.
     * @throws SQLException
     * @since 2.3.0
     */
    public static boolean schemaExists(Connection connection, String schema) 
    throws SQLException
    {
        logger.debug("schemaExists(connection={}, schema={}) - start", connection, schema);

        if(schema == null)
        {
            throw new NullPointerException("The parameter 'schema' must not be null");
        }
        
        DatabaseMetaData metaData = connection.getMetaData();
        ResultSet rs = metaData.getSchemas(); //null, schemaPattern);
        try
        {
            while(rs.next())
            {
                String foundSchema = rs.getString("TABLE_SCHEM");
                if(foundSchema.equals(schema))
                {
                    return true;
                }
            }
            return false;
        }
        finally
        {
            rs.close();
        }
    }

    /**
     * Checks if the given table exists.
     * @param metaData The database meta data
     * @param schema The schema in which the table should be searched. If <code>null</code>
     * the schema is not used to narrow the table name.
     * @param tableName The table name to be searched
     * @return Returns <code>true</code> if the given table exists in the given schema.
     * Else returns <code>false</code>.
     * @throws SQLException
     * @since 2.3.0
     */
    public static boolean tableExists(DatabaseMetaData metaData, String schema,
            String tableName) 
    throws SQLException 
    {
        ResultSet tableRs = metaData.getTables(null, schema, tableName, null);
        try 
        {
            return tableRs.next();
        }
        finally
        {
            if(tableRs != null)
                tableRs.close();
        }
    }

}
