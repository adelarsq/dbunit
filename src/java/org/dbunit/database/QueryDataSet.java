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
import org.dbunit.dataset.AbstractDataSet;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.ITableIterator;
import org.dbunit.dataset.DefaultTableIterator;

/**
 * @author     Eric Pugh
 * @since      December 4, 2002
 * @version    $Revision$
 */
public class QueryDataSet extends AbstractDataSet
{

    private final IDatabaseConnection _connection;
    private final List _tables = new ArrayList();


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
        _tables.add(new TableEntry(tableName, query));
    }

    ////////////////////////////////////////////////////////////////////////////
    // AbstractDataSet class

    protected ITableIterator createIterator(boolean reversed) throws DataSetException
    {
        try
        {
            List tableList = new ArrayList();
            for (Iterator it = _tables.iterator(); it.hasNext();)
            {
                TableEntry entry = (TableEntry)it.next();

                ITable table = entry.getTable();
                if (table == null)
                {
                    table = _connection.createQueryTable(
                            entry.getTableName(), entry.getQuery());
                    entry.setTable(table);
                }
                tableList.add(table);
            }

            ITable[] tables = (ITable[])tableList.toArray(new ITable[0]);
            return new DefaultTableIterator(tables, reversed);
        }
        catch (SQLException e)
        {
            throw new DataSetException(e);
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    // IDataSet interface

    public String[] getTableNames() throws DataSetException
    {
        List names = new ArrayList();
        for (Iterator it = _tables.iterator(); it.hasNext();)
        {
            TableEntry entry = (TableEntry)it.next();
            names.add(entry.getTableName());
        }

        return (String[])names.toArray(new String[0]);
    }

    private static class TableEntry
    {
        private final String _tableName;
        private final String _query;
        private ITable _table;

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

        public ITable getTable()
        {
            return _table;
        }

        public void setTable(ITable table)
        {
            _table = table;
        }
    }
}


