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

public class DoubleDataTypeTest extends AbstractDataTypeTest
{
    private final static DataType THIS_TYPE = DataType.DOUBLE;

    public DoubleDataTypeTest(String name)
    {
        super(name);
    }

    /**
     *
     */
    public void testToString() throws Exception
    {
        assertEquals("name", "double", THIS_TYPE.toString());
    }

    /**
     *
     */
    public void testGetTypeClass() throws Exception
    {
        assertEquals("class", Double.class, THIS_TYPE.getTypeClass());
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
            new Float(Float.MAX_VALUE),
            new Double(Double.MIN_VALUE),
            "-7500",
            "2.34E23",
            new Double(0.666),
            new Double(5.49879),
            "-99.9",
            new BigDecimal(1234),
        };

        Double[] expected = {
            null,
            new Double(5.555),
            new Double(Float.MAX_VALUE),
            new Double(Double.MIN_VALUE),
            new Double(-7500),
            Double.valueOf("2.34E23"),
            new Double(0.666),
            new Double(5.49879),
            new Double(-99.9),
            new Double(1234),
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
        assertEquals(THIS_TYPE, DataType.forSqlType(Types.FLOAT));
        assertEquals(THIS_TYPE, DataType.forSqlType(Types.DOUBLE));
    }

    /**
     *
     */
    public void testForObject() throws Exception
    {
        assertEquals(THIS_TYPE, DataType.forObject(new Double(1234)));
    }

}
