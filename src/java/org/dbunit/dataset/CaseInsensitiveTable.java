/*
 * CaseInsensitiveTable.java   Mar 27, 2002
 *
 * Copyright (c)2002 Manuel Laflamme. All Rights Reserved.
 *
 * This software is the proprietary information of Manuel Laflamme.
 * Use is subject to license terms.
 *
 */

package org.dbunit.dataset;

/**
 * @author Manuel Laflamme
 * @version 1.0
 */
public class CaseInsensitiveTable implements ITable
{
    private final ITable _table;

    public CaseInsensitiveTable(ITable table)
    {
        _table = table;
    }

    private String getInternalColumnName(String columnName)
            throws DataSetException
    {
        Column[] columns = _table.getTableMetaData().getColumns();

        for (int i = 0; i < columns.length; i++)
        {
            Column column = columns[i];
            if (columnName.equalsIgnoreCase(column.getColumnName()))
            {
                return column.getColumnName();
            }
        }

        throw new NoSuchColumnException(columnName);
    }

    ////////////////////////////////////////////////////////////////////////////
    // ITable interface

    public ITableMetaData getTableMetaData()
    {
        return _table.getTableMetaData();
    }

    public int getRowCount()
    {
        return _table.getRowCount();
    }

    public Object getValue(int row, String column) throws DataSetException
    {
        return _table.getValue(row, getInternalColumnName(column));
    }
}
