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

package org.dbunit.operation;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.SQLException;

import org.dbunit.AbstractDatabaseIT;
import org.dbunit.DatabaseUnitException;
import org.dbunit.TestFeature;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.xml.XmlDataSet;
import org.dbunit.testutil.TestUtils;

/**
 * @author Manuel Laflamme
 * @version $Revision$
 * @since Feb 21, 2002
 */
public class TransactionOperationIT extends AbstractDatabaseIT
{
    public TransactionOperationIT(String s)
    {
        super(s);
    }
    
    protected boolean runTest(String testName) {
      return environmentHasFeature(TestFeature.TRANSACTION);
    }

    public void testExecuteCommit() throws Exception
    {
        String tableName = "TEST_TABLE";
        Reader in = new FileReader(
                TestUtils.getFile("xml/transactionOperationTest.xml"));
        IDataSet xmlDataSet = new XmlDataSet(in);
        Connection jdbcConnection = _connection.getConnection();

        ITable tableBefore = _connection.createDataSet().getTable(tableName);
        assertEquals("before row count", 6, tableBefore.getRowCount());
        assertEquals("autocommit before", true, jdbcConnection.getAutoCommit());

        DatabaseOperation operation = new CompositeOperation(
                DatabaseOperation.DELETE_ALL, DatabaseOperation.INSERT);
        operation = new TransactionOperation(operation);
        operation.execute(_connection, xmlDataSet);

        // snapshot after operation
        ITable tableAfter = _connection.createDataSet().getTable(tableName);
        assertEquals("after row count", 1, tableAfter.getRowCount());
        assertEquals("autocommit after", true, jdbcConnection.getAutoCommit());
    }

    public void testExclusiveTransaction() throws Exception
    {
        String tableName = "TEST_TABLE";
        Reader in = new FileReader(
                TestUtils.getFile("xml/transactionOperationTest.xml"));
        IDataSet xmlDataSet = new XmlDataSet(in);
        Connection jdbcConnection = _connection.getConnection();

        jdbcConnection.setAutoCommit(false);

        // before operation
        assertEquals("autocommit before", false, jdbcConnection.getAutoCommit());
        ITable tableBefore = _connection.createDataSet().getTable(tableName);
        assertEquals("before exclusive", 6, tableBefore.getRowCount());

        try
        {
            // try with exclusive transaction
            DatabaseOperation operation = new TransactionOperation(
                    DatabaseOperation.DELETE);
            operation.execute(_connection, xmlDataSet);
            fail("Should throw ExclusiveTransactionException");
        }
        catch (ExclusiveTransactionException e)
        {
        }
        finally
        {
            jdbcConnection.setAutoCommit(true);
        }

        // after operation
        ITable tableAfter = _connection.createDataSet().getTable(tableName);
        assertEquals("after", 6, tableAfter.getRowCount());
    }

    public void testExecuteRollback() throws Exception
    {
        String tableName = "TEST_TABLE";
        Reader in = new FileReader(
                TestUtils.getFile("xml/transactionOperationTest.xml"));
        IDataSet xmlDataSet = new XmlDataSet(in);
        Exception[] exceptions = new Exception[]{
            new SQLException(),
            new DatabaseUnitException(),
            new RuntimeException(),
        };
        Connection jdbcConnection = _connection.getConnection();


        for (int i = 0; i < exceptions.length; i++)
        {

            // snapshot before operation
            ITable tableBefore = _connection.createDataSet().getTable(tableName);
            assertEquals("before row count", 6, tableBefore.getRowCount());
            assertEquals("autocommit before", true, jdbcConnection.getAutoCommit());

            MockDatabaseOperation mockOperation = new MockDatabaseOperation();
            mockOperation.setExpectedExecuteCalls(1);
            mockOperation.setupThrowExceptionOnExecute(exceptions[i]);

            try
            {
                DatabaseOperation operation = new CompositeOperation(
                        DatabaseOperation.DELETE_ALL,
                        mockOperation);
                operation = new TransactionOperation(operation);
                operation.execute(_connection, xmlDataSet);
                fail("Should throw an exception");
            }
            catch (Exception e)
            {
                mockOperation.verify();
            }

            // snapshot after operation
            ITable tableAfter = _connection.createDataSet().getTable(tableName);
            assertEquals("after row count", 6, tableAfter.getRowCount());
            assertEquals("autocommit after", true, jdbcConnection.getAutoCommit());

        }
    }
}




