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
package org.dbunit.dataset;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Manuel Laflamme
 * @since Mar 14, 2003
 * @version $Revision$
 */
public class BooleanDataSet implements IDataSet
{
    private final IDataSet _dataSet;
    private final Object _trueValue;
    private final Object _falseValue;

    public BooleanDataSet(IDataSet dataSet)
    {
        _dataSet = dataSet;
        _falseValue = BooleanTable.DEFAULT_FALSE_VALUE;
        _trueValue = BooleanTable.DEFAULT_TRUE_VALUE;
    }

    ////////////////////////////////////////////////////////////////////////////
    // IDataSet interface

    public String[] getTableNames() throws DataSetException
    {
        return _dataSet.getTableNames();
    }

    public ITableMetaData getTableMetaData(String tableName)
            throws DataSetException
    {
        return _dataSet.getTableMetaData(tableName);
    }

    public ITable getTable(String tableName) throws DataSetException
    {
        return new BooleanTable(_dataSet.getTable(tableName), _trueValue, _falseValue);
    }

    public ITable[] getTables() throws DataSetException
    {
        List tableList = new ArrayList();
        ITable[] tables = _dataSet.getTables();
        for (int i = 0; i < tables.length; i++)
        {
            ITable table = tables[i];
            tableList.add(new BooleanTable(table, _trueValue, _falseValue));
        }
        return (ITable[])tableList.toArray(new ITable[0]);
    }

}
