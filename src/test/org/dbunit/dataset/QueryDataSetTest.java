/*
 * QueryDataSetTest.java   Feb 18, 2002
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

package org.dbunit.dataset;

import org.dbunit.DatabaseEnvironment;
import org.dbunit.*;

import org.dbunit.database.*;
import org.dbunit.dataset.datatype.DataType;

import java.util.Arrays;
import java.util.Comparator;
import java.lang.reflect.Array;

/**
 * @author Manuel Laflamme
 * @version $Revision$
 */
public class QueryDataSetTest extends AbstractDataSetTest
{
    private static final String ESCAPE_PATTERN_KEY = "dbunit.name.escapePattern";

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

        _connection = DatabaseEnvironment.getInstance().getConnection();
    }

    protected void tearDown() throws Exception
    {
        super.tearDown();

        _connection = null;
    }

    ////////////////////////////////////////////////////////////////////////////
    // AbstractDataSetTest class

    protected IDataSet createDataSet() throws Exception
    {
        return _connection.createDataSet();
    }

    protected IDataSet createDuplicateDataSet() throws Exception
    {
        throw new UnsupportedOperationException();
    }

    protected void sort(Object[] array)
    {
        if (ITable[].class.isInstance(array))
        {
            Arrays.sort(array, new TableComparator());
        }
        else
        {
            Arrays.sort(array);
        }
    }

    private class TableComparator implements Comparator
    {
        public int compare(Object o1, Object o2)
        {
            String name1 = ((ITable)o1).getTableMetaData().getTableName();
            String name2 = ((ITable)o2).getTableMetaData().getTableName();

            return name1.compareTo(name2);
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    // Test methods

    public void testGetSelectStatement() throws Exception
    {
        String schemaName = "schema";
        String tableName = "table";
        String query = "select c1, c2, c3 from schema.table where c1 > 100";
        Column[] columns = new Column[]{
            new Column("c1", DataType.UNKNOWN),
            new Column("c2", DataType.UNKNOWN),
            new Column("c3", DataType.UNKNOWN),
        };
        String expected = "select c1, c2, c3 from schema.table where c1 > 100";

        ITableMetaData metaData = new DefaultTableMetaData(tableName, columns);
        QueryDataSet ptds = new QueryDataSet(_connection);
        ptds.addTable(tableName,query);

        String s = ptds.getQuery(tableName);
        assertEquals("where clause coming out",query,s);

    }


    public void testGetSelectStatementWith2Tables() throws Exception
    {
        String sql = null;
        String tableName="table";
        String tableName2="table2";
        String query = "select c1, c2, c3 from schema.table where c1 > 100 order by c1, c2, c3";
        String query2 = "select a1, a2, a3 from schema.table2 where c1 > 100 order by c1, c2, c3";



        QueryDataSet ptds = new QueryDataSet(_connection);
        ptds.addTable(tableName,query);

        String s = ptds.getQuery(tableName);
        assertEquals("where clause coming out",query,s);

        ptds.addTable(tableName2,query2);
        s = ptds.getQuery(tableName2);
        assertEquals("where clause coming out",query2,s);

    }

    public void testGetSelectPartialData() throws Exception
    {

        QueryDataSet ptds = new QueryDataSet(_connection);
        ptds.addTable("PK_TABLE","SELECT PK0, PK1 FROM PK_TABLE where PK0 = 0");

        ITable table = ptds.getTable("PK_TABLE");
        assertEquals("","0",table.getValue(0,"PK0").toString());
        assertEquals("","1",new String(table.getRowCount() + ""));

    }

    public void testGetAllColumnsWithStar() throws Exception
    {

        QueryDataSet ptds = new QueryDataSet(_connection);
        ptds.addTable("PK_TABLE","SELECT * FROM PK_TABLE where PK0 = 0");

        ITable table = ptds.getTable("PK_TABLE");
        assertEquals("","0",table.getValue(0,"PK0").toString());
        assertEquals("","1",new String(table.getRowCount() + ""));

    }

    public void testGetAllRowsSingleColumn() throws Exception
    {

        QueryDataSet ptds = new QueryDataSet(_connection);
        ptds.addTable("PK_TABLE","SELECT PK0 FROM PK_TABLE");

        ITable table = ptds.getTable("PK_TABLE");
        assertEquals("","0",table.getValue(0,"PK0").toString());
        assertEquals("","3",new String(table.getRowCount() + ""));
    }


    public void testOnlySpecifiedColumnsReturned() throws Exception
    {

        QueryDataSet ptds = new QueryDataSet(_connection);
        ptds.addTable("PK_TABLE","SELECT PK0 FROM PK_TABLE");

        ITable table = ptds.getTable("PK_TABLE");
        assertEquals("","0",table.getValue(0,"PK0").toString());

        try {
            String test = table.getValue(0,"PK1").toString();
            fail("Should not have reached here, we should have thrown a NoSuchColumnException");
        }
        catch (NoSuchColumnException nsce){
            String errorMsg = "org.dbunit.dataset.NoSuchColumnException: PK_TABLE.PK1";
            assertTrue("Find text:" + errorMsg,nsce.toString().indexOf(errorMsg)>=0);


        }
    }

    public void testGetSelectPartialData2() throws Exception
    {

        QueryDataSet ptds = new QueryDataSet(_connection);
        ptds.addTable("SECOND_TABLE","SELECT * FROM SECOND_TABLE where COLUMN0='row 0 col 0'");

        ITable table = ptds.getTable("SECOND_TABLE");
        assertEquals("","row 0 col 0",table.getValue(0,"COLUMN0").toString());
        assertEquals("","row 0 col 3",table.getValue(0,"COLUMN3").toString());
        assertEquals("","1",new String(table.getRowCount() + ""));

    }

    public void testCombinedWhere() throws Exception
    {

        QueryDataSet ptds = new QueryDataSet(_connection);
        ptds.addTable("SECOND_TABLE","SELECT COLUMN0, COLUMN3 FROM SECOND_TABLE where COLUMN0='row 0 col 0' and COLUMN2='row 0 col 2'");

        ITable table = ptds.getTable("SECOND_TABLE");
        assertEquals("","row 0 col 0",table.getValue(0,"COLUMN0").toString());
        assertEquals("","row 0 col 3",table.getValue(0,"COLUMN3").toString());
        assertEquals("","1",new String(table.getRowCount() + ""));

    }

    public void testMultipleTables() throws Exception
    {
        ITable table = null;

        QueryDataSet ptds = new QueryDataSet(_connection);
        ptds.addTable("SECOND_TABLE","SELECT * from SECOND_TABLE where COLUMN0='row 0 col 0' and COLUMN2='row 0 col 2'");
        ptds.addTable("PK_TABLE","SELECT * FROM PK_TABLE where PK0 = 0");

        table = ptds.getTable("SECOND_TABLE");
        assertEquals("","row 0 col 0",table.getValue(0,"COLUMN0").toString());
        assertEquals("","row 0 col 3",table.getValue(0,"COLUMN3").toString());
        assertEquals("","1",new String(table.getRowCount() + ""));

        table = ptds.getTable("PK_TABLE");
        assertEquals("","0",table.getValue(0,"PK0").toString());
        assertEquals("","1",new String(table.getRowCount() + ""));

    }

    /* This JUNIT test case only works against Hypersonic! */
    public void testLengthSyntax() throws Exception
    {
        if (DatabaseEnvironment.getInstance() instanceof HypersonicEnvironment){
            ITable table = null;

            QueryDataSet ptds = new QueryDataSet(_connection);
            ptds.addTable("ATABLE","CALL LENGTH('hello world')");
            table = ptds.getTable("ATABLE");
            assertEquals("","1",new String(table.getRowCount() + ""));
        }



    }

    public void testMultipleTablesWithMissingWhere() throws Exception
    {
        ITable table = null;

        QueryDataSet ptds = new QueryDataSet(_connection);
        ptds.addTable("SECOND_TABLE","SELECT * from SECOND_TABLE where COLUMN0='row 0 col 0' and COLUMN2='row 0 col 2'");
        ptds.addTable("PK_TABLE",null);

    }


    public void testGetDuplicateTable() throws Exception
    {
        // Cannot test! Unsupported feature.
    }

    public void testGetDuplicateTableMetaData() throws Exception
    {
        // Cannot test! Unsupported feature.
    }

    public void testGetDuplicateTableNames() throws Exception
    {
        // Cannot test! Unsupported feature.
    }

    public void testGetDuplicateTables() throws Exception
    {
        // Cannot test! Unsupported feature.
    }

    public void testGetCaseInsensitiveDuplicateTable() throws Exception
    {
        // Cannot test! Unsupported feature.
    }

    public void testGetCaseInsensitiveDuplicateTableMetaData() throws Exception
    {
        // Cannot test! Unsupported feature.
    }
}











