/*
 *  QueryDataSet.java   Dec 4, 2002
 *
 *  The DbUnit Database Testing Framework
 *  Copyright (C)2002, Manuel Laflamme
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package org.dbunit.database;
import java.sql.*;
import java.util.*;

import org.dbunit.database.*;
import org.dbunit.dataset.*;

/**
 * @author     Eric Pugh
 * @created    December 4, 2002
 * @version    $Revision$
 */
public class QueryDataSet implements IDataSet {

    private final IDatabaseConnection _connection;
    private Map _tableMap = new HashMap();
    private Map _queryMap = null;


    /**
     * Create a QueryDataSet by passing in the connection to the database to use.
     *
     * @param  connection        The connection object to the database.
     * @exception  java.sql.SQLException  Description of the Exception
     */
    public QueryDataSet(IDatabaseConnection connection)
        throws SQLException {
        _connection = connection;
    }


    /**
     *  Gets the tableMap attribute of the QueryDataSet object
     *
     * @return                       The tableMap value
     * @exception  org.dbunit.dataset.DataSetException  Thrown if there is an issue.
     */
    private Map getTableMap()
        throws DataSetException {

        return _tableMap;
    }



    ////////////////////////////////////////////////////////////////////////////
    // IDataSet interface

    /**
     *  Gets the tableNames attribute of the QueryDataSet object
     *
     * @return                       An array of all the table names
     * @exception  org.dbunit.dataset.DataSetException  Thrown if there is an issue.
     */
    public String[] getTableNames()
        throws DataSetException {
        return (String[]) getTableMap().keySet().toArray(new String[0]);
    }


    /**
     *  Gets the tableMetaData attribute of the QueryDataSet object
     *
     * @param  tableName             The name of the table to retrieve
     * @return                       The tableMetaData value
     * @exception  org.dbunit.dataset.DataSetException  Thrown if there is an issue.
     */
    public ITableMetaData getTableMetaData(String tableName)
        throws DataSetException {
        ITableMetaData metaData = (ITableMetaData) getTableMap().get(tableName);
        if (metaData != null) {
            return metaData;
        }

        if (!getTableMap().containsKey(tableName)) {
            throw new NoSuchTableException(tableName);
        }

        metaData = new DatabaseTableMetaData(tableName, _connection);
        getTableMap().put(tableName, metaData);
        return metaData;
    }


    /**
     *  Gets a specific table of the QueryDataSet object
     *
     * @param  tableName             The name of the table to retrieve
     * @return                       The table
     * @exception  org.dbunit.dataset.DataSetException  Thrown if there is an issue.
     */
    public ITable getTable(String tableName)
        throws DataSetException {
        try {

            Connection jdbcConnection = _connection.getConnection();
//            String schema = _connection.getSchema();
            Statement statement = jdbcConnection.createStatement();

            try {
                String sql = getQuery(tableName);
                ResultSet resultSet = statement.executeQuery(sql);
                try {
                    ITableMetaData metaData = ResultSetTable.createTableMetaData(tableName, resultSet);
                    return new CachedResultSetTable(metaData, resultSet);
                }
                finally {
                    resultSet.close();
                }
            }
            finally {
                statement.close();
            }
        }
        catch (SQLException e) {
            throw new DataSetException(e);
        }
    }


    /**
     *  Gets the tables attribute of the QueryDataSet object
     *
     * @return                       The tables value
     * @exception  org.dbunit.dataset.DataSetException  Thrown if there is an issue.
     */
    public ITable[] getTables()
        throws DataSetException {
        String[] names = getTableNames();
        List tableList = new ArrayList(names.length);
        for (int i = 0; i < names.length; i++) {
            tableList.add(getTable(names[i]));
        }
        return (ITable[]) tableList.toArray(new ITable[0]);
    }


    /**
     *  Gets the query to be used for a specific table added to the QueryDataSet object
     *
     * @param  tableName  The name of the table
     * @return            The query value
     */
    public String getQuery(String tableName) {
        return (String) _queryMap.get(tableName);
    }


    /**
     *  Adds a table and it's associted query to this dataset.
     *
     * @param  tableName  The name of the table
     * @param  query  The query to retrieve data with for this table
     */
    public void addTable(String tableName, String query) {
        if (_queryMap == null) {
            _queryMap = new HashMap();
        }
        _queryMap.put(tableName, query);
        _tableMap.put(tableName, null);

    }

}


