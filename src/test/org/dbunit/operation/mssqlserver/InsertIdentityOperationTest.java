/*
 * InsertIdentityOperationTest.java   Feb 19, 2002
 *
 * DbUnit Database Testing Framework
 * Copyright (C)2002, Manuel Laflamme
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

package org.dbunit.operation.mssqlserver;

import org.dbunit.operation.*;
import org.dbunit.*;
import org.dbunit.database.MockDatabaseConnection;
import org.dbunit.database.statement.MockBatchStatement;
import org.dbunit.database.statement.MockStatementFactory;
import org.dbunit.dataset.*;
import org.dbunit.dataset.datatype.DataType;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.XmlDataSet;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Manuel Laflamme
 * @version $Revision$
 */
public class InsertIdentityOperationTest extends AbstractDatabaseTest
{
    public InsertIdentityOperationTest(String s)
    {
        super(s);
    }

//    public static Test suite()
//    {
//        return new InsertIdentityOperationTest("testInsertBlob");
//    }



    public void testExecute() throws Exception
    {
        if (DatabaseEnvironment.getInstance() instanceof MSSQLServerEnvironment){
            InputStream in = new FileInputStream("src/xml/insertIdentityOperationTest.xml");
            IDataSet xmlDataSet = new XmlDataSet(in);

            ITable[] tablesBefore = DataSetUtils.getTables(_connection.createDataSet());
            InsertIdentityOperation.CLEAN_INSERT.execute(_connection, xmlDataSet);
            ITable[] tablesAfter = DataSetUtils.getTables(_connection.createDataSet());

            assertEquals("table count", tablesBefore.length, tablesAfter.length);
            for (int i = 0; i < tablesBefore.length; i++)
            {
                ITable table = tablesBefore[i];
                String name = table.getTableMetaData().getTableName();


                if (name.startsWith("IDENTITY"))
                {

                    assertTrue("Should have either 0 or 6", table.getRowCount()==0 | table.getRowCount()==6);
                }
            }

            for (int i = 0; i < tablesAfter.length; i++)
            {
                ITable table = tablesAfter[i];
                String name = table.getTableMetaData().getTableName();
                if (name.startsWith("IDENTITY"))
                {
                    Assertion.assertEquals(xmlDataSet.getTable(name), table);
                }
            }
        }

    }
}










