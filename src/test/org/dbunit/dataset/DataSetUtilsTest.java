/*
 * DataSetUtilsTest.java   Feb 19, 2002
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

package org.dbunit.dataset;

import org.dbunit.dataset.datatype.DataType;
import junit.framework.TestCase;

/**
 * @author Manuel Laflamme
 * @version 1.0
 */
public class DataSetUtilsTest extends TestCase
{
    public DataSetUtilsTest(String s)
    {
        super(s);
    }

    public void testGetAbsoluteName() throws Exception
    {
        String schemaName = "schema";
        String tableName = "table";

        assertEquals("with schema", "schema.table",
                DataSetUtils.getAbsoluteName(schemaName, tableName));
        assertEquals("no schema", "table",
                DataSetUtils.getAbsoluteName(null, tableName));
    }

    public void testGetColumn() throws Exception
    {
        Column[] columns = new Column[]{
            new Column("c0", null),
            new Column("c1", null),
            new Column("c2", null),
            new Column("c3", null),
            new Column("c4", null),
        };

        for (int i = 0; i < columns.length; i++)
        {
            assertEquals("find column same", columns[i],
                    DataSetUtils.getColumn("c" + i, columns));
        }
    }

    public void testGetTables() throws Exception
    {
        String[] expected = {"t0", "t1", "t2", "t3"};
        ITable[] testTables = new ITable[]{
            new DefaultTable(new DefaultTableMetaData("t0", null), null),
            new DefaultTable(new DefaultTableMetaData("t1", null), null),
            new DefaultTable(new DefaultTableMetaData("t2", null), null),
            new DefaultTable(new DefaultTableMetaData("t3", null), null),
        };

        ITable[] tables = DataSetUtils.getTables(new DefaultDataSet(testTables));
        assertEquals("table count", expected.length, tables.length);
        for (int i = 0; i < tables.length; i++)
        {
            String name = tables[i].getTableMetaData().getTableName();
            assertEquals("table name", expected[i], name);
        }
    }

    public void testGetTablesByNames() throws Exception
    {
        String[] expected = {"t0", "t2"};
        ITable[] testTables = new ITable[]{
            new DefaultTable(new DefaultTableMetaData("t0", null), null),
            new DefaultTable(new DefaultTableMetaData("t1", null), null),
            new DefaultTable(new DefaultTableMetaData("t2", null), null),
            new DefaultTable(new DefaultTableMetaData("t3", null), null),
        };

        ITable[] tables = DataSetUtils.getTables(expected,
                new DefaultDataSet(testTables));
        assertEquals("table count", expected.length, tables.length);
        for (int i = 0; i < tables.length; i++)
        {
            String name = tables[i].getTableMetaData().getTableName();
            assertEquals("table name", expected[i], name);
        }
    }

    public void testGetReserseNames() throws Exception
    {
        String[] expected = {"t3", "t2", "t1", "t0"};
        ITable[] testTables = new ITable[]{
            new DefaultTable(new DefaultTableMetaData("t0", null), null),
            new DefaultTable(new DefaultTableMetaData("t1", null), null),
            new DefaultTable(new DefaultTableMetaData("t2", null), null),
            new DefaultTable(new DefaultTableMetaData("t3", null), null),
        };

        String[] names = DataSetUtils.getReverseTableNames(new DefaultDataSet(testTables));
        assertEquals("table count", expected.length, names.length);
        for (int i = 0; i < names.length; i++)
        {
            assertEquals("table name", expected[i], names[i]);
        }
    }

    public void testGetSqlValueString() throws Exception
    {
        ValueStringData[] values = new ValueStringData[]{
            new ValueStringData(null, DataType.FLOAT, "NULL"),
            new ValueStringData("1234", DataType.NUMBER, "1234"),
            new ValueStringData("1234", DataType.STRING, "'1234'"),
            new ValueStringData(new Float(1234.45), DataType.FLOAT, "1234.45"),
//            new ValueStringData(new java.sql.Date(0L), DataType.DATE, "'1970-01-01'"),
//            new ValueStringData(new java.sql.Time(0L), DataType.TIME, "'00:00:00'"),
//            new ValueStringData(new java.sql.Timestamp(0L), DataType.TIMESTAMP, "'1970-01-01 00:00:00.0'"),
        };

        for (int i = 0; i < values.length; i++)
        {
            ValueStringData data = values[i];
            String valueString = DataSetUtils.getSqlValueString(
                    data.getValue(), data.getDataType());
            assertEquals("data " + i, data.getExpected(), valueString);
        }
    }

    private class ValueStringData
    {
        private final Object _value;
        private final DataType _dataType;
        private final String _expected;

        public ValueStringData(Object value, DataType dataType, String expected)
        {
            _value = value;
            _dataType = dataType;
            _expected = expected;
        }

        public Object getValue()
        {
            return _value;
        }

        public DataType getDataType()
        {
            return _dataType;
        }

        public String getExpected()
        {
            return _expected;
        }
    }

}
