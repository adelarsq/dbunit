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

import java.util.HashMap;
import java.util.Map;

/**
 * Decorator that replace configured values from the decorated dataset
 * with replacement values.
 *
 * @author Manuel Laflamme
 * @since Mar 17, 2003
 * @version $Revision$
 */
public class ReplacementDataSet extends AbstractDataSet
{

    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(ReplacementDataSet.class);

    private final IDataSet _dataSet;
    private final Map _objectMap;
    private final Map _substringMap;
    private String _startDelim;
    private String _endDelim;


    /**
     * Create a new ReplacementDataSet object that decorates the specified dataset.
     *
     * @param dataSet the decorated table
     */
    public ReplacementDataSet(IDataSet dataSet)
    {
        _dataSet = dataSet;
        _objectMap = new HashMap();
        _substringMap = new HashMap();
    }

    /**
     * Create a new ReplacementDataSet object that decorates the specified dataset.
     *
     * @param dataSet the decorated dataset
     * @param objectMap the replacement objects mapping
     * @param substringMap the replacement substrings mapping
     */
    public ReplacementDataSet(IDataSet dataSet, Map objectMap, Map substringMap)
    {
        _dataSet = dataSet;
        _objectMap = objectMap == null ? new HashMap() : objectMap;
        _substringMap = substringMap == null ? new HashMap() : substringMap;
    }

    /**
     * Add a new Object replacement mapping.
     *
     * @param originalObject the object to replace
     * @param replacementObject the replacement object
     */
    public void addReplacementObject(Object originalObject, Object replacementObject)
    {
        logger.debug("addReplacementObject(originalObject=" + originalObject + ", replacementObject="
                + replacementObject + ") - start");

        _objectMap.put(originalObject, replacementObject);
    }

    /**
     * Add a new substring replacement mapping.
     *
     * @param originalSubstring the substring to replace
     * @param replacementSubstring the replacement substring
     */
    public void addReplacementSubstring(String originalSubstring,
            String replacementSubstring)
    {
        logger.debug("addReplacementSubstring(originalSubstring=" + originalSubstring + ", replacementSubstring="
                + replacementSubstring + ") - start");

        if (originalSubstring == null || replacementSubstring == null)
        {
            throw new NullPointerException();
        }

        _substringMap.put(originalSubstring, replacementSubstring);
    }

    /**
     * Sets substring delimiters.
     */
    public void setSubstringDelimiters(String startDelimiter, String endDelimiter)
    {
        logger.debug("setSubstringDelimiters(startDelimiter=" + startDelimiter + ", endDelimiter=" + endDelimiter
                + ") - start");

        if (startDelimiter == null || endDelimiter == null)
        {
            throw new NullPointerException();
        }

        _startDelim = startDelimiter;
        _endDelim = endDelimiter;
    }

    private ReplacementTable createReplacementTable(ITable table)
    {
        logger.debug("createReplacementTable(table=" + table + ") - start");

        return new ReplacementTable(table, _objectMap, _substringMap,
                _startDelim, _endDelim);
    }

    ////////////////////////////////////////////////////////////////////////////
    // AbstractDataSet class

    protected ITableIterator createIterator(boolean reversed)
            throws DataSetException
    {
        logger.debug("createIterator(reversed=" + reversed + ") - start");

        return new ReplacementIterator(reversed ?
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

        return _dataSet.getTableMetaData(tableName);
    }

    public ITable getTable(String tableName) throws DataSetException
    {
        logger.debug("getTable(tableName=" + tableName + ") - start");

        return createReplacementTable(_dataSet.getTable(tableName));
    }

    ////////////////////////////////////////////////////////////////////////////
    // ReplacementIterator class

    private class ReplacementIterator implements ITableIterator
    {

        /**
         * Logger for this class
         */
        private final Logger logger = LoggerFactory.getLogger(ReplacementIterator.class);

        private final ITableIterator _iterator;

        public ReplacementIterator(ITableIterator iterator)
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

            return createReplacementTable(_iterator.getTable());
        }
    }
}
