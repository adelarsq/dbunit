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

import java.sql.SQLException;

import org.dbunit.DatabaseUnitException;
import org.dbunit.database.*;
import org.dbunit.database.statement.*;
import org.dbunit.dataset.*;

/**
 * Deletes entire database table contents for each table contained in dataset.
 * In other words, if a dataset does not contain a particular table, but that
 * table exists in the database, the contents of that table is not deleted.
 * Deletes are performed on table in reverse sequence.
 *
 * @author Manuel Laflamme
 * @version $Revision$
 */
public class DeleteAllOperation extends DatabaseOperation
{
    DeleteAllOperation()
    {
    }

    ////////////////////////////////////////////////////////////////////////////
    // DatabaseOperation class

    public void execute(IDatabaseConnection connection, IDataSet dataSet)
            throws DatabaseUnitException, SQLException
    {
        IStatementFactory statementFactory = connection.getStatementFactory();
        IBatchStatement statement = statementFactory.createBatchStatement(connection);
        try
        {
            String[] tableNames = DataSetUtils.getReverseTableNames(dataSet);
            for (int i = 0; i < tableNames.length; i++)
            {
                String name = tableNames[i];
                ITableMetaData metaData = dataSet.getTableMetaData(name);

                StringBuffer sqlBuffer = new StringBuffer(128);
                sqlBuffer.append("delete from ");
                sqlBuffer.append(DataSetUtils.getQualifiedName(
                        connection.getSchema(), metaData.getTableName()));
                statement.addBatch(sqlBuffer.toString());
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





