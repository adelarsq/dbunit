/*
 * UpdateOperation.java   Feb 19, 2002
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

package org.dbunit.operation;

import org.dbunit.DatabaseUnitException;
import org.dbunit.dataset.*;

/**
 * @author Manuel Laflamme
 * @version 1.0
 */
public class UpdateOperation extends AbstractRowOperation
{
    UpdateOperation()
    {
    }

    ////////////////////////////////////////////////////////////////////////////
    // AbstractRowOperation class

    String getOperationStatement(String schema, ITable table,
            int row) throws DatabaseUnitException
    {
        ITableMetaData metaData = table.getTableMetaData();
        Column[] columns = metaData.getColumns();
        Column[] primaryKeys = metaData.getPrimaryKeys();

        // cannot construct where clause if no primary key
        if (primaryKeys.length == 0)
        {
            throw new NoPrimaryKeyException(metaData.getTableName());
        }

        // update table
        String sql = "update " + DataSetUtils.getAbsoluteName(schema,
                metaData.getTableName());

        // set
        boolean firstSet = true;
        sql += " set ";
        for (int i = 0; i < columns.length; i++)
        {
            Column column = columns[i];


            // update only if not primary key
            if (DataSetUtils.getColumn(column.getColumnName(), primaryKeys) == null)
            {
                Object value = table.getValue(row, column.getColumnName());
                if (value != ITable.NO_VALUE)
                {
                    if (!firstSet)
                    {
                        sql += ", ";
                    }
                    firstSet = false;

                    sql += column.getColumnName() + " = " +
                            DataSetUtils.getSqlValueString(value, column.getDataType());

//                    // add comma if not last updatable column
//                    if (i + 1 < columns.length && DataSetUtils.getColumn(
//                            columns[i + 1].getColumnName(), primaryKeys) == null)
//                    {
//                        sql += ", ";
//                    }
                }
            }
        }

        // where
        sql += " where ";
        for (int i = 0; i < primaryKeys.length; i++)
        {
            Column key = primaryKeys[i];
            Object value = table.getValue(row, key.getColumnName());
            sql += key.getColumnName() + " = " +
                    DataSetUtils.getSqlValueString(value, key.getDataType());
            if (i + 1 < primaryKeys.length)
            {
                sql += " and ";
            }
        }

        return sql;
    }
}
