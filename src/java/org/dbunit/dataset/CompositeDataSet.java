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
    private final ITable[] _tables;

    /**
     * Creates a composite dataset that combines specified datasets.
     */
    public CompositeDataSet(IDataSet[] dataSets) throws DataSetException
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

        _tables = combineTables((ITable[])tableList.toArray(new ITable[0]));
    }

    /**
     * Creates a composite dataset that combines the two specified datasets.
     */
    public CompositeDataSet(IDataSet dataSet1, IDataSet dataSet2)
            throws DataSetException
    {
        this(new IDataSet[]{dataSet1, dataSet2});
    }

    /**
     * Creates a composite dataset that combines tables having identical name.
     */
    public CompositeDataSet(ITable[] tables) throws DataSetException
    {
        _tables = combineTables(tables);
    }

    private ITable[] combineTables(ITable[] tables) throws DataSetException
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
            if (tableName.equals(table.getTableMetaData().getTableName()))
            {
                return i;
            }
        }

        return -1;
    }

    ////////////////////////////////////////////////////////////////////////////
    // AbstractDataSet class

    protected ITable[] getTables() throws DataSetException
    {
        return _tables;
    }
}






