/*
 * AbstractDatabaseTest.java   Feb 18, 2002
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

package org.dbunit;

import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.IDataSet;

/**
 * @author Manuel Laflamme
 * @version 1.0
 */
public abstract class AbstractDatabaseTest extends DatabaseTestCase
{
    protected DatabaseConnection _connection;

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
        String sql = "select * from " + tableName + " order by " + orderByColumn;
        return _connection.createQueryTable(tableName, sql);
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

        _connection = null;
    }

    ////////////////////////////////////////////////////////////////////////////
    // DatabaseTestCase class

    protected DatabaseConnection getConnection() throws Exception
    {
        return getEnvironment().getConnection();
    }

    protected IDataSet getDataSet() throws Exception
    {
        return getEnvironment().getInitDataSet();
    }

}
