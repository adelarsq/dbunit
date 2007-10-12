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

import org.dbunit.dataset.*;

import java.sql.SQLException;
import java.util.List;

/**
 * @author Manuel Laflamme
 * @since Sep 15, 2003
 * @version $Revision$
 */
public class QueryTableIterator implements ITableIterator
{

    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(QueryTableIterator.class);

    private final List _tableEntries;
    private final IDatabaseConnection _connection;
    private IResultSetTable _currentTable;
    private int _index = -1;

    public QueryTableIterator(List tableEntries, IDatabaseConnection connection)
    {
        _tableEntries = tableEntries;
        _connection = connection;
        _currentTable = null;
    }

    ////////////////////////////////////////////////////////////////////////////
    // ITableIterator interface

    public boolean next() throws DataSetException
    {
        logger.debug("next() - start");

        _index++;

        // Ensure previous table is closed
        if (_currentTable != null)
        {
            _currentTable.close();
            _currentTable = null;
        }

        return _index < _tableEntries.size();
    }

    public ITableMetaData getTableMetaData() throws DataSetException
    {
        logger.debug("getTableMetaData() - start");

        QueryDataSet.TableEntry entry = (QueryDataSet.TableEntry)_tableEntries.get(_index);

        // No query specified, use metadata from dataset
        if (entry.getQuery() == null)
        {
            try
            {
                IDataSet dataSet = _connection.createDataSet();
                return dataSet.getTableMetaData(entry.getTableName());
            }
            catch (SQLException e)
            {
                logger.error("getTableMetaData()", e);

                throw new DataSetException(e);
            }
        }
        else
        {
            return getTable().getTableMetaData();
        }
    }

    public ITable getTable() throws DataSetException
    {
        logger.debug("getTable() - start");

        if (_currentTable == null)
        {
            try
            {
                QueryDataSet.TableEntry entry = (QueryDataSet.TableEntry)_tableEntries.get(_index);

                // No query specified, use table from dataset
                if (entry.getQuery() == null)
                {
                    IDataSet dataSet = _connection.createDataSet();
                    _currentTable = (IResultSetTable)dataSet.getTable(entry.getTableName());
                }
                else
                {
                    DatabaseConfig config = _connection.getConfig();
                    IResultSetTableFactory factory = (IResultSetTableFactory)config.getProperty(
                            DatabaseConfig.PROPERTY_RESULTSET_TABLE_FACTORY);

                    _currentTable = factory.createTable(entry.getTableName(), entry.getQuery(), _connection);
                }
            }
            catch (SQLException e)
            {
                logger.error("getTable()", e);

                throw new DataSetException(e);
            }
        }
        return _currentTable;
    }
}
