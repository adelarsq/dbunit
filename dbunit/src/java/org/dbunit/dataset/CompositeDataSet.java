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

import org.dbunit.database.AmbiguousTableNameException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Combines multiple datasets into a single logical dataset.
 *
 * @author Manuel Laflamme
 * @version $Revision$
 * @since Feb 19, 2002
 */
public class CompositeDataSet extends AbstractDataSet
{

    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(CompositeDataSet.class);

    private final ITable[] _tables;

    /**
     * Creates a composite dataset that combines specified datasets.
     * Tables having the same name are merged into one table.
     */
    public CompositeDataSet(IDataSet[] dataSets) throws DataSetException
    {
        this(dataSets, true);
    }

    /**
     * Creates a composite dataset that combines specified datasets.
     *
     * @param dataSets
     *      list of datasets
     * @param combine
     *      if <code>true</code>, tables having the same name are merged into
     *      one table.
     */
    public CompositeDataSet(IDataSet[] dataSets, boolean combine)
            throws DataSetException
    {
        this(dataSets, combine, false);
    }
    
    /**
     * Creates a composite dataset that combines specified datasets.
     *
     * @param dataSets
     *      list of datasets
     * @param combine
     *      if <code>true</code>, tables having the same name are merged into
     *      one table.
     * @param caseSensitiveTableNames Whether or not table names are handled in a case sensitive
     * way over all datasets.
     * @since 2.4.2
     */
    public CompositeDataSet(IDataSet[] dataSets, boolean combine, boolean caseSensitiveTableNames)
            throws DataSetException
    {
        super(caseSensitiveTableNames);
        
        // Check for duplicates using the OrderedTableNameMap as helper
        OrderedTableNameMap orderedTableMap = super.createTableNameMap();
        for (int i = 0; i < dataSets.length; i++)
        {
            IDataSet dataSet = dataSets[i];
            ITableIterator iterator = dataSet.iterator();
            while(iterator.next())
            {
                addTable(iterator.getTable(), orderedTableMap, combine);
            }
        }

        _tables = (ITable[]) orderedTableMap.orderedValues().toArray(new ITable[0]);
    }

    /**
     * Creates a composite dataset that combines the two specified datasets.
     * Tables having the same name are merged into one table.
     */
    public CompositeDataSet(IDataSet dataSet1, IDataSet dataSet2)
            throws DataSetException
    {
        this(new IDataSet[]{dataSet1, dataSet2});
    }

    /**
     * Creates a composite dataset that combines the two specified datasets.
     *
     * @param dataSet1
     *      first dataset
     * @param dataSet2
     *      second dataset
     * @param combine
     *      if <code>true</code>, tables having the same name are merged into
     *      one table.
     */
    public CompositeDataSet(IDataSet dataSet1, IDataSet dataSet2, boolean combine)
            throws DataSetException
    {
        this(new IDataSet[]{dataSet1, dataSet2}, combine);
    }

    /**
     * Creates a composite dataset that combines duplicate tables of the specified dataset.
     *
     * @param dataSet
     *      the dataset
     * @param combine
     *      if <code>true</code>, tables having the same name are merged into
     *      one table.
     * @deprecated This constructor is useless when the combine parameter is
     * <code>false</code>. Use overload that doesn't have the combine argument. 
     */
    public CompositeDataSet(IDataSet dataSet, boolean combine)
            throws DataSetException
    {
        this(new IDataSet[]{dataSet}, combine);
    }

    /**
     * Creates a composite dataset that combines duplicate tables of the specified dataset.
     *
     * @param dataSet
     *      the dataset
     */
    public CompositeDataSet(IDataSet dataSet) throws DataSetException
    {
        this(new IDataSet[]{dataSet}, true);
    }

    /**
     * Creates a composite dataset that combines tables having identical name.
     * Tables having the same name are merged into one table.
     */
    public CompositeDataSet(ITable[] tables) throws DataSetException
    {
        this(tables, false);
    }
    
    /**
     * Creates a composite dataset that combines tables having identical name.
     * Tables having the same name are merged into one table.
     * @param tables The tables to merge to one dataset
     * @param caseSensitiveTableNames Whether or not table names are handled in a case sensitive
     * way over all datasets.
     * @since 2.4.2
     */
    public CompositeDataSet(ITable[] tables, boolean caseSensitiveTableNames) throws DataSetException
    {
        super(caseSensitiveTableNames);
        
        OrderedTableNameMap orderedTableMap = super.createTableNameMap();
        for (int i = 0; i < tables.length; i++)
        {
            addTable(tables[i], orderedTableMap, true);
        }

        _tables = (ITable[]) orderedTableMap.orderedValues().toArray(new ITable[0]);
    }

    
    /**
     * @param newTable
     * @param tableMap
     * @param combine
     * @throws AmbiguousTableNameException Can only occur when the combine flag is set to <code>false</code>.
     */
    private void addTable(ITable newTable, OrderedTableNameMap tableMap, boolean combine) 
    throws AmbiguousTableNameException
    {
    	if (logger.isDebugEnabled())
    	{
    		logger.debug("addTable(newTable={}, tableList={}, combine={}) - start",
    				new Object[] { newTable, tableMap, String.valueOf(combine) });
    	}

        String tableName = newTable.getTableMetaData().getTableName();
        
        // No merge required, simply add new table at then end of the list
        if (!combine)
        {
            tableMap.add(tableName, newTable);
            return;
        }

        // Merge required, search for existing table with the same name
        ITable existingTable = (ITable) tableMap.get(tableName);
        if(existingTable != null) {
            // Found existing table, merge existing and new tables together
            tableMap.update(tableName, new CompositeTable(existingTable, newTable));
            return;
        }
        else {
            // No existing table found, add new table at the end of the list
            tableMap.add(tableName, newTable);
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    // AbstractDataSet class

    protected ITableIterator createIterator(boolean reversed)
            throws DataSetException
    {
        if(logger.isDebugEnabled())
            logger.debug("createIterator(reversed={}) - start", String.valueOf(reversed));
        
        return new DefaultTableIterator(_tables, reversed);
    }
}
