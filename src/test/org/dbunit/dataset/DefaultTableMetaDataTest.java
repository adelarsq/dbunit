/*
 * DefaultTableMetaDataTest.java   Feb 17, 2002
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

import org.dbunit.dataset.datatype.DataType;
import junit.framework.TestCase;

/**
 * @author Manuel Laflamme
 * @version 1.0
 */
public class DefaultTableMetaDataTest extends TestCase
{
    public DefaultTableMetaDataTest(String s)
    {
        super(s);
    }

    public void testGetTableName() throws Exception
    {
        String expected = "tableName";
        ITableMetaData metaData = new DefaultTableMetaData(
                expected, null);

        assertEquals("table name", expected, metaData.getTableName());
    }

    public void testGetColumns() throws Exception
    {
        Column[] columns = new Column[]{
            new Column("numberColumn", DataType.NUMBER),
            new Column("stringColumn", DataType.STRING),
            new Column("booleanColumn", DataType.BOOLEAN),
        };

        ITableMetaData metaData = new DefaultTableMetaData(
                "toto", columns);

        assertEquals("column count", columns.length, metaData.getColumns().length);
        for (int i = 0; i < columns.length; i++)
        {
            Column column = columns[i];
            assertEquals("columns" + i, column, metaData.getColumns()[i]);
        }
        assertEquals("key count", 0, metaData.getPrimaryKeys().length);
    }

    public void testGetPrimaryKeys() throws Exception
    {
        Column[] columns = new Column[]{
            new Column("numberColumn", DataType.NUMBER),
            new Column("stringColumn", DataType.STRING),
            new Column("booleanColumn", DataType.BOOLEAN),
        };
        String[] keyNames = new String[]{"booleanColumn", "numberColumn"};


        ITableMetaData metaData = new DefaultTableMetaData(
                "toto", columns, keyNames);

        Column[] keys = metaData.getPrimaryKeys();
        assertEquals("key count", keyNames.length, keys.length);
        for (int i = 0; i < keys.length; i++)
        {
            assertEquals("key name", keyNames[i], keys[i].getColumnName());
        }
    }

    public void testGetPrimaryKeysColumnDontMatch() throws Exception
    {
        Column[] columns = new Column[]{
            new Column("numberColumn", DataType.NUMBER),
            new Column("stringColumn", DataType.STRING),
            new Column("booleanColumn", DataType.BOOLEAN),
        };
        String[] keyNames = new String[]{"invalidColumn"};


        ITableMetaData metaData = new DefaultTableMetaData(
                "toto", columns, keyNames);

        Column[] keys = metaData.getPrimaryKeys();
        assertEquals("key count", 0, keys.length);
    }

}
