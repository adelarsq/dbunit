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

/**
 * @author Manuel Laflamme
 * @version 1.0
 */

public class StringDataTypeTest extends AbstractDataTypeTest
{
    private final static DataType[] TYPES = {
        DataType.CHAR,
        DataType.VARCHAR,
        DataType.LONGVARCHAR,
    };

    public StringDataTypeTest(String name)
    {
        super(name);
    }

    public void testToString() throws Exception
    {
        String[] expected = {"CHAR", "VARCHAR", "LONGVARCHAR"};

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
            assertEquals("class", String.class, TYPES[i].getTypeClass());
        }
    }

    public void testIsNumber() throws Exception
    {
        for (int i = 0; i < TYPES.length; i++)
        {
            assertEquals("is number", false, TYPES[i].isNumber());
        }
    }

    /**
     *
     */
    public void testTypeCast() throws Exception
    {
        Object[] values = {
            null,
            "bla",
            new java.sql.Date(1234),
            new java.sql.Time(1234),
            new java.sql.Timestamp(1234),
            Boolean.TRUE,
            new Integer(1234),
            new Long(1234),
            new Double(12.34),
        };
        String[] expected = {
            null,
            "bla",
            new java.sql.Date(1234).toString(),
            new java.sql.Time(1234).toString(),
            new java.sql.Timestamp(1234).toString(),
            "true",
            "1234",
            "1234",
            "12.34",
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
        Object[] values = {new Object()};

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
        int[] sqlTypes = {Types.CHAR, Types.VARCHAR, Types.LONGVARCHAR};

        assertEquals("count", sqlTypes.length, TYPES.length);
        for (int i = 0; i < TYPES.length; i++)
        {
            assertEquals("forSqlType", TYPES[i], DataType.forSqlType(sqlTypes[i]));
            assertEquals("getSqlType", sqlTypes[i], TYPES[i].getSqlType());
        }
    }

    public void testForObject() throws Exception
    {
        assertEquals(DataType.VARCHAR, DataType.forObject(""));
    }

}

