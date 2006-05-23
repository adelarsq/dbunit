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

package org.dbunit.ext.mssql;

import org.dbunit.AbstractDatabaseTest;
import org.dbunit.Assertion;
import org.dbunit.TestFeature;
import org.dbunit.dataset.*;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.XmlDataSet;

import java.io.FileReader;
import java.io.Reader;

/**
 * @author Manuel Laflamme
 * @author Eric Pugh
 * @version $Revision$
 * @since Feb 19, 2002
 */
public class InsertIdentityOperationTest extends AbstractDatabaseTest
{
    public InsertIdentityOperationTest(String s)
    {
        super(s);
    }
    
    protected boolean runTest(String testName) {
      return environmentHasFeature(TestFeature.INSERT_IDENTITY);
    }

    public void testExecuteXML() throws Exception
    {
        Reader in = new FileReader("src/xml/insertIdentityOperationTest.xml");
        IDataSet dataSet = new XmlDataSet(in);

        testExecute(dataSet);
    }

    public void testExecuteFlatXML() throws Exception
    {
        Reader in = new FileReader("src/xml/insertIdentityOperationTestFlat.xml");
        IDataSet dataSet = new FlatXmlDataSet(in);

        testExecute(dataSet);
    }

    public void testExecuteLowerCase() throws Exception
    {
        Reader in = new FileReader("src/xml/insertIdentityOperationTestFlat.xml");
        IDataSet dataSet = new LowerCaseDataSet(new FlatXmlDataSet(in));

        testExecute(dataSet);
    }

    public void testExecuteForwardOnly() throws Exception
    {
        Reader in = new FileReader("src/xml/insertIdentityOperationTestFlat.xml");
        IDataSet dataSet = new ForwardOnlyDataSet(new FlatXmlDataSet(in));

        testExecute(dataSet);
    }

    private void testExecute(IDataSet dataSet) throws Exception
    {
        ITable[] tablesBefore = DataSetUtils.getTables(_connection.createDataSet());
//        InsertIdentityOperation.CLEAN_INSERT.execute(_connection, dataSet);
        InsertIdentityOperation.INSERT.execute(_connection, dataSet);
        ITable[] tablesAfter = DataSetUtils.getTables(_connection.createDataSet());

        assertEquals("table count", tablesBefore.length, tablesAfter.length);

        // Verify tables after
        for (int i = 0; i < tablesAfter.length; i++)
        {
            ITable tableBefore = tablesBefore[i];
            ITable tableAfter = tablesAfter[i];

            String name = tableAfter.getTableMetaData().getTableName();
            if (name.startsWith("IDENTITY"))
            {
                assertEquals("row count before: " + name, 0, tableBefore.getRowCount());
                if (dataSet instanceof ForwardOnlyDataSet)
                {
                    assertTrue(name, tableAfter.getRowCount() > 0);
                }
                else
                {
                    Assertion.assertEquals(dataSet.getTable(name), tableAfter);
                }
            }
            else
            {
                // Other tables should have not been affected
                Assertion.assertEquals(tableBefore, tableAfter);
            }
        }
    }

    /* test case was added to validate the bug that tables with Identity columns that are not
    one of the primary keys are able to figure out if an IDENTITY_INSERT is needed.
    Thanks to Gaetano Di Gregorio for finding the bug.
    */
    public void testIdentityInsertNoPK() throws Exception
    {
        Reader in = new FileReader("src/xml/insertIdentityOperationTestNoPK.xml");
        IDataSet xmlDataSet = new FlatXmlDataSet(in);

        ITable[] tablesBefore = DataSetUtils.getTables(_connection.createDataSet());
        InsertIdentityOperation.CLEAN_INSERT.execute(_connection, xmlDataSet);
        ITable[] tablesAfter = DataSetUtils.getTables(_connection.createDataSet());

        // Verify tables after
        for (int i = 0; i < tablesAfter.length; i++)
        {
            ITable tableBefore = tablesBefore[i];
            ITable tableAfter = tablesAfter[i];

            String name = tableAfter.getTableMetaData().getTableName();
            if (name.equals("TEST_IDENTITY_NOT_PK"))
            {
                assertEquals("row count before: " + name, 0, tableBefore.getRowCount());
                Assertion.assertEquals(xmlDataSet.getTable(name), tableAfter);
            }
            else
            {
                // Other tables should have not been affected
                Assertion.assertEquals(tableBefore, tableAfter);
            }
        }
    }
}










