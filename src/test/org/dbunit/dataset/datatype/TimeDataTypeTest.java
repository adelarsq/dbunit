/*
 *
 * Copyright 2001 Karat Software Corp. All Rights Reserved.
 *
 * This software is the proprietary information of Karat Software Corp.
 * Use is subject to license terms.
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

public class TimeDataTypeTest extends AbstractDataTypeTest
{
    private final static DataType THIS_TYPE = DataType.TIME;

    public TimeDataTypeTest(String name)
    {
        super(name);
    }

    /**
     *
     */
    public void testToString() throws Exception
    {
        assertEquals("name", "TIME", THIS_TYPE.toString());
    }

    /**
     *
     */
    public void testGetTypeClass() throws Exception
    {
        assertEquals("class", Time.class, THIS_TYPE.getTypeClass());
    }

    /**
     *
     */
    public void testIsNumber() throws Exception
    {
        assertEquals("is number", false, THIS_TYPE.isNumber());
    }

    /**
     *
     */
    public void testTypeCast() throws Exception
    {
        Object[] values = {
            null,
            new Time(1234),
            new java.sql.Date(1234),
            new Timestamp(1234),
            new Time(1234).toString(),
            new java.util.Date(1234),
        };

        java.sql.Time[] expected = {
            null,
            new Time(1234),
            new Time(new java.sql.Date(1234).getTime()),
            new Time(new Timestamp(1234).getTime()),
            Time.valueOf(new Time(1234).toString()),
            new Time(1234),
        };

        assertEquals("actual vs expected count", values.length, expected.length);

        for (int i = 0; i < values.length; i++)
        {
            assertEquals("typecast " + i, expected[i],
                    THIS_TYPE.typeCast(values[i]));
        }
    }

    /**
     *
     */
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
        assertEquals(THIS_TYPE, DataType.forSqlType(Types.TIME));
        assertEquals(Types.TIME, THIS_TYPE.getSqlType());
    }

    /**
     *
     */
    public void testForObject() throws Exception
    {
        assertEquals(THIS_TYPE, DataType.forObject(new Time(1234)));
    }

}

