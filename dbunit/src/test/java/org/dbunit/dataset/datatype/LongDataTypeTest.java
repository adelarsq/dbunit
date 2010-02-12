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

public class LongDataTypeTest extends AbstractDataTypeTest
{
    private final static DataType THIS_TYPE = DataType.BIGINT_AUX_LONG;

    public LongDataTypeTest(String name)
    {
        super(name);
    }

    /**
     *
     */
    public void testToString() throws Exception
    {
        assertEquals("name", "BIGINT", THIS_TYPE.toString());
    }

    /**
     *
     */
    public void testGetTypeClass() throws Exception
    {
        assertEquals("class", Long.class, THIS_TYPE.getTypeClass());
    }

    /**
     *
     */
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
            "5",
            new Long(1234),
            new Float(Long.MAX_VALUE),
            new Float(Long.MIN_VALUE),
            "-7500",
            new Double(Long.MAX_VALUE),
            new Double(Long.MIN_VALUE),
            new Float(0.666),
            new Double(0.666),
            new Double(5.49),
            "-99.9",
            new Double(1.5E6),
            new BigDecimal((double)1234),
        };

        Long[] expected = {
            null,
            new Long(5),
            new Long(1234),
            new Long(Long.MAX_VALUE),
            new Long(Long.MIN_VALUE),
            new Long(-7500),
            new Long(Long.MAX_VALUE),
            new Long(Long.MIN_VALUE),
            new Long(0),
            new Long(0),
            new Long(5),
            new Long(-99),
            new Long(1500000),
            new Long(1234),
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
            "5",
            new Long(1234),
            new Float(Long.MAX_VALUE),
            new Float(Long.MIN_VALUE),
            "-7500",
            new Double(Long.MAX_VALUE),
            new Double(Long.MIN_VALUE),
            new Float(0.666),
            new Double(0.666),
            new Double(5.49),
            "-99.9",
            new Double(1.5E6),
            new BigDecimal((double)1234),
        };

        Object[] values2 = {
            null,
            new Long(5),
            new Long(1234),
            new Long(Long.MAX_VALUE),
            new Long(Long.MIN_VALUE),
            new Long(-7500),
            new Long(Long.MAX_VALUE),
            new Long(Long.MIN_VALUE),
            new Long(0),
            new Long(0),
            new Long(5),
            new Long(-99),
            new Long(1500000),
            new Long(1234),
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
            null,
            "-7500",
        };

        Object[] greater = {
            "0",
            new Long(-5),
            new Long(5),
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
        // This test was commented out in release 2.4.6 because the LongDataType is not used anymore
        // by default for the SQL type BIGINT. This is due to a bug with values that have more than 19 digits
        // where a BigInteger is now favored.
//        assertEquals(THIS_TYPE, DataType.forSqlType(Types.BIGINT));
//        assertEquals("forSqlTypeName", THIS_TYPE, DataType.forSqlTypeName(THIS_TYPE.toString()));
//        assertEquals(Types.BIGINT, THIS_TYPE.getSqlType());
    }

    public void testForObject() throws Exception
    {
        DataType actual = DataType.forObject(new Long(1234));
        assertEquals(THIS_TYPE, actual);
    }

    public void testAsString() throws Exception
    {
        Long[] values = {
            new Long(1234),
        };

        String[] expected = {
            "1234",
        };


        assertEquals("actual vs expected count", values.length, expected.length);

        for (int i = 0; i < values.length; i++)
        {
            assertEquals("asString " + i, expected[i], DataType.asString(values[i]));
        }
    }

    public void testGetSqlValue() throws Exception
    {
        Long[] expected = {
            null,
            new Long(5),
            new Long(1234),
            new Long(Long.MAX_VALUE),
            new Long(Long.MIN_VALUE),
            new Long(-7500),
            new Long(0),
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
