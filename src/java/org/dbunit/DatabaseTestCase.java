/*
 * DatabaseTestCase.java   Feb 17, 2002
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

package org.dbunit;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.operation.DatabaseOperation;

import junit.framework.TestCase;

/**
 * @author Manuel Laflamme
 * @version $Revision$
 */
public abstract class DatabaseTestCase extends TestCase
{
    public DatabaseTestCase()
    {
    }

    public DatabaseTestCase(String name)
    {
        super(name);
    }

    /**
     * Returns the test database connection.
     */
    protected abstract IDatabaseConnection getConnection() throws Exception;

    /**
     * Returns the test dataset.
     */
    protected abstract IDataSet getDataSet() throws Exception;

    /**
     * Close the specified connection. Ovverride this method of you want to
     * keep your connection alive between tests.
     */
    protected void closeConnection(IDatabaseConnection connection) throws Exception
    {
        connection.close();
    }

    /**
     * Returns the database operation executed in test setup.
     */
    protected DatabaseOperation getSetUpOperation() throws Exception
    {
        return DatabaseOperation.CLEAN_INSERT;
    }

    /**
     * Returns the database operation executed in test cleanup.
     */
    protected DatabaseOperation getTearDownOperation() throws Exception
    {
        return DatabaseOperation.NONE;
    }

    private void executeOperation(DatabaseOperation operation) throws Exception
    {
        if (operation != DatabaseOperation.NONE)
        {
            IDatabaseConnection connection = getConnection();
            try
            {
                operation.execute(connection, getDataSet());
            }
            finally
            {
                closeConnection(connection);
            }
        }
    }


    ////////////////////////////////////////////////////////////////////////////
    // TestCase class

    protected void setUp() throws Exception
    {
        super.setUp();

        executeOperation(getSetUpOperation());
    }

    protected void tearDown() throws Exception
    {
        super.tearDown();

        executeOperation(getTearDownOperation());
    }
}





