/*
 * InsertIdentityOperation.java   Apr 9, 2002
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

 */

package org.dbunit.operation;

import java.sql.SQLException;
import java.sql.Statement;

import org.dbunit.DatabaseUnitException;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.*;

/**
 * This class is a decorator that disable the MS SQL Server automatic
 * identifier generation during the execution of the decorated operation.
 * <p>
 * I don't have access to a SQL Server database and this class has not been
 * tested by me.
 *
 * @author Manuel Laflamme
 * @version $Revision$
 */
public class InsertIdentityOperation extends DatabaseOperation
{
    public static final DatabaseOperation INSERT =
            new InsertIdentityOperation(DatabaseOperation.INSERT);
    public static final DatabaseOperation CLEAN_INSERT =
            new CompositeOperation(DatabaseOperation.DELETE_ALL,
                    new InsertIdentityOperation(DatabaseOperation.INSERT));
    public static final DatabaseOperation REFRESH =
            new InsertIdentityOperation(DatabaseOperation.REFRESH);

    private final DatabaseOperation _operation;

    /**
     * Creates a new InsertIdentityOperation object that decorates the
     * specified operation.
     */
    public InsertIdentityOperation(DatabaseOperation operation)
    {
        _operation = operation;
    }

    ////////////////////////////////////////////////////////////////////////////
    // DatabaseOperation class

    public void execute(IDatabaseConnection connection, IDataSet dataSet)
            throws DatabaseUnitException, SQLException
    {
        Statement statement = connection.getConnection().createStatement();

        try
        {
            ITable[] tables = DataSetUtils.getTables(dataSet);
            for (int i = 0; i < tables.length; i++)
            {
                ITable table = tables[i];
                String name = DataSetUtils.getQualifiedName(
                        connection.getSchema(),
                        table.getTableMetaData().getTableName());

                // enable identity insert
                StringBuffer sqlBuffer = new StringBuffer(128);
                sqlBuffer.append("SET IDENTITY_INSERT ");
                sqlBuffer.append(name);
                sqlBuffer.append(" ON");
                statement.execute(sqlBuffer.toString());

                try
                {
                    _operation.execute(connection, new DefaultDataSet(table));
                }
                finally
                {
                    // disable identity insert
                    sqlBuffer = new StringBuffer(128);
                    sqlBuffer.append("SET IDENTITY_INSERT ");
                    sqlBuffer.append(name);
                    sqlBuffer.append(" OFF");
                    statement.execute(sqlBuffer.toString());
                }
            }
        }
        finally
        {
            statement.close();
        }
    }
}
