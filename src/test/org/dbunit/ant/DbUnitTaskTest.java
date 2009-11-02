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

package org.dbunit.ant;

import java.io.File;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;
import junitx.framework.ArrayAssert;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.BuildFileTest;
import org.apache.tools.ant.Target;
import org.dbunit.DatabaseEnvironment;
import org.dbunit.DatabaseUnitException;
import org.dbunit.IDatabaseTester;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.FilteredDataSet;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.NoSuchTableException;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.datatype.IDataTypeFactory;
import org.dbunit.ext.mssql.InsertIdentityOperation;
import org.dbunit.ext.oracle.OracleDataTypeFactory;
import org.dbunit.operation.DatabaseOperation;
import org.dbunit.util.FileHelper;

/**
 * Ant-based test class for the Dbunit ant task definition.
 *
 * @author Timothy Ruppert
 * @author Ben Cox
 * @author Last changed by: $Author$
 * @version $Revision$ $Date$
 * @since Jun 10, 2002
 * @see org.dbunit.ant.AntTest
 */
public class DbUnitTaskTest extends BuildFileTest
{
    static protected Class classUnderTest = DbUnitTaskTest.class;
    
    private static final String BUILD_FILE_DIR = "src/xml";
    private static final String OUTPUT_DIR = "target/xml";
    
    private File outputDir;
    
    public DbUnitTaskTest(String name)
    {
        super(name);
    }

    public void setUp() throws Exception
    {
        // This line ensure test database is initialized
        DatabaseEnvironment.getInstance();

        String filePath = BUILD_FILE_DIR + "/antTestBuildFile.xml";
        assertTrue("Buildfile not found", new File(filePath).isFile());
        configureProject(filePath);
        
        outputDir = new File(getProjectDir(), OUTPUT_DIR);
        outputDir.mkdirs();
    }

    protected void tearDown() throws Exception 
    {
        super.tearDown();
        
        outputDir = new File(getProjectDir(), OUTPUT_DIR);
        FileHelper.deleteDirectory(outputDir);
    }

    public void testNoDriver()
    {
        expectBuildException("no-driver", "Should have required a driver attribute.");
    }

    public void testNoDbUrl()
    {
        expectBuildException("no-db-url", "Should have required a url attribute.");
    }

    public void testNoUserid()
    {
        expectBuildException("no-userid", "Should have required a userid attribute.");
    }

    public void testNoPassword()
    {
        expectBuildException("no-password", "Should have required a password attribute.");
    }

    public void testInvalidDatabaseInformation()
    {
        Throwable sql = null;
        try
        {
            executeTarget("invalid-db-info");
        }
        catch (BuildException e)
        {
            sql = e.getException();
        }
        finally
        {
            assertNotNull("Should have thrown a SQLException.", sql);
            assertTrue("Should have thrown a SQLException.", (sql instanceof SQLException));
        }
    }

    public void testInvalidOperationType()
    {
        Throwable iae = null;
        try
        {
            executeTarget("invalid-type");
        }
        catch (BuildException e)
        {
            iae = e.getException();
        }
        finally
        {
            assertNotNull("Should have thrown an IllegalArgumentException.", iae);
            assertTrue("Should have thrown an IllegalArgumentException.",
                    (iae instanceof IllegalArgumentException));
        }
    }

    public void testSetFlatFalse()
    {
        String targetName = "set-format-xml";
        Operation operation = (Operation)getFirstStepFromTarget(targetName);
        assertTrue("Operation attribute format should have been 'xml', but was: "
                + operation.getFormat(), operation.getFormat().equalsIgnoreCase("xml"));
    }

    public void testResolveOperationTypes()
    {
        assertOperationType("Should have been a NONE operation",
                "test-type-none", DatabaseOperation.NONE);
        assertOperationType("Should have been an DELETE_ALL operation",
                "test-type-delete-all", DatabaseOperation.DELETE_ALL);
        assertOperationType("Should have been an INSERT operation",
                "test-type-insert", DatabaseOperation.INSERT);
        assertOperationType("Should have been an UPDATE operation",
                "test-type-update", DatabaseOperation.UPDATE);
        assertOperationType("Should have been an REFRESH operation",
                "test-type-refresh", DatabaseOperation.REFRESH);
        assertOperationType("Should have been an CLEAN_INSERT operation",
                "test-type-clean-insert", DatabaseOperation.CLEAN_INSERT);
        assertOperationType("Should have been an DELETE operation",
                "test-type-delete", DatabaseOperation.DELETE);
        assertOperationType("Should have been an MSSQL_INSERT operation",
                "test-type-mssql-insert", InsertIdentityOperation.INSERT);
        assertOperationType("Should have been an MSSQL_REFRESH operation",
                "test-type-mssql-refresh", InsertIdentityOperation.REFRESH);
        assertOperationType("Should have been an MSSQL_CLEAN_INSERT operation",
                "test-type-mssql-clean-insert", InsertIdentityOperation.CLEAN_INSERT);
    }

    public void testInvalidCompositeOperationSrc()
    {
        expectBuildException("invalid-composite-operation-src",
                "Should have objected to nested operation src attribute "
                + "being set.");
    }

    public void testInvalidCompositeOperationFlat()
    {
        expectBuildException("invalid-composite-operation-format-flat",
                "Should have objected to nested operation format attribute "
                + "being set.");
    }

    public void testExportFull()
    {
        String targetName = "test-export-full";
        Export export = (Export)getFirstStepFromTarget(targetName);
        assertTrue("Should have been a flat format, "
                + "but was: " + export.getFormat(),
                export.getFormat().equalsIgnoreCase("flat"));
        List tables = export.getTables();
        assertTrue("Should have been an empty table list "
                + "(indicating a full dataset), but was: "
                + tables, tables.size() == 0);
    }
	
    public void testExportPartial()
    {
        String targetName = "test-export-partial";
        Export export = (Export)getFirstStepFromTarget(targetName);
        List tables = export.getTables();
        assertEquals("table count", 2, tables.size());
        Table testTable = (Table)tables.get(0);
        Table pkTable = (Table)tables.get(1);
        assertTrue("Should have been been TABLE TEST_TABLE, but was: "
                + testTable.getName(), testTable.getName().equals("TEST_TABLE"));
        assertTrue("Should have been been TABLE PK_TABLE, but was: "
                + pkTable.getName(), pkTable.getName().equals("PK_TABLE"));
    }

    public void testExportFlat()
    {
        String targetName = "test-export-format-flat";
        Export export = (Export)getFirstStepFromTarget(targetName);
        assertEquals("format", "flat", export.getFormat());
    }
    
    public void testExportFlatWithDocytpe()
    {
        String targetName = "test-export-format-flat-with-doctype";
        Export export = (Export)getFirstStepFromTarget(targetName);
        assertEquals("format", "flat", export.getFormat());
        assertEquals("doctype", "dataset.dtd", export.getDoctype());
    }

    public void testExportFlatWithEncoding()
    {
        String targetName = "test-export-format-flat-with-encoding";
        Export export = (Export)getFirstStepFromTarget(targetName);
        assertEquals("format", "flat", export.getFormat());
        assertEquals("encoding", "ISO-8859-1", export.getEncoding());
    }
    
    public void testExportXml()
    {
        String targetName = "test-export-format-xml";
        Export export = (Export)getFirstStepFromTarget(targetName);
        assertTrue("Should have been an xml format, "
                + "but was: " + export.getFormat(),
                export.getFormat().equalsIgnoreCase("xml"));
    }

	public void testExportCsv() {
		String targetName = "test-export-format-csv";
		Export export = (Export)getFirstStepFromTarget(targetName);
		assertTrue("Should have been a csv format, "
				+ "but was: " + export.getFormat(),
				export.getFormat().equalsIgnoreCase("csv")); 
	}
	
    public void testExportDtd()
    {
        String targetName = "test-export-format-dtd";
        Export export = (Export)getFirstStepFromTarget(targetName);
        assertTrue("Should have been a dtd format, "
                + "but was: " + export.getFormat(),
                export.getFormat().equalsIgnoreCase("dtd"));
    }

    public void testInvalidExportFormat()
    {
        expectBuildException("invalid-export-format",
                "Should have objected to invalid format attribute.");
    }

    public void testExportXmlOrdered() throws Exception
    {
        String targetName = "test-export-format-xml-ordered";
        Export export = (Export)getFirstStepFromTarget(targetName);
        assertEquals("Should be ordered", true, export.isOrdered());
        assertTrue("Should have been an xml format, "
                + "but was: " + export.getFormat(),
                export.getFormat().equalsIgnoreCase("xml"));
        
        // Test if the correct dataset is created for ordered export
        DbUnitTask task = getFirstTargetTask(targetName);
        IDatabaseConnection connection = task.createConnection();
        IDataSet dataSetToBeExported = export.getExportDataSet(connection);
        // Ordered export should use the filtered dataset
        assertEquals(dataSetToBeExported.getClass(), FilteredDataSet.class);
    }

    public void testExportQuery()
    {
        String targetName = "test-export-query";
        Export export = (Export)getFirstStepFromTarget(targetName);
        assertEquals("format", "flat", export.getFormat());

        List queries = export.getTables();
        assertEquals("query count", 2, getQueryCount(queries));

        Query testTable = (Query)queries.get(0);
        assertEquals("name", "TEST_TABLE", testTable.getName());
        assertEquals("sql", "SELECT * FROM test_table ORDER BY column0 DESC", testTable.getSql());

        Query pkTable = (Query)queries.get(1);
        assertEquals("name", "PK_TABLE", pkTable.getName());
        assertEquals("sql", "SELECT * FROM pk_table", pkTable.getSql());
    }

	public void testExportWithQuerySet() {
		String targetName = "test-export-with-queryset";
		Export export = (Export)getFirstStepFromTarget(targetName);
		assertEquals("format", "csv", export.getFormat());
		
		List queries = export.getTables();
		
		assertEquals("query count", 1, getQueryCount(queries));
		assertEquals("table count", 1, getTableCount(queries));	
		assertEquals("queryset count", 2, getQuerySetCount(queries));
		
		Query secondTable = (Query)queries.get(0);
		assertEquals("name", "SECOND_TABLE", secondTable.getName());
		assertEquals("sql", "SELECT * FROM SECOND_TABLE", secondTable.getSql());
			
		QuerySet queryset1 = (QuerySet)queries.get(1);
		
		Query testTable = (Query)queryset1.getQueries().get(0);
		
		assertEquals("name", "TEST_TABLE", testTable.getName());
		
		QuerySet queryset2 = (QuerySet)queries.get(2);
		
		Query pkTable = (Query)queryset2.getQueries().get(0);
		Query testTable2 = (Query)queryset2.getQueries().get(1);
		
		assertEquals("name", "PK_TABLE", pkTable.getName());
		assertEquals("name", "TEST_TABLE", testTable2.getName());
		
		Table emptyTable = (Table)queries.get(3);
		
		assertEquals("name", "EMPTY_TABLE", emptyTable.getName());
	}
	
	public void testWithBadQuerySet() {
		expectBuildException("invalid-queryset",
			"Cannot specify 'id' and 'refid' attributes together in queryset.");
	}
	
	public void testWithReferenceQuerySet() {
		String targetName = "test-queryset-reference";
		
		Export export = (Export)getFirstStepFromTarget(targetName);
		
		List tables = export.getTables();
		
		assertEquals("total count", 1, tables.size());
		
		QuerySet queryset = (QuerySet)tables.get(0);
		Query testTable = (Query)queryset.getQueries().get(0);
		Query secondTable = (Query)queryset.getQueries().get(1);
		
		assertEquals("name", "TEST_TABLE", testTable.getName());
		assertEquals("sql", "SELECT * FROM TEST_TABLE WHERE COLUMN0 = 'row0 col0'", 
					testTable.getSql());
					
		assertEquals("name", "SECOND_TABLE", secondTable.getName());	
		assertEquals("sql", 
			"SELECT B.* FROM TEST_TABLE A, SECOND_TABLE B " +
			"WHERE A.COLUMN0 = 'row0 col0' AND B.COLUMN0 = A.COLUMN0",
			secondTable.getSql());
		
	}
	
    public void testExportQueryMixed() {
        String targetName = "test-export-query-mixed";
        Export export = (Export)getFirstStepFromTarget(targetName);
        assertEquals("format", "flat", export.getFormat());

        List tables = export.getTables();
        assertEquals("total count", 2, tables.size());
        assertEquals("table count", 1, getTableCount(tables));
        assertEquals("query count", 1, getQueryCount(tables));

        Table testTable = (Table)tables.get(0);
        assertEquals("name", "TEST_TABLE", testTable.getName());

        Query pkTable = (Query)tables.get(1);
        assertEquals("name", "PK_TABLE", pkTable.getName());
    }
    
    /**
     * Tests the exception that is thrown when the compare fails because
     * the source format was different from the previous "export" task's write format.
     */
    public void testExportAndCompareFormatMismatch() {
        String targetName = "test-export-and-compare-format-mismatch";
        
        try {
        	getFirstTargetTask(targetName);
        	fail("Should not be able to invoke ant task where the expected table was not found because it was tried to read in the wrong format.");
        }
        catch(BuildException expected){
        	Throwable cause = expected.getCause();
        	assertTrue(cause instanceof DatabaseUnitException);
        	DatabaseUnitException dbUnitException = (DatabaseUnitException)cause;
        	String filename = new File(outputDir, "antExportDataSet.xml").toString();
        	String expectedMsg = "Did not find table in source file '" + filename + "' using format 'xml'";
        	assertEquals(expectedMsg, dbUnitException.getMessage());
        	assertTrue(dbUnitException.getCause() instanceof NoSuchTableException);
        	NoSuchTableException nstException = (NoSuchTableException)dbUnitException.getCause();
        	assertEquals("TEST_TABLE", nstException.getMessage());
        }
    }
    
    public void testDataTypeFactory() throws Exception
    {
        String targetName = "test-datatypefactory";
        DbUnitTask task = getFirstTargetTask(targetName);

        IDatabaseConnection connection = task.createConnection();
        IDataTypeFactory factory = (IDataTypeFactory)connection.getConfig().getProperty(
                        DatabaseConfig.PROPERTY_DATATYPE_FACTORY);

        Class expectedClass = OracleDataTypeFactory.class;
        assertEquals("factory", expectedClass, factory.getClass());
    }

    public void testEscapePattern() throws Exception
    {
        String targetName = "test-escapepattern";
        DbUnitTask task = getFirstTargetTask(targetName);

        IDatabaseConnection connection = task.createConnection();
        String actualPattern = (String)connection.getConfig().getProperty(
                        DatabaseConfig.PROPERTY_ESCAPE_PATTERN);

        String expectedPattern = "[?]";
        assertEquals("factory", expectedPattern, actualPattern);
    }
    
    public void testDataTypeFactoryViaGenericConfig() throws Exception
    {
        String targetName = "test-datatypefactory-via-generic-config";
        DbUnitTask task = getFirstTargetTask(targetName);

        IDatabaseConnection connection = task.createConnection();
        
        DatabaseConfig config =connection.getConfig();
        
        IDataTypeFactory factory = (IDataTypeFactory)config.getProperty(
                        DatabaseConfig.PROPERTY_DATATYPE_FACTORY);
        Class expectedClass = OracleDataTypeFactory.class;
        assertEquals("factory", expectedClass, factory.getClass());

        String[] actualTableType = (String[])config.getProperty(DatabaseConfig.PROPERTY_TABLE_TYPE);
        ArrayAssert.assertEquals("tableType", new String[]{"TABLE", "SYNONYM"}, actualTableType);
        assertTrue("batched statements feature should be true", 
                connection.getConfig().getFeature(DatabaseConfig.FEATURE_BATCHED_STATEMENTS));
        assertTrue("qualified tablenames feature should be true", 
                connection.getConfig().getFeature(DatabaseConfig.FEATURE_CASE_SENSITIVE_TABLE_NAMES));
    }
    
    
    public void testClasspath() throws Exception
    {
        String targetName = "test-classpath";

        try
        {
            executeTarget(targetName);
            fail("Should not be able to connect with invalid url!");
        }
        catch (BuildException e)
        {
            // Verify exception type
            assertEquals("nested exception type", SQLException.class, e.getException().getClass());
        }

    }

    public void testDriverNotInClasspath() throws Exception
    {
        String targetName = "test-drivernotinclasspath";

        try
        {
            executeTarget(targetName);
            fail("Should not have found driver!");
        }
        catch (BuildException e)
        {
            // Verify exception type
            assertEquals("nested exception type", ClassNotFoundException.class, e.getException().getClass());
        }
    }

    public void testReplaceOperation() throws Exception {
        String targetName = "test-replace";
        final IDatabaseTester dbTest = DatabaseEnvironment.getInstance().getDatabaseTester();
        executeTarget(targetName);
        final IDataSet ds = dbTest.getConnection().createDataSet();
        final ITable table = ds.getTable("PK_TABLE");
        assertNull(table.getValue(0,"NORMAL0"));
        assertEquals("row 1",table.getValue(1,"NORMAL0"));
    }

    public void testOrderedOperation() throws Exception {
        String targetName = "test-ordered";
        final IDatabaseTester dbTest = DatabaseEnvironment.getInstance().getDatabaseTester();
        executeTarget(targetName);
        final IDataSet ds = dbTest.getConnection().createDataSet();
        final ITable table = ds.getTable("PK_TABLE");
        assertEquals("row 0",table.getValue(0,"NORMAL0"));
        assertEquals("row 1",table.getValue(1,"NORMAL0"));
    }

    public void testReplaceOrderedOperation() throws Exception {
        String targetName = "test-replace-ordered";
        final IDatabaseTester dbTest = DatabaseEnvironment.getInstance().getDatabaseTester();
        executeTarget(targetName);
        final IDataSet ds = dbTest.getConnection().createDataSet();
        final ITable table = ds.getTable("PK_TABLE");
        assertNull(table.getValue(0,"NORMAL0"));
        assertEquals("row 1",table.getValue(1,"NORMAL0"));
    }

    protected void assertOperationType(String failMessage, String targetName, DatabaseOperation expected)
    {
        Operation oper = (Operation)getFirstStepFromTarget(targetName);
        DatabaseOperation dbOper = oper.getDbOperation();
        assertTrue(failMessage + ", but was: " + dbOper, expected.equals(dbOper));
    }

    protected int getQueryCount(List tables)
    {
        int count = 0;
        for (Iterator it = tables.iterator(); it.hasNext();)
        {
            if (it.next() instanceof Query)
            {
                count++;
            }
        }

        return count;
    }

    protected int getTableCount(List tables)
    {
        int count = 0;
        for (Iterator it = tables.iterator(); it.hasNext();)
        {
            if (it.next() instanceof Table)
            {
                count++;
            }
        }

        return count;
    }

	protected int getQuerySetCount(List tables) {
		int count = 0;
		for (Iterator it = tables.iterator(); it.hasNext();) {
			if (it.next() instanceof QuerySet) {
				count++;
			}
		}

		return count;
	}
	
    protected DbUnitTaskStep getFirstStepFromTarget(String targetName)
    {
    	return getStepFromTarget(targetName, 0);
    }

    protected DbUnitTaskStep getStepFromTarget(String targetName, int index)
    {
        DbUnitTask task = getFirstTargetTask(targetName);
        List steps = task.getSteps();
        if(steps == null || steps.size() == 0)
        {
        	fail("Can't get a dbunit <step> from the target: " + targetName + ". No steps available.");
        }
        return (DbUnitTaskStep)steps.get(index);
    }

    private DbUnitTask getFirstTargetTask(String targetName)
    {
        Hashtable targets = project.getTargets();
        executeTarget(targetName);
        Target target = (Target)targets.get(targetName);
        
        DbUnitTask task = null;
        
        Object[] tasks = target.getTasks();
        for(int i = 0; i < tasks.length; i++) {
        	if(tasks[i] instanceof DbUnitTask) {
        		task = (DbUnitTask)tasks[i];
        	}
        }
        
        return task;
    }

    public static Test suite()
    {
        TestSuite suite = new TestSuite(classUnderTest);
        return suite;
    }

    public static void main(String args[])
    {
        if (args.length > 0 && args[0].equals("-gui"))
        {
            String[] testCaseName = {classUnderTest.getName()};
            junit.swingui.TestRunner.main(testCaseName);
        }
        else
        {
            junit.textui.TestRunner.run(suite());
        }
    }

}
