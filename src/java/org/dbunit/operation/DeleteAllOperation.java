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

import java.sql.SQLException;

import org.dbunit.DatabaseUnitException;
import org.dbunit.database.BatchStatement;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.*;

/**
 * @author Manuel Laflamme
 * @version 1.0
 */
public class DeleteAllOperation extends DatabaseOperation
{
    DeleteAllOperation()
    {
    }

    String getDeleteStatement(String schemaName,
            ITableMetaData metaData) throws DataSetException
    {
        return "delete from " + DataSetUtils.getAbsoluteName(schemaName,
                metaData.getTableName());
    }

    ////////////////////////////////////////////////////////////////////////////
    // DatabaseOperation class

    public void execute(DatabaseConnection connection, IDataSet dataSet)
            throws DatabaseUnitException, SQLException
    {
        BatchStatement statement = connection.createBatchStatment();
        try
        {
            String[] tableNames = DataSetUtils.getReverseTableNames(dataSet);
            for (int i = 0; i < tableNames.length; i++)
            {
                String name = tableNames[i];
                ITableMetaData metaData = dataSet.getTableMetaData(name);

                String sql = getDeleteStatement(connection.getSchema(), metaData);
                statement.add(sql);
            }

            statement.execute();
        }
        finally
        {
            statement.close();
        }
    }
}
