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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Types;

/**
 * @author Manuel Laflamme
 * @version 1.0
 */

public class DoubleDataTypeTest extends AbstractDataTypeTest
{
    private final static DataType[] TYPES = {DataType.FLOAT, DataType.DOUBLE};

    public DoubleDataTypeTest(String name)
    {
        super(name);
    }

    public void testToString() throws Exception
    {
        String[] expected = {"FLOAT", "DOUBLE"};

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
            assertEquals("class", Double.class, TYPES[i].getTypeClass());
        }
    }

    public void testIsNumber() throws Exception
    {
        for (int i = 0; i < TYPES.length; i++)
        {
            assertEquals("is number", true, TYPES[i].isNumber());
        }
    }

    public void testTypeCast() throws Exception
    {
        Object[] values = {
            null,
            "5.555",
            new Float(Float.MAX_VALUE),
            new Double(Double.MIN_VALUE),
            "-7500",
            "2.34E23",
            new Double(0.666),
            new Double(5.49879),
            "-99.9",
            new BigDecimal(1234),
        };

        Double[] expected = {
            null,
            new Double(5.555),
            new Double(Float.MAX_VALUE),
            new Double(Double.MIN_VALUE),
            new Double(-7500),
            Double.valueOf("2.34E23"),
            new Double(0.666),
            new Double(5.49879),
            new Double(-99.9),
            new Double(1234),
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

    /**
     *
     */
    public void testInvalidTypeCast() throws Exception
    {
        Object[] values = {new Object(), "bla", new java.util.Date()};

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
        int[] sqlTypes = {Types.FLOAT, Types.DOUBLE};

        assertEquals("count", sqlTypes.length, TYPES.length);
        for (int i = 0; i < TYPES.length; i++)
        {
            assertEquals("forSqlType", TYPES[i], DataType.forSqlType(sqlTypes[i]));
            assertEquals("getSqlType", sqlTypes[i], TYPES[i].getSqlType());
        }
    }

    public void testForObject() throws Exception
    {
        assertEquals(DataType.DOUBLE, DataType.forObject(new Double(1234)));
    }


}

