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

package org.dbunit.dataset.datatype;

import org.dbunit.database.ExtendedMockSingleRowResultSet;
import org.dbunit.dataset.ITable;

import java.math.BigDecimal;
import java.sql.Types;

/**
 * @author Manuel Laflamme
 * @version $Revision$
 */

public class FloatDataTypeTest extends AbstractDataTypeTest
{
    private final static DataType THIS_TYPE = DataType.REAL;

    public FloatDataTypeTest(String name)
    {
        super(name);
    }

    public void testToString() throws Exception
    {
        assertEquals("name", "REAL", THIS_TYPE.toString());
    }

    public void testGetTypeClass() throws Exception
    {
        assertEquals("class", Float.class, THIS_TYPE.getTypeClass());
    }

    public void testIsNumber() throws Exception
    {
        assertEquals("is number", true, THIS_TYPE.isNumber());
    }

    public void testIsDateTime() throws Exception
    {
        assertEquals("is date/time", false, THIS_TYPE.isDateTime());
    }

    public void testTypeCast() throws Exception
    {
        Object[] values = {
            null,
            "5.555",
            new Double(Float.MAX_VALUE),
            new Double(Float.MIN_VALUE),
            "-7500",
            "2.34E3",
            new Double(0.666),
            new Double(5.49879),
            "-99.9",
            new BigDecimal((double)1234),
        };

        Float[] expected = {
            null,
            new Float(5.555),
            new Float(Float.MAX_VALUE),
            new Float(Float.MIN_VALUE),
            new Float(-7500),
            Float.valueOf("2.34E3"),
            new Float(0.666),
            new Float(5.49879),
            new Float(-99.9),
            new Float(1234),
        };

        assertEquals("actual vs expected count", values.length, expected.length);

        for (int i = 0; i < values.length; i++)
        {
            assertEquals("typecast " + i, expected[i],
                    THIS_TYPE.typeCast(values[i]));
        }
    }

    public void testTypeCastNone() throws Exception
    {
        assertEquals("typecast", null, THIS_TYPE.typeCast(ITable.NO_VALUE));
    }

    public void testTypeCastInvalid() throws Exception
    {
        Object[] values = {new Object(), "bla", new java.util.Date()};

        for (int i = 0; i < values.length; i++)
        {
            try
            {
                THIS_TYPE.typeCast(values[i]);
                fail("Should throw TypeCastException");
            }
            catch (TypeCastException e)
            {
            }
        }
    }

    public void testCompareEquals() throws Exception
    {
        Object[] values1 = {
            null,
            "5.555",
            new Double(Float.MAX_VALUE),
            new Double(Float.MIN_VALUE),
            "-7500",
            "2.34E3",
            new Double(0.666),
            new Double(5.49879),
            "-99.9",
            new BigDecimal((double)1234),
        };

        Float[] values2 = {
            null,
            new Float(5.555),
            new Float(Float.MAX_VALUE),
            new Float(Float.MIN_VALUE),
            new Float(-7500),
            Float.valueOf("2.34E3"),
            new Float(0.666),
            new Float(5.49879),
            new Float(-99.9),
            new Float(1234),
        };

        assertEquals("values count", values1.length, values2.length);

        for (int i = 0; i < values1.length; i++)
        {
            assertEquals("compare1 " + i, 0, THIS_TYPE.compare(values1[i], values2[i]));
            assertEquals("compare2 " + i, 0, THIS_TYPE.compare(values2[i], values1[i]));
        }
    }

    public void testCompareInvalid() throws Exception
    {
        Object[] values1 = {
            new Object(),
            "bla",
            new java.util.Date()
        };
        Object[] values2 = {
            null,
            null,
            null
        };

        assertEquals("values count", values1.length, values2.length);

        for (int i = 0; i < values1.length; i++)
        {
            try
            {
                THIS_TYPE.compare(values1[i], values2[i]);
                fail("Should throw TypeCastException");
            }
            catch (TypeCastException e)
            {
            }

            try
            {
                THIS_TYPE.compare(values2[i], values1[i]);
                fail("Should throw TypeCastException");
            }
            catch (TypeCastException e)
            {
            }
        }
    }

    public void testCompareDifferent() throws Exception
    {
        Object[] less = {
            null,
            "-7500",
            new Double(Float.MIN_VALUE),
        };

        Object[] greater = {
            "0",
            "5.555",
            new Float(Float.MAX_VALUE),
        };

        assertEquals("values count", less.length, greater.length);

        for (int i = 0; i < less.length; i++)
        {
            assertTrue("less " + i, THIS_TYPE.compare(less[i], greater[i]) < 0);
            assertTrue("greater " + i, THIS_TYPE.compare(greater[i], less[i]) > 0);
        }
    }

    public void testSqlType() throws Exception
    {
        assertEquals(THIS_TYPE, DataType.forSqlType(Types.REAL));
        assertEquals("forSqlTypeName", THIS_TYPE, DataType.forSqlTypeName(THIS_TYPE.toString()));
        assertEquals(Types.REAL, THIS_TYPE.getSqlType());
    }

    public void testForObject() throws Exception
    {
        assertEquals(THIS_TYPE, DataType.forObject(new Float(1234)));
    }

    public void testAsString() throws Exception
    {
        Object[] values = {
            new Float("1234"),
            new Float("12.34"),
        };

        String[] expected = {
            "1234.0",
            "12.34",
        };

        assertEquals("actual vs expected count", values.length, expected.length);

        for (int i = 0; i < values.length; i++)
        {
            assertEquals("asString " + i, expected[i], DataType.asString(values[i]));
        }
    }

    public void testGetSqlValue() throws Exception
    {
        Float[] expected = {
            null,
            new Float(5.555),
            new Float(Float.MAX_VALUE),
            new Float(Float.MIN_VALUE),
            new Float(-7500),
            Float.valueOf("2.34E3"),
            new Float(0.666),
            new Float(5.49879),
            new Float(-99.9),
            new Float(1234),
        };

        ExtendedMockSingleRowResultSet resultSet = new ExtendedMockSingleRowResultSet();
        resultSet.addExpectedIndexedValues(expected);

        for (int i = 0; i < expected.length; i++)
        {
            Object expectedValue = expected[i];
            Object actualValue = THIS_TYPE.getSqlValue(i + 1, resultSet);
            assertEquals("value", expectedValue, actualValue);
        }
    }
}
