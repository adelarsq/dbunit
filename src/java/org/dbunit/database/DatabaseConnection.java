/*
 * DatabaseConnection.java   Feb 21, 2002
 *
 * The dbUnit database testing framework.
 * Copyright (C) 2002   Manuel Laflamme
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

import java.sql.*;

import org.dbunit.dataset.*;

/**
 * @author Manuel Laflamme
 * @version 1.0
 */
public class DatabaseConnection extends AbstractDatabaseConnection implements IDatabaseConnection
{
    private final Connection _connection;
    private final String _schema;

    public DatabaseConnection(Connection connection, String schema)
    {
        _connection = connection;
        _schema = schema;
    }

    public DatabaseConnection(Connection connection)
    {
        _connection = connection;
        _schema = null;
    }

    public Connection getConnection() throws SQLException
    {
        return _connection;
    }

    public String getSchema()
    {
        return _schema;
    }

    public void close() throws SQLException
    {
        _connection.close();
    }
}
