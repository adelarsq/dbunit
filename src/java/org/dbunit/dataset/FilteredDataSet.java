/*
 * FilteredDataSet.java   Feb 22, 2002
 *
 * The DbUnit Database Testing Framework
 * Copyright (C)2002, Manuel Laflamme
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

import org.dbunit.dataset.filter.IncludeTableFilter;
import org.dbunit.dataset.filter.SequenceTableFilter;
import org.dbunit.dataset.filter.ExcludeTableFilter;
import org.dbunit.dataset.filter.ITableFilter;

/**
 * Decorates a dataset and exposes only some tables from it.
 *
 * @see ITableFilter
 * @see SequenceTableFilter
 * @see IncludeTableFilter
 * @see ExcludeTableFilter
 *
 * @author Manuel Laflamme
 * @version $Revision$
 */
public class FilteredDataSet extends AbstractDataSet
{
    private final IDataSet _dataSet;
    private final ITableFilter _filter;

    /**
     * Creates a FilteredDataSet that decorates the specified dataset and
     * exposes only the specified tables. Use the {@link SequenceTableFilter} as
     * filtering startegy.
     */
    public FilteredDataSet(String[] tableNames, IDataSet dataSet)
    {
        _filter = new SequenceTableFilter(tableNames);
        _dataSet = dataSet;
    }

    /**
     * Creates a FilteredDataSet that decorates the specified dataset and
     * exposes only the tables allowed by the specified filtering strategy.
     *
     * @param dataSet the filtered dataset
     * @param filter the filtering strategy
     */
    public FilteredDataSet(ITableFilter filter, IDataSet dataSet)
    {
        _dataSet = dataSet;
        _filter = filter;
    }

    ////////////////////////////////////////////////////////////////////////////
    // AbstractDataSet class

    protected ITableIterator createIterator(boolean reversed)
            throws DataSetException
    {
        return _filter.iterator(_dataSet, reversed);
    }

    ////////////////////////////////////////////////////////////////////////////
    // IDataSet interface

    public String[] getTableNames() throws DataSetException
    {
        return _filter.getTableNames(_dataSet);
    }

    public ITableMetaData getTableMetaData(String tableName)
            throws DataSetException
    {
        if (!_filter.isValidName(tableName))
        {
            throw new NoSuchTableException(tableName);
        }

        return _dataSet.getTableMetaData(tableName);
    }

    public ITable getTable(String tableName) throws DataSetException
    {
        if (!_filter.isValidName(tableName))
        {
            throw new NoSuchTableException(tableName);
        }

        return _dataSet.getTable(tableName);
    }
}







