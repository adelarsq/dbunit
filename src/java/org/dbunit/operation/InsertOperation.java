/*
 * CompositeOperation.java   Feb 18, 2002
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

package org.dbunit.operation;

import org.dbunit.dataset.*;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.DatabaseUnitException;

import java.math.BigInteger;

/**
 * Inserts the dataset contents into the database. This operation assumes that
 * table data does not exist in the database and fails if this is not the case.
 * To prevent problems with foreign keys, tables must be sequenced appropriately
 * in dataset.
 *
 * @author Manuel Laflamme
 * @version $Revision$
 */
public class InsertOperation extends AbstractBatchOperation
{
    InsertOperation()
    {
    }

    ////////////////////////////////////////////////////////////////////////////
    // AbstractBatchOperation class

    public OperationData getOperationData(ITableMetaData metaData,
            BigInteger ignoreMapping, IDatabaseConnection connection) throws DataSetException
    {
        Column[] columns = metaData.getColumns();

        // insert
        StringBuffer sqlBuffer = new StringBuffer(128);
        sqlBuffer.append("insert into ");
        sqlBuffer.append(getQualifiedName(connection.getSchema(),
                metaData.getTableName(), connection));

        // columns
        sqlBuffer.append(" (");
        String columnSeparator = "";
        for (int i = 0; i < columns.length; i++)
        {
            if (!ignoreMapping.testBit(i))
            {
                // escape column name
                String columnName = getQualifiedName(null,
                        columns[i].getColumnName(), connection);
                sqlBuffer.append(columnSeparator);
                sqlBuffer.append(columnName);
                columnSeparator = ", ";
            }
        }

        // values
        sqlBuffer.append(") values (");
        String valueSeparator = "";
        for (int i = 0; i < columns.length; i++)
        {
            if (!ignoreMapping.testBit(i))
            {
                sqlBuffer.append(valueSeparator);
                sqlBuffer.append("?");
                valueSeparator = ", ";
            }
        }
        sqlBuffer.append(")");

        return new OperationData(sqlBuffer.toString(), columns);
    }

    protected BigInteger getIngnoreMapping(ITable table, int row) throws DataSetException
    {
        Column[] columns = table.getTableMetaData().getColumns();
        int n = columns.length;
        int byteNum = n / 8;
        byte[] result = new byte[byteNum + 2];

        for (int i = 0; i < n; i++)
        {
            Object value = table.getValue(row, columns[i].getColumnName());
            if (value == null)
            {
                result[result.length - (i / 8) - 1] |= (1 << (i % 8));
            }
        }
        return new BigInteger(result);
    }

    protected boolean equalsIgnoreMapping(BigInteger ignoreMapping, ITable table,
            int row) throws DataSetException
    {
        Column[] columns = table.getTableMetaData().getColumns();

        for (int i = 0; i < columns.length; i++)
        {
            boolean bit = ignoreMapping.testBit(i);
            Object value = table.getValue(row, columns[i].getColumnName());
            if ((bit && value != null) || (!bit && value == null))
            {
                return false;
            }
        }

        return true;
    }
}








