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

import org.dbunit.AbstractDatabaseTest;
import org.dbunit.dataset.*;
import org.dbunit.dataset.datatype.DataType;

/**
 * @author Manuel Laflamme
 * @version $Revision$
 * @since Mar 14, 2002
 */
public class DatabaseTableMetaDataTest extends AbstractDatabaseTest
{
    public DatabaseTableMetaDataTest(String s)
    {
        super(s);
    }

    protected IDataSet createDataSet() throws Exception
    {
        return _connection.createDataSet();
    }

    public void testGetPrimaryKeys() throws Exception
    {
        String tableName = "PK_TABLE";
//        String[] expected = {"PK0"};
        String[] expected = {"PK0", "PK1", "PK2"};

        ITableMetaData metaData = createDataSet().getTableMetaData(tableName);
        Column[] columns = metaData.getPrimaryKeys();

        assertEquals("pk count", expected.length, columns.length);
        for (int i = 0; i < columns.length; i++)
        {
            Column column = columns[i];
            assertEquals("name", expected[i], column.getColumnName());
        }
    }

    public void testGetNoPrimaryKeys() throws Exception
    {
        String tableName = "TEST_TABLE";

        ITableMetaData metaData = createDataSet().getTableMetaData(tableName);
        Column[] columns = metaData.getPrimaryKeys();

        assertEquals("pk count", 0, columns.length);
    }

    public void testGetNoColumns() throws Exception
    {
        String tableName = "UNKNOWN_TABLE";

        ITableMetaData metaData = new DatabaseTableMetaData(tableName,
                getConnection());
        try
        {
            metaData.getColumns();
            fail("Should not be here!");
        }
        catch (NoColumnsFoundException e)
        {
        }

        // try a second times to ensure error is consistent
        try
        {
            metaData.getColumns();
            fail("Should not be here!");
        }
        catch (NoColumnsFoundException e)
        {
        }
    }

    public void testColumnIsNullable() throws Exception
    {
        String tableName = "PK_TABLE";
        String[] notNullable = {"PK0", "PK1", "PK2"};
        String[] nullable = {"NORMAL0", "NORMAL1"};

        ITableMetaData metaData = createDataSet().getTableMetaData(tableName);
        Column[] columns = metaData.getColumns();

        assertEquals("column count", nullable.length + notNullable.length,
                columns.length);

        // not nullable
        for (int i = 0; i < notNullable.length; i++)
        {
            Column column = DataSetUtils.getColumn(notNullable[i], columns);
            assertEquals(notNullable[i], Column.NO_NULLS, column.getNullable());
        }

        // nullable
        for (int i = 0; i < nullable.length; i++)
        {
            Column column = DataSetUtils.getColumn(nullable[i], columns);
            assertEquals(nullable[i], Column.NULLABLE, column.getNullable());
        }
    }

//    public void testUnsupportedColumnDataType() throws Exception
//    {
//        fail("Mock this test!");
//        String tableName = "EMPTY_MULTITYPE_TABLE";
//        String[] expectedNames = {
//            "VARCHAR_COL",
//            "NUMERIC_COL",
//            "TIMESTAMP_COL",
//        };
//
//        ITableMetaData metaData = createDataSet().getTableMetaData(tableName);
//        Column[] columns = metaData.getColumns();
//
//        assertEquals("column count", expectedNames.length, columns.length);
//
//        for (int i = 0; i < columns.length; i++)
//        {
//            Column column = columns[i];
//            assertEquals("name", expectedNames[i], column.getColumnName());
//        }
//    }

    public void testColumnDataType() throws Exception
    {
        String tableName = "EMPTY_MULTITYPE_TABLE";
        String[] expectedNames = {
            "VARCHAR_COL",
            "NUMERIC_COL",
            "TIMESTAMP_COL",
            "VARBINARY_COL",
        };
        DataType[] expectedTypes = {
            DataType.VARCHAR,
            DataType.NUMERIC,
            DataType.TIMESTAMP,
            DataType.VARBINARY,
        };

        ITableMetaData metaData = createDataSet().getTableMetaData(tableName);
        Column[] columns = metaData.getColumns();

        assertEquals("expected columns", expectedNames.length, expectedTypes.length);
        assertEquals("column count", expectedNames.length, columns.length);

        for (int i = 0; i < columns.length; i++)
        {
            Column column = columns[i];
            assertEquals("name", expectedNames[i], column.getColumnName());
            assertEquals("datatype", expectedTypes[i], column.getDataType());
        }
    }
}







