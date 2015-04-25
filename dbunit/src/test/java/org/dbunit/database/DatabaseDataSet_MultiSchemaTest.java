package org.dbunit.database;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.sql.Connection;
import java.util.Arrays;

import org.dbunit.H2Environment;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.NoSuchTableException;
import org.dbunit.testutil.TestUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test the multiple schema support of DatabaseDataSet.
 * 
 * <p>
 * This test case uses the H2 database because it offers easy handling of
 * schemas / users.
 * </p>
 */
public class DatabaseDataSet_MultiSchemaTest
{
    private static final String DATABASE = "multischematest";
    private static final String USERNAME_ADMIN = "sa";
    private static final String USERNAME_DBUNIT = "DBUNITUSER";
    private static final String USERNAME_DEFAULT = "DEFAULTUSER";
    private static final String PASSWORD = "test";
    private static final String PASSWORD_NONE = "";
    private static final String SCHEMA_DEFAULT = USERNAME_DEFAULT;
    private static final String SCHEMA_DBUNIT = USERNAME_DBUNIT;
    private static final String SCHEMA_NONE = null;

    private static final String TABLE_BAR = "BAR";
    private static final String TABLE_FOO = "FOO";

    private static final String TABLE_BAR_IN_SCHEMA_DBUNIT = SCHEMA_DBUNIT
            + "." + TABLE_BAR;
    private static final String TABLE_FOO_IN_SCHEMA_DEFAULT = SCHEMA_DEFAULT
            + "." + TABLE_FOO;

    private static final Boolean IS_USING_QUALIFIED_TABLE_NAMES = Boolean.TRUE;
    private static final Boolean IS_NOT_USING_QUALIFIED_TABLE_NAMES =
            Boolean.FALSE;

    private static final String SETUP_DDL_FILE =
            "sql/h2_multischema_permission_test.sql";

    private static Connection connectionDdl;

    private IDatabaseConnection connectionTest;

    @BeforeClass
    public static void setUpClass() throws Exception
    {
        // create database and schemas for tests
        connectionDdl = H2Environment.createJdbcConnection(DATABASE);
        H2Environment.executeDdlFile(TestUtils.getFile(SETUP_DDL_FILE),
                connectionDdl);
    }

    @AfterClass
    public static void tearDownClass() throws Exception
    {
        // close connection after all tests so schemas stay around
        if (connectionDdl != null && !connectionDdl.isClosed())
        {
            connectionDdl.close();
        }
    }

    @After
    public void tearDown() throws Exception
    {
        if (connectionTest != null)
        {
            connectionTest.close();
        }
    }

    /**
     * Admin user has full access to all tables in all schemas.
     * 
     * @throws Exception
     */
    @Test
    public void testPermissions_AdminUser_QualifiedTableNames()
            throws Exception
    {
        IDataSet dataSet =
                makeDataSet(DATABASE, USERNAME_ADMIN, PASSWORD_NONE,
                        SCHEMA_NONE, IS_USING_QUALIFIED_TABLE_NAMES);

        String[] allTables = dataSet.getTableNames();
        Arrays.sort(allTables);
        assertEquals(2, allTables.length);
        assertEquals(TABLE_BAR_IN_SCHEMA_DBUNIT, allTables[0]);
        assertEquals(TABLE_FOO_IN_SCHEMA_DEFAULT, allTables[1]);
    }

    /**
     * As basic schema owner you will have access to your own tables, but not to
     * other ones.
     * 
     * @throws Exception
     */
    @Test
    public void testPermissions_OwningUser_QualifiedTableNames()
            throws Exception
    {
        IDataSet dataSet =
                makeDataSet(DATABASE, USERNAME_DEFAULT, PASSWORD,
                        SCHEMA_DEFAULT, IS_USING_QUALIFIED_TABLE_NAMES);

        // Own table
        String[] allTables = dataSet.getTableNames();
        Arrays.sort(allTables);
        assertEquals(1, allTables.length);
        assertEquals(TABLE_FOO_IN_SCHEMA_DEFAULT, allTables[0]);

        // Table of other user/schema
        try
        {
            dataSet.getTable(TABLE_BAR_IN_SCHEMA_DBUNIT);
            fail();
        } catch (DataSetException e)
        {
            // Not enough permissions
        }
    }

    /**
     * If we don't use qualified table names, then we still use only our own
     * tables.
     * 
     * @throws Exception
     */
    @Test
    public void testPermissions_OwningUser_UnqualifiedTableNames()
            throws Exception
    {
        IDataSet dataSet =
                makeDataSet(DATABASE, USERNAME_DEFAULT, PASSWORD,
                        SCHEMA_DEFAULT, IS_NOT_USING_QUALIFIED_TABLE_NAMES);

        String[] allTables = dataSet.getTableNames();
        Arrays.sort(allTables);
        assertEquals(1, allTables.length);
        assertEquals(TABLE_FOO, allTables[0]);

        // Table of other user/schema
        try
        {
            dataSet.getTable(TABLE_BAR);
            fail();
        } catch (NoSuchTableException e)
        {
            // expected
        }
    }

    /**
     * A special dbunit user could be allowed to access tables from other users
     * to prepare test data.
     * 
     * @throws Exception
     */
    @Test
    // THIS ONE FAILS WITHOUT ISSUE 368 IN PLACE
    public void testPermissions_DbunitUser_QualifiedTables() throws Exception
    {
        IDataSet dataSet =
                makeDataSet(DATABASE, USERNAME_DBUNIT, PASSWORD, SCHEMA_DBUNIT,
                        IS_USING_QUALIFIED_TABLE_NAMES);

        String[] allTables = dataSet.getTableNames();
        Arrays.sort(allTables);
        assertEquals(1, allTables.length);
        assertEquals(TABLE_BAR_IN_SCHEMA_DBUNIT, allTables[0]);

        // Access table of other owner - metadata will be lazy loaded
        ITable table = dataSet.getTable(TABLE_FOO_IN_SCHEMA_DEFAULT);
        assertNotNull(table);

        // Unqualified access to table isn't possible
        try
        {
            table = dataSet.getTable(TABLE_FOO);
            fail();
        } catch (NoSuchTableException e)
        {
            // expected
        }
    }

    @Test
    public void testPermissions_DbunitUser_UnqualifiedTables() throws Exception
    {
        IDataSet dataSet =
                makeDataSet(DATABASE, USERNAME_DBUNIT, PASSWORD, SCHEMA_DBUNIT,
                        IS_NOT_USING_QUALIFIED_TABLE_NAMES);

        String[] allTables = dataSet.getTableNames();
        Arrays.sort(allTables);
        assertEquals(1, allTables.length);
        assertEquals(TABLE_BAR, allTables[0]);

        // Access table of other owner
        ITable table = dataSet.getTable(TABLE_BAR);
        assertNotNull(table);

        try
        {
            dataSet.getTable(TABLE_FOO);
            fail();
        } catch (NoSuchTableException e)
        {
            // expected
        }
    }

    /**
     * Without explicit schema selection, all available tables will be loaded...
     * 
     * @throws Exception
     */
    @Test
    public void testPermissions_DbunitUser_QualifiedTableNames_NoSpecifiedSchema()
            throws Exception
    {
        IDataSet dataSet =
                makeDataSet(DATABASE, USERNAME_DBUNIT, PASSWORD, SCHEMA_NONE,
                        IS_USING_QUALIFIED_TABLE_NAMES);

        String[] allTables = dataSet.getTableNames();
        Arrays.sort(allTables);
        assertEquals(2, allTables.length);
        assertEquals(TABLE_BAR_IN_SCHEMA_DBUNIT, allTables[0]);
        assertEquals(TABLE_FOO_IN_SCHEMA_DEFAULT, allTables[1]);

        // Qualified access to own tables...
        ITable table = dataSet.getTable(TABLE_BAR_IN_SCHEMA_DBUNIT);
        assertNotNull(table);

        // Qualified access to other tables...
        table = dataSet.getTable(TABLE_FOO_IN_SCHEMA_DEFAULT);
        assertNotNull(table);

        // But unqualified access doesn't work...
        try
        {
            dataSet.getTable(TABLE_FOO);
            fail();
        } catch (NoSuchTableException e)
        {
            // expected
        }
    }

    /**
     * Without explizit schema selection, all available tables will be loaded -
     * but without qualified table access, no metadata will be found.
     * 
     * @throws Exception
     */
    @Test
    public void testPermissions_DbunitUser_UnqualifiedTableNames_NoSpecifiedSchema()
            throws Exception
    {
        IDataSet dataSet =
                makeDataSet(DATABASE, USERNAME_DBUNIT, PASSWORD, SCHEMA_NONE,
                        IS_NOT_USING_QUALIFIED_TABLE_NAMES);

        String[] allTables = dataSet.getTableNames();
        Arrays.sort(allTables);
        assertEquals(2, allTables.length);
        assertEquals(TABLE_BAR, allTables[0]);
        assertEquals(TABLE_FOO, allTables[1]);

        // Qualified access to own tables...
        try
        {
            dataSet.getTable(TABLE_BAR);
        } catch (DataSetException e1)
        {
            // No metadata could be loaded...
        }

        // Qualified access to other tables...
        try
        {
            dataSet.getTable(TABLE_FOO);
        } catch (DataSetException e1)
        {
            // No metadata could be loaded...
        }

        // But unqualified access doesn't work...
        try
        {
            dataSet.getTable(TABLE_FOO_IN_SCHEMA_DEFAULT);
            fail();
        } catch (NoSuchTableException e)
        {
            // expected
        }
    }

    private IDataSet makeDataSet(String databaseName, String username,
            String password, String schema, boolean useQualifiedTableNames)
            throws Exception
    {
        makeDatabaseConnection(databaseName, username, password, schema,
                useQualifiedTableNames);

        return connectionTest.createDataSet();
    }

    private void makeDatabaseConnection(String databaseName, String username,
            String password, String schema, boolean useQualifiedTableNames)
            throws Exception
    {
        Connection jdbcConnection =
                H2Environment.createJdbcConnection(databaseName, username,
                        password);
        connectionTest = new DatabaseConnection(jdbcConnection, schema);
        connectionTest.getConfig().setProperty(
                DatabaseConfig.FEATURE_QUALIFIED_TABLE_NAMES,
                useQualifiedTableNames);
    }
}
