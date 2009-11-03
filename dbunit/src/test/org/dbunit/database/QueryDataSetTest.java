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

import org.dbunit.DatabaseEnvironment;
import org.dbunit.dataset.AbstractDataSetTest;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.NoSuchColumnException;
import org.dbunit.operation.DatabaseOperation;

/**
 * @author Manuel Laflamme
 * @version $Revision$
 * @since Feb 18, 2002
 */
public class QueryDataSetTest extends AbstractDataSetTest
{
    private IDatabaseConnection _connection;

    public QueryDataSetTest(String s)
    {
        super(s);
    }

    ////////////////////////////////////////////////////////////////////////////
    // TestCase class

    protected void setUp() throws Exception
    {
        super.setUp();

        DatabaseEnvironment env = DatabaseEnvironment.getInstance();
        _connection = env.getConnection();

        DatabaseOperation.CLEAN_INSERT.execute(_connection, env.getInitDataSet());
    }

    protected void tearDown() throws Exception
    {
        super.tearDown();

        _connection = null;
    }

    ////////////////////////////////////////////////////////////////////////////
    // AbstractDataSetTest class

    protected String[] getExpectedNames() throws Exception
    {
        return getExpectedLowerNames();
    }

    protected IDataSet createDataSet() throws Exception
    {
        String[] names = getExpectedNames();

        QueryDataSet dataSet = new QueryDataSet(_connection);
        for (int i = 0; i < names.length; i++)
        {
            String name = names[i];
            String query = "select * from " + name;
            dataSet.addTable(name, query);
/*
            if (i % 2 == 0)
            {
                String query = "select * from " + name;
                dataSet.addTable(name, query);
            }
            else
            {
                dataSet.addTable(name);
            }
*/
        }
        return dataSet;
    }

    protected IDataSet createDuplicateDataSet() throws Exception
    {
        QueryDataSet dataSet = new QueryDataSet(_connection);
        String[] names = getExpectedDuplicateNames();

        // first table expect 1 row
        String queryOneRow = "select * from only_pk_table";
        dataSet.addTable(names[0], queryOneRow);

        // second table expect 0 row
        String queryNoRow = "select * from empty_table";
        dataSet.addTable(names[1], queryNoRow);

        // third table expect 2 row
        String queryTwoRow = "select * from pk_table where PK0=0 or PK0=1";
        dataSet.addTable(names[2], queryTwoRow);

        return dataSet;
    }

    protected IDataSet createMultipleCaseDuplicateDataSet() throws Exception {
        QueryDataSet dataSet = new QueryDataSet(_connection);
        String[] names = getExpectedDuplicateNames();

        // first table expect 1 row
        String queryOneRow = "select * from only_pk_table";
        dataSet.addTable(names[0], queryOneRow);

        // second table expect 0 row
        String queryNoRow = "select * from empty_table";
        dataSet.addTable(names[1], queryNoRow);

        // third table expect 2 row
        String queryTwoRow = "select * from pk_table where PK0=0 or PK0=1";
        dataSet.addTable(names[2].toLowerCase(), queryTwoRow); // lowercase table name which should fail as well

        return dataSet;
    }

    
    ////////////////////////////////////////////////////////////////////////////
    // Test methods

    public void testGetSelectPartialData() throws Exception
    {

        QueryDataSet ptds = new QueryDataSet(_connection);
        ptds.addTable("PK_TABLE", "SELECT PK0, PK1 FROM pk_table where PK0 = 0");

        ITable table = ptds.getTable("PK_TABLE");
        assertEquals("", "0", table.getValue(0, "PK0").toString());
        assertEquals("", "1", new String(table.getRowCount() + ""));

    }

    public void testGetAllColumnsWithStar() throws Exception
    {

        QueryDataSet ptds = new QueryDataSet(_connection);
        ptds.addTable("PK_TABLE", "SELECT * FROM pk_table where PK0 = 0");

        ITable table = ptds.getTable("PK_TABLE");
        assertEquals("", "0", table.getValue(0, "PK0").toString());
        assertEquals("", "1", new String(table.getRowCount() + ""));

    }

    public void testGetAllRowsSingleColumn() throws Exception
    {

        QueryDataSet ptds = new QueryDataSet(_connection);
        ptds.addTable("PK_TABLE", "SELECT PK0 FROM pk_table");

        ITable table = ptds.getTable("PK_TABLE");
        assertEquals("", "0", table.getValue(0, "PK0").toString());
        assertEquals("", "3", new String(table.getRowCount() + ""));
    }


    public void testOnlySpecifiedColumnsReturned() throws Exception
    {

        QueryDataSet ptds = new QueryDataSet(_connection);
        ptds.addTable("PK_TABLE", "SELECT PK0 FROM pk_table");

        ITable table = ptds.getTable("PK_TABLE");
        assertEquals("", "0", table.getValue(0, "PK0").toString());

        try
        {
            table.getValue(0, "PK1").toString();
            fail("Should not have reached here, we should have thrown a NoSuchColumnException");
        }
        catch (NoSuchColumnException nsce)
        {
            String errorMsg = "org.dbunit.dataset.NoSuchColumnException: PK_TABLE.PK1";
            assertTrue("Find text:" + errorMsg, nsce.toString().indexOf(errorMsg) >= 0);
        }
    }

    public void testGetSelectPartialData2() throws Exception
    {

        QueryDataSet ptds = new QueryDataSet(_connection);
        ptds.addTable("SECOND_TABLE",
                "SELECT * FROM second_table where COLUMN0='row 0 col 0'");

        ITable table = ptds.getTable("SECOND_TABLE");
        assertEquals("", "row 0 col 0", table.getValue(0, "COLUMN0").toString());
        assertEquals("", "row 0 col 3", table.getValue(0, "COLUMN3").toString());
        assertEquals("", "1", new String(table.getRowCount() + ""));

    }

    public void testCombinedWhere() throws Exception
    {

        QueryDataSet ptds = new QueryDataSet(_connection);
        ptds.addTable("SECOND_TABLE",
                "SELECT COLUMN0, COLUMN3 FROM second_table where COLUMN0='row 0 col 0' and COLUMN2='row 0 col 2'");

        ITable table = ptds.getTable("SECOND_TABLE");
        assertEquals("", "row 0 col 0", table.getValue(0, "COLUMN0").toString());
        assertEquals("", "row 0 col 3", table.getValue(0, "COLUMN3").toString());
        assertEquals("", "1", new String(table.getRowCount() + ""));

    }

    public void testMultipleTables() throws Exception
    {
        ITable table = null;

        QueryDataSet ptds = new QueryDataSet(_connection);
        ptds.addTable("SECOND_TABLE",
                "SELECT * from second_table where COLUMN0='row 0 col 0' and COLUMN2='row 0 col 2'");
        ptds.addTable("PK_TABLE",
                "SELECT * FROM pk_table where PK0 = 0");

        table = ptds.getTable("SECOND_TABLE");
        assertEquals("", "row 0 col 0", table.getValue(0, "COLUMN0").toString());
        assertEquals("", "row 0 col 3", table.getValue(0, "COLUMN3").toString());
        assertEquals("", "1", new String(table.getRowCount() + ""));

        table = ptds.getTable("PK_TABLE");
        assertEquals("", "0", table.getValue(0, "PK0").toString());
        assertEquals("", "1", new String(table.getRowCount() + ""));

    }

    public void testMultipleTablesWithMissingWhere() throws Exception
    {
        QueryDataSet ptds = new QueryDataSet(_connection);
        ptds.addTable("SECOND_TABLE",
                "SELECT * from second_table where COLUMN0='row 0 col 0' and COLUMN2='row 0 col 2'");
        ptds.addTable("PK_TABLE", null);
    }
}











