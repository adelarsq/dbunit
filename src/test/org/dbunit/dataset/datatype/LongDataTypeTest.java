/*
 *
 * Copyright 2001 Karat Software Corp. All Rights Reserved.
 *
 * This software is the proprietary information of Karat Software Corp.
 * Use is subject to license terms.
 *
 */

package org.dbunit.dataset.datatype;

import java.sql.Types;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @author Manuel Laflamme
 * @version 1.0
 */

public class LongDataTypeTest extends AbstractDataTypeTest
{
    private final static DataType THIS_TYPE = DataType.LONG;

    public LongDataTypeTest(String name)
    {
        super(name);
    }

    /**
     *
     */
    public void testToString() throws Exception
    {
        assertEquals("name", "long", THIS_TYPE.toString());
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

    /**
     *
     */
    public void testTypeCast() throws Exception
    {
        Object[] values = {
            null,
            "5",
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
            new BigDecimal(1234),
        };

        Long[] expected = {
            null,
            new Long(5),
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

    /**
     *
     */
    public void testInvalidTypeCast() throws Exception
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

    public void testForSqlType() throws Exception
    {
        assertEquals(THIS_TYPE, DataType.forSqlType(Types.BIGINT));
    }

    /**
     *
     */
    public void testForObject() throws Exception
    {
        assertEquals(THIS_TYPE, DataType.forObject(new Long(1234)));
    }

}
