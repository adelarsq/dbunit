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
package org.dbunit.dataset.filter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.dbunit.database.AmbiguousTableNameException;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.DataSetUtils;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITableIterator;
import org.dbunit.dataset.ITableMetaData;
import org.dbunit.dataset.NoSuchTableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This filter expose a specified table sequence and can be used to reorder
 * tables in a dataset. This implementation do not support duplicate table names.
 * Thus you cannot specify the same table name more than once in this filter
 * and the filtered dataset must not contains duplicate table names. This is
 * the default filter used by the {@link org.dbunit.dataset.FilteredDataSet}.
 *
 * @author Manuel Laflamme
 * @author Last changed by: $Author$
 * @version $Revision$ $Date$
 * @since Mar 7, 2003
 */
public class SequenceTableFilter implements ITableFilter
{

    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(SequenceTableFilter.class);

    private final String[] _tableNames;

    /**
     * Creates a new SequenceTableFilter with specified table names sequence.
     */
    public SequenceTableFilter(String[] tableNames)
    {
        _tableNames = tableNames;
    }

    private boolean accept(String tableName, String[] tableNames,
            boolean verifyDuplicate) throws AmbiguousTableNameException
    {
    	if(logger.isDebugEnabled())
    		logger.debug("accept(tableName={}, tableNames={}, verifyDuplicate={}) - start",
    				new Object[]{tableName, tableNames, new Boolean(verifyDuplicate) } );

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

    public boolean accept(String tableName) throws DataSetException
    {
        logger.debug("accept(tableName={}) - start", tableName);

        return accept(tableName, _tableNames, true);
    }

    public String[] getTableNames(IDataSet dataSet) throws DataSetException
    {
        logger.debug("getTableNames(dataSet={}) - start", dataSet);

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
    	if(logger.isDebugEnabled())
    		logger.debug("iterator(dataSet={}, reversed={}) - start", dataSet, new Boolean(reversed));

        String[] tableNames = getTableNames(dataSet);
        return new SequenceTableIterator(reversed ?
                DataSetUtils.reverseStringArray(tableNames) : tableNames, dataSet);
    }
    
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append(getClass().getName()).append("[");
        sb.append("tableNames=").append(_tableNames==null ? "null" : Arrays.asList(_tableNames).toString());
        sb.append("]");
        return sb.toString();
    }
}

