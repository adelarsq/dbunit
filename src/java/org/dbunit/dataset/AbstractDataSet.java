/*
 * AbstractDataSet.java   Feb 22, 2002
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

import org.dbunit.database.AmbiguousTableNameException;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

/**
 * This abstract class provides the basic implementation of the IDataSet
 * interface. Subclass are only required to implement the {@link #createIterator}
 * method.
 *
 * @author Manuel Laflamme
 * @version $Revision$
 */
public abstract class AbstractDataSet implements IDataSet
{
    protected ITable[] cloneTables(ITable[] tables)
    {
        ITable[] clones = new ITable[tables.length];
        for (int i = 0; i < tables.length; i++)
        {
            clones[i] = tables[i];
        }
        return clones;
    }

    protected abstract ITableIterator createIterator(boolean reversed)
            throws DataSetException;

    ////////////////////////////////////////////////////////////////////////////
    // IDataSet interface

    public String[] getTableNames() throws DataSetException
    {
        List tableNameList = new ArrayList();
        ITableIterator iterator = createIterator(false);
        while (iterator.next())
        {
            tableNameList.add(iterator.getTableMetaData().getTableName());
        }
        return (String[])tableNameList.toArray(new String[0]);
    }

    public ITableMetaData getTableMetaData(String tableName) throws DataSetException
    {
        return getTable(tableName).getTableMetaData();
    }

    public ITable getTable(String tableName) throws DataSetException
    {
        ITable found = null;
        ITableIterator iterator = createIterator(false);
        while (iterator.next())
        {
            ITable table = iterator.getTable();
            if (tableName.equalsIgnoreCase(table.getTableMetaData().getTableName()))
            {
                if (found != null)
                {
                    throw new AmbiguousTableNameException(tableName);
                }

                found = table;
            }
        }

        if (found != null)
        {
            return found;
        }

        throw new NoSuchTableException(tableName);
    }

    public ITable[] getTables() throws DataSetException
    {
        List tableList = new ArrayList();
        ITableIterator iterator = createIterator(false);
        while (iterator.next())
        {
            tableList.add(iterator.getTable());
        }
        return (ITable[])tableList.toArray(new ITable[0]);
    }

    public ITableIterator iterator() throws DataSetException
    {
        return createIterator(false);
    }

    public ITableIterator reverseIterator() throws DataSetException
    {
        return createIterator(true);
    }

    ////////////////////////////////////////////////////////////////////////////
    // Object class

    public String toString()
    {
        try
        {
            return Arrays.asList(getTableNames()).toString();
        }
        catch (DataSetException e)
        {
            return super.toString();
        }
    }
}






