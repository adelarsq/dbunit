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
            new Timestamp(1234),
            new Date(1234),
            new Time(1234),
            new Timestamp(1234).toString(),
            new java.util.Date(1234),
        };

        Timestamp[] expected = {
            null,
            new Timestamp(1234),
            new Timestamp(new Date(1234).getTime()),
            new Timestamp(new Time(1234).getTime()),
            Timestamp.valueOf(new Timestamp(1234).toString()),
            new Timestamp(1234),
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
        assertEquals(THIS_TYPE, DataType.forSqlType(Types.TIMESTAMP));
        assertEquals(Types.TIMESTAMP, THIS_TYPE.getSqlType());
    }

    /**
     *
     */
    public void testForObject() throws Exception
    {
        assertEquals(THIS_TYPE, DataType.forObject(new Timestamp(1234)));
    }

}

