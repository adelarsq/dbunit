/*
 * AbstractTable.java   Feb 17, 2002
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

import java.util.Arrays;

/**
 * @author Manuel Laflamme
 * @version 1.0
 */
public abstract class AbstractTable implements ITable
{
    protected void assertValidRowIndex(int row) throws DataSetException
    {
        if (row < 0)
        {
            throw new RowOutOfBoundsException(row + " < 0");
        }

        if (row >= getRowCount())
        {
            throw new RowOutOfBoundsException(row + " > " + getRowCount());
        }
    }

    protected int getColumnIndex(String columnName) throws NoSuchColumnException
    {
        ITableMetaData metaData = getTableMetaData();
        Column[] columns = metaData.getColumns();
        for (int i = 0; i < columns.length; i++)
        {
            Column column = columns[i];
            if (column.getColumnName().equals(columnName))
            {
                return i;
            }
        }

        throw new NoSuchColumnException("<" + columnName +
                "> not a column of table <" + metaData.getTableName() + ">");
    }

    ////////////////////////////////////////////////////////////////////////////
    // Object class

    public String toString()
    {
        ITableMetaData metaData = getTableMetaData();
        String tableName = metaData.getTableName();
        String columns = Arrays.asList(metaData.getColumns()).toString();

        return "[name=" + tableName + ", rowCount=" + getRowCount() +
                ", columns=" + columns + "]";
    }
}
