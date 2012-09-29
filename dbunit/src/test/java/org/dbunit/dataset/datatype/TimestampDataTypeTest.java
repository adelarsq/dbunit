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

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * @author Manuel Laflamme
 * @version $Revision$
 */

public class TimestampDataTypeTest extends AbstractDataTypeTest
{
    private final static DataType THIS_TYPE = DataType.TIMESTAMP;

    public TimestampDataTypeTest(String name)
    {
        super(name);
    }

    /**
     *
     */
    public void testToString() throws Exception
    {
        assertEquals("name", "TIMESTAMP", THIS_TYPE.toString());
    }

    /**
     *
     */
    public void testGetTypeClass() throws Exception
    {
        assertEquals("class", Timestamp.class, THIS_TYPE.getTypeClass());
    }

    public void testIsNumber() throws Exception
    {
        assertEquals("is number", false, THIS_TYPE.isNumber());
    }

    public void testIsDateTime() throws Exception
    {
        assertEquals("is date/time", true, THIS_TYPE.isDateTime());
    }

    private static Timestamp makeTimestamp(int year, int month, int day, int hour, int minute, int second, int millis, TimeZone timeZone)
    {
        Calendar cal = new GregorianCalendar(timeZone);
        cal.clear();
        cal.set(year, month, day, hour, minute, second);
        cal.set(Calendar.MILLISECOND, millis);
        return new Timestamp(cal.getTime().getTime());
    }

    private static Timestamp makeTimestamp(int year, int month, int day, int hour, int minute, int second, int millis, String timeZone)
    {
       return makeTimestamp(year, month, day, hour, minute, second, millis, TimeZone.getTimeZone(timeZone));
    }

    private static Timestamp makeTimestamp(int year, int month, int day, int hour, int minute, int second, int millis)
    {
        return makeTimestamp(year, month, day, hour, minute, second, millis, TimeZone.getDefault());
    }

    private static Timestamp makeTimestamp(int year, int month, int day, int hour, int minute, int second, String timeZone)
    {
        return makeTimestamp(year, month, day, hour, minute, second, 0, TimeZone.getTimeZone(timeZone));
    }

    private static Timestamp makeTimestamp(int year, int month, int day, int hour, int minute, int second)
    {
        return makeTimestamp(year, month, day, hour, minute, second, 0, TimeZone.getDefault());
    }

    public void testTypeCast() throws Exception
    {
        // Useful when manually testing this for other timezones
        // Default setting is to test from default timezone
        // TimeZone testTimeZone = TimeZone.getTimeZone("America/New_York");
        // TimeZone testTimeZone = TimeZone.getTimeZone("Europe/Berlin");
        // TimeZone.setDefault(testTimeZone);
        TimeZone currentTimeZone = TimeZone.getDefault();
        int rawOffset = currentTimeZone.getRawOffset();
        int hourOffset = rawOffset / 1000 / 60 / 60;

        Object[] values = {
            null,
            new Timestamp(1234),
            new Date(1234),
            new Time(1234),
            new Timestamp(1234).toString(),
            new Date(1234).toString(),
            new java.util.Date(1234),
            "1995-01-07 01:22:41.9 -0500",
            "1995-01-07 01:22:41.923 -0500",
            "1995-01-07 01:22:41.9",
            "1995-01-07 01:22:41.923",
            "1995-01-07 01:22:41 -0500",
            "1995-01-07 01:22:41",
            "2008-11-27 14:52:38 +0100"
        };

        Timestamp[] expected = {
            null,
            new Timestamp(1234),
            new Timestamp(new Date(1234).getTime()),
            new Timestamp(new Time(1234).getTime()),
            new Timestamp(1234),
            new Timestamp(Date.valueOf((new Date(1234).toString())).getTime()),
            new Timestamp(1234),
            makeTimestamp(1995, 0, 7, 1 - hourOffset, 22, 41, 900, "America/New_York"),
            makeTimestamp(1995, 0, 7, 1 - hourOffset, 22, 41, 923, "America/New_York"),
            makeTimestamp(1995, 0, 7, 1, 22, 41, 900),
            makeTimestamp(1995, 0, 7, 1, 22, 41, 923),
            makeTimestamp(1995, 0, 7, 1 - hourOffset, 22, 41, "America/New_York"),
            makeTimestamp(1995, 0, 7, 1, 22, 41),
            makeTimestamp(2008, 10, 27, 14 - hourOffset, 52, 38, "Europe/Berlin")
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
            new Timestamp(1234),
            new Date(1234),
            new Time(1234),
            new Timestamp(1234).toString(),
            new java.util.Date(1234),
            "1970-01-01 00:00:00.0",
        };

        Timestamp[] values2 = {
            null,
            new Timestamp(1234),
            new Timestamp(new Date(1234).getTime()),
            new Timestamp(new Time(1234).getTime()),
            Timestamp.valueOf(new Timestamp(1234).toString()),
            new Timestamp(1234),
            Timestamp.valueOf("1970-01-01 00:00:00.0"),
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
            "1974-23-06 23:40:00.0"
        };

        Object[] greater = {
            new java.sql.Date(1234),
            new java.sql.Date(System.currentTimeMillis()),
            Timestamp.valueOf("2003-01-30 11:42:00.0"),
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
        assertEquals(THIS_TYPE, DataType.forSqlType(Types.TIMESTAMP));
        assertEquals("forSqlTypeName", THIS_TYPE, DataType.forSqlTypeName(THIS_TYPE.toString()));
        assertEquals(Types.TIMESTAMP, THIS_TYPE.getSqlType());
    }

    public void testForObject() throws Exception
    {
        assertEquals(THIS_TYPE, DataType.forObject(new Timestamp(1234)));
    }

    public void testAsString() throws Exception
    {
        java.sql.Timestamp[] values = {
            new java.sql.Timestamp(1234),
        };

        String[] expected = {
            new java.sql.Timestamp(1234).toString(),
        };


        assertEquals("actual vs expected count", values.length, expected.length);

        for (int i = 0; i < values.length; i++)
        {
            assertEquals("asString " + i, expected[i], DataType.asString(values[i]));
        }
    }

    public void testGetSqlValue() throws Exception
    {
        Timestamp[] expected = {
            null,
            new Timestamp(1234),
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





