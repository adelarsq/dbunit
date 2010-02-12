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

import java.sql.DatabaseMetaData;

/**
 * @author Manuel Laflamme
 * @version $Revision$
 * @since Feb 17, 2002
 */
public class ColumnTest extends TestCase
{
    public ColumnTest(String s)
    {
        super(s);
    }

    public void testGetColumnName() throws Exception
    {
        String expected = "columnName";
        Column column = new Column(expected, DataType.REAL);

        assertEquals("column name", expected, column.getColumnName());
    }

    public void testGetDataType() throws Exception
    {
        DataType expected = DataType.DATE;
        Column column = new Column(expected.toString(), expected);

        assertEquals("data type", expected, column.getDataType());
    }

    public void testNullableValue() throws Exception
    {
        assertEquals("nullable", Column.NULLABLE,
                Column.nullableValue(DatabaseMetaData.columnNullable));

        assertEquals("not nullable", Column.NO_NULLS,
                Column.nullableValue(DatabaseMetaData.columnNoNulls));

        assertEquals("nullable unknown", Column.NULLABLE_UNKNOWN,
                Column.nullableValue(DatabaseMetaData.columnNullableUnknown));


        try
        {
            Column.nullableValue(12345);
            fail("Should throw an IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
        }

    }

}





