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

import org.dbunit.database.AmbiguousTableNameException;
import org.dbunit.dataset.*;

import java.util.ArrayList;
import java.util.List;

/**
 * This filter expose a specified table sequence and can be used to reorder
 * dataset table names. This implementation do not support duplicate table names.
 * Thus you cannot specify the same table name more than once in this filter
 * and the filtered dataset must not contains duplicate table names. This is
 * the default filter used by the {@link FilteredDataSet}.
 *
 * @author Manuel Laflamme
 * @since Mar 7, 2003
 * @version $Revision$
 */
public class SequenceTableFilter implements ITableFilter
{
    private final String[] _tableNames;

    /**
     * Creates a new SequenceTableFilter with specified table names sequence.
     */
    public SequenceTableFilter(String[] tableNames)
    {
        _tableNames = tableNames;
    }

    private boolean isValidName(String tableName, String[] tableNames,
            boolean verifyDuplicate) throws AmbiguousTableNameException
    {
        boolean found = false;
        for (int i = 0; i < tableNames.length; i++)
        {
            if (tableName.equalsIgnoreCase(tableNames[i]))
            {
                if (!verifyDuplicate)
                {
                    return true;
                }

                if (found)
                {
                    throw new AmbiguousTableNameException(tableName);
                }
                found = true;
            }
        }

        return found;
    }

    ////////////////////////////////////////////////////////////////////////////
    // ITableFilter interface

    public boolean isValidName(String tableName) throws DataSetException
    {
        return isValidName(tableName, _tableNames, true);
    }

    public String[] getTableNames(IDataSet dataSet) throws DataSetException
    {
        List nameList = new ArrayList();
        for (int i = 0; i < _tableNames.length; i++)
        {
            try
            {
                // Use the table name from the filtered dataset. This ensure
                // that table names are having the same case (lower/upper) from
                // getTableNames() and getTables() methods.
                ITableMetaData metaData = dataSet.getTableMetaData(_tableNames[i]);
                nameList.add(metaData.getTableName());
            }
            catch (NoSuchTableException e)
            {
                // Skip this table name because the filtered dataset does not
                // contains it.
            }
        }

        return (String[])nameList.toArray(new String[0]);
    }

    public ITableIterator iterator(IDataSet dataSet, boolean reversed)
            throws DataSetException
    {
        String[] tableNames = getTableNames(dataSet);
        return new SequenceTableIterator(reversed ?
                DataSetUtils.reverseStringArray(tableNames) : tableNames, dataSet);
    }
}

