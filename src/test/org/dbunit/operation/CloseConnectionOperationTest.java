/*
 * CloseConnectionOperation.java   Mar 6, 2002
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

package org.dbunit.operation;

import java.io.*;
import java.sql.SQLException;

import org.dbunit.AbstractDatabaseTest;
import org.dbunit.database.*;
import org.dbunit.database.statement.BatchStatement;
import org.dbunit.database.statement.StatementFactory;
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

    public void testMockExecute() throws Exception
    {
        // setup mock objects
        MockDatabaseOperation operation = new MockDatabaseOperation();
        operation.setExpectedExecuteCalls(1);

        MockDatabaseConnection connection = new MockDatabaseConnection();
        connection.setExpectedCloseCalls(1);

        // execute operation
        new CloseConnectionOperation(operation).execute(connection, null);

        // verify
        operation.verify();
        connection.verify();
    }

}



