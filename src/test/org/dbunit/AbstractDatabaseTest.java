/*
 * AbstractDatabaseTest.java   Feb 18, 2002
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
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.SortedTable;
import org.dbunit.operation.DatabaseOperation;

/**
 * @author Manuel Laflamme
 * @version $Revision$
 */
public abstract class AbstractDatabaseTest extends DatabaseTestCase
{
    private static final String ESCAPE_PATTERN_KEY = "dbunit.name.escapePattern";
    protected IDatabaseConnection _connection;

    public AbstractDatabaseTest(String s)
    {
        super(s);
    }

    protected DatabaseEnvironment getEnvironment() throws Exception
    {
        return DatabaseEnvironment.getInstance();
    }

    protected ITable createOrderedTable(String tableName, String orderByColumn)
            throws Exception
    {
        return new SortedTable(_connection.createDataSet().getTable(tableName),
                new String[]{orderByColumn});
//        String sql = "select * from " + tableName + " order by " + orderByColumn;
//        return _connection.createQueryTable(tableName, sql);
    }

    public static void setEscapePattern(String pattern)
    {
        if (pattern == null)
        {
            System.getProperties().remove(ESCAPE_PATTERN_KEY);
            return;
        }
        System.setProperty(ESCAPE_PATTERN_KEY, pattern);
    }

    ////////////////////////////////////////////////////////////////////////////
    // TestCase class

    protected void setUp() throws Exception
    {
        super.setUp();

        _connection = getEnvironment().getConnection();
    }

    protected void tearDown() throws Exception
    {
        super.tearDown();

        DatabaseOperation.DELETE_ALL.execute(_connection, _connection.createDataSet());

        _connection = null;
        setEscapePattern(null);
    }

    ////////////////////////////////////////////////////////////////////////////
    // DatabaseTestCase class

    protected IDatabaseConnection getConnection() throws Exception
    {
        IDatabaseConnection connection = getEnvironment().getConnection();
        return connection;

//        return new DatabaseEnvironment(getEnvironment().getProfile()).getConnection();
//        return new DatabaseConnection(connection.getConnection(), connection.getSchema());
    }

    protected IDataSet getDataSet() throws Exception
    {
        return getEnvironment().getInitDataSet();
    }

    protected void closeConnection(IDatabaseConnection connection) throws Exception
    {
//        getEnvironment().closeConnection();
    }
//
//    protected DatabaseOperation getTearDownOperation() throws Exception
//    {
//        return DatabaseOperation.DELETE_ALL;
//    }
}






