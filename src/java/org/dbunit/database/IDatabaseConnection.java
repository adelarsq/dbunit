/*
 * IDatabaseConnection.java   Mar 6, 2002
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

import org.dbunit.dataset.*;
import org.dbunit.database.statement.*;

/**
 * @author Manuel Laflamme
 * @version 1.0
 */
public interface IDatabaseConnection
{
    /**
     * Returns a JDBC database connection.
     */
    public Connection getConnection() throws SQLException;

    /**
     * Returns the database schema name.
     */
    public String getSchema();

    /**
     * Close this connection.
     */
    public void close() throws SQLException;

    /**
     * Creates a dataset corresponding to the entire database.
     */
    public IDataSet createDataSet() throws SQLException;

    /**
     * Creates a dataset containing only the specified tables from
     * the database.
     */
    public IDataSet createDataSet(String[] tableNames) throws SQLException;

    /**
     * Creates a table with the result of the specified SQL statement. The
     * table can be the result of a join statement.
     *
     * @param resultName The name tobe returned by {@link TableMetaData.getTableName}.
     * @param sql The SQL <code>SELECT</code> statement
     */
    public ITable createQueryTable(String resultName, String sql)
            throws DataSetException, SQLException;

    public IStatementFactory getStatementFactory();
}



