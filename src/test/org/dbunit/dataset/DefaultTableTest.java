/*
 * DefaultTableTest.java   Feb 17, 2002
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

package org.dbunit.dataset;

import java.util.ArrayList;
import java.util.List;

import org.dbunit.dataset.datatype.DataType;

/**
 * @author Manuel Laflamme
 * @version 1.0
 */
public class DefaultTableTest extends AbstractTableTest
{
    public DefaultTableTest(String s)
    {
        super(s);
    }

    protected ITable createTable() throws Exception
    {
        return createTable(COLUMN_COUNT, ROW_COUNT, 0);
    }

    protected ITable createTable(int columnCount, int rowCount, int startRow)
            throws Exception
    {
        List list = new ArrayList();
        for (int i = 0; i < rowCount; i++)
        {
            Object[] rowValues = new Object[columnCount];
            for (int j = 0; j < rowValues.length; j++)
            {
                rowValues[j] = "row " + (i + startRow) + " col " + j;
            }

            list.add(rowValues);
        }

        return new DefaultTable(createTableMetaData(columnCount), list);
    }

    protected ITableMetaData createTableMetaData(int columnCount) throws Exception
    {
        Column[] columns = new Column[columnCount];
        for (int i = 0; i < columns.length; i++)
        {
            columns[i] = new Column("COLUMN" + i, DataType.OBJECT);
        }

        return new DefaultTableMetaData("myTable", columns);
    }

    public void testGetMissingValue() throws Exception
    {
        String columnName = "COLUMN0";
        Object expected = ITable.NO_VALUE;

        List list = new ArrayList();
        list.add(new Object[]{
            ITable.NO_VALUE, ITable.NO_VALUE, ITable.NO_VALUE,
            ITable.NO_VALUE, ITable.NO_VALUE, ITable.NO_VALUE});

        ITable table = new DefaultTable(createTableMetaData(COLUMN_COUNT), list);
        Column[] columns = table.getTableMetaData().getColumns();
        assertNotNull(DataSetUtils.getColumn(columnName, columns));
        assertEquals("no value", expected, table.getValue(0, columnName));
    }

}
