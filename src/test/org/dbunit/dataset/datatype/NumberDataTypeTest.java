/*
 *
 * Copyright 2001 Karat Software Corp. All Rights Reserved.
 *
 * This software is the proprietary information of Karat Software Corp.
 * Use is subject to license terms.
 *
 */

package org.dbunit.dataset.datatype;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Types;

/**
 * @author Manuel Laflamme
 * @version 1.0
 */

public class NumberDataTypeTest extends AbstractDataTypeTest
{
    private final static DataType[] TYPES = {DataType.NUMERIC, DataType.DECIMAL};

    public NumberDataTypeTest(String name)
    {
        super(name);
    }

    public void testToString() throws Exception
    {
        String[] expected = {"NUMERIC", "DECIMAL"};

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
            assertEquals("class", BigDecimal.class, TYPES[i].getTypeClass());
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
            new BigDecimal(1234),
            "1234",
            "12.34",
        };
        BigDecimal[] expected = {
            null,
            new BigDecimal(1234),
            new BigDecimal(1234),
            new BigDecimal("12.34"),
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

    /**
     *
     */
    public void testInvalidTypeCast() throws Exception
    {
        Object[] values = {new Object(), "bla"};

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
        int[] sqlTypes = {Types.NUMERIC, Types.DECIMAL};

        assertEquals("count", sqlTypes.length, TYPES.length);
        for (int i = 0; i < TYPES.length; i++)
        {
            assertEquals("forSqlType", TYPES[i], DataType.forSqlType(sqlTypes[i]));
            assertEquals("getSqlType", sqlTypes[i], TYPES[i].getSqlType());
        }
    }

    public void testForObject() throws Exception
    {
        assertEquals(DataType.NUMERIC, DataType.forObject(new BigDecimal(1234)));
    }


}

