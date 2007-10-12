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

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

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

    private ITable[] _tables;

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
        List tableList = new ArrayList();
        for (int i = 0; i < dataSets.length; i++)
        {
            IDataSet dataSet = dataSets[i];
            ITableIterator iterator = dataSet.iterator();
            while(iterator.next())
            {
                addTable(iterator.getTable(), tableList, combine);
            }
        }

        _tables = (ITable[])tableList.toArray(new ITable[0]);
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
        List tableList = new ArrayList();
        for (int i = 0; i < tables.length; i++)
        {
            addTable(tables[i], tableList, true);
        }

        _tables = (ITable[])tableList.toArray(new ITable[0]);
    }

    private void addTable(ITable newTable, List tableList, boolean combine)
    {
        logger.debug("addTable(newTable=" + newTable + ", tableList=" + tableList + ", combine=" + combine
                + ") - start");

        // No merge required, simply add new table at then end of the list
        if (!combine)
        {
            tableList.add(newTable);
            return;
        }

        // Merge required, search for existing table with the same name
        String tableName = newTable.getTableMetaData().getTableName();
        for (ListIterator it = tableList.listIterator(); it.hasNext();)
        {
            ITable table = (ITable)it.next();
            if (tableName.equalsIgnoreCase(table.getTableMetaData().getTableName()))
            {
                // Found existing table, merge existing and new tables together
                it.set(new CompositeTable(table, newTable));
                return;
            }
        }

        // No existing table found, add new table at the end of the list
        tableList.add(newTable);
    }

    ////////////////////////////////////////////////////////////////////////////
    // AbstractDataSet class

    protected ITableIterator createIterator(boolean reversed)
            throws DataSetException
    {
        logger.debug("createIterator(reversed=" + reversed + ") - start");

        return new DefaultTableIterator(_tables, reversed);
    }
}







