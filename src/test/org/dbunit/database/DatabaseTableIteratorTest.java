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

import org.dbunit.dataset.AbstractTableIteratorTest;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.ITableIterator;
import org.dbunit.dataset.MockDataSet;

/**
 * @author Manuel Laflamme
 * @since Apr 6, 2003
 * @version $Revision$
 */
public class DatabaseTableIteratorTest extends AbstractTableIteratorTest
{
    public DatabaseTableIteratorTest(String s)
    {
        super(s);
    }

    private MockDataSet createMockDataSet(String[] expectedNames)
    {
        MockDataSet dataSet = new MockDataSet();
        for (int i = 0; i < expectedNames.length; i++)
        {
            String tableName = expectedNames[i];
            MockResultSetTable table = new MockResultSetTable();
            table.setupTableMetaData(tableName);
            table.setExpectedCloseCalls(1);
            dataSet.addTable(table);
        }
        return dataSet;
    }

    protected ITableIterator getIterator() throws Exception
    {
        String[] expectedNames = getExpectedNames();
        MockDataSet dataSet = createMockDataSet(expectedNames);

        return new DatabaseTableIterator(expectedNames, dataSet);
    }

    protected ITableIterator getEmptyIterator() throws Exception
    {
        return new DatabaseTableIterator(new String[0], new MockDataSet());
    }

    public void testGetTableClose() throws Exception
    {
        int i = 0;
        String[] expectedNames = getExpectedNames();
        MockDataSet dataSet = createMockDataSet(expectedNames);

        ITableIterator iterator = new DatabaseTableIterator(expectedNames, dataSet);
        while(iterator.next())
        {
            ITable table = iterator.getTable();
            assertEquals("name " + i, expectedNames[i],
                    table.getTableMetaData().getTableName());
            i++;
        }

        assertEquals("count", expectedNames.length, i);
        dataSet.verify();
    }
}
