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

package org.dbunit.dataset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.dbunit.database.AmbiguousTableNameException;

/**
 * Allows access to a decorated dataset in a case insensitive way. Dataset
 * implementations provided by the framework are case sensitive. This class
 * allows using them in situation where case sensitiveness is not desirable.
 *
 * @author Manuel Laflamme
 * @version $Revision$
 * @deprecated All IDataSet implementations are case insensitive since DbUnit 1.5
 */
public class CaseInsensitiveDataSet extends AbstractDataSet
{

    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(CaseInsensitiveDataSet.class);

    private final IDataSet _dataSet;

    public CaseInsensitiveDataSet(IDataSet dataSet)
    {
        _dataSet = dataSet;
    }

    private String getInternalTableName(String tableName) throws DataSetException
    {
        logger.debug("getInternalTableName(tableName=" + tableName + ") - start");

        String found = null;
        String[] names = _dataSet.getTableNames();
        for (int i = 0; i < names.length; i++)
        {
            if (tableName.equalsIgnoreCase(names[i]))
            {
                if (found != null)
                {
                    throw new AmbiguousTableNameException(tableName);
                }
                found = names[i];
            }
        }

        if (found != null)
        {
            return found;
        }

        throw new NoSuchTableException(tableName);
    }

    ////////////////////////////////////////////////////////////////////////////
    // AbstractDataSet class

    protected ITableIterator createIterator(boolean reversed)
            throws DataSetException
    {
        logger.debug("createIterator(reversed=" + reversed + ") - start");

        return new CaseInsensitiveIterator(reversed ?
                _dataSet.reverseIterator() : _dataSet.iterator());
    }

    ////////////////////////////////////////////////////////////////////////////
    // IDataSet interface

    public String[] getTableNames() throws DataSetException
    {
        logger.debug("getTableNames() - start");

        return _dataSet.getTableNames();
    }

    public ITableMetaData getTableMetaData(String tableName)
            throws DataSetException
    {
        logger.debug("getTableMetaData(tableName=" + tableName + ") - start");

        return _dataSet.getTableMetaData(getInternalTableName(tableName));
    }

    public ITable getTable(String tableName) throws DataSetException
    {
        logger.debug("getTable(tableName=" + tableName + ") - start");

        ITable table = _dataSet.getTable(getInternalTableName(tableName));
        return new CaseInsensitiveTable(table);
    }

    ////////////////////////////////////////////////////////////////////////////
    // CaseInsensitiveIterator class

    private class CaseInsensitiveIterator implements ITableIterator
    {

        /**
         * Logger for this class
         */
        private final Logger logger = LoggerFactory.getLogger(CaseInsensitiveIterator.class);

        private final ITableIterator _iterator;

        public CaseInsensitiveIterator(ITableIterator iterator)
        {
            _iterator = iterator;
        }

        ////////////////////////////////////////////////////////////////////////
        // ITableIterator interface

        public boolean next() throws DataSetException
        {
            logger.debug("next() - start");

            return _iterator.next();
        }

        public ITableMetaData getTableMetaData() throws DataSetException
        {
            logger.debug("getTableMetaData() - start");

            return _iterator.getTableMetaData();
        }

        public ITable getTable() throws DataSetException
        {
            logger.debug("getTable() - start");

            return new CaseInsensitiveTable(_iterator.getTable());
        }
    }
}



