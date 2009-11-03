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

    public void testIsDateTime() throws Exception
    {
        for (int i = 0; i < TYPES.length; i++)
        {
            assertEquals("is date/time", false, TYPES[i].isDateTime());
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
            new BigDecimal((double)1234),
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


    public void testTypeCastNone() throws Exception
    {
        for (int i = 0; i < TYPES.length; i++)
        {
            DataType type = TYPES[i];
            assertEquals("typecast " + type, null, type.typeCast(ITable.NO_VALUE));
        }
    }

    public void testTypeCastInvalid() throws Exception
    {
        Object[] values = {
            new Object(),
            "bla",
            new java.util.Date()};

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

    public void testCompareEquals() throws Exception
    {
        Object[] values1 = {
            null,
            "5.555",
            new Float(Float.MAX_VALUE),
            new Double(Double.MIN_VALUE),
            "-7500",
            "2.34E23",
            new Double(0.666),
            new Double(5.49879),
            "-99.9",
            new BigDecimal((double)1234),
            "123",
        };

        Object[] values2 = {
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
            new Double(123.0)
        };

        assertEquals("values count", values1.length, values2.length);

        for (int i = 0; i < TYPES.length; i++)
        {
            for (int j = 0; j < values1.length; j++)
            {
                assertEquals("compare1 " + j, 0, TYPES[i].compare(values1[j], values2[j]));
                assertEquals("compare2 " + j, 0, TYPES[i].compare(values2[j], values1[j]));
            }
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

        for (int i = 0; i < TYPES.length; i++)
        {
            for (int j = 0; j < values1.length; j++)
            {
                try
                {
                    TYPES[i].compare(values1[j], values2[j]);
                    fail("Should throw TypeCastException");
                }
                catch (TypeCastException e)
                {
                }

                try
                {
                    TYPES[i].compare(values2[j], values1[j]);
                    fail("Should throw TypeCastException");
                }
                catch (TypeCastException e)
                {
                }
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

        for (int i = 0; i < TYPES.length; i++)
        {
            for (int j = 0; j < less.length; j++)
            {
                assertTrue("less " + j, TYPES[i].compare(less[j], greater[j]) < 0);
                assertTrue("greater " + j, TYPES[i].compare(greater[j], less[j]) > 0);
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
            assertEquals("forSqlTypeName", TYPES[i], DataType.forSqlTypeName(TYPES[i].toString()));
            assertEquals("getSqlType", sqlTypes[i], TYPES[i].getSqlType());
        }
    }

    public void testForObject() throws Exception
    {
        assertEquals(DataType.DOUBLE, DataType.forObject(new Double(1234)));
    }

    public void testAsString() throws Exception
    {
        Object[] values = {
            new Double("1234"),
            new Double("12.34"),
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

        ExtendedMockSingleRowResultSet resultSet = new ExtendedMockSingleRowResultSet();
        resultSet.addExpectedIndexedValues(expected);

        for (int i = 0; i < expected.length; i++)
        {
            Object expectedValue = expected[i];

            for (int j = 0; j < TYPES.length; j++)
            {
                DataType dataType = TYPES[j];
                Object actualValue = dataType.getSqlValue(i + 1, resultSet);
                assertEquals("value " + j, expectedValue, actualValue);
            }
        }
    }

}
