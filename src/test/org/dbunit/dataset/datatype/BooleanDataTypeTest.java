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

public class BooleanDataTypeTest extends AbstractDataTypeTest
{
    private final static DataType THIS_TYPE = DataType.BOOLEAN;

    public BooleanDataTypeTest(String name)
    {
        super(name);
    }

    /**
     *
     */
    public void testToString() throws Exception
    {
        assertEquals("name", "BIT", THIS_TYPE.toString());
    }

    /**
     *
     */
    public void testGetTypeClass() throws Exception
    {
        assertEquals("class", Boolean.class, THIS_TYPE.getTypeClass());
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
            "1",
            "0",
            "true",
            "false",
            Boolean.TRUE,
            Boolean.FALSE,
            new Integer(1),
            new Integer(0),
            new Integer(123),
        };
        Boolean[] expected = {
            null,
            Boolean.TRUE,
            Boolean.FALSE,
            Boolean.TRUE,
            Boolean.FALSE,
            Boolean.TRUE,
            Boolean.FALSE,
            Boolean.TRUE,
            Boolean.FALSE,
            Boolean.TRUE,
        };

        assertEquals("actual vs expected count", values.length, expected.length);

        for (int i = 0; i < values.length; i++)
        {
            assertEquals("typecast " + i, expected[i],
                    THIS_TYPE.typeCast(values[i]));
        }
    }

    public void testInvalidTypeCast() throws Exception
    {
        Object[] values = {"bla"};

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
        assertEquals("forSqlType", THIS_TYPE, DataType.forSqlType(Types.BIT));
        assertEquals("getSqlType", Types.BIT, THIS_TYPE.getSqlType());
    }

    public void testForObject() throws Exception
    {
        assertEquals(THIS_TYPE, DataType.forObject(Boolean.TRUE));
    }

}

