/*
 * TransactionOperation.java   Feb 21, 2002
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

import java.sql.Connection;
import java.sql.SQLException;

import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.IDataSet;

/**
 * @author Manuel Laflamme
 * @version 1.0
 */
public class TransactionOperation extends DatabaseOperation
{
    private final DatabaseOperation _operation;

    public TransactionOperation(DatabaseOperation operation)
    {
        _operation = operation;
    }

    ////////////////////////////////////////////////////////////////////////////
    // DatabaseOperation class

    public void execute(DatabaseConnection connection, IDataSet dataSet)
            throws DatabaseUnitException, SQLException
    {
        DatabaseConnection databaseConnection = connection;
        Connection jdbcConnection = databaseConnection.getConnection();

        if (jdbcConnection.getAutoCommit() == false)
        {
            throw new ExclusiveTransactionException();
        }

        jdbcConnection.setAutoCommit(false);
        try
        {
            _operation.execute(databaseConnection, dataSet);
            jdbcConnection.commit();
        }
        catch (DatabaseUnitException e)
        {
            jdbcConnection.rollback();
            throw e;
        }
        catch (SQLException e)
        {
            jdbcConnection.rollback();
            throw e;
        }
        catch (RuntimeException e)
        {
            jdbcConnection.rollback();
            throw e;
        }
        finally
        {
            jdbcConnection.setAutoCommit(true);
        }
    }
}
