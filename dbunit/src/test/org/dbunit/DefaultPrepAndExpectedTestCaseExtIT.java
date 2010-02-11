package org.dbunit;

import junit.framework.ComparisonFailure;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.util.fileloader.DataFileLoader;
import org.dbunit.util.fileloader.FlatXmlDataFileLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Integration test of extends of the PrepAndExpected.
 * 
 * @author Jeff Jensen jeffjensen AT users.sourceforge.net
 * @author Last changed by: $Author$
 * @version $Revision$ $Date$
 * @since 2.4.8
 */
public class DefaultPrepAndExpectedTestCaseExtIT extends
        DefaultPrepAndExpectedTestCase {
    private static final String PREP_DATA_FILE_NAME = "/flatXmlDataSetTest.xml";
    private static final String EXP_DATA_FILE_NAME =
            "/flatXmlDataSetTestChanged.xml";

    private static final VerifyTableDefinition TEST_TABLE =
            new VerifyTableDefinition("TEST_TABLE", new String[] {});

    private static final VerifyTableDefinition SECOND_TABLE =
            new VerifyTableDefinition("SECOND_TABLE", new String[] {});

    private final Logger LOG =
            LoggerFactory.getLogger(DefaultPrepAndExpectedTestCaseExtIT.class);

    private final DataFileLoader dataFileLoader = new FlatXmlDataFileLoader();

    private DatabaseEnvironment dbEnv;
    private IDatabaseConnection connection;
    private IDatabaseTester databaseTester;

    protected void setUp() throws Exception {
        dbEnv = DatabaseEnvironment.getInstance();
        connection = dbEnv.getConnection();
        databaseTester = new DefaultDatabaseTester(connection);

        setDataFileLoader(dataFileLoader);
        setDatabaseTester(databaseTester);

        // don't call super.setUp() here as prep data is not loaded yet
        // (getDataSet() is null)
        // super.setUp();
    }

    public void testSuccessRun() throws Exception {
        String[] prepDataFiles = {PREP_DATA_FILE_NAME};
        String[] expectedDataFiles = {PREP_DATA_FILE_NAME};
        VerifyTableDefinition[] tables = {TEST_TABLE, SECOND_TABLE};

        configureTest(tables, prepDataFiles, expectedDataFiles);
        preTest();

        // skip modifying data and just verify the insert

        // reopen connection as DefaultOperationListener closes it after inserts
        // maybe we need a KeepConnectionOpenOperationListener class?!
        connection = dbEnv.getConnection();
        databaseTester = new DefaultDatabaseTester(connection);
        setDatabaseTester(databaseTester);

        postTest();
    }

    public void testFailRun() throws Exception {
        String[] prepDataFiles = {PREP_DATA_FILE_NAME};
        String[] expectedDataFiles = {EXP_DATA_FILE_NAME};
        VerifyTableDefinition[] tables = {TEST_TABLE, SECOND_TABLE};

        configureTest(tables, prepDataFiles, expectedDataFiles);
        preTest();

        // skip modifying data and just verify the insert

        // reopen connection as DefaultOperationListener closes it after inserts
        // maybe we need a KeepConnectionOpenOperationListener class?!
        connection = dbEnv.getConnection();
        databaseTester = new DefaultDatabaseTester(connection);
        setDatabaseTester(databaseTester);

        try {
            postTest();
            fail("Did not catch expected exception:"
                    + " junit.framework.ComparisonFailure");
        } catch (ComparisonFailure e) {
            // test passes
        }
    }
}
