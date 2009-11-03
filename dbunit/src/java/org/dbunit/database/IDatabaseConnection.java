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

import org.dbunit.database.statement.IStatementFactory;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * This interface represents a connection to a specific database.
 *
 * @author Manuel Laflamme
 * @version $Revision$
 * @since Mar 6, 2002
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
     * @param tableNames The tables for which a dataset shall be created
     * @return The new dataset
     * @throws SQLException
     * @throws DataSetException
     */
    public IDataSet createDataSet(String[] tableNames) 
            throws SQLException, DataSetException;

    /**
     * Creates a table with the result of the specified SQL statement. The
     * table can be the result of a join statement.
     *
     * @param tableName The name to be returned by {@link org.dbunit.dataset.ITableMetaData#getTableName}.
     * @param sql The SQL <code>SELECT</code> statement
     * @return The new table
     * @throws DataSetException
     * @throws SQLException
     */
    public ITable createQueryTable(String tableName, String sql)
            throws DataSetException, SQLException;

    /**
     * Creates a table using the given PreparedStatement to retrieve a ResultSet.
     *
     * @param tableName The name to be returned by {@link org.dbunit.dataset.ITableMetaData#getTableName}.
     * @param preparedStatement The statement to be executed as query
     * @return The new table
     * @throws DataSetException
     * @throws SQLException
     * @since 2.4.4
     */
    public ITable createTable(String tableName, PreparedStatement preparedStatement)
            throws DataSetException, SQLException;

    /**
     * Creates a table with the result of a <code>select * from <i>tableName</i></code> SQL statement. 
     *
     * @param tableName The name of the database table to be queried which is also returned by 
     * {@link org.dbunit.dataset.ITableMetaData#getTableName}.
     */
    public ITable createTable(String tableName)
            throws DataSetException, SQLException;
    
    /**
     * Returns the specified table row count.
     *
     * @param tableName the table name
     * @return the row count
     */
    public int getRowCount(String tableName) throws SQLException;

    /**
     * Returns the specified table row count according specified where clause.
     *
     * @param tableName the table name
     * @param whereClause the where clause
     * @return the row count
     */
    public int getRowCount(String tableName, String whereClause) throws SQLException;

    /**
     * Returns this connection database configuration
     */
    public DatabaseConfig getConfig();

    /**
     * @deprecated Use {@link #getConfig}
     */
    public IStatementFactory getStatementFactory();
}








