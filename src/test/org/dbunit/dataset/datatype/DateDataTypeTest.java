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

import java.sql.*;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @author Manuel Laflamme
 * @version 1.0
 */

public class DateDataTypeTest extends AbstractDataTypeTest
{
    private final static DataType THIS_TYPE = DataType.DATE;

    public DateDataTypeTest(String name)
    {
        super(name);
    }

    /**
     *
     */
    public void testToString() throws Exception
    {
        assertEquals("name", "DATE", THIS_TYPE.toString());
    }

    /**
     *
     */
    public void testGetTypeClass() throws Exception
    {
        assertEquals("class", java.sql.Date.class, THIS_TYPE.getTypeClass());
    }

    /**
     *
     */
    public void testIsNumber() throws Exception
    {
        assertEquals("is number", false, THIS_TYPE.isNumber());
    }

    public void testTypeCast() throws Exception
    {
        Object[] values = {
            null,
            new java.sql.Date(1234),
            new Time(1234),
            new Timestamp(1234),
            new java.sql.Date(1234).toString(),
            new java.util.Date(1234),
        };

        java.sql.Date[] expected = {
            null,
            new java.sql.Date(1234),
            new java.sql.Date(new Time(1234).getTime()),
            new java.sql.Date(new Timestamp(1234).getTime()),
            java.sql.Date.valueOf(new java.sql.Date(1234).toString()),
            new java.sql.Date(1234),
        };

        assertEquals("actual vs expected count", values.length, expected.length);

        for (int i = 0; i < values.length; i++)
        {
            assertEquals("typecast " + i, expected[i],
                    THIS_TYPE.typeCast(values[i]));
        }
    }

    public void testInvalidTypeCast() throws Exception
    {
        Object[] values = {
            new Integer(1234),
            new Object(),
            "bla",
            "2000.05.05",
        };

        for (int i = 0; i < values.length; i++)
        {
            try
            {
                THIS_TYPE.typeCast(values[i]);
                fail("Should throw TypeCastException - " + i);
            }
            catch (TypeCastException e)
            {
            }
        }
    }

    public void testSqlType() throws Exception
    {
        assertEquals(THIS_TYPE, DataType.forSqlType(Types.DATE));
        assertEquals(Types.DATE, THIS_TYPE.getSqlType());
    }

    /**
     *
     */
    public void testForObject() throws Exception
    {
        assertEquals(THIS_TYPE, DataType.forObject(new java.sql.Date(1234)));
    }

    public void testAsString() throws Exception
    {
        java.sql.Date[] values = {
            new java.sql.Date(1234),
        };

        String[] expected = {
            new java.sql.Date(1234).toString(),
        };


        assertEquals("actual vs expected count", values.length, expected.length);

        for (int i = 0; i < values.length; i++)
        {
            assertEquals("asString " + i, expected[i], DataType.asString(values[i]));
        }
    }

}


