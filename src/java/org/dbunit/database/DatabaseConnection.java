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

package org.dbunit.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * This class adapts a JDBC <code>Connection</code> to a
 * {@link IDatabaseConnection}.
 *
 * @author Manuel Laflamme
 * @version $Revision$
 * @since Feb 21, 2002
 */
public class DatabaseConnection extends AbstractDatabaseConnection
        implements IDatabaseConnection
{

    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConnection.class);

    private final Connection _connection;
    private final String _schema;

    /**
     * Creates a new <code>DatabaseConnection</code>.
     *
     * @param connection the adapted JDBC connection
     * @param schema the database schema
     */
    public DatabaseConnection(Connection connection, String schema)
    {
        _connection = connection;
        _schema = schema;
    }

    /**
     * Creates a new <code>DatabaseConnection</code>.
     *
     * @param connection the adapted JDBC connection
     */
    public DatabaseConnection(Connection connection)
    {
      this( connection, null );
    }
    
    ////////////////////////////////////////////////////////////////////////////
    // IDatabaseConnection interface

    public Connection getConnection() throws SQLException
    {
        logger.debug("getConnection() - start");

        return _connection;
    }

    public String getSchema()
    {
        logger.debug("getSchema() - start");

        return _schema;
    }

    public void close() throws SQLException
    {
        logger.debug("close() - start");

        _connection.close();
    }
}






