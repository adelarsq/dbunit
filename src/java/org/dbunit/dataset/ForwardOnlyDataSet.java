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
 * Decorator that allows forward only access to decorated dataset.
 *
 * @author Manuel Laflamme
 * @since Apr 9, 2003
 * @version $Revision$
 */
public class ForwardOnlyDataSet extends AbstractDataSet
{

    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(ForwardOnlyDataSet.class);

    private final IDataSet _dataSet;
    private int _iteratorCount;

    public ForwardOnlyDataSet(IDataSet dataSet)
    {
        _dataSet = dataSet;
    }

    ////////////////////////////////////////////////////////////////////////////
    // AbstractDataSet class

    protected ITableIterator createIterator(boolean reversed)
            throws DataSetException
    {
        logger.debug("createIterator(reversed=" + reversed + ") - start");

        if (reversed)
        {
            throw new UnsupportedOperationException(
                    "Reverse iterator not supported!");
        }

        if (_iteratorCount > 0)
        {
            throw new UnsupportedOperationException(
                    "Only one iterator allowed!");
        }

        return new ForwardOnlyIterator(_dataSet.iterator());
    }

    ////////////////////////////////////////////////////////////////////////////
    // IDataSet interface

    public String[] getTableNames() throws DataSetException
    {
        logger.debug("getTableNames() - start");

        throw new UnsupportedOperationException();
    }

    public ITableMetaData getTableMetaData(String tableName) throws DataSetException
    {
        logger.debug("getTableMetaData(tableName=" + tableName + ") - start");

        throw new UnsupportedOperationException();
    }

    public ITable getTable(String tableName) throws DataSetException
    {
        logger.debug("getTable(tableName=" + tableName + ") - start");

        throw new UnsupportedOperationException();
    }

    ////////////////////////////////////////////////////////////////////////////
    // ForwardOnlyIterator class

    private class ForwardOnlyIterator implements ITableIterator
    {

        /**
         * Logger for this class
         */
        private final Logger logger = LoggerFactory.getLogger(ForwardOnlyIterator.class);

        private final ITableIterator _iterator;

        public ForwardOnlyIterator(ITableIterator iterator)
        {
            _iterator = iterator;
            _iteratorCount++;
        }

        ////////////////////////////////////////////////////////////////////////////
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

            return new ForwardOnlyTable(_iterator.getTable());
        }
    }
}
