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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Decorator that replace configured values from the decorated dataset
 * with replacement values.
 *
 * @author Manuel Laflamme
 * @since Mar 17, 2003
 * @version $Revision$
 */
public class ReplacementDataSet implements IDataSet
{
    private final IDataSet _dataSet;
    private final Map _objectMap;
    private final Map _substringMap;

    /**
     * Create a new ReplacementDataSet object that decorates the specified dataset.
     *
     * @param dataSet the decorated table
     */
    public ReplacementDataSet(IDataSet dataSet)
    {
        _dataSet = dataSet;
        _objectMap = new HashMap();
        _substringMap = new HashMap();
    }

    /**
     * Add a new Object replacement mapping.
     *
     * @param originalObject the object to replace
     * @param replacementObject the replacement object
     */
    public void addReplacementObject(Object originalObject, Object replacementObject)
    {
        _objectMap.put(originalObject, replacementObject);
    }

    /**
     * Add a new substring replacement mapping.
     *
     * @param originalSubstring the substring to replace
     * @param replacementSubstring the replacement substring
     */
    public void addReplacementSubstring(String originalSubstring,
            String replacementSubstring)
    {
        if (originalSubstring == null || replacementSubstring == null)
        {
            throw new NullPointerException();
        }

        _substringMap.put(originalSubstring, replacementSubstring);
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
        return new ReplacementTable(_dataSet.getTable(tableName), _objectMap,
                _substringMap);
    }

    public ITable[] getTables() throws DataSetException
    {
        List tableList = new ArrayList();
        ITable[] tables = _dataSet.getTables();
        for (int i = 0; i < tables.length; i++)
        {
            ITable table = tables[i];
            tableList.add(new ReplacementTable(table, _objectMap, _substringMap));
        }
        return (ITable[])tableList.toArray(new ITable[0]);
    }

}
