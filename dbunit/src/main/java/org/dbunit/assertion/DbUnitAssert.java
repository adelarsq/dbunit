/*
 *
 * The DbUnit Database Testing Framework
 * Copyright (C)2002-2008, DbUnit.org
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
package org.dbunit.assertion;

import java.sql.SQLException;
import java.util.Arrays;

import org.dbunit.Assertion;
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.Column;
import org.dbunit.dataset.Columns;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.ITableMetaData;
import org.dbunit.dataset.datatype.DataType;
import org.dbunit.dataset.datatype.UnknownDataType;
import org.dbunit.dataset.filter.DefaultColumnFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default implementation of DbUnit assertions, based on the original methods present
 * at {@link Assertion}
 * 
 * @author Felipe Leme (dbunit@felipeal.net)
 * @author gommma (gommma AT users.sourceforge.net)
 * @version $Revision$ $Date$
 * @since 2.4.0
 */
public class DbUnitAssert
{

    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(DbUnitAssert.class);

    private FailureFactory junitFailureFactory = getJUnitFailureFactory();

    /**
     * Default constructor
     */
    public DbUnitAssert()
    {
    }

    /**
     * Compare one table present in two datasets ignoring specified columns.
     * 
     * @param expectedDataset
     *            First dataset.
     * @param actualDataset
     *            Second dataset.
     * @param tableName
     *            Table name of the table to be compared.
     * @param ignoreCols
     *            Columns to be ignored in comparison.
     * @throws org.dbunit.DatabaseUnitException
     *             If an error occurs.
     */
    public void assertEqualsIgnoreCols(final IDataSet expectedDataset,
            final IDataSet actualDataset, final String tableName,
            final String[] ignoreCols) throws DatabaseUnitException 
    {
        if (logger.isDebugEnabled())
            logger.debug(
                            "assertEqualsIgnoreCols(expectedDataset={}, actualDataset={}, tableName={}, ignoreCols={}) - start",
                    new Object[] { expectedDataset, actualDataset, tableName,
                            Arrays.asList(ignoreCols) });

        assertEqualsIgnoreCols(expectedDataset.getTable(tableName), actualDataset
                .getTable(tableName), ignoreCols);
    }

    /**
     * Compare the given tables ignoring specified columns.
     * 
     * @param expectedTable
     *            First table.
     * @param actualTable
     *            Second table.
     * @param ignoreCols
     *            Columns to be ignored in comparison.
     * @throws org.dbunit.DatabaseUnitException
     *             If an error occurs.
     */
    public void assertEqualsIgnoreCols(final ITable expectedTable,
            final ITable actualTable, final String[] ignoreCols)
    throws DatabaseUnitException 
    {
        if (logger.isDebugEnabled())
            logger
                    .debug(
                            "assertEqualsIgnoreCols(expectedTable={}, actualTable={}, ignoreCols={}) - start",
                            new Object[] {expectedTable, actualTable,
                                    Arrays.asList(ignoreCols)});

        final ITable expectedTableFiltered = DefaultColumnFilter
        .excludedColumnsTable(expectedTable, ignoreCols);
        final ITable actualTableFiltered = DefaultColumnFilter
        .excludedColumnsTable(actualTable, ignoreCols);
        assertEquals(expectedTableFiltered, actualTableFiltered);
    }

    /**
     * Compare a table from a dataset with a table generated from an sql query.
     * 
     * @param expectedDataset
     *            Dataset to retrieve the first table from.
     * @param connection
     *            Connection to use for the SQL statement.
     * @param sqlQuery
     *          SQL query that will build the data in returned second table rows.
     * @param tableName
     *            Table name of the table to compare
     * @param ignoreCols
     *            Columns to be ignored in comparison.
     * @throws DatabaseUnitException
     *             If an error occurs while performing the comparison.
     * @throws java.sql.SQLException
     *             If an SQL error occurs.
     */
    public void assertEqualsByQuery(final IDataSet expectedDataset,
            final IDatabaseConnection connection, final String sqlQuery,
            final String tableName, final String[] ignoreCols)
    throws DatabaseUnitException, SQLException 
    {
        if (logger.isDebugEnabled())
            logger.debug(
                            "assertEqualsByQuery(expectedDataset={}, connection={}, tableName={}, sqlQuery={}, ignoreCols={}) - start",
                    new Object[] { expectedDataset, connection, tableName, sqlQuery,
                            ignoreCols });

        ITable expectedTable = expectedDataset.getTable(tableName);
        assertEqualsByQuery(expectedTable, connection, tableName, sqlQuery,
                ignoreCols);
    }

    /**
     * Compare a table with a table generated from an sql query.
     * 
     * @param expectedTable
     *            Table containing all expected results.
     * @param connection
     *            Connection to use for the SQL statement.
     * @param tableName
     *            The name of the table to query from the database
     * @param sqlQuery
     *          SQL query that will build the data in returned second table rows.
     * @param ignoreCols
     *            Columns to be ignored in comparison.
     * @throws DatabaseUnitException
     *             If an error occurs while performing the comparison.
     * @throws java.sql.SQLException
     *             If an SQL error occurs.
     */
    public void assertEqualsByQuery(final ITable expectedTable,
            final IDatabaseConnection connection, final String tableName,
            final String sqlQuery, final String[] ignoreCols)
    throws DatabaseUnitException, SQLException 
    {
        if (logger.isDebugEnabled())
            logger.debug(
                            "assertEqualsByQuery(expectedTable={}, connection={}, tableName={}, sqlQuery={}, ignoreCols={}) - start",
                    new Object[] { expectedTable, connection, tableName, sqlQuery,
                            ignoreCols });

        ITable expected = DefaultColumnFilter.excludedColumnsTable(expectedTable,
                        ignoreCols);
        ITable queriedTable = connection.createQueryTable(tableName, sqlQuery);
        ITable actual = DefaultColumnFilter.excludedColumnsTable(queriedTable,
                        ignoreCols);
        assertEquals(expected, actual);
    }

    /**
     * Asserts that the two specified dataset are equals. This method ignore the
     * tables order.
     */
    public void assertEquals(IDataSet expectedDataSet, IDataSet actualDataSet)
    throws DatabaseUnitException 
    {
        logger.debug("assertEquals(expectedDataSet={}, actualDataSet={}) - start",
                expectedDataSet, actualDataSet);
        assertEquals(expectedDataSet, actualDataSet, null);
    }

    /**
     * Asserts that the two specified dataset are equals. This method ignore the
     * tables order.
     * 
     * @since 2.4
     */
    public void assertEquals(IDataSet expectedDataSet, IDataSet actualDataSet,
            FailureHandler failureHandler) throws DatabaseUnitException 
    {
        if (logger.isDebugEnabled())
            logger.debug(
                            "assertEquals(expectedDataSet={}, actualDataSet={}, failureHandler={}) - start",
                    new Object[] { expectedDataSet, actualDataSet, failureHandler });

        // do not continue if same instance
        if (expectedDataSet == actualDataSet) {
            return;
        }

        if (failureHandler == null) {
            logger.debug("FailureHandler is null. Using default implementation");
            failureHandler = getDefaultFailureHandler();
        }

        String[] expectedNames = getSortedUpperTableNames(expectedDataSet);
        String[] actualNames = getSortedUpperTableNames(actualDataSet);

        // tables count
        if (expectedNames.length != actualNames.length) {
            throw failureHandler.createFailure("table count", String
                    .valueOf(expectedNames.length), String.valueOf(actualNames.length));
        }

        // table names in no specific order
        for (int i = 0; i < expectedNames.length; i++) {
            if (!actualNames[i].equals(expectedNames[i])) {
                throw failureHandler.createFailure("tables", Arrays.asList(
                        expectedNames).toString(), Arrays.asList(actualNames).toString());
            }

        }

        // tables
        for (int i = 0; i < expectedNames.length; i++) {
            String name = expectedNames[i];
            assertEquals(expectedDataSet.getTable(name), actualDataSet.getTable(name), failureHandler);
        }

    }

    /**
     * Asserts that the two specified tables are equals. This method ignores the
     * table names, the columns order, the columns data type and which columns are
     * composing the primary keys.
     * 
     * @param expectedTable
     *            Table containing all expected results.
     * @param actualTable
     *            Table containing all actual results.
     * @throws DatabaseUnitException
     */
    public void assertEquals(ITable expectedTable, ITable actualTable)
    throws DatabaseUnitException 
    {
        logger.debug("assertEquals(expectedTable={}, actualTable={}) - start",
                expectedTable, actualTable);
        assertEquals(expectedTable, actualTable, (Column[]) null);
    }

    /**
     * Asserts that the two specified tables are equals. This method ignores the
     * table names, the columns order, the columns data type and which columns are
     * composing the primary keys. <br />
     * Example: <code><pre>
     * ITable actualTable = ...;
     * ITable expectedTable = ...;
     * ITableMetaData metaData = actualTable.getTableMetaData();
     * Column[] additionalInfoCols = Columns.getColumns(new String[] {"MY_PK_COLUMN"}, metaData.getColumns());
     * assertEquals(expectedTable, actualTable, additionalInfoCols);
     * </pre></code>
     * 
     * @param expectedTable
     *            Table containing all expected results.
     * @param actualTable
     *            Table containing all actual results.
     * @param additionalColumnInfo
     *          The columns to be printed out if the assert fails because of a
     *          data mismatch. Provides some additional column values that may be
     *          useful to quickly identify the columns for which the mismatch
     *          occurred (for example a primary key column). Can be
     *            <code>null</code>
     * @throws DatabaseUnitException
     */
    public void assertEquals(ITable expectedTable, ITable actualTable,
            Column[] additionalColumnInfo) throws DatabaseUnitException 
    {
        logger.debug(
                        "assertEquals(expectedTable={}, actualTable={}, additionalColumnInfo={}) - start",
                new Object[] { expectedTable, actualTable, additionalColumnInfo });

        FailureHandler failureHandler = null;
        if (additionalColumnInfo != null)
            failureHandler = getDefaultFailureHandler(additionalColumnInfo);

        assertEquals(expectedTable, actualTable, failureHandler);
    }

    /**
     * Asserts that the two specified tables are equals. This method ignores the
     * table names, the columns order, the columns data type and which columns are
     * composing the primary keys. <br />
     * Example: <code><pre>
     * ITable actualTable = ...;
     * ITable expectedTable = ...;
     * ITableMetaData metaData = actualTable.getTableMetaData();
     * FailureHandler failureHandler = new DefaultFailureHandler();
     * assertEquals(expectedTable, actualTable, failureHandler);
     * </pre></code>
     * 
     * @param expectedTable
     *            Table containing all expected results.
     * @param actualTable
     *            Table containing all actual results.
     * @param failureHandler
     *          The failure handler used if the assert fails because of a data
     *          mismatch. Provides some additional information that may be useful
     *          to quickly identify the rows for which the mismatch occurred (for
     *          example by printing an additional primary key column). Can be
     *          <code>null</code>
     * @throws DatabaseUnitException
     * @since 2.4
     */
    public void assertEquals(ITable expectedTable, ITable actualTable,
            FailureHandler failureHandler) throws DatabaseUnitException
    {
        logger.trace("assertEquals(expectedTable, actualTable, failureHandler) - start");
        logger.debug("assertEquals: expectedTable={}", expectedTable);
        logger.debug("assertEquals: actualTable={}", actualTable);
        logger.debug("assertEquals: failureHandler={}", failureHandler);

        // Do not continue if same instance
        if (expectedTable == actualTable) {
            logger.debug(
                            "The given tables reference the same object. Will return immediately. (Table={})",
                            expectedTable);
            return;
        }

        if (failureHandler == null) {
            logger.debug("FailureHandler is null. Using default implementation");
            failureHandler = getDefaultFailureHandler();
        }

        ITableMetaData expectedMetaData = expectedTable.getTableMetaData();
        ITableMetaData actualMetaData = actualTable.getTableMetaData();
        String expectedTableName = expectedMetaData.getTableName();

        // Verify row count
        int expectedRowsCount = expectedTable.getRowCount();
        int actualRowsCount = actualTable.getRowCount();
        if (expectedRowsCount != actualRowsCount) {
            String msg = "row count (table=" + expectedTableName + ")";
            Error error =
                    failureHandler.createFailure(msg, String
                            .valueOf(expectedRowsCount), String
                            .valueOf(actualRowsCount));
            logger.error(error.toString());
            throw error;
        }
        // if both tables are empty, it is not necessary to compare columns, as
        // such
        // comparison
        // can fail if column metadata is different (which could occurs when
        // comparing empty tables)
        if (expectedRowsCount == 0 && actualRowsCount == 0) {
            logger.debug("Tables are empty, hence equals.");
            return;
        }

        // Put the columns into the same order
        Column[] expectedColumns = Columns.getSortedColumns(expectedMetaData);
        Column[] actualColumns = Columns.getSortedColumns(actualMetaData);

        // Verify columns
        Columns.ColumnDiff columnDiff =
                Columns.getColumnDiff(expectedMetaData, actualMetaData);
        if (columnDiff.hasDifference()) {
            String message = columnDiff.getMessage();
            Error error =
                    failureHandler.createFailure(message, Columns
                            .getColumnNamesAsString(expectedColumns), Columns
                            .getColumnNamesAsString(actualColumns));
            logger.error(error.toString());
            throw error;
        }

        // Get the datatypes to be used for comparing the sorted columns
        ComparisonColumn[] comparisonCols = getComparisonColumns(expectedTableName,
                expectedColumns, actualColumns, failureHandler);

        // Finally compare the data
        compareData(expectedTable, actualTable, comparisonCols, failureHandler);
    }

    /**
     * @return The default failure handler
     * @since 2.4
     */
    protected FailureHandler getDefaultFailureHandler() 
    {
        return getDefaultFailureHandler(null);
    }

    /**
     * @return The default failure handler
     * @since 2.4
     */
    protected FailureHandler getDefaultFailureHandler(Column[] additionalColumnInfo) 
    {
        DefaultFailureHandler failureHandler = new DefaultFailureHandler(additionalColumnInfo);
        if (junitFailureFactory != null) {
            failureHandler.setFailureFactory(junitFailureFactory);
        }
        return failureHandler;
    }

    /**
     * @return the JUnitFailureFactory if JUnit is on the classpath or <code>null</code> if
     * JUnit is not on the classpath.
     */
    private FailureFactory getJUnitFailureFactory() 
    {
        try {
            Class.forName("junit.framework.Assert");
            // JUnit available
            return new JUnitFailureFactory();
        }
        catch (ClassNotFoundException e) {
            // JUnit not available on the classpath return null
            logger.debug("JUnit does not seem to be on the classpath. " + e);
        }
        return null;
    }

    /**
     * @param expectedTable
     *            Table containing all expected results.
     * @param actualTable
     *            Table containing all actual results.
     * @param comparisonCols
     *            The columns to be compared, also including the correct
     *            {@link DataType}s for comparison
     * @param failureHandler
     *          The failure handler used if the assert fails because of a data
     *          mismatch. Provides some additional information that may be useful
     *          to quickly identify the rows for which the mismatch occurred (for
     *          example by printing an additional primary key column). Must not be
     *          <code>null</code> at this stage
     * @throws DataSetException
     * @since 2.4
     */
    protected void compareData(ITable expectedTable, ITable actualTable,
            ComparisonColumn[] comparisonCols, FailureHandler failureHandler)
            throws DataSetException
    {
        logger.debug("compareData(expectedTable={}, actualTable={}, "
                + "comparisonCols={}, failureHandler={}) - start",
                new Object[] {expectedTable, actualTable, comparisonCols,
                        failureHandler});

        if (expectedTable == null) {
            throw new NullPointerException(
                    "The parameter 'expectedTable' must not be null");
        }
        if (actualTable == null) {
            throw new NullPointerException(
                    "The parameter 'actualTable' must not be null");
        }
        if (comparisonCols == null) {
            throw new NullPointerException(
                    "The parameter 'comparisonCols' must not be null");
        }
        if (failureHandler == null) {
            throw new NullPointerException(
                    "The parameter 'failureHandler' must not be null");
        }

        // iterate over all rows
        for (int i = 0; i < expectedTable.getRowCount(); i++) {
            // iterate over all columns of the current row
            for (int j = 0; j < comparisonCols.length; j++) {
                ComparisonColumn compareColumn = comparisonCols[j];

                String columnName = compareColumn.getColumnName();
                DataType dataType = compareColumn.getDataType();

                Object expectedValue = expectedTable.getValue(i, columnName);
                Object actualValue = actualTable.getValue(i, columnName);

                // Compare the values
                if (skipCompare(columnName, expectedValue, actualValue)) {
                    if (logger.isTraceEnabled()) {
                        logger.trace( "ignoring comparison " + expectedValue + "=" +
                                actualValue + " on column " + columnName);                        
                    }
                    continue;
                }

                if (dataType.compare(expectedValue, actualValue) != 0) {

                    Difference diff = new Difference(
                            expectedTable, actualTable, 
                            i, columnName, 
                            expectedValue, actualValue);

                    // Handle the difference (throw error immediately or something else)
                    failureHandler.handle(diff);
                }
            }
        }

    }

    /**
     * Method to last-minute intercept the comparison of a single 
     * expected and actual value. Designed to be overridden in order
     * to skip cell comparison by specific cell values.
     * 
     * @param columnName The column being compared
     * @param expectedValue The expected value to be compared
     * @param actualValue The actual value to be compared
     * @return <code>false</code> always so that the comparison is never skipped
     * @since 2.4
     */
    protected boolean skipCompare(String columnName, Object expectedValue, Object actualValue) 
    {
        return false;
    }

    /**
     * @param expectedTableName
     * @param expectedColumns
     * @param actualColumns
     * @param failureHandler
     *            The {@link FailureHandler} to be used when no datatype can be
     *            determined
     * @return The columns to be used for the assertion, including the correct
     *         datatype
     * @since 2.4
     */
    protected ComparisonColumn[] getComparisonColumns(String expectedTableName,
            Column[] expectedColumns, Column[] actualColumns,
            FailureHandler failureHandler) 
    {
        ComparisonColumn[] result = new ComparisonColumn[expectedColumns.length];

        for (int j = 0; j < expectedColumns.length; j++) {
            Column expectedColumn = expectedColumns[j];
            Column actualColumn = actualColumns[j];
            result[j] = new ComparisonColumn(expectedTableName, expectedColumn,
                            actualColumn, failureHandler);
        }
        return result;
    }

    protected String[] getSortedUpperTableNames(IDataSet dataSet)
    throws DataSetException 
    {
        logger.debug("getSortedUpperTableNames(dataSet={}) - start", dataSet);

        String[] names = dataSet.getTableNames();
        for (int i = 0; i < names.length; i++) {
            names[i] = names[i].toUpperCase();
        }
        Arrays.sort(names);
        return names;
    }

    /**
     * Represents a single column to be used for the comparison of table data. It
     * contains the {@link DataType} to be used for comparing the given column.
     * This {@link DataType} matches the expected and actual column's datatype.
     * 
     * @author gommma (gommma AT users.sourceforge.net)
     * @author Last changed by: $Author: gommma $
     * @version $Revision: 864 $ $Date: 2008-11-07 06:27:26 -0800 (Fri, 07 Nov
     *          2008) $
     * @since 2.4.0
     */
    public static class ComparisonColumn 
    {
        /**
         * Logger for this class
         */
        private static final Logger logger = LoggerFactory
        .getLogger(ComparisonColumn.class);

        private String columnName;
        private DataType dataType;

        /**
         * @param tableName
         *            The table name which is only needed for debugging output
         * @param expectedColumn
         *          The expected column needed to resolve the {@link DataType} to
         *          use for the actual comparison
         * @param actualColumn
         *          The actual column needed to resolve the {@link DataType} to use
         *          for the actual comparison
         * @param failureHandler
         *          The {@link FailureHandler} to be used when no datatype can be
         *          determined
         */
        public ComparisonColumn(String tableName, Column expectedColumn,
                Column actualColumn, FailureHandler failureHandler) {
            super();
            this.columnName = expectedColumn.getColumnName();
            this.dataType = getComparisonDataType(tableName, expectedColumn,
                            actualColumn, failureHandler);
        }

        /**
         * @return The column actually being compared
         */
        public String getColumnName() {
            return this.columnName;
        }

        /**
         * @return The {@link DataType} to use for the actual comparison
         */
        public DataType getDataType() {
            return this.dataType;
        }

        /**
         * @param tableName
         *            The table name which is only needed for debugging output
         * @param expectedColumn
         * @param actualColumn
         * @param failureHandler
         *          The {@link FailureHandler} to be used when no datatype can be
         *          determined
         * @return The dbunit {@link DataType} to use for comparing the given
         *         column.
         */
        private DataType getComparisonDataType(String tableName,
                Column expectedColumn, Column actualColumn,
                FailureHandler failureHandler) {
            if (logger.isDebugEnabled())
                logger.debug(
                                "getComparisonDataType(tableName={}, expectedColumn={}, actualColumn={}, failureHandler={}) - start",
                        new Object[] { tableName, expectedColumn, actualColumn,
                                failureHandler });

            DataType expectedDataType = expectedColumn.getDataType();
            DataType actualDataType = actualColumn.getDataType();

            // The two columns have different data type
            if (!expectedDataType.getClass().isInstance(actualDataType)) {
                // Expected column data type is unknown, use actual column data type
                if (expectedDataType instanceof UnknownDataType) {
                    return actualDataType;
                }

                // Actual column data type is unknown, use expected column data type
                if (actualDataType instanceof UnknownDataType) {
                    return expectedDataType;
                }

                // Impossible to determine which data type to use
                String msg = "Incompatible data types: (table=" + tableName + ", col="
                + expectedColumn.getColumnName() + ")";
                throw failureHandler.createFailure(msg, String
                        .valueOf(expectedDataType), String.valueOf(actualDataType));
            }

            // Both columns have same data type, return any one of them
            return expectedDataType;
        }

    }

}
