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

package org.dbunit.dataset;

import org.dbunit.dataset.xml.FlatXmlDataSet;

import java.io.File;

/**
 * @author Manuel Laflamme
 * @version $Revision$
 */
public class SortedTableTest extends AbstractTableTest
{
    public SortedTableTest(String s)
    {
        super(s);
    }

    protected ITable createTable() throws Exception
    {
        return createDataSet().getTable("TEST_TABLE");
    }

    protected IDataSet createDataSet() throws Exception
    {
        return new SortedDataSet(new FlatXmlDataSet(
                new File("src/xml/sortedTableTest.xml")));
    }

    public void testGetMissingValue() throws Exception
    {
        String columnName = "COLUMN2";
        Object[] expected = {null, null, null, "0", "1"};

        ITable table = createDataSet().getTable("MISSING_VALUES");

        Column[] columns = table.getTableMetaData().getColumns();
        assertEquals("column count", 3, columns.length);
        assertEquals("row count", 5, table.getRowCount());
        for (int i = 0; i < table.getRowCount(); i++)
        {
            assertEquals("value row " + i, expected[i],
                    table.getValue(i, columnName));
        }
    }


}









