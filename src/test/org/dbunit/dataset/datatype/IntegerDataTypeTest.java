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

public class IntegerDataTypeTest extends AbstractDataTypeTest
{
    private final static DataType THIS_TYPE = DataType.INTEGER;

    public IntegerDataTypeTest(String name)
    {
        super(name);
    }

    /**
     *
     */
    public void testToString() throws Exception
    {
        assertEquals("name", "integer", THIS_TYPE.toString());
    }

    /**
     *
     */
    public void testGetTypeClass() throws Exception
    {
        assertEquals("class", Integer.class, THIS_TYPE.getTypeClass());
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
            new Long(Integer.MAX_VALUE - 1),
            new Double(Integer.MIN_VALUE + 1),
            "-7500",
            new Long(Integer.MAX_VALUE),
            new Double(Integer.MIN_VALUE),
            new Float(0.666),
            new Double(0.666),
            new Double(5.49),
            "-99.9",
            new Double(1.5E2),
            new BigDecimal(1234),
        };

        Integer[] expected = {
            null,
            new Integer(5),
            new Integer(Integer.MAX_VALUE - 1),
            new Integer(Integer.MIN_VALUE + 1),
            new Integer(-7500),
            new Integer(Integer.MAX_VALUE),
            new Integer(Integer.MIN_VALUE),
            new Integer(0),
            new Integer(0),
            new Integer(5),
            new Integer(-99),
            new Integer(150),
            new Integer(1234),

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
        assertEquals(THIS_TYPE, DataType.forSqlType(Types.TINYINT));
        assertEquals(THIS_TYPE, DataType.forSqlType(Types.SMALLINT));
        assertEquals(THIS_TYPE, DataType.forSqlType(Types.INTEGER));
    }

    /**
     *
     */
    public void testForObject() throws Exception
    {
        assertEquals(THIS_TYPE, DataType.forObject(new Integer(1234)));
    }

}
