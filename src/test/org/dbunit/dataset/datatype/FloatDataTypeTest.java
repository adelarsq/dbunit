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

public class FloatDataTypeTest extends AbstractDataTypeTest
{
    private final static DataType THIS_TYPE = DataType.REAL;

    public FloatDataTypeTest(String name)
    {
        super(name);
    }

    /**
     *
     */
    public void testToString() throws Exception
    {
        assertEquals("name", "REAL", THIS_TYPE.toString());
    }

    /**
     *
     */
    public void testGetTypeClass() throws Exception
    {
        assertEquals("class", Float.class, THIS_TYPE.getTypeClass());
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
            "5.555",
            new Double(Float.MAX_VALUE),
            new Double(Float.MIN_VALUE),
            "-7500",
            "2.34E3",
            new Double(0.666),
            new Double(5.49879),
            "-99.9",
            new BigDecimal(1234),
        };

        Float[] expected = {
            null,
            new Float(5.555),
            new Float(Float.MAX_VALUE),
            new Float(Float.MIN_VALUE),
            new Float(-7500),
            Float.valueOf("2.34E3"),
            new Float(0.666),
            new Float(5.49879),
            new Float(-99.9),
            new Float(1234),
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

    public void testSqlType() throws Exception
    {
        assertEquals(THIS_TYPE, DataType.forSqlType(Types.REAL));
        assertEquals(Types.REAL, THIS_TYPE.getSqlType());
    }

    /**
     *
     */
    public void testForObject() throws Exception
    {
        assertEquals(THIS_TYPE, DataType.forObject(new Float(1234)));
    }

}

