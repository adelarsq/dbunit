/*
 * RefreshOperation.java   Feb 19, 2002
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
import java.sql.Statement;

import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.*;

/**
 * @author Manuel Laflamme
 * @version 1.0
 */
public class RefreshOperation extends DatabaseOperation
{
    RefreshOperation()
    {
    }

    private String getUpdateStatement(String schema, ITable table,
            int row) throws DatabaseUnitException
    {
        return ((UpdateOperation)DatabaseOperation.UPDATE).getOperationStatement(
                schema, table, row);
    }

    private String getInsertStatement(String schema, ITable table,
            int row) throws DatabaseUnitException
    {
        return ((InsertOperation)DatabaseOperation.INSERT).getOperationStatement(
                schema, table, row);
    }

    private void executeRowAction(DatabaseConnection connection, ITable table,
            int row) throws DatabaseUnitException, SQLException
    {
        Statement statement = connection.getConnection().createStatement();
        try
        {
            // try to update row
            String updateSql = getUpdateStatement(connection.getSchema(), table, row);
            if (statement.executeUpdate(updateSql) == 0)
            {
                // no row updated, insert it
                String insertSql = getInsertStatement(connection.getSchema(), table, row);
                statement.executeUpdate(insertSql);
            }
        }
        finally
        {
            statement.close();
        }

    }

    ////////////////////////////////////////////////////////////////////////////
    // DatabaseOperation class

    public void execute(DatabaseConnection connection, IDataSet dataSet)
            throws DatabaseUnitException, SQLException
    {
        // this dataset is used to get metadata from database
        IDataSet databaseDataSet = connection.createDataSet();

        // for each table
        ITable[] tables = DataSetUtils.getTables(dataSet);
        for (int i = 0; i < tables.length; i++)
        {
            // use database metadata
            String name = tables[i].getTableMetaData().getTableName();
            ITableMetaData metaData = databaseDataSet.getTableMetaData(name);
            ITable table = new CompositeTable(metaData, dataSet.getTable(name));

            // execute action on each row
            for (int j = 0; j < table.getRowCount(); j++)
            {
                executeRowAction(connection, table, j);
            }
        }

    }
}
