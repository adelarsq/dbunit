/*
 * RefreshOperation.java   Feb 19, 2002
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
import org.dbunit.dataset.datatype.DataType;

import java.sql.*;

/**
 * This operation literally refreshes dataset contents into the database. This
 * means that data of existing rows is updated and non-existing row get
 * inserted. Any rows which exist in the database but not in dataset stay
 * unaffected.
 *
 * @author Manuel Laflamme
 * @version $Revision$
 */
public class RefreshOperation extends DatabaseOperation
{
    private final InsertOperation _insertOperation;
    private final UpdateOperation _updateOperation;

    RefreshOperation()
    {
        _insertOperation = (InsertOperation)DatabaseOperation.INSERT;
        _updateOperation = (UpdateOperation)DatabaseOperation.UPDATE;
    }

    private boolean rowExist(PreparedStatement statement,
            Column[] columns, ITable table, int row)
            throws DataSetException, SQLException
    {
        for (int i = 0; i < columns.length; i++)
        {
            Object value = table.getValue(row, columns[i].getColumnName());
            DataType dataType = columns[i].getDataType();
            statement.setObject(i + 1, dataType.typeCast(value),
                    dataType.getSqlType());
        }

        ResultSet resultSet = statement.executeQuery();
        try
        {
            resultSet.next();
            return resultSet.getInt(1) > 0;
        }
        finally
        {
            resultSet.close();
        }
    }

    public OperationData getSelectCountData(String schemaName,
            ITableMetaData metaData) throws DataSetException
    {
        Column[] primaryKeys = metaData.getPrimaryKeys();

        // cannot construct where clause if no primary key
        if (primaryKeys.length == 0)
        {
            throw new NoPrimaryKeyException(metaData.getTableName());
        }

        // select count
        StringBuffer sqlBuffer = new StringBuffer(128);
        sqlBuffer.append("select COUNT(*) from ");
        sqlBuffer.append(DataSetUtils.getQualifiedName(schemaName,
                metaData.getTableName(), true));

        // where
        sqlBuffer.append(" where ");
        for (int i = 0; i < primaryKeys.length; i++)
        {
            Column column = primaryKeys[i];

            if (i > 0)
            {
                sqlBuffer.append(" and ");
            }
            sqlBuffer.append(column.getColumnName());
            sqlBuffer.append(" = ?");
        }

        return new OperationData(sqlBuffer.toString(), primaryKeys);
    }

    private int executeRowOperation(IPreparedBatchStatement statement,
            Column[] columns, ITable table, int row)
            throws DataSetException, SQLException
    {
        for (int i = 0; i < columns.length; i++)
        {
            Object value = table.getValue(row, columns[i].getColumnName());
            statement.addValue(value, columns[i].getDataType());
        }
        statement.addBatch();
        int result = statement.executeBatch();
        statement.clearBatch();

        return result;
    }

    ////////////////////////////////////////////////////////////////////////////
    // DatabaseOperation class

    public void execute(IDatabaseConnection connection, IDataSet dataSet)
            throws DatabaseUnitException, SQLException
    {
        IStatementFactory factory = connection.getStatementFactory();
        String schema = connection.getSchema();

        // this dataset is used to get metadata from database
        IDataSet databaseDataSet = connection.createDataSet();

        // for each table
        ITable[] tables = dataSet.getTables();
        for (int i = 0; i < tables.length; i++)
        {
            // do not process empty table
            ITable table = tables[i];
            if (table.getRowCount() == 0)
            {
                continue;
            }

            ITableMetaData metaData = AbstractBatchOperation.getOperationMetaData(
                    connection, table.getTableMetaData());

            // setup select count statement
            OperationData countData = getSelectCountData(schema, metaData);
            PreparedStatement countStatement =
                    connection.getConnection().prepareStatement(countData.getSql());

            // setup insert statement
            OperationData insertData = _insertOperation.getOperationData(schema, metaData);
            IPreparedBatchStatement insertStatement =
                    factory.createPreparedBatchStatement(insertData.getSql(), connection);

            // setup update statement
            OperationData updateData = _updateOperation.getOperationData(schema, metaData);
            IPreparedBatchStatement updateStatement =
                    factory.createPreparedBatchStatement(updateData.getSql(), connection);

            // refresh each table's row
            for (int j = 0; j < table.getRowCount(); j++)
            {
                // verify if row exist
                if (rowExist(countStatement, countData.getColumns(), table, j))
                {
                    // update only if columns are not all primary keys
                    if (metaData.getColumns().length > metaData.getPrimaryKeys().length)
                    {
                        // update row
                        executeRowOperation(updateStatement, updateData.getColumns(),
                                table, j);
                    }
                }
                else
                {
                    // insert row
                    executeRowOperation(insertStatement, insertData.getColumns(),
                            table, j);
                }
            }
        }

    }
}













