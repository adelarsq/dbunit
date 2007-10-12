/*
 *
 *  The DbUnit Database Testing Framework
 *  Copyright (C)2002-2004, DbUnit.org
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.dbunit.dataset.AbstractDataSet;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.ITableIterator;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Holds collection of tables resulting from database query.
 *
 * @author     Eric Pugh
 * @since      Dec 4, 2002
 * @version    $Revision$
 */
public class QueryDataSet extends AbstractDataSet
{

    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(QueryDataSet.class);

    private final IDatabaseConnection _connection;
    private final List _tableEntries = new ArrayList();


    /**
     * Create a QueryDataSet by passing in the connection to the database to use.
     *
     * @param  connection        The connection object to the database.
     * @exception  java.sql.SQLException  Description of the Exception
     */
    public QueryDataSet(IDatabaseConnection connection)
            throws SQLException
    {
        _connection = connection;
    }

    /**
     *  Adds a table and it's associted query to this dataset.
     *
     * @param  tableName  The name of the table
     * @param  query  The query to retrieve data with for this table
     */
    public void addTable(String tableName, String query)
    {
        logger.debug("addTable(tableName=" + tableName + ", query=" + query + ") - start");

        _tableEntries.add(new TableEntry(tableName, query));
    }

    /**
     *  Adds a table with using 'SELECT * FROM <code>tableName</code>' as query.
     *
     * @param  tableName  The name of the table
     */
    public void addTable(String tableName)
    {
        logger.debug("addTable(tableName=" + tableName + ") - start");

        _tableEntries.add(new TableEntry(tableName, null));
    }

    ////////////////////////////////////////////////////////////////////////////
    // AbstractDataSet class

    protected ITableIterator createIterator(boolean reversed) throws DataSetException
    {
        logger.debug("createIterator(reversed=" + reversed + ") - start");

        List tableEntries = new ArrayList(_tableEntries);
        if (reversed)
        {
            Collections.reverse(tableEntries);
        }

        return new QueryTableIterator(tableEntries, _connection);
    }

    ////////////////////////////////////////////////////////////////////////////
    // IDataSet interface

    public String[] getTableNames() throws DataSetException
    {
        logger.debug("getTableNames() - start");

        List names = new ArrayList();
        for (Iterator it = _tableEntries.iterator(); it.hasNext();)
        {
            TableEntry entry = (TableEntry)it.next();
            names.add(entry.getTableName());
        }

        return (String[])names.toArray(new String[0]);
    }

    static class TableEntry
    {

        /**
         * Logger for this class
         */
        private static final Logger logger = LoggerFactory.getLogger(TableEntry.class);

        private final String _tableName;
        private final String _query;

        public TableEntry(String tableName, String query)
        {
            _tableName = tableName;
            _query = query;
        }

        public String getTableName()
        {
            logger.debug("getTableName() - start");

            return _tableName;
        }

        public String getQuery()
        {
            logger.debug("getQuery() - start");

            return _query;
        }
    }
}


