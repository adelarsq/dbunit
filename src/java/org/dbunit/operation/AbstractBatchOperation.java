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
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.statement.IPreparedBatchStatement;
import org.dbunit.database.statement.IStatementFactory;
import org.dbunit.dataset.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.math.BigInteger;

/**
 * Base implementation for database operation that are executed in batch.
 *
 * @author Manuel Laflamme
 * @version $Revision$
 */
public abstract class AbstractBatchOperation extends DatabaseOperation
{
    private static final BigInteger EMPTY_IGNORE_MAPPING = new BigInteger("0");
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

    static boolean isEmpty(ITable table) throws DataSetException
    {
        Column[] columns = table.getTableMetaData().getColumns();

        // No columns = empty
        if (columns.length == 0)
        {
            return true;
        }

        // Try to fetch first table value
        try
        {
            table.getValue(0, columns[0].getColumnName());
            return false;
        }
        catch (RowOutOfBoundsException e)
        {
            // Not able to access first row thus empty
            return true;
        }
    }

    /**
     * Returns list of tables this operation is applied to. This method
     * allow subclass to do filtering.
     */
    protected ITableIterator iterator(IDataSet dataSet) throws DatabaseUnitException
    {
        return dataSet.iterator();
    }

    /**
     * Returns mapping of columns to ignore by this operation. Each bit set represent
     * a column to ignore.
     */
    BigInteger getIngnoreMapping(ITable table, int row)
            throws DataSetException
    {
        return EMPTY_IGNORE_MAPPING;
    }

    /**
     * Returns false if the specified table row have a different ignore mapping
     * than the specified mapping.
     */
    boolean equalsIgnoreMapping(BigInteger ignoreMapping, ITable table,
            int row) throws DataSetException
    {
        return true;
    }

    abstract OperationData getOperationData(ITableMetaData metaData,
            BigInteger ignoreMapping, IDatabaseConnection connection) throws DataSetException;

    ////////////////////////////////////////////////////////////////////////////
    // DatabaseOperation class

    public void execute(IDatabaseConnection connection, IDataSet dataSet)
            throws DatabaseUnitException, SQLException
    {
        DatabaseConfig databaseConfig = connection.getConfig();
        IStatementFactory factory = (IStatementFactory)databaseConfig.getProperty(
                DatabaseConfig.PROPERTY_STATEMENT_FACTORY);

        // for each table
        ITableIterator iterator = iterator(dataSet);
        while (iterator.next())
        {
            ITable table = iterator.getTable();

            // Do not process empty table
            if (isEmpty(table))
            {
                continue;
            }

            ITableMetaData metaData = getOperationMetaData(connection,
                    table.getTableMetaData());
            BigInteger ignoreMapping = null;
            OperationData operationData = null;
            IPreparedBatchStatement statement = null;

            try
            {
                // For each row
                int start = _reverseRowOrder ? table.getRowCount() - 1 : 0;
                int increment = _reverseRowOrder ? -1 : 1;

                try
                {
                    for (int i = start; ; i = i + increment)
                    {
                        int row = i;

                        // If current row have a diffrent ignore value mapping than
                        // previous one, we generate a new statement
                        if (ignoreMapping == null || !equalsIgnoreMapping(ignoreMapping, table, row))
                        {
                            // Execute and close previous statement
                            if (statement != null)
                            {
                                statement.executeBatch();
                                statement.clearBatch();
                                statement.close();
                            }

                            ignoreMapping = getIngnoreMapping(table, row);
                            operationData = getOperationData(metaData, ignoreMapping, connection);
                            statement = factory.createPreparedBatchStatement(
                                    operationData.getSql(), connection);
                        }


                        // for each column
                        Column[] columns = operationData.getColumns();
                        for (int j = 0; j < columns.length; j++)
                        {
                            // Bind value only if not in ignore mapping
                            if (!ignoreMapping.testBit(j))
                            {
                                Column column = columns[j];
                                statement.addValue(table.getValue(row,
                                        column.getColumnName()), column.getDataType());
                            }
                        }
                        statement.addBatch();
                    }
                }
                catch (RowOutOfBoundsException e)
                {
                    // end of table
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












