/*
 *
 * The DbUnit Database Testing Framework
 * Copyright (C)2002, Manuel Laflamme
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
package org.dbunit.ext.mssqlserver;

import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.FilteredDataSet;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.datatype.IDataTypeFactory;
import org.dbunit.dataset.filter.ExcludeTableFilter;
import org.dbunit.dataset.filter.ITableFilter;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author Manuel Laflamme
 * @since May 19, 2003
 * @version $Revision$
 */
public class MsSqlConnection extends DatabaseConnection
{
    private final IDataTypeFactory _dataTypeFactory = new MsSqlDataTypeFactory();
    private final ITableFilter _filter = new ExcludeTableFilter(
            new String[] {"dtproperties"});

    /**
     * Creates a new <code>MsSqlConnection</code>.
     *
     * @param connection the adapted JDBC connection
     * @param schema the database schema
     */
    public MsSqlConnection(Connection connection, String schema)
    {
        super(connection, schema);
    }

    /**
     * Creates a new <code>MsSqlConnection</code>.
     *
     * @param connection the adapted JDBC connection
     */
    public MsSqlConnection(Connection connection)
    {
        super(connection);
    }

    ////////////////////////////////////////////////////////////////////////////
    // IDatabaseConnection

    public IDataSet createDataSet() throws SQLException
    {
        IDataSet dataSet = super.createDataSet();
        return new FilteredDataSet(_filter, dataSet);
    }

    public IDataSet createDataSet(String[] tableNames) throws SQLException
    {
        IDataSet dataSet = super.createDataSet(tableNames);
        return new FilteredDataSet(_filter, dataSet);
    }

    ////////////////////////////////////////////////////////////////////////////
    // AbstractDatabaseConnection

    protected IDataTypeFactory getDataTypeFactory()
    {
        return _dataTypeFactory;
    }
}
