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
    private final static DataType THIS_TYPE = DataType.STRING;

    public StringDataTypeTest(String name)
    {
        super(name);
    }

    /**
     *
     */
    public void testToString() throws Exception
    {
        assertEquals("name", "string", THIS_TYPE.toString());
    }

    /**
     *
     */
    public void testGetTypeClass() throws Exception
    {
        assertEquals("class", String.class, THIS_TYPE.getTypeClass());
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
        Object[] values = {new Object()};

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
        assertEquals(THIS_TYPE, DataType.forSqlType(Types.CHAR));
        assertEquals(THIS_TYPE, DataType.forSqlType(Types.VARCHAR));
        assertEquals(THIS_TYPE, DataType.forSqlType(Types.LONGVARCHAR));
    }

    /**
     *
     */
    public void testForObject() throws Exception
    {
        assertEquals(THIS_TYPE, DataType.forObject(""));
    }

}
