/*
 * CompositeDataSet.java   Feb 19, 2002
 *
 * The dbUnit database testing framework.
 * Copyright (C) 2002   Manuel Laflamme
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
 * @version 1.0
 */
public class CompositeDataSet extends DefaultDataSet
{
    public CompositeDataSet(IDataSet[] dataSets) throws DataSetException
    {
        super(combineTables(dataSets));
    }

    public CompositeDataSet(IDataSet dataSet1, IDataSet dataSet2) throws DataSetException
    {
        super(combineTables(new IDataSet[]{dataSet1, dataSet2}));
    }

    private static ITable[] combineTables(IDataSet[] dataSets)
            throws DataSetException
    {
        List tableList = new ArrayList();

        // process each dataset
        for (int i = 0; i < dataSets.length; i++)
        {
            // process each table
            ITable[] tables = DataSetUtils.getTables(dataSets[i]);
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
                    table = new CompositeTable((ITable)tableList.get(index),
                            table);
                    tableList.set(index, table);
                }
            }
        }

        return (ITable[])tableList.toArray(new ITable[0]);
    }

    private static int getTableIndex(String tableName, List list)
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
}
