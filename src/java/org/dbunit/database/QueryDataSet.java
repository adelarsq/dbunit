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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Arrays;
import java.util.Collections;

import org.dbunit.dataset.AbstractDataSet;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.DefaultTableIterator;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.ITableIterator;
import org.dbunit.dataset.DataSetUtils;

/**
 * @author     Eric Pugh
 * @since      December 4, 2002
 * @version    $Revision$
 */
public class QueryDataSet extends AbstractDataSet
{

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
        _tableEntries.add(new TableEntry(tableName, query));
    }

    /**
     *  Adds a table with using 'SELECT * FROM <code>tableName</code>' as query.
     *
     * @param  tableName  The name of the table
     */
    public void addTable(String tableName)
    {
        _tableEntries.add(new TableEntry(tableName, null));
    }

    ////////////////////////////////////////////////////////////////////////////
    // AbstractDataSet class

    protected ITableIterator createIterator(boolean reversed) throws DataSetException
    {
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
        private final String _tableName;
        private final String _query;

        public TableEntry(String tableName, String query)
        {
            _tableName = tableName;
            _query = query;
        }

        public String getTableName()
        {
            return _tableName;
        }

        public String getQuery()
        {
            return _query;
        }
    }
}


