/*
 * AbstractBatchOperation.java   Feb 19, 2002
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

import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

import org.dbunit.DatabaseUnitException;
import org.dbunit.database.*;
import org.dbunit.database.statement.*;
import org.dbunit.dataset.*;

/**
 * Base implementation for database operation that are executed in batch.
 *
 * @author Manuel Laflamme
 * @version $Revision$
 */
public abstract class AbstractBatchOperation extends DatabaseOperation
{
    /**
     * Returns list of table names this operation is applied to. This method
     * allow subclass to do filtering.
     */
    protected String[] getTableNames(IDataSet dataSet) throws DatabaseUnitException
    {
        return dataSet.getTableNames();
    }

    abstract public OperationData getOperationData(String schemaName,
            ITableMetaData metaData) throws DataSetException;

    ////////////////////////////////////////////////////////////////////////////
    // DatabaseOperation class

    public void execute(IDatabaseConnection connection, IDataSet dataSet)
            throws DatabaseUnitException, SQLException
    {
        // this dataset is used to get metadata from database
        IDataSet databaseDataSet = connection.createDataSet();
        IStatementFactory factory = connection.getStatementFactory();
        String[] tableNames = getTableNames(dataSet);

        // for each table
        for (int i = 0; i < tableNames.length; i++)
        {
            // do not process empty table
            String tableName = tableNames[i];
            ITable table = dataSet.getTable(tableName);
            if (table.getRowCount() == 0)
            {
                continue;
            }

            // use database columns metadata
            ITableMetaData databaseMetaData = databaseDataSet.getTableMetaData(tableName);
            Column[] datasetColumns = dataSet.getTableMetaData(tableName).getColumns();
            Column[] databaseColumns = databaseMetaData.getColumns();

            List columnList = new ArrayList();
            for (int j = 0; j < datasetColumns.length; j++)
            {
                String columnName = datasetColumns[j].getColumnName();
                Column column = DataSetUtils.getColumn(
                        columnName, databaseColumns);
                if (column == null)
                {
                    throw new NoSuchColumnException(columnName);
                }
                columnList.add(column);
            }

            ITableMetaData metaData = new DefaultTableMetaData(tableName,
                    (Column[])columnList.toArray(new Column[0]),
                    databaseMetaData.getPrimaryKeys());
            OperationData operationData = getOperationData(
                    connection.getSchema(), metaData);

//            OperationData operationData = getOperationData(
//                    connection.getSchema(), databaseMetaData);
            IPreparedBatchStatement statement = factory.createPreparedBatchStatement(
                    operationData.getSql(), connection);

            try
            {
                Column[] columns = operationData.getColumns();

                // for each row
                for (int j = 0; j < table.getRowCount(); j++)
                {
                    // for each column
                    for (int k = 0; k < columns.length; k++)
                    {
                        Column column = columns[k];
                        statement.addValue(table.getValue(j,
                                column.getColumnName()), column.getDataType());
                    }
                    statement.addBatch();
                }

                statement.executeBatch();
                statement.clearBatch();
            }
            finally
            {
                statement.close();
            }
        }
    }
}








