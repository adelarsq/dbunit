/*
 * UpdateOperationTest.java   Feb 19, 2002
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

import org.dbunit.dataset.*;
import org.dbunit.dataset.xml.XmlDataSet;
import org.dbunit.AbstractDatabaseTest;

/**
 * @author Manuel Laflamme
 * @version 1.0
 */
public class RefreshOperationTest extends AbstractDatabaseTest
{
    public RefreshOperationTest(String s)
    {
        super(s);
    }

    public void testExecute() throws Exception
    {
        String tableName = "PK_TABLE";
        String[] columnNames = {"PK0", "PK1", "PK2", "NORMAL0", "NORMAL1"};
        int updatedRow = 1;
        int insertedRow = 3;

        InputStream in = new FileInputStream(
                new File("src/xml/refreshOperationTest.xml"));
        IDataSet xmlDataSet = new XmlDataSet(in);

        // verify table before
        ITable tableBefore = createOrderedTable(tableName, columnNames[0]);
        assertEquals("row count before", 3, tableBefore.getRowCount());

        DatabaseOperation.REFRESH.execute(_connection, xmlDataSet);

        // verify table after
        ITable tableAfter = createOrderedTable(tableName, columnNames[0]);
        assertEquals("row count after", 4, tableAfter.getRowCount());
        for (int i = 0; i < tableAfter.getRowCount(); i++)
        {
            // verify updated row
            if (i == updatedRow)
            {
                assertEquals("PK0", "1",
                        tableAfter.getValue(i, "PK0").toString());
                assertEquals("PK1", "11",
                        tableAfter.getValue(i, "PK1").toString());
                assertEquals("PK2", "111",
                        tableAfter.getValue(i, "PK2").toString());
                assertEquals("NORMAL0", "toto",
                        tableAfter.getValue(i, "NORMAL0").toString());
                assertEquals("NORMAL1", "qwerty",
                        tableAfter.getValue(i, "NORMAL1").toString());
            }
            // verify inserted row
            else if (i == insertedRow)
            {
                assertEquals("PK0", "3",
                        tableAfter.getValue(i, "PK0").toString());
                assertEquals("PK1", "33",
                        tableAfter.getValue(i, "PK1").toString());
                assertEquals("PK2", "333",
                        tableAfter.getValue(i, "PK2").toString());
                assertEquals("NORMAL0", "3333",
                        tableAfter.getValue(i, "NORMAL0").toString());
                assertEquals("NORMAL1", "33333",
                        tableAfter.getValue(i, "NORMAL1").toString());
            }
            // all other row must be equals than before
            else
            {
                for (int j = 0; j < columnNames.length; j++)
                {
                    String name = columnNames[j];
                    Object valueAfter = tableAfter.getValue(i, name);
                    Object valueBefore = tableBefore.getValue(i, name);
                    assertEquals("c=" + name + ",r=" + j, valueBefore, valueAfter);
                }
            }
        }
    }


}
