/*
 * CloseConnectionOperation.java   Mar 6, 2002
 *
 * The dbUnit database testing framework.
 * Copyright (C) 2002   Manuel Laflamme
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

import java.io.*;
import java.sql.SQLException;

import org.dbunit.AbstractDatabaseTest;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.xml.XmlDataSet;

/**
 * @author Manuel Laflamme
 * @version 1.0
 */
public class CloseConnectionOperationTest extends AbstractDatabaseTest
{
    public CloseConnectionOperationTest(String s)
    {
        super(s);
    }

    public void testExecute() throws Exception
    {
        String tableName = "TEST_TABLE";
        InputStream in = new FileInputStream(
                new File("src/xml/closeConnectionOperationTest.xml"));
        IDataSet xmlDataSet = new XmlDataSet(in);

        // verify table before
        IDatabaseConnection connection = getConnection();
        ITable tableBefore = connection.createDataSet().getTable(tableName);
        assertEquals("row count before", 6, tableBefore.getRowCount());

        DatabaseOperation operation = new CloseConnectionOperation(
                DatabaseOperation.DELETE_ALL);
        operation.execute(_connection, xmlDataSet);

        ITable tableAfter = connection.createDataSet().getTable(tableName);
        assertEquals("row count after", 0, tableAfter.getRowCount());

        try
        {
            connection.createBatchStatement();
            fail("Should throw an SQLException");
        }
        catch (SQLException e)
        {
        }
    }

}

