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

import org.dbunit.DatabaseUnitException;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.database.statement.IPreparedBatchStatement;
import org.dbunit.database.statement.IStatementFactory;
import org.dbunit.dataset.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Base implementation for database operation that are executed in batch.
 *
 * @author Manuel Laflamme
 * @version $Revision$
 */
public abstract class AbstractBatchOperation extends DatabaseOperation
{
    protected boolean _reverseRowOrder = false;

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
                throw new NoSuchColumnException(tableName + "." + columnName);
            }
            columnList.add(column);
        }

        return new DefaultTableMetaData(databaseMetaData.getTableName(),
                (Column[])columnList.toArray(new Column[0]),
                databaseMetaData.getPrimaryKeys());
    }

    /**
     * Returns list of tables this operation is applied to. This method
     * allow subclass to do filtering.
     */
    protected ITableIterator iterator(IDataSet dataSet) throws DatabaseUnitException
    {
        return dataSet.iterator();
    }

    abstract public OperationData getOperationData(String schemaName,
            ITableMetaData metaData) throws DataSetException;

    ////////////////////////////////////////////////////////////////////////////
    // DatabaseOperation class

    public void execute(IDatabaseConnection connection, IDataSet dataSet)
            throws DatabaseUnitException, SQLException
    {
        IStatementFactory factory = connection.getStatementFactory();

        // for each table
        ITableIterator iterator = iterator(dataSet);
        while(iterator.next())
        {
            ITable table = iterator.getTable();

            // do not process empty table
            if (table.getRowCount() == 0)
            {
                continue;
            }

            ITableMetaData metaData = getOperationMetaData(connection,
                    table.getTableMetaData());
            OperationData operationData = getOperationData(
                    connection.getSchema(), metaData);

            IPreparedBatchStatement statement = factory.createPreparedBatchStatement(
                    operationData.getSql(), connection);

            try
            {
                Column[] columns = operationData.getColumns();

                // for each row
                int rowCount = table.getRowCount();
                for (int i = 0; i < rowCount; i++)
                {
                    int row = _reverseRowOrder ? (rowCount - 1 - i) : i;

                    // for each column
                    for (int j = 0; j < columns.length; j++)
                    {
                        Column column = columns[j];
                        statement.addValue(table.getValue(row,
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












