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

import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;

/**
 * @author Manuel Laflamme
 * @version $Revision$
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

    public void testIsDateTime() throws Exception
    {
        assertEquals("is date/time", true, THIS_TYPE.isDateTime());
    }

    public void testTypeCast() throws Exception
    {
        Object[] values = {
            null,
            new java.sql.Date(1234),
            new Time(1234),
            new Timestamp(1234),
            new java.sql.Date(1234).toString(),
            new Timestamp(1234).toString(),
            new java.util.Date(1234),
        };

        java.sql.Date[] expected = {
            null,
            new java.sql.Date(1234),
            new java.sql.Date(new Time(1234).getTime()),
            new java.sql.Date(new Timestamp(1234).getTime()),
            java.sql.Date.valueOf(new java.sql.Date(1234).toString()),
            new java.sql.Date(
                    Timestamp.valueOf(new Timestamp(1234).toString()).getTime()),
            new java.sql.Date(1234),
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
    public void testCompareEquals() throws Exception
    {
        Object[] values1 = {
            null,
            new java.sql.Date(1234),
            new Time(1234),
            new Timestamp(1234),
            new java.sql.Date(1234).toString(),
            new java.util.Date(1234),
            "2003-01-30"
        };

        Object[] values2 = {
            null,
            new java.sql.Date(1234),
            new java.sql.Date(new Time(1234).getTime()),
            new java.sql.Date(new Timestamp(1234).getTime()),
            java.sql.Date.valueOf(new java.sql.Date(1234).toString()),
            new java.sql.Date(1234),
            java.sql.Date.valueOf("2003-01-30"),
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
            new Integer(1234),
            new Object(),
            "bla",
            "2000.05.05",
        };
        Object[] values2 = {
            null,
            null,
            null,
            null,
        };

        assertEquals("values count", values1.length, values2.length);

        for (int i = 0; i < values1.length; i++)
        {
            try
            {
                THIS_TYPE.compare(values1[i], values2[i]);
                fail("Should throw TypeCastException - " + i);
            }
            catch (TypeCastException e)
            {
            }

            try
            {
                THIS_TYPE.compare(values1[i], values2[i]);
                fail("Should throw TypeCastException - " + i);
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
            new java.sql.Date(0),
            "1974-23-06"
        };

        Object[] greater = {
            new java.sql.Date(1234),
            new java.sql.Date(System.currentTimeMillis()),
            java.sql.Date.valueOf("2003-01-30"),
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
        assertEquals(THIS_TYPE, DataType.forSqlType(Types.DATE));
        assertEquals("forSqlTypeName", THIS_TYPE, DataType.forSqlTypeName(THIS_TYPE.toString()));
        assertEquals(Types.DATE, THIS_TYPE.getSqlType());
    }

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

    public void testGetSqlValue() throws Exception
    {
        java.sql.Date[] expected = {
            null,
            new java.sql.Date(1234),
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
