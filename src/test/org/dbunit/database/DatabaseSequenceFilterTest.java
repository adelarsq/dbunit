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

import java.io.File;
import java.sql.Connection;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.dbunit.HypersonicEnvironment;
import org.dbunit.dataset.FilteredDataSet;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.filter.ITableFilter;

/**
 * @author Manuel Laflamme
 * @since May 8, 2004
 * @version $Revision$
 */
public class DatabaseSequenceFilterTest extends TestCase
{
    Connection _jdbcConnection;

    public DatabaseSequenceFilterTest(String s)
    {
        super(s);
    }

    protected void setUp() throws Exception
    {
        super.setUp();

        _jdbcConnection = HypersonicEnvironment.createJdbcConnection("tempdb");
    }

    protected void tearDown() throws Exception
    {
        super.tearDown();

        HypersonicEnvironment.shutdown(_jdbcConnection);
        _jdbcConnection.close();

        HypersonicEnvironment.deleteFiles("tempdb");
    }

    public void testGetTableNames() throws Exception
    {
        String[] expectedNoFilter = {"A","B","C","D","E","F","G","H",};
        String[] expectedFiltered = {"D","A","F","C","G","E","H","B",};

        HypersonicEnvironment.executeDdlFile(new File("src/sql/hypersonic_fk.sql"),
                _jdbcConnection);
        IDatabaseConnection connection = new DatabaseConnection(_jdbcConnection);

        IDataSet databaseDataset = connection.createDataSet();
        String[] actualNoFilter = databaseDataset.getTableNames();
        assertEquals("no filter", Arrays.asList(expectedNoFilter), Arrays.asList(actualNoFilter));

        ITableFilter filter = new DatabaseSequenceFilter(connection);
        IDataSet filteredDataSet = new FilteredDataSet(filter, databaseDataset);
        String[] actualFiltered = filteredDataSet.getTableNames();
        assertEquals("filtered", Arrays.asList(expectedFiltered), Arrays.asList(actualFiltered));
    }


    public void testGetTableNamesCyclic() throws Exception
    {
        String[] expectedNoFilter = {"A","B","C","D","E",};

        HypersonicEnvironment.executeDdlFile(new File("src/sql/hypersonic_cyclic.sql"),
                _jdbcConnection);
        IDatabaseConnection connection = new DatabaseConnection(_jdbcConnection);

        IDataSet databaseDataset = connection.createDataSet();
        String[] actualNoFilter = databaseDataset.getTableNames();
        assertEquals("no filter", Arrays.asList(expectedNoFilter), Arrays.asList(actualNoFilter));

        try
        {
            ITableFilter filter = new DatabaseSequenceFilter(connection);
            IDataSet filteredDataSet = new FilteredDataSet(filter, databaseDataset);
            filteredDataSet.getTableNames();
            fail("Should not be here!");
        }
        catch (CyclicTablesDependencyException expected)
        {
            Set expectedCycle = new HashSet(Arrays.asList(new String[]{"A", "C", "E"}));
            assertEquals(expectedCycle.toString(), expected.getMessage());
        }
    }


}
