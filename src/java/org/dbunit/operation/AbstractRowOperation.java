/*
 * AbstractRowOperation.java   Feb 19, 2002
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
 * Base implementation for database operation that are applied on every
 * table rows.
 *
 * @author Manuel Laflamme
 * @version 1.0
 */
public abstract class AbstractRowOperation extends DatabaseOperation
{
    /**
     * Returns statement for the specified table at the specified row. This
     * template method must be implemented by subclass.
     */
    abstract String getOperationStatement(String schema, ITable table,
            int row) throws DatabaseUnitException;

    /**
     * Returns list of table names this operation is applied to. This method
     * allow subclass to do filtering.
     */
    protected String[] getTableNames(IDataSet dataSet) throws DatabaseUnitException
    {
        return dataSet.getTableNames();
    }

    /**
     * Returns all statements to be applied to the specified table.
     */
    String[] getOperationStatements(String schema, ITable table)
            throws DatabaseUnitException
    {
        ITableMetaData metaData = table.getTableMetaData();

        String[] sql = new String[table.getRowCount()];
        for (int i = 0; i < sql.length; i++)
        {
            sql[i] = getOperationStatement(schema, table, i);
        }

        return sql;
    }

    ////////////////////////////////////////////////////////////////////////////
    // DatabaseOperation class

    public void execute(DatabaseConnection connection, IDataSet dataSet)
            throws DatabaseUnitException, SQLException
    {
        // this dataset is used to get metadata from database
        IDataSet databaseDataSet = connection.createDataSet();

        BatchStatement statement = connection.createBatchStatment();
        try
        {
            String[] tableNames = getTableNames(dataSet);
            for (int i = 0; i < tableNames.length; i++)
            {
                String name = tableNames[i];

                // use database metadata
                ITableMetaData metaData = databaseDataSet.getTableMetaData(name);
                ITable table = new CompositeTable(metaData, dataSet.getTable(name));

                String[] sql = getOperationStatements(connection.getSchema(), table);
                for (int j = 0; j < sql.length; j++)
                {
                    statement.add(sql[j]);
                }

                statement.execute();
                statement.clear();
            }
        }
        finally
        {
            statement.close();
        }
    }
}
