/*
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
package org.dbunit.dataset.filter;

import org.dbunit.dataset.AbstractTableIteratorTest;
import org.dbunit.dataset.ITableIterator;
import org.dbunit.dataset.filter.SequenceTableIterator;
import org.dbunit.DatabaseEnvironment;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.database.DatabaseDataSet;

/**
 * @author Manuel Laflamme
 * @since Apr 6, 2003
 * @version $Revision$
 */
public class SequenceTableIteratorTest extends AbstractTableIteratorTest
{
    protected IDatabaseConnection _connection;

    public SequenceTableIteratorTest(String s)
    {
        super(s);
    }

    ////////////////////////////////////////////////////////////////////////////
    // TestCase class

    protected void setUp() throws Exception
    {
        super.setUp();

        _connection = DatabaseEnvironment.getInstance().getConnection();
    }

    protected void tearDown() throws Exception
    {
        super.tearDown();

        _connection = null;
    }

    protected String[] getExpectedNames() throws Exception
    {
        return _connection.createDataSet().getTableNames();
    }

    protected ITableIterator getIterator() throws Exception
    {
        return _connection.createDataSet().iterator();
    }

    protected ITableIterator getEmptyIterator() throws Exception
    {
        return new SequenceTableIterator(new String[0],
                (DatabaseDataSet)_connection.createDataSet());
    }
}
