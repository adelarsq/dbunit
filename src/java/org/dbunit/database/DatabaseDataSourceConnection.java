/*
 * DatabaseDataSourceConnection.java   Mar 8, 2002
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

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * @author Manuel Laflamme
 * @since 1.1
 * @version 1.0
 */
public class DatabaseDataSourceConnection extends AbstractDatabaseConnection
        implements IDatabaseConnection
{
    private final String _schema;
    private final DataSource _dataSource;
    private Connection _connection;

    public DatabaseDataSourceConnection(InitialContext context, String jndiName,
            String schema) throws NamingException, SQLException
    {
        this((DataSource)context.lookup(jndiName), schema);
    }

    public DatabaseDataSourceConnection(InitialContext context, String jndiName)
            throws NamingException, SQLException
    {
        this(context, jndiName, null);
    }

    public DatabaseDataSourceConnection(DataSource dataSource)
            throws SQLException
    {
        this(dataSource, null);
    }

    public DatabaseDataSourceConnection(DataSource dataSource, String schema)
            throws SQLException
    {
        _dataSource = dataSource;
        _schema = schema;
    }

    ////////////////////////////////////////////////////////////////////////////
    // IDatabaseConnection interface

    public Connection getConnection() throws SQLException
    {
        if (_connection == null)
        {
            _connection = _dataSource.getConnection();
        }
        return _connection;
    }

    public String getSchema()
    {
        return _schema;
    }

    public void close() throws SQLException
    {
        if (_connection != null)
        {
            _connection.close();
            _connection = null;
        }
    }
}
