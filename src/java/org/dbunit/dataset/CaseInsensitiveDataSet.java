/*
 * CaseInsensitiveDataSet.java   Mar 27, 2002
 *
 * Copyright (c)2002 Manuel Laflamme. All Rights Reserved.
 *
 * This software is the proprietary information of Manuel Laflamme.
 * Use is subject to license terms.
 *
 */

package org.dbunit.dataset;

/**
 * Allows access to a decorated dataset in a case insensitive way. Dataset
 * implementations provided by the framework are case sensitive. This class
 * allows using them in situation where case sensitiveness is not desirable.
 *
 * @author Manuel Laflamme
 * @version $Revision$
 */
public class CaseInsensitiveDataSet implements IDataSet
{
    private final IDataSet _dataSet;

    public CaseInsensitiveDataSet(IDataSet dataSet)
    {
        _dataSet = dataSet;
    }

    private String getInternalTableName(String tableName) throws DataSetException
    {
        String[] names = _dataSet.getTableNames();
        for (int i = 0; i < names.length; i++)
        {
            if (tableName.equalsIgnoreCase(names[i]))
            {
                return names[i];
            }
        }

        throw new NoSuchTableException(tableName);
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
        return _dataSet.getTableMetaData(getInternalTableName(tableName));
    }

    public ITable getTable(String tableName) throws DataSetException
    {
        ITable table = _dataSet.getTable(getInternalTableName(tableName));
        return new CaseInsensitiveTable(table);
    }
}



