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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.PreparedStatement;
import java.sql.Types;

import org.dbunit.database.ExtendedMockSingleRowResultSet;
import org.dbunit.database.statement.MockPreparedStatement;
import org.dbunit.dataset.ITable;
import org.junit.Test;

/**
 * @author Manuel Laflamme
 * @version $Revision$
 */

public class BigIntegerDataTypeTest extends AbstractDataTypeTest
{
    private static final String NUMBER_LARGER_THAN_LONG =
            "17446744073709551630";
    private final static DataType THIS_TYPE = DataType.BIGINT;

    public BigIntegerDataTypeTest(String name)
    {
        super(name);
    }

    /**
     *
     */
    @Override
    public void testToString() throws Exception
    {
        assertEquals("name", "BIGINT", THIS_TYPE.toString());
    }

    /**
     *
     */
    @Override
    public void testGetTypeClass() throws Exception
    {
        assertEquals("class", BigInteger.class, THIS_TYPE.getTypeClass());
    }

    /**
     *
     */
    @Override
    public void testIsNumber() throws Exception
    {
        assertEquals("is number", true, THIS_TYPE.isNumber());
    }

    @Override
    public void testIsDateTime() throws Exception
    {
        assertEquals("is date/time", false, THIS_TYPE.isDateTime());
    }

    @Override
    public void testTypeCast() throws Exception
    {
        Object[] values =
                {null, "5", new Long(1234), new Float(Long.MAX_VALUE),
                        new Float(Long.MIN_VALUE), "-7500",
                        new Double(Long.MAX_VALUE), new Double(Long.MIN_VALUE),
                        new Float(0.666), new Double(0.666), new Double(5.49),
                        "-99.9", new Double(1.5E6),
                        new BigDecimal((double) 1234), NUMBER_LARGER_THAN_LONG,
                        new BigDecimal(NUMBER_LARGER_THAN_LONG),};

        BigInteger[] expected =
                {null, new BigInteger("5"), new BigInteger("1234"),
                        new BigInteger("" + Long.MAX_VALUE),
                        new BigInteger("" + Long.MIN_VALUE),
                        new BigInteger("-7500"),
                        new BigInteger("" + Long.MAX_VALUE),
                        new BigInteger("" + Long.MIN_VALUE),
                        new BigInteger("0"), new BigInteger("0"),
                        new BigInteger("5"), new BigInteger("-99"),
                        new BigInteger("1500000"), new BigInteger("1234"),
                        new BigInteger(NUMBER_LARGER_THAN_LONG),
                        new BigInteger(NUMBER_LARGER_THAN_LONG),};

        assertEquals("actual vs expected count", values.length, expected.length);

        for (int i = 0; i < values.length; i++)
        {
            assertEquals("typecast " + i, expected[i],
                    THIS_TYPE.typeCast(values[i]));
        }
    }

    @Override
    public void testTypeCastNone() throws Exception
    {
        assertEquals("typecast", null, THIS_TYPE.typeCast(ITable.NO_VALUE));
    }

    @Override
    public void testTypeCastInvalid() throws Exception
    {
        Object[] values = {new Object(), "bla", new java.util.Date()};

        for (Object value : values)
        {
            try
            {
                THIS_TYPE.typeCast(value);
                fail("Should throw TypeCastException");
            } catch (TypeCastException e)
            {
            }
        }
    }

    @Override
    public void testCompareEquals() throws Exception
    {
        Object[] values1 =
                {null, "5", new Long(1234), new Float(Long.MAX_VALUE),
                        new Float(Long.MIN_VALUE), "-7500",
                        new Double(Long.MAX_VALUE), new Double(Long.MIN_VALUE),
                        new Float(0.666), new Double(0.666), new Double(5.49),
                        "-99.9", new Double(1.5E6),
                        new BigDecimal((double) 1234),};

        Object[] values2 =
                {null, new Long(5), new Long(1234), new Long(Long.MAX_VALUE),
                        new Long(Long.MIN_VALUE), new Long(-7500),
                        new Long(Long.MAX_VALUE), new Long(Long.MIN_VALUE),
                        new Long(0), new Long(0), new Long(5), new Long(-99),
                        new Long(1500000), new Long(1234),};

        assertEquals("values count", values1.length, values2.length);

        for (int i = 0; i < values1.length; i++)
        {
            assertEquals("compare1 " + i, 0,
                    THIS_TYPE.compare(values1[i], values2[i]));
            assertEquals("compare2 " + i, 0,
                    THIS_TYPE.compare(values2[i], values1[i]));
        }
    }

    @Override
    public void testCompareInvalid() throws Exception
    {
        Object[] values1 = {new Object(), "bla", new java.util.Date()};
        Object[] values2 = {null, null, null};

        assertEquals("values count", values1.length, values2.length);

        for (int i = 0; i < values1.length; i++)
        {
            try
            {
                THIS_TYPE.compare(values1[i], values2[i]);
                fail("Should throw TypeCastException");
            } catch (TypeCastException e)
            {
            }

            try
            {
                THIS_TYPE.compare(values2[i], values1[i]);
                fail("Should throw TypeCastException");
            } catch (TypeCastException e)
            {
            }
        }
    }

    @Override
    public void testCompareDifferent() throws Exception
    {
        Object[] less = {null, null, "-7500",};

        Object[] greater = {"0", new Long(-5), new Long(5),};

        assertEquals("values count", less.length, greater.length);

        for (int i = 0; i < less.length; i++)
        {
            assertTrue("less " + i, THIS_TYPE.compare(less[i], greater[i]) < 0);
            assertTrue("greater " + i,
                    THIS_TYPE.compare(greater[i], less[i]) > 0);
        }
    }

    @Override
    public void testSqlType() throws Exception
    {
        assertEquals(THIS_TYPE, DataType.forSqlType(Types.BIGINT));
        assertEquals("forSqlTypeName", THIS_TYPE,
                DataType.forSqlTypeName(THIS_TYPE.toString()));
        assertEquals(Types.BIGINT, THIS_TYPE.getSqlType());
    }

    @Override
    public void testForObject() throws Exception
    {
        assertEquals(THIS_TYPE, DataType.forObject(new BigInteger("1234")));
    }

    @Override
    public void testAsString() throws Exception
    {
        Long[] values = {new Long(1234),};

        String[] expected = {"1234",};

        assertEquals("actual vs expected count", values.length, expected.length);

        for (int i = 0; i < values.length; i++)
        {
            assertEquals("asString " + i, expected[i],
                    DataType.asString(values[i]));
        }
    }

    @Override
    public void testGetSqlValue() throws Exception
    {
        BigInteger[] expected =
                {null, new BigInteger("5"), new BigInteger("1234"),
                        new BigInteger("" + Long.MAX_VALUE),
                        new BigInteger("" + Long.MIN_VALUE),
                        new BigInteger("-7500"), new BigInteger("0"),};

        // Internally BigIntegerDataType uses resultSet.getBigDecimal() on the
        // JDBC API because there is no resultSet.getBigInteger().
        BigDecimal[] expectedForMock = new BigDecimal[expected.length];
        for (int i = 0; i < expectedForMock.length; i++)
        {
            if (expected[i] == null)
            {
                expectedForMock[i] = null;
            } else
            {
                expectedForMock[i] = new BigDecimal(expected[i]);
            }
        }

        ExtendedMockSingleRowResultSet resultSet =
                new ExtendedMockSingleRowResultSet();
        resultSet.addExpectedIndexedValues(expectedForMock);

        for (int i = 0; i < expected.length; i++)
        {
            Object expectedValue = expected[i];
            Object actualValue = THIS_TYPE.getSqlValue(i + 1, resultSet);
            if (expectedValue != null && actualValue != null)
            {
                assertEquals("type mismatch", expectedValue.getClass(),
                        actualValue.getClass());
            }

            assertEquals("value", expectedValue, actualValue);
        }
    }

    /** Issue 361: NPE when value is null. */
    @Test
    public void testSetSqlValue_Null() throws Exception
    {
        Object value = null;
        int column = 1;
        PreparedStatement statement = new MockPreparedStatement();

        THIS_TYPE.setSqlValue(value, column, statement);
    }

    @Test
    public void testSetSqlValue_Integer() throws Exception
    {
        Object value = 1;
        int column = 1;
        PreparedStatement statement = new MockPreparedStatement();

        THIS_TYPE.setSqlValue(value, column, statement);
    }
}
