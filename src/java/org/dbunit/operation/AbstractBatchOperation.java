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
     * Returns the metadata to use in this operation.
     *
     * @param connection the database connection
     * @param metaData the xml table metadata
     */
    static ITableMetaData getOperationMetaData(IDatabaseConnection connection,
            ITableMetaData metaData) throws DatabaseUnitException, SQLException
    {
        IDataSet databaseDataSet = connection.createDataSet();
        String tableName = metaData.getTableName();

        ITableMetaData databaseMetaData = databaseDataSet.getTableMetaData(tableName);
        Column[] databaseColumns = databaseMetaData.getColumns();
        Column[] columns = metaData.getColumns();

        List columnList = new ArrayList();
        for (int j = 0; j < columns.length; j++)
        {
            String columnName = columns[j].getColumnName();
            Column column = DataSetUtils.getColumn(
                    columnName, databaseColumns);
            if (column == null)
            {
                throw new NoSuchColumnException(tableName + "." +columnName);
            }
            columnList.add(column);
        }

        return new DefaultTableMetaData(tableName,
                (Column[])columnList.toArray(new Column[0]),
                databaseMetaData.getPrimaryKeys());
    }

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

            ITableMetaData metaData = getOperationMetaData(connection,
                    dataSet.getTableMetaData(tableName));
            OperationData operationData = getOperationData(
                    connection.getSchema(), metaData);

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










