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
 * Inserts the dataset contents into the database. This operation assumes that
 * table data does not exist in the database and fails if this is not the case.
 * To prevent problems with foreign keys, tables must be sequenced appropriately
 * in dataset.
 *
 * @author Manuel Laflamme
 * @version 1.0
 */
public class InsertOperation extends AbstractBatchOperation
{
    InsertOperation()
    {
    }

    ////////////////////////////////////////////////////////////////////////////
    // AbstractBatchOperation class

    public OperationData getOperationData(String schemaName,
            ITableMetaData metaData) throws DataSetException
    {
        Column[] columns = metaData.getColumns();

        // insert
        StringBuffer sqlBuffer = new StringBuffer();
        sqlBuffer.append("insert into ");
        sqlBuffer.append(DataSetUtils.getQualifiedName(schemaName,
                metaData.getTableName()));

        // columns
        sqlBuffer.append(" (");
        for (int i = 0; i < columns.length; i++)
        {
            if (i > 0)
            {
                sqlBuffer.append(", ");
            }
            sqlBuffer.append(columns[i].getColumnName());
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


