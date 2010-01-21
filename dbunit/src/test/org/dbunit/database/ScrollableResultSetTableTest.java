/*
 *
 * The DbUnit Database Testing Framework
 * Copyright (C)2002-2004, DbUnit.org
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

package org.dbunit.database;

import org.dbunit.AbstractDatabaseIT;
import org.dbunit.DatabaseEnvironment;
import org.dbunit.TestFeature;
import org.dbunit.dataset.AbstractTableTest;
import org.dbunit.dataset.ITable;
import org.dbunit.operation.DatabaseOperation;

/**
 * @author Manuel Laflamme
 * @version $Revision$
 * @since Feb 19, 2002
 */
public class ScrollableResultSetTableTest extends AbstractTableTest
{
    public ScrollableResultSetTableTest(String s)
    {
        super(s);
    }
    
    protected boolean runTest(String testName) {
      return AbstractDatabaseIT.environmentHasFeature(TestFeature.SCROLLABLE_RESULTSET);
    }
    
    protected ITable createTable() throws Exception
    {
        DatabaseEnvironment env = DatabaseEnvironment.getInstance();
        IDatabaseConnection connection = env.getConnection();

        DatabaseOperation.CLEAN_INSERT.execute(connection, env.getInitDataSet());

        String selectStatement = "select * from TEST_TABLE order by COLUMN0";
        return new ScrollableResultSetTable("TEST_TABLE", selectStatement, connection);
    }

    public void testGetMissingValue() throws Exception
    {
        // Do not test this!
    }
}





