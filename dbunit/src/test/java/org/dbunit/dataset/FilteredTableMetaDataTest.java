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

import junit.framework.TestCase;

import org.dbunit.dataset.datatype.DataType;
import org.dbunit.dataset.filter.IColumnFilter;
import org.dbunit.dataset.filter.DefaultColumnFilter;

/**
 * @author Manuel Laflamme
 * @version $Revision$
 * @since May 11, 2004
 */
public class FilteredTableMetaDataTest extends TestCase
{
    public FilteredTableMetaDataTest(String s)
    {
        super(s);
    }

    protected IColumnFilter createColumnFilter() throws Exception
    {
        DefaultColumnFilter filter = new DefaultColumnFilter();
        filter.excludeColumn("excluded*");
        return filter;
    }

    public void testGetTableName() throws Exception
    {
        String expected = "tableName";
        ITableMetaData metaData = new DefaultTableMetaData(expected, null, (Column[])null);
        metaData = new FilteredTableMetaData(metaData, createColumnFilter());

        assertEquals("table name", expected, metaData.getTableName());
    }

    public void testGetColumns() throws Exception
    {
        Column[] columns = new Column[]{
            new Column("numberColumn", DataType.NUMERIC),
            new Column("stringColumn", DataType.VARCHAR),
            new Column("booleanColumn", DataType.BOOLEAN),
            new Column("excludedColumn", DataType.BOOLEAN),
        };

        ITableMetaData metaData = new DefaultTableMetaData("toto", columns, (Column[])null);
        metaData = new FilteredTableMetaData(metaData, createColumnFilter());

        assertEquals("column count", 3, metaData.getColumns().length);
        for (int i = 0; i < 3; i++)
        {
            Column column = columns[i];
            assertEquals("columns" + i, column, metaData.getColumns()[i]);
        }
        assertEquals("key count", 0, metaData.getPrimaryKeys().length);
    }

    public void testGetPrimaryKeys() throws Exception
    {
        Column[] columns = new Column[]{
            new Column("numberColumn", DataType.NUMERIC),
            new Column("stringColumn", DataType.VARCHAR),
            new Column("booleanColumn", DataType.BOOLEAN),
            new Column("excludedColumn", DataType.BOOLEAN),
        };
        String[] keyNames = new String[]{"booleanColumn", "numberColumn", "excludedColumn"};


        ITableMetaData metaData = new DefaultTableMetaData("toto", columns, keyNames);
        metaData = new FilteredTableMetaData(metaData, createColumnFilter());

        Column[] keys = metaData.getPrimaryKeys();
        assertEquals("key count", 2, keys.length);
        for (int i = 0; i < 2; i++)
        {
            assertEquals("key name", keyNames[i], keys[i].getColumnName());
        }
    }
}





