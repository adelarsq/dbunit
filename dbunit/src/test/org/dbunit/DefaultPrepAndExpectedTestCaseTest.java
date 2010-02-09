package org.dbunit;

import junit.framework.TestCase;

import org.dbunit.dataset.IDataSet;
import org.dbunit.util.fileloader.DataFileLoader;
import org.dbunit.util.fileloader.FlatXmlDataFileLoader;

public class DefaultPrepAndExpectedTestCaseTest extends TestCase {
    private static final String PREP_DATA_FILE_NAME = "/flatXmlDataSetTest.xml";
    private static final String EXP_DATA_FILE_NAME = "/flatXmlDataSetTest.xml";

    private final DataFileLoader dataFileLoader = new FlatXmlDataFileLoader();
    // private final IDatabaseTester databaseTester = new
    // JdbcDatabaseTester(driverClass, connectionUrl);

    private final DefaultPrepAndExpectedTestCase tc =
            new DefaultPrepAndExpectedTestCase();

    protected void setUp() throws Exception {
        super.setUp();
        // tc.setDatabaseTester(databaseTester);
        tc.setDataFileLoader(dataFileLoader);
    }

    public void testConfigureTest() throws Exception {
        VerifyTableDefinition[] tables = {};
        String[] prepDataFiles = {PREP_DATA_FILE_NAME};
        String[] expectedDataFiles = {EXP_DATA_FILE_NAME};

        tc.configureTest(tables, prepDataFiles, expectedDataFiles);

        assertEquals("Configured tables do not match expected.", tables, tc
                .getTableDefs());

        IDataSet expPrepDs = dataFileLoader.load(PREP_DATA_FILE_NAME);
        Assertion.assertEquals(expPrepDs, tc.getPrepDs());

        IDataSet expExpDs = dataFileLoader.load(EXP_DATA_FILE_NAME);
        Assertion.assertEquals(expExpDs, tc.getExpectedDs());
    }

    public void testPreTest() throws Exception {
        // TODO implement test
    }

    public void testPostTest() {
        // TODO implement test
    }

    public void testSetupData() {
        // TODO implement test
    }

    public void testVerifyData() {
        // TODO implement test
    }

    public void testVerifyDataITableITableStringArrayStringArray() {
        // TODO implement test
    }

    public void testCleanupData() {
        // TODO implement test
    }

    public void testMakeCompositeDataSet() {
        // TODO implement test
    }

    public void testApplyColumnFilters() {
        // TODO implement test
    }
}
