/*
 * DefaultDataSet.java   Feb 18, 2002
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


/**
 * @author Manuel Laflamme
 * @version 1.0
 */
public class DefaultDataSet extends AbstractDataSet
{
    private final ITable[] _tables;

    public DefaultDataSet(ITable table)
    {
        _tables = new ITable[]{table};
    }

    public DefaultDataSet(ITable[] tables)
    {
        _tables = tables;
    }

    ////////////////////////////////////////////////////////////////////////////
    // IDataSet interface

    public String[] getTableNames() throws DataSetException
    {
        String[] tableNames = new String[_tables.length];
        for (int i = 0; i < _tables.length; i++)
        {
            ITable table = _tables[i];
            tableNames[i] = table.getTableMetaData().getTableName();
        }
        return tableNames;
    }

    public ITableMetaData getTableMetaData(String tableName) throws DataSetException
    {
        return getTable(tableName).getTableMetaData();
    }

    public ITable getTable(String tableName) throws DataSetException
    {
        for (int i = 0; i < _tables.length; i++)
        {
            ITable table = _tables[i];
            if (tableName.equals(table.getTableMetaData().getTableName()))
            {
                return table;
            }
        }

        throw new NoSuchTableException(tableName);
    }
}
