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
    private final static DataType[] TYPES = {
        DataType.TINYINT,
        DataType.SMALLINT,
        DataType.INTEGER,
    };

    public IntegerDataTypeTest(String name)
    {
        super(name);
    }

    /**
     *
     */
    public void testToString() throws Exception
    {
        String[] expected = {
            "TINYINT",
            "SMALLINT",
            "INTEGER",
        };

        assertEquals("type count", expected.length, TYPES.length);
        for (int i = 0; i < TYPES.length; i++)
        {
            assertEquals("name", expected[i], TYPES[i].toString());
        }
    }

    public void testGetTypeClass() throws Exception
    {
        for (int i = 0; i < TYPES.length; i++)
        {
            assertEquals("class", Integer.class, TYPES[i].getTypeClass());
        }
    }

    public void testIsNumber() throws Exception
    {
        for (int i = 0; i < TYPES.length; i++)
        {
            assertEquals("is number", true, TYPES[i].isNumber());
        }
    }

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

        for (int i = 0; i < TYPES.length; i++)
        {
            for (int j = 0; j < values.length; j++)
            {
                assertEquals("typecast " + j, expected[j],
                        TYPES[i].typeCast(values[j]));
            }
        }
    }

    public void testInvalidTypeCast() throws Exception
    {
        Object[] values = {new Object(), "bla", new java.util.Date()};

        for (int i = 0; i < TYPES.length; i++)
        {
            for (int j = 0; j < values.length; j++)
            {
                try
                {
                    TYPES[i].typeCast(values[j]);
                    fail("Should throw TypeCastException");
                }
                catch (TypeCastException e)
                {
                }
            }
        }
    }

    public void testSqlType() throws Exception
    {
        int[] sqlTypes = {
            Types.TINYINT,
            Types.SMALLINT,
            Types.INTEGER,
        };

        assertEquals("count", sqlTypes.length, TYPES.length);
        for (int i = 0; i < TYPES.length; i++)
        {
            assertEquals("forSqlType", TYPES[i], DataType.forSqlType(sqlTypes[i]));
            assertEquals("getSqlType", sqlTypes[i], TYPES[i].getSqlType());
        }
    }

    /**
     *
     */
    public void testForObject() throws Exception
    {
        assertEquals(DataType.INTEGER, DataType.forObject(new Integer(1234)));
    }

}

