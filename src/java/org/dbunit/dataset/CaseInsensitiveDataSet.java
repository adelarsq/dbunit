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

import org.dbunit.database.AmbiguousTableNameException;

/**
 * Allows access to a decorated dataset in a case insensitive way. Dataset
 * implementations provided by the framework are case sensitive. This class
 * allows using them in situation where case sensitiveness is not desirable.
 *
 * @author Manuel Laflamme
 * @version $Revision$
 * @deprecated All IDataSet implementations are case insensitive since DbUnit 1.5
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
        String found = null;
        String[] names = _dataSet.getTableNames();
        for (int i = 0; i < names.length; i++)
        {
            if (tableName.equalsIgnoreCase(names[i]))
            {
                if (found != null)
                {
                    throw new AmbiguousTableNameException(tableName);
                }
                found = names[i];
            }
        }

        if (found != null)
        {
            return found;
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

    public ITable[] getTables() throws DataSetException
    {
        return _dataSet.getTables();
    }
}



