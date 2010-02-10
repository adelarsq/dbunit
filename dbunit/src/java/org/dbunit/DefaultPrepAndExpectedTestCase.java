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
package org.dbunit;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.CompositeDataSet;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.SortedTable;
import org.dbunit.dataset.filter.DefaultColumnFilter;
import org.dbunit.util.fileloader.DataFileLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Test case base class supporting prep data and expected data.
 * 
 * Use this class in two ways:
 * <ol>
 * <li>Dependency inject it as its interface into a test class.</li>
 * <p>
 * Configure a bean of its interface, injecting a IDatabaseTester and a
 * DataFileLoader using the databaseTester and a dataFileLoader properties.
 * </p>
 * 
 * <li>Extend it in a test class.</li>
 * <p>
 * Obtain IDatabaseTester and DataFileLoader instances and set them accordingly
 * (possibly dependency injecting them into the test class).
 * 
 * <pre>
 * setDatabaseTester(databaseTester);
 * setDataFileLoader(dataFileLoader);
 * </pre>
 * 
 * </p>
 * </ol>
 * 
 * To setup, execute, and clean up tests, call the configureTest(), preTest(),
 * and postTest() methods. Where the test case calls them depends on data needs:
 * <ul>
 * <li>For the whole test case, i.e. in setUp() and tearDown().</li>
 * <li>In each test method.</li>
 * <li>Or some combination of both test case setup/teardown and test methods.</li>
 * </ul>
 * 
 * <h4>When each test method requires different prep and expected data</h4>
 * 
 * If each test method requires its own prep and expected data, then the test
 * methods will look something like the following:
 * 
 * <pre>
 * &#064;Autowired
 * private PrepAndExpectedTestCase tc;
 * 
 * &#064;Test
 * public void testExample() throws Exception {
 *     String[] prepDataFiles = {};
 *     String[] expectedDataFiles = {};
 *     VerifyTableDefinition[] tables = {};
 * 
 *     tc.configureTest(tables, prepDataFiles, expectedDataFiles);
 *     tc.preTest();
 * 
 *     // execute test
 * 
 *     tc.postTest();
 * }
 * 
 * </pre>
 * 
 * <h4>When all test methods share the same prep and/or expected data</h4>
 * 
 * If each test method can share all of the prep and/or expected data, then use
 * setUp() for the configureTest() and preTest() calls and tearDown() for the
 * postTest() call. The methods will look something like the following:
 * 
 * <pre>
 * &#064;Override
 * protected void setUp() throws Exception {
 *     setDatabaseTester(databaseTester);
 *     setDataFileLoader(dataFileLoader);
 * 
 *     String[] prepDataFiles = {};
 *     String[] expectedDataFiles = {};
 *     VerifyTableDefinition[] tables = {};
 * 
 *     configureTest(tables, prepDataFiles, expectedDataFiles);
 *     preTest();
 *     super.setUp();
 * }
 * 
 * &#064;Override
 * protected void tearDown() throws Exception {
 *     postTest();
 *     super.tearDown();
 * }
 * 
 * &#064;Test
 * public void testExample() throws Exception {
 *     // execute test
 * }
 * </pre>
 * 
 * Note that it is unlikely that all test methods can share the same expected
 * data.
 * 
 * <h4>Sharing common (but not all) prep or expected data among test methods.</h4>
 * 
 * Put common data in one or more files and pass the needed ones in the correct
 * data file array.
 * 
 * <h4>Additional examples</h4>
 * 
 * For additional examples, refer to the ITs (listed in the See Also section).
 * 
 * @see org.dbunit.DefaultPrepAndExpectedTestCaseDiIT
 * @see org.dbunit.DefaultPrepAndExpectedTestCaseExtIT
 * 
 * @author Jeff Jensen jeffjensen AT users.sourceforge.net
 * @author Last changed by: $Author$
 * @version $Revision$ $Date$
 * @since 2.4.8
 */
public class DefaultPrepAndExpectedTestCase extends TestCase implements
        PrepAndExpectedTestCase {
    private final Logger LOG =
            LoggerFactory.getLogger(DefaultPrepAndExpectedTestCase.class);

    private IDatabaseTester databaseTester;
    private DataFileLoader dataFileLoader;

    private IDataSet prepDs;
    private IDataSet expectedDs;
    private VerifyTableDefinition[] tableDefs;

    /** Create new instance. */
    public DefaultPrepAndExpectedTestCase() {
    }

    /**
     * Create new instance with specified dataFileLoader and databasetester.
     * 
     * @param dataFileLoader
     *            Load to use for loading the data files.
     * @param databaseTester
     *            Tester to use for database manipulation.
     */
    public DefaultPrepAndExpectedTestCase(DataFileLoader dataFileLoader,
            IDatabaseTester databaseTester) {
        this.dataFileLoader = dataFileLoader;
        this.databaseTester = databaseTester;
    }

    /**
     * Create new instance with specified test case name.
     * 
     * @param name
     *            The test case name.
     */
    public DefaultPrepAndExpectedTestCase(String name) {
        super(name);
    }

    /**
     * {@inheritDoc}
     */
    public void configureTest(VerifyTableDefinition[] tables,
            String[] prepDataFiles, String[] expectedDataFiles)
            throws Exception {
        LOG.debug("configureTest: saving instance variables");
        this.prepDs = makeCompositeDataSet(prepDataFiles);
        this.expectedDs = makeCompositeDataSet(expectedDataFiles);
        this.tableDefs = tables;
    }

    /**
     * {@inheritDoc}
     */
    public void preTest() throws Exception {
        setupData();
    }

    /**
     * {@inheritDoc}
     */
    public void postTest() throws Exception {
        verifyData();
        cleanupData();
    }

    /**
     * {@inheritDoc}
     */
    public void cleanupData() throws Exception {
        if (databaseTester == null) {
            throw new IllegalStateException(
                    "databaseTester is null; must configure or set it first");
        }

        IDataSet dataset = new CompositeDataSet(prepDs, expectedDs);
        databaseTester.setDataSet(dataset);
        databaseTester.onTearDown();
    }

    /**
     * Use the provided databaseTester to prep the database with the provided
     * prep dataset. See {@link org.dbunit.IDatabaseTester#onSetup()}.
     * 
     * @throws Exception
     */
    protected void setupData() throws Exception {
        LOG.debug("setupData: setting prep dataset and inserting rows");
        if (databaseTester == null) {
            throw new IllegalStateException(
                    "databaseTester is null; must configure or set it first");
        }

        databaseTester.setDataSet(prepDs);
        databaseTester.onSetup();
    }

    /**
     * For the provided VerifyTableDefinitions, verify each table's actual
     * results are as expected. Uses the connection from the provided
     * databaseTester.
     * 
     * @throws Exception
     */
    protected void verifyData() throws Exception {
        if (databaseTester == null) {
            throw new IllegalStateException(
                    "databaseTester is null; must configure or set it first");
        }

        IDatabaseConnection connection = databaseTester.getConnection();

        int count = tableDefs.length;
        LOG.info("verifyData: about to verify {} tables", new Integer(count));
        if (count == 0) {
            LOG.warn("No tables to verify;"
                    + " no VerifyTableDefinitions specified");
        }

        for (int i = 0; i < count; i++) {
            VerifyTableDefinition td = tableDefs[i];
            String[] excludeColumns = td.getColumnExclusionFilters();
            String[] includeColumns = td.getColumnInclusionFilters();
            String tableName = td.getTableName();

            LOG.info("Verifying table '{}'", tableName);

            LOG.debug("  Loading its rows from expected dataset");
            ITable expectedTable = expectedDs.getTable(tableName);

            LOG.debug("  Loading its rows from actual table");
            ITable actualTable = connection.createTable(tableName);

            verifyData(expectedTable, actualTable, excludeColumns,
                    includeColumns);
        }
    }

    /**
     * For the specified expected and actual tables (and excluding and including
     * the specified columns), verify the actual data is as expected.
     * 
     * @param expectedTable
     *            The expected table to compare the actual table to.
     * @param actualTable
     *            The actual table to compare to the expected table.
     * @param excludeColumns
     *            The column names to exclude from comparison. See
     *            {@link org.dbunit.dataset.filter.DefaultColumnFilter#excludeColumn(String)}
     *            .
     * @param includeColumns
     *            The column names to only include in comparison. See
     *            {@link org.dbunit.dataset.filter.DefaultColumnFilter#includeColumn(String)}
     *            .
     * @throws DatabaseUnitException.
     */
    protected void verifyData(ITable expectedTable, ITable actualTable,
            String[] excludeColumns, String[] includeColumns)
            throws DatabaseUnitException {
        // Filter out the columns from the expected and actual results
        LOG.debug("Applying filters to expected table");
        ITable expectedFilteredTable =
                applyColumnFilters(expectedTable, excludeColumns,
                        includeColumns);
        LOG.debug("Applying filters to actual table");
        ITable actualFilteredTable =
                applyColumnFilters(actualTable, excludeColumns, includeColumns);

        LOG.debug("Sorting expected table");
        SortedTable expectedSortedTable =
                new SortedTable(expectedFilteredTable);
        LOG.debug("Sorting actual table");
        SortedTable actualSortedTable =
                new SortedTable(actualFilteredTable, expectedFilteredTable
                        .getTableMetaData());

        LOG.debug("Comparing expected table to actual table");
        Assertion.assertEquals(expectedSortedTable, actualSortedTable);
    }

    /**
     * Make a <code>IDataSet</code> from the specified files.
     * 
     * @param dataFiles
     *            Represents the array of dbUnit data files.
     * @return The composite dataset.
     * @throws DataSetException
     *             On dbUnit errors.
     */
    protected IDataSet makeCompositeDataSet(String[] dataFiles)
            throws DataSetException {
        if (dataFileLoader == null) {
            throw new IllegalStateException(
                    "dataFileLoader is null; must configure or set it first");
        }

        int count = dataFiles.length;
        LOG.debug("makeCompositeDataSet: dataFiles count=" + count);
        if (count == 0) {
            LOG.warn("makeCompositeDataSet: Specified zero data files");
        }

        List list = new ArrayList();
        for (int i = 0; i < count; i++) {
            IDataSet ds = dataFileLoader.load(dataFiles[i]);
            list.add(ds);
        }

        IDataSet[] dataSet = (IDataSet[]) list.toArray(new IDataSet[] {});
        IDataSet compositeDS = new CompositeDataSet(dataSet);
        return compositeDS;
    }

    /**
     * Apply the specified exclude and include column filters to the specified
     * table.
     * 
     * @param table
     *            The table to apply the filters to.
     * @param excludeColumns
     *            The exclude filters.
     * @param includeColumns
     *            The include filters.
     * @return The filtered table.
     * @throws DataSetException
     */
    protected ITable applyColumnFilters(ITable table, String[] excludeColumns,
            String[] includeColumns) throws DataSetException {
        ITable filteredTable = table;

        if (table == null) {
            throw new IllegalArgumentException("table is null");
        }

        if (excludeColumns == null) {
            throw new IllegalArgumentException("excludeColumns is null");
        }

        if (includeColumns == null) {
            throw new IllegalArgumentException("includeColumns is null");
        }

        // only apply the inclusion filters if the array is not empty,
        // as dbunit interprets an empty inclusion filter array as one
        // not wanting to compare anything!
        if (includeColumns.length > 0) {
            LOG.debug("applyColumnFilters: including columns={}",
                    new Object[] {includeColumns});
            filteredTable =
                    DefaultColumnFilter.includedColumnsTable(filteredTable,
                            includeColumns);
        } else {
            LOG.debug("applyColumnFilters: including columns=(all)");
        }

        LOG.debug("applyColumnFilters: excluding columns={}",
                new Object[] {excludeColumns});
        filteredTable =
                DefaultColumnFilter.excludedColumnsTable(filteredTable,
                        excludeColumns);

        return filteredTable;
    }

    /**
     * {@inheritDoc}
     */
    public IDataSet getPrepDataset() {
        return prepDs;
    }

    /**
     * {@inheritDoc}
     */
    public IDataSet getExpectedDataset() {
        return expectedDs;
    }

    /**
     * Get the databaseTester.
     * 
     * @see {@link databaseTester}.
     * 
     * @return The databaseTester.
     */
    public IDatabaseTester getDatabaseTester() {
        return databaseTester;
    }

    /**
     * Set the databaseTester.
     * 
     * @see {@link databaseTester}.
     * 
     * @param databaseTester
     *            The databaseTester to set.
     */
    public void setDatabaseTester(IDatabaseTester databaseTester) {
        this.databaseTester = databaseTester;
    }

    /**
     * Get the dataFileLoader.
     * 
     * @see {@link dataFileLoader}.
     * 
     * @return The dataFileLoader.
     */
    public DataFileLoader getDataFileLoader() {
        return dataFileLoader;
    }

    /**
     * Set the dataFileLoader.
     * 
     * @see {@link dataFileLoader}.
     * 
     * @param dataFileLoader
     *            The dataFileLoader to set.
     */
    public void setDataFileLoader(DataFileLoader dataFileLoader) {
        this.dataFileLoader = dataFileLoader;
    }

    /**
     * Set the prepDs.
     * 
     * @see {@link prepDs}.
     * 
     * @param prepDs
     *            The prepDs to set.
     */
    public void setPrepDs(IDataSet prepDs) {
        this.prepDs = prepDs;
    }

    /**
     * Set the expectedDs.
     * 
     * @see {@link expectedDs}.
     * 
     * @param expectedDs
     *            The expectedDs to set.
     */
    public void setExpectedDs(IDataSet expectedDs) {
        this.expectedDs = expectedDs;
    }

    /**
     * Get the tableDefs.
     * 
     * @see {@link tableDefs}.
     * 
     * @return The tableDefs.
     */
    public VerifyTableDefinition[] getTableDefs() {
        return tableDefs;
    }

    /**
     * Set the tableDefs.
     * 
     * @see {@link tableDefs}.
     * 
     * @param tableDefs
     *            The tableDefs to set.
     */
    public void setTableDefs(VerifyTableDefinition[] tableDefs) {
        this.tableDefs = tableDefs;
    }
}
