/*
 * DatabaseMetaDataTest.java   Mar 14, 2002
 *
 * Copyright 2001 Karat Software Corp. All Rights Reserved.
 *
 * This software is the proprietary information of Karat Software Corp.
 * Use is subject to license terms.
 *
 */

package org.dbunit.database;

import org.dbunit.AbstractDatabaseTest;
import org.dbunit.dataset.*;
import org.dbunit.dataset.datatype.DataType;

/**
 * @author Manuel Laflamme
 * @version 1.0
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
        String[] expected = {"PK0"};
//        String[] expected = {"PK0", "PK1", "PK2"};

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
            assertEquals(notNullable[i],  Column.NO_NULLS, column.getNullable());
        }

        // nullable
        for (int i = 0; i < nullable.length; i++)
        {
            Column column = DataSetUtils.getColumn(nullable[i], columns);
            assertEquals(nullable[i], Column.NULLABLE, column.getNullable());
        }
    }

    public void testUnsupportedColumnDataType() throws Exception
    {
        String tableName = "EMPTY_MULTITYPE_TABLE";
        String[] expectedNames = {"VARCHAR_COL", "NUMERIC_COL"};

        ITableMetaData metaData = createDataSet().getTableMetaData(tableName);
        Column[] columns = metaData.getColumns();

        assertEquals("column count", expectedNames.length, columns.length);

        for (int i = 0; i < columns.length; i++)
        {
            Column column = columns[i];
            assertEquals("name", expectedNames[i], column.getColumnName());
        }
    }

    public void testColumnDataType() throws Exception
    {
        String tableName = "EMPTY_MULTITYPE_TABLE";
        String[] expectedNames = {"VARCHAR_COL", "NUMERIC_COL"};
        DataType[] expectedTypes = {DataType.STRING, DataType.NUMBER};

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

