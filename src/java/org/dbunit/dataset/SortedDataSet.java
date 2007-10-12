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


/**
 * @author Manuel Laflamme
 * @version $Revision$
 * @since Feb 19, 2003
 */
public class SortedDataSet extends AbstractDataSet
{

    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(SortedDataSet.class);

    private final IDataSet _dataSet;

    public SortedDataSet(IDataSet dataSet) throws DataSetException
    {
        _dataSet = dataSet;
    }

    ////////////////////////////////////////////////////////////////////////////
    // AbstractDataSet class

    protected ITableIterator createIterator(boolean reversed)
            throws DataSetException
    {
        logger.debug("createIterator(reversed=" + reversed + ") - start");

        return new SortedIterator(reversed ?
                _dataSet.reverseIterator() : _dataSet.iterator());
    }

    ////////////////////////////////////////////////////////////////////////////
    // IDataSet interface

    public String[] getTableNames() throws DataSetException
    {
        logger.debug("getTableNames() - start");

        return _dataSet.getTableNames();
    }

    public ITableMetaData getTableMetaData(String tableName) throws DataSetException
    {
        logger.debug("getTableMetaData(tableName=" + tableName + ") - start");

        return _dataSet.getTableMetaData(tableName);
    }

    public ITable getTable(String tableName) throws DataSetException
    {
        logger.debug("getTable(tableName=" + tableName + ") - start");

        return new SortedTable(_dataSet.getTable(tableName));
    }

    ////////////////////////////////////////////////////////////////////////////
    // SortedIterator class

    private class SortedIterator implements ITableIterator
    {

        /**
         * Logger for this class
         */
        private final Logger logger = LoggerFactory.getLogger(SortedIterator.class);

        private final ITableIterator _iterator;

        public SortedIterator(ITableIterator iterator)
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

            return new SortedTable(_iterator.getTable());
        }
    }

}






