/*
 * CompositeOperation.java   Feb 18, 2002
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
public class InsertOperation extends AbstractRowOperation
{
//    private static final Object NO_VALUE = new Object();

    InsertOperation()
    {
    }

    ////////////////////////////////////////////////////////////////////////////
    // AbstractRowOperation class

    String getOperationStatement(String schemaName, ITable table,
            int row) throws DatabaseUnitException
    {
        ITableMetaData metaData = table.getTableMetaData();
        Column[] columns = metaData.getColumns();
        Object[] values = new Object[columns.length];

        // insert
        String sql = "insert into " + DataSetUtils.getAbsoluteName(schemaName,
                metaData.getTableName());

        // column
        boolean firstColumn = true;
        sql += " (";
        for (int i = 0; i < columns.length; i++)
        {
            Column column = columns[i];

            values[i] = table.getValue(row, column.getColumnName());
            if (values[i] != ITable.NO_VALUE)
            {

                if (!firstColumn)
                {
                    sql += ", ";
                }
                firstColumn = false;

                sql += column.getColumnName();
            }
        }

        // values
        boolean firstValue = true;
        sql += ") values (";
        for (int i = 0; i < columns.length; i++)
        {
            Column column = columns[i];
            Object value = table.getValue(row, column.getColumnName());
            if (value != ITable.NO_VALUE)
            {
                if (!firstValue)
                {
                    sql += ", ";
                }
                firstValue = false;

                sql += DataSetUtils.getSqlValueString(value, column.getDataType());
            }
        }
        sql += ")";

        return sql;
    }

}
