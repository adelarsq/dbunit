/*
 * CompositeDataSet.java   Feb 19, 2002
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

import java.util.ArrayList;
import java.util.List;

/**
 * @author Manuel Laflamme
 * @version $Revision$
 */
public class CompositeDataSet extends AbstractDataSet
{
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
            ITable[] tables = DataSetUtils.getTables(dataSets[i]);
            for (int j = 0; j < tables.length; j++)
            {
                ITable table = tables[j];
                tableList.add(table);
            }
        }

        _tables = (ITable[])tableList.toArray(new ITable[0]);
        if (combine)
        {
            _tables = combineTables(_tables);
        }
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
     * Creates a composite dataset that combines dsuplicate tables of the specified dataset.
     *
     * @param dataSet
     *      the dataset
     * @param combine
     *      if <code>true</code>, tables having the same name are merged into
     *      one table.
     */
    public CompositeDataSet(IDataSet dataSet, boolean combine)
            throws DataSetException
    {
        this(new IDataSet[]{dataSet}, combine);
    }

    /**
     * Creates a composite dataset that combines tables having identical name.
     * Tables having the same name are merged into one table.
     */
    public CompositeDataSet(ITable[] tables) throws DataSetException
    {
        _tables = combineTables(tables);
    }

    private ITable[] combineTables(ITable[] tables) //throws DataSetException
    {
        List tableList = new ArrayList();

        // process each table
        for (int j = 0; j < tables.length; j++)
        {
            // search table in list
            ITable table = tables[j];
            int index = getTableIndex(
                    table.getTableMetaData().getTableName(), tableList);

            // not found, add new table in list
            if (index == -1)
            {
                tableList.add(table);
            }
            // found so combine them together
            else
            {
                table = new CompositeTable((ITable)tableList.get(index), table);
                tableList.set(index, table);
            }
        }

        return (ITable[])tableList.toArray(new ITable[0]);
    }

    private int getTableIndex(String tableName, List list)
    {
        for (int i = 0; i < list.size(); i++)
        {
            ITable table = (ITable)list.get(i);
            if (tableName.equalsIgnoreCase(table.getTableMetaData().getTableName()))
            {
                return i;
            }
        }

        return -1;
    }

    ////////////////////////////////////////////////////////////////////////////
    // AbstractDataSet class

    protected ITableIterator createIterator(boolean reversed)
            throws DataSetException
    {
        return new DefaultTableIterator(_tables, reversed);
    }
}







