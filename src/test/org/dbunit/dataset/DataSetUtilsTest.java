/*
 * DataSetUtilsTest.java   Feb 19, 2002
 *
 * DbUnit Database Testing Framework
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

import java.io.FileInputStream;
import java.sql.Time;
import java.sql.Timestamp;

import org.dbunit.dataset.datatype.DataType;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import junit.framework.TestCase;
import junit.framework.AssertionFailedError;

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

    private IDataSet getDataSet() throws Exception
    {
        return new FlatXmlDataSet(new FileInputStream(
                "src/xml/dataSetUtilsTest.xml"));
    }

    ////////////////////////////////////////////////////////////////////////////
    // Test methods

    public void testAssertEquals() throws Exception
    {
        IDataSet dataSet = getDataSet();
        DataSetUtils.assertEquals(dataSet.getTable("TEST_TABLE"),
                dataSet.getTable("TEST_TABLE_WITH_SAME_VALUE"));
    }

    public void testAssertTableNamesNotEquals() throws Exception
    {
        IDataSet dataSet = getDataSet();
        DataSetUtils.assertEquals(dataSet.getTable("TEST_TABLE"),
                dataSet.getTable("TEST_TABLE_WITH_DIFFERENT_NAME"));
    }

    public void testAssertColumnCountNotEquals() throws Exception
    {
        IDataSet dataSet = getDataSet();

        try
        {
            DataSetUtils.assertEquals(dataSet.getTable("TEST_TABLE"),
                    dataSet.getTable("TEST_TABLE_WITH_3_COLUMNS"));
            throw new IllegalStateException("Should throw an AssertionFailedError");
        }
        catch (AssertionFailedError e)
        {
        }
    }

    public void testAssertColumnSequenceNotEquals() throws Exception
    {
        IDataSet dataSet = getDataSet();

        DataSetUtils.assertEquals(dataSet.getTable("TEST_TABLE"),
                dataSet.getTable("TEST_TABLE_WITH_DIFFERENT_COLUMN_SEQUENCE"));
    }

    public void testAssertColumnNamesNotEquals() throws Exception
    {
        IDataSet dataSet = getDataSet();

        try
        {
            DataSetUtils.assertEquals(dataSet.getTable("TEST_TABLE"),
                    dataSet.getTable("TEST_TABLE_WITH_DIFFERENT_COLUMN_NAMES"));
            throw new IllegalStateException("Should throw an AssertionFailedError");
        }
        catch (AssertionFailedError e)
        {
        }
    }

    public void testAssertRowCountNotEquals() throws Exception
    {
        IDataSet dataSet = getDataSet();

        try
        {
            DataSetUtils.assertEquals(dataSet.getTable("TEST_TABLE"),
                    dataSet.getTable("TEST_TABLE_WITH_ONE_ROW"));
            throw new IllegalStateException("Should throw an AssertionFailedError");
        }
        catch (AssertionFailedError e)
        {
        }
    }

    public void testAssertValuesNotEquals() throws Exception
    {
        IDataSet dataSet = getDataSet();

        try
        {
            DataSetUtils.assertEquals(dataSet.getTable("TEST_TABLE"),
                    dataSet.getTable("TEST_TABLE_WITH_WRONG_VALUE"));
            throw new IllegalStateException("Should throw an AssertionFailedError");
        }
        catch (AssertionFailedError e)
        {
        }
    }

    public void testGetQualifiedName() throws Exception
    {
        assertEquals("prefix + name", "prefix.name",
                DataSetUtils.getQualifiedName("prefix", "name"));

        assertEquals("null prefix", "name",
                DataSetUtils.getQualifiedName(null, "name"));

        assertEquals("empty prefix", "name",
                DataSetUtils.getQualifiedName("", "name"));

        assertEquals("existing prefix", "prefix.name",
                DataSetUtils.getQualifiedName("wrongPrefix", "prefix.name"));
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
            new ValueStringData(null, DataType.REAL, "NULL"),
            new ValueStringData("1234", DataType.NUMERIC, "1234"),
            new ValueStringData("1234", DataType.VARCHAR, "'1234'"),
            new ValueStringData(new Float(1234.45), DataType.REAL, "1234.45"),
            new ValueStringData(new java.sql.Date(0L), DataType.DATE,
                    "{d '" + new java.sql.Date(0L).toString() + "'}"),
            new ValueStringData(new Time(0L), DataType.TIME,
                    "{t '" + new Time(0L).toString() + "'}"),
            new ValueStringData(new Timestamp(0L), DataType.TIMESTAMP,
                    "{ts '" + new Timestamp(0L).toString() + "'}"),
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




