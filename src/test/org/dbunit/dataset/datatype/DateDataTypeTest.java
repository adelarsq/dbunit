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
        assertEquals("name", "date", THIS_TYPE.toString());
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

    /**
     *
     */
    public void testTypeCast() throws Exception
    {
        Object[] values = {
            null,
            new java.sql.Date(1234),
            new Time(1234),
            new Timestamp(1234),
            new java.sql.Date(1234).toString(),
            new java.util.Date(1234),
        };

        java.sql.Date[] expected = {
            null,
            new java.sql.Date(1234),
            new java.sql.Date(new Time(1234).getTime()),
            new java.sql.Date(new Timestamp(1234).getTime()),
            java.sql.Date.valueOf(new java.sql.Date(1234).toString()),
            new java.sql.Date(1234),
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

    public void testForSqlType() throws Exception
    {
        assertEquals(THIS_TYPE, DataType.forSqlType(Types.DATE));
    }

    /**
     *
     */
    public void testForObject() throws Exception
    {
        assertEquals(THIS_TYPE, DataType.forObject(new java.sql.Date(1234)));
    }

}
