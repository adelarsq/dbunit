/*
 * AbstractTable.java   Feb 17, 2002
 *
 * The DbUnit Database Testing Framework
 * Copyright (C)2002-2004, DbUnit.org
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Manuel Laflamme
 * @version $Revision$
 * @since Feb 17, 2002
 */
public abstract class AbstractTable implements ITable
{

    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(AbstractTable.class);

    protected void assertValidRowIndex(int row) throws DataSetException
    {
        logger.debug("assertValidRowIndex(row=" + row + ") - start");

        assertValidRowIndex(row, getRowCount());
    }

    protected void assertValidRowIndex(int row, int rowCount)
            throws DataSetException
    {
        logger.debug("assertValidRowIndex(row=" + row + ", rowCount=" + rowCount + ") - start");

        if (row < 0)
        {
            throw new RowOutOfBoundsException(row + " < 0");
        }

        if (row >= rowCount)
        {
            throw new RowOutOfBoundsException(row + " >= " + rowCount);
        }
    }

    protected void assertValidColumn(String columnName) throws DataSetException
    {
        logger.debug("assertValidColumn(columnName=" + columnName + ") - start");

        ITableMetaData metaData = getTableMetaData();
        if (DataSetUtils.getColumn(columnName, metaData.getColumns()) == null)
        {
            throw new NoSuchColumnException(metaData.getTableName() + "." + columnName);
        }
    }

    protected int getColumnIndex(String columnName) throws DataSetException
    {
        logger.debug("getColumnIndex(columnName=" + columnName + ") - start");

        ITableMetaData metaData = getTableMetaData();
        Column[] columns = metaData.getColumns();
        for (int i = 0; i < columns.length; i++)
        {
            Column column = columns[i];
            if (column.getColumnName().equalsIgnoreCase(columnName))
            {
                return i;
            }
        }

        throw new NoSuchColumnException(metaData.getTableName() + "." + columnName);
    }

    ////////////////////////////////////////////////////////////////////////////
    // Object class

//    public String toString()
//    {
//
//        try
//        {
//            ITableMetaData metaData = getTableMetaData();
//            String tableName = metaData.getTableName();
//            String columns = Arrays.asList(metaData.getColumns()).toString();
//
//            return "[name=" + tableName + ", rowCount=" + getRowCount() +
//                    ", columns=" + columns + "]";
//        }
//        catch (DataSetException e)
//        {
//            return super.toString();
//        }
//    }
}






