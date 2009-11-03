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
 * @author Last changed by: $Author$
 * @version $Revision$ $Date$
 */
public class BooleanDataTypeTest extends AbstractDataTypeTest
{
    private final static DataType THIS_TYPE = DataType.BOOLEAN;

    public BooleanDataTypeTest(String name)
    {
        super(name);
    }

    /**
     *
     */
    public void testToString() throws Exception
    {
        assertEquals("name", "BOOLEAN", THIS_TYPE.toString());
    }

    /**
     *
     */
    public void testGetTypeClass() throws Exception
    {
        assertEquals("class", Boolean.class, THIS_TYPE.getTypeClass());
    }

    /**
     *
     */
    public void testIsNumber() throws Exception
    {
        assertEquals("is number", false, THIS_TYPE.isNumber());
    }

    public void testIsDateTime() throws Exception
    {
        assertEquals("is date/time", false, THIS_TYPE.isDateTime());
    }

    public void testTypeCast() throws Exception
    {
        Object[] values = {
            null,
            "1", // Strings
            "0",
            "true",
            "false",
            "4894358", //TODO should it be possible to cast this into a Boolean?
            Boolean.TRUE, // Booleans
            Boolean.FALSE,
            new Integer(1), // Numbers
            new Integer(0),
            new Integer(123), //TODO should it be possible to cast this into a Boolean?
            new BigDecimal("20.53"), //TODO should it be possible to cast this into a Boolean?
        };
        Boolean[] expected = {
            null,
            Boolean.TRUE, // Strings
            Boolean.FALSE,
            Boolean.TRUE,
            Boolean.FALSE,
            Boolean.TRUE,
            Boolean.TRUE, // Booleans
            Boolean.FALSE,
            Boolean.TRUE, // Numbers
            Boolean.FALSE,
            Boolean.TRUE,
            Boolean.TRUE,
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
        Object[] values = {"bla"};

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
            "1",
            "0",
            Boolean.TRUE,
            Boolean.FALSE,
        };
        Object[] values2 = {
            null,
            Boolean.TRUE,
            Boolean.FALSE,
            "true",
            "false",
        };

        assertEquals("values count", values1.length, values2.length);

        for (int i = 0; i < values1.length; i++)
        {
            assertEquals("compare1 " + i,
                    0, THIS_TYPE.compare(values1[i], values2[i]));
            assertEquals("compare2 " + i,
                    0, THIS_TYPE.compare(values2[i], values1[i]));
        }
    }

    public void testCompareInvalid() throws Exception
    {
        Object[] values1 = {
            "bla",
            Boolean.FALSE,
        };
        Object[] values2 = {
            Boolean.TRUE,
            "bla",
        };

        assertEquals("values count", values1.length, values2.length);

        for (int i = 0; i < values1.length; i++)
        {
            try
            {
                THIS_TYPE.compare(values1[i], values2[i]);
                fail("Should have throw TypeCastException");
            }
            catch (TypeCastException e)
            {

            }

            try
            {
                THIS_TYPE.compare(values2[i], values1[i]);
                fail("Should have throw TypeCastException");
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
            Boolean.FALSE,
        };
        Object[] greater = {
            Boolean.TRUE,
            Boolean.FALSE,
            Boolean.TRUE,
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
        assertEquals("forSqlType", THIS_TYPE, DataType.forSqlType(Types.BOOLEAN));
        assertEquals("forSqlTypeName", THIS_TYPE, DataType.forSqlTypeName(THIS_TYPE.toString()));
        assertEquals("getSqlType", Types.BOOLEAN, THIS_TYPE.getSqlType());
    }

    public void testForObject() throws Exception
    {
        assertEquals(THIS_TYPE, DataType.forObject(Boolean.TRUE));
    }

    /**
     *
     */
    public void testAsString() throws Exception
    {
        Boolean[] values = {
            Boolean.TRUE,
            Boolean.FALSE,
        };

        String[] expected = {
            "true",
            "false",
        };

        assertEquals("actual vs expected count", values.length, expected.length);

        for (int i = 0; i < values.length; i++)
        {
            assertEquals("asString " + i, expected[i], DataType.asString(values[i]));
        }
    }

    public void testGetSqlValue() throws Exception
    {
        Object[] expected = new Object[] {
            null,
            Boolean.TRUE,
            Boolean.FALSE,
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
