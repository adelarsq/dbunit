/*
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
package org.dbunit.dataset.filter;

import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.ITableIterator;
import org.dbunit.dataset.ITableMetaData;

import java.util.ArrayList;
import java.util.List;

/**
 * This class provides a skeletal implementation of the {@link ITableFilter}
 * interface to minimize the effort required to implement a filter. Subsclasses
 * are only required to implement the {@link ITableFilter#isValidName} method.
 *
 * @author Manuel Laflamme
 * @since Mar 8, 2003
 * @version $Revision$
 */
public abstract class AbstractTableFilter implements ITableFilter
{

    ////////////////////////////////////////////////////////////////////////////
    // ITableFilter interface

    public String[] getTableNames(IDataSet dataSet) throws DataSetException
    {
        String[] tableNames = dataSet.getTableNames();
        List nameList = new ArrayList();
        for (int i = 0; i < tableNames.length; i++)
        {
            String tableName = tableNames[i];
            if (isValidName(tableName))
            {
                nameList.add(tableName);
            }
        }
        return (String[])nameList.toArray(new String[0]);
    }

    public ITableIterator iterator(IDataSet dataSet, boolean reversed)
            throws DataSetException
    {
        return new FilterIterator(reversed ?
                dataSet.reverseIterator() : dataSet.iterator());
    }

    ////////////////////////////////////////////////////////////////////////////
    // FilterIterator class

    private class FilterIterator implements ITableIterator
    {
        private final ITableIterator _iterator;

        public FilterIterator(ITableIterator iterator)
        {
            _iterator = iterator;
        }

        ////////////////////////////////////////////////////////////////////////////
        // ITableIterator interface

        public boolean next() throws DataSetException
        {
            while(_iterator.next())
            {
                if (isValidName(_iterator.getTableMetaData().getTableName()))
                {
                    return true;
                }
            }
            return false;
        }

        public ITableMetaData getTableMetaData() throws DataSetException
        {
            return _iterator.getTableMetaData();
        }

        public ITable getTable() throws DataSetException
        {
            return _iterator.getTable();
        }
    }
}
