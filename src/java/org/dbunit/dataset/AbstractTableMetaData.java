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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dbunit.dataset.filter.IColumnFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Manuel Laflamme
 * @version $Revision$
 * @since Mar 8, 2002
 */
public abstract class AbstractTableMetaData implements ITableMetaData
{

	private Map _columnsToIndexes;
	
    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(AbstractTableMetaData.class);

    private static final Column[] EMPTY_COLUMNS = new Column[0];

    protected static Column[] getPrimaryKeys(Column[] columns, String[] keyNames)
    {
        logger.debug("getPrimaryKeys(columns={}, keyNames={}) - start", columns, keyNames);

        if (keyNames == null || keyNames.length == 0)
        {
            return EMPTY_COLUMNS;
        }

        List keyList = new ArrayList();
        for (int i = 0; i < keyNames.length; i++)
        {
            Column primaryKey = DataSetUtils.getColumn(keyNames[i], columns);
            if (primaryKey != null)
            {
                keyList.add(primaryKey);
            }
        }

        return (Column[])keyList.toArray(new Column[0]);
    }

    protected static Column[] getPrimaryKeys(String tableName, Column[] columns,
            IColumnFilter columnFilter)
    {
    	if (logger.isDebugEnabled())
    	{
    		logger.debug("getPrimaryKeys(tableName={}, columns={}, columnFilter={}) - start",
    				new Object[]{ tableName, columns, columnFilter });
    	}

        List keyList = new ArrayList();
        for (int i = 0; i < columns.length; i++)
        {
            Column column = columns[i];
            if (columnFilter.accept(tableName, column))
            {
                keyList.add(column);
            }
        }

        return (Column[])keyList.toArray(new Column[0]);
    }

	/**
	 * Provides the index of the column with the given name within this table.
	 * Uses method {@link ITableMetaData#getColumns()} to retrieve all available columns.
	 * @throws DataSetException 
	 * @see org.dbunit.dataset.ITableMetaData#getColumnIndex(java.lang.String)
	 */
	public int getColumnIndex(String columnName) throws DataSetException 
	{
        logger.debug("getColumnIndex(columnName={}) - start", columnName);

        if(this._columnsToIndexes == null) 
		{
			// lazily create the map
			this._columnsToIndexes = createColumnIndexesMap(this.getColumns());
		}
		
        String columnNameUpperCase = columnName.toUpperCase();
		Integer colIndex = (Integer) this._columnsToIndexes.get(columnNameUpperCase);
		if(colIndex != null) 
		{
			return colIndex.intValue();
		}
		else 
		{
			throw new NoSuchColumnException(this.getTableName() + "." + columnNameUpperCase + 
					" (Non-uppercase input column: "+columnName+") in ColumnNameToIndexes cache map. " +
					"Note that the map's column names are NOT case sensitive.");
		}
	}

	/**
	 * @param columns The columns to be put into the hash table
	 * @return A hashtable having the key value pair [columnName, columnIndexInInputArray]
	 */
	private Map createColumnIndexesMap(Column[] columns) 
	{
		Map colsToIndexes = new HashMap(columns.length);
		for (int i = 0; i < columns.length; i++) 
		{
			colsToIndexes.put(columns[i].getColumnName().toUpperCase(), new Integer(i));
		}
		return colsToIndexes;
	}
    
}
