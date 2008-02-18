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

package org.dbunit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import junit.framework.Assert;
import org.dbunit.dataset.*;
import org.dbunit.dataset.datatype.DataType;
import org.dbunit.dataset.datatype.UnknownDataType;

import java.util.Arrays;
import java.util.Comparator;

/**
 * @author Manuel Laflamme
 * @version $Revision$
 * @since Mar 22, 2002
 */
public class Assertion
{

    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(Assertion.class);

    private static final ColumnComparator COLUMN_COMPARATOR = new ColumnComparator();

    private Assertion()
    {
    }

    /**
     * Asserts that the two specified dataset are equals. This method ignore
     * the tables order.
     */
    public static void assertEquals(IDataSet expectedDataSet,
            IDataSet actualDataSet) throws DatabaseUnitException
    {
        logger.debug("assertEquals(expectedDataSet={}, actualDataSet={}) - start", expectedDataSet, actualDataSet);

        // do not continue if same instance
        if (expectedDataSet == actualDataSet)
        {
            return;
        }

        String[] expectedNames = getSortedUpperTableNames(expectedDataSet);
        String[] actualNames = getSortedUpperTableNames(actualDataSet);

        // tables count
        Assert.assertEquals("table count", expectedNames.length, actualNames.length);


        // table names in no specific order
        for (int i = 0; i < expectedNames.length; i++)
        {
            if (!actualNames[i].equals(expectedNames[i]))
            {
                Assert.fail("expected tables " + Arrays.asList(expectedNames) +
                        " but was " + Arrays.asList(actualNames));
            }

        }

        // tables
        for (int i = 0; i < expectedNames.length; i++)
        {
            String name = expectedNames[i];
            assertEquals(expectedDataSet.getTable(name),
                    actualDataSet.getTable(name));
        }

    }


    /**
     * Asserts that the two specified tables are equals. This method ignore the
     * table names, the columns order, the columns data type and which columns
     * are composing the primary keys.
     */
    public static void assertEquals(ITable expectedTable, ITable actualTable)
            throws DatabaseUnitException
    {
        logger.debug("assertEquals(expectedTable={}, actualTable={}) - start", expectedTable, actualTable);

        // Do not continue if same instance
        if (expectedTable == actualTable)
        {
            return;
        }

        ITableMetaData expectedMetaData = expectedTable.getTableMetaData();
        ITableMetaData actualMetaData = actualTable.getTableMetaData();
        String expectedTableName = expectedMetaData.getTableName();

//        // verify table name
//        Assert.assertEquals("table name", expectedMetaData.getTableName(),
//                actualMetaData.getTableName());

        // Verify columns
        Column[] expectedColumns = getSortedColumns(expectedMetaData);
        Column[] actualColumns = getSortedColumns(actualMetaData);
        Assert.assertEquals("column count (table=" + expectedTableName + ")",
                expectedColumns.length, actualColumns.length);

        for (int i = 0; i < expectedColumns.length; i++)
        {
            String expectedName = expectedColumns[i].getColumnName();
            String actualName = actualColumns[i].getColumnName();
            if (!expectedName.equalsIgnoreCase(actualName))
            {
                Assert.fail("expected columns " + getColumnNamesAsString(expectedColumns) +
                        " but was " + getColumnNamesAsString(actualColumns) +
                        " (table=" + expectedTableName + ")");
            }
        }

        // Verify row count
        Assert.assertEquals("row count (table=" + expectedTableName + ")",
                expectedTable.getRowCount(), actualTable.getRowCount());

        // values as strings
        for (int i = 0; i < expectedTable.getRowCount(); i++)
        {
            for (int j = 0; j < expectedColumns.length; j++)
            {
                Column expectedColumn = expectedColumns[j];
                Column actualColumn = actualColumns[j];

                String columnName = expectedColumn.getColumnName();
                Object expectedValue = expectedTable.getValue(i, columnName);
                Object actualValue = actualTable.getValue(i, columnName);

                DataType dataType = getComparisonDataType(
                        expectedTableName, expectedColumn, actualColumn);
                if (dataType.compare(expectedValue, actualValue) != 0)
                {
                    Assert.fail("value (table=" + expectedTableName + ", " +
                            "row=" + i + ", col=" + columnName + "): expected:<" +
                            expectedValue + "> but was:<" + actualValue + ">");
                }
            }
        }
    }

    static DataType getComparisonDataType(String tableName, Column expectedColumn,
            Column actualColumn)
    {
        logger.debug("getComparisonDataType(tableName={}, expectedColumn={}, actualColumn={}) - start", new Object[] {tableName, expectedColumn, actualColumn});

        DataType expectedDataType = expectedColumn.getDataType();
        DataType actualDataType = actualColumn.getDataType();

        // The two columns have different data type
        if (!expectedDataType.getClass().isInstance(actualDataType))
        {
            // Expected column data type is unknown, use actual column data type
            if (expectedDataType instanceof UnknownDataType)
            {
                return actualDataType;
            }

            // Actual column data type is unknown, use expected column data type
            if (actualDataType instanceof UnknownDataType)
            {
                return expectedDataType;
            }

            // Impossible to determine which data type to use
            Assert.fail("Incompatible data types: " + expectedDataType + ", " +
                    actualDataType + " (table=" + tableName + ", col=" +
                    expectedColumn.getColumnName() + ")");
        }
//        // Both columns have unknown data type, use string comparison
//        else if (expectedDataType instanceof UnknownDataType)
//        {
//            return DataType.LONGVARCHAR;
//        }

        // Both columns have same data type, return any one of them
        return expectedDataType;
    }

    private static Column[] getSortedColumns(ITableMetaData metaData)
            throws DataSetException
    {
        logger.debug("getSortedColumns(metaData={}) - start", metaData);

        Column[] columns = metaData.getColumns();
        Column[] sortColumns = new Column[columns.length];
        System.arraycopy(columns, 0, sortColumns, 0, columns.length);
        Arrays.sort(sortColumns, COLUMN_COMPARATOR);
        return sortColumns;
    }

    private static String getColumnNamesAsString(Column[] columns)
    {
        logger.debug("getColumnNamesAsString(columns={}) - start", columns);

        String[] names = new String[columns.length];
        for (int i = 0; i < columns.length; i++)
        {
            Column column = columns[i];
            names[i] = column.getColumnName();
        }
        return Arrays.asList(names).toString();
    }

    private static String[] getSortedUpperTableNames(IDataSet dataSet)
            throws DataSetException
    {
        logger.debug("getSortedUpperTableNames(dataSet={}) - start", dataSet);

        String[] names = dataSet.getTableNames();
        for (int i = 0; i < names.length; i++)
        {
            names[i] = names[i].toUpperCase();
        }
        Arrays.sort(names);
        return names;
    }

    ////////////////////////////////////////////////////////////////////////////
    // ColumnComparator class

    private static class ColumnComparator implements Comparator
    {

        /**
         * Logger for this class
         */
        private static final Logger logger = LoggerFactory.getLogger(ColumnComparator.class);

        public int compare(Object o1, Object o2)
        {
            logger.debug("compare(o1={}, o2={}) - start", o1, o2);

            Column column1 = (Column)o1;
            Column column2 = (Column)o2;

            String columnName1 = column1.getColumnName();
            String columnName2 = column2.getColumnName();
            return columnName1.compareToIgnoreCase(columnName2);
        }
    }
}




