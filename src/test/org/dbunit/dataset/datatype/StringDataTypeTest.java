/*
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

package org.dbunit.dataset.datatype;

import java.sql.Types;

/**
 * @author Manuel Laflamme
 * @version 1.0
 */

public class StringDataTypeTest extends AbstractDataTypeTest
{
    private final static DataType[] TYPES = {
        DataType.CHAR,
        DataType.VARCHAR,
        DataType.LONGVARCHAR,
    };

    public StringDataTypeTest(String name)
    {
        super(name);
    }

    public void testToString() throws Exception
    {
        String[] expected = {"CHAR", "VARCHAR", "LONGVARCHAR"};

        assertEquals("type count", expected.length, TYPES.length);
        for (int i = 0; i < TYPES.length; i++)
        {
            assertEquals("name", expected[i], TYPES[i].toString());
        }
    }

    public void testGetTypeClass() throws Exception
    {
        for (int i = 0; i < TYPES.length; i++)
        {
            assertEquals("class", String.class, TYPES[i].getTypeClass());
        }
    }

    public void testIsNumber() throws Exception
    {
        for (int i = 0; i < TYPES.length; i++)
        {
            assertEquals("is number", false, TYPES[i].isNumber());
        }
    }

    /**
     *
     */
    public void testTypeCast() throws Exception
    {
        Object[] values = {
            null,
            "bla",
            new java.sql.Date(1234),
            new java.sql.Time(1234),
            new java.sql.Timestamp(1234),
            Boolean.TRUE,
            new Integer(1234),
            new Long(1234),
            new Double(12.34),
        };
        String[] expected = {
            null,
            "bla",
            new java.sql.Date(1234).toString(),
            new java.sql.Time(1234).toString(),
            new java.sql.Timestamp(1234).toString(),
            "true",
            "1234",
            "1234",
            "12.34",
        };

        assertEquals("actual vs expected count", values.length, expected.length);

        for (int i = 0; i < TYPES.length; i++)
        {
            for (int j = 0; j < values.length; j++)
            {
                assertEquals("typecast " + j, expected[j],
                        TYPES[i].typeCast(values[j]));
            }
        }
    }

    public void testInvalidTypeCast() throws Exception
    {
        Object[] values = {new Object()};

        for (int i = 0; i < TYPES.length; i++)
        {
            for (int j = 0; j < values.length; j++)
            {
                try
                {
                    TYPES[i].typeCast(values[j]);
                    fail("Should throw TypeCastException");
                }
                catch (TypeCastException e)
                {
                }
            }
        }
    }

    public void testSqlType() throws Exception
    {
        int[] sqlTypes = {Types.CHAR, Types.VARCHAR, Types.LONGVARCHAR};

        assertEquals("count", sqlTypes.length, TYPES.length);
        for (int i = 0; i < TYPES.length; i++)
        {
            assertEquals("forSqlType", TYPES[i], DataType.forSqlType(sqlTypes[i]));
            assertEquals("getSqlType", sqlTypes[i], TYPES[i].getSqlType());
        }
    }

    public void testForObject() throws Exception
    {
        assertEquals(DataType.VARCHAR, DataType.forObject(""));
    }

}

