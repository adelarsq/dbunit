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

    public OperationData getOperationData(
            ITableMetaData metaData, IDatabaseConnection connection) throws DataSetException
    {
        Column[] columns = metaData.getColumns();

        // insert
        StringBuffer sqlBuffer = new StringBuffer(128);
        sqlBuffer.append("insert into ");
        sqlBuffer.append(getQualifiedName(connection.getSchema(),
                metaData.getTableName(), connection));

        // columns
        sqlBuffer.append(" (");
        for (int i = 0; i < columns.length; i++)
        {
            if (i > 0)
            {
                sqlBuffer.append(", ");
            }

            // escape column name
            String columnName = getQualifiedName(null,
                    columns[i].getColumnName(), connection);
             sqlBuffer.append(columnName);
        }

        // values
        sqlBuffer.append(") values (");
        for (int i = 0; i < columns.length; i++)
        {
            if (i > 0)
            {
                sqlBuffer.append(", ");
            }
            sqlBuffer.append("?");
        }
        sqlBuffer.append(")");

        return new OperationData(sqlBuffer.toString(), columns);
    }

}








