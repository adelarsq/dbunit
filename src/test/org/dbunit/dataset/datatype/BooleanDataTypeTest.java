/*
 *
 * DbUnit Database Testing Framework
 * Copyright (C)2002, Manuel Laflamme
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

package org.dbunit.dataset.datatype;

import java.sql.Types;

/**
 * @author Manuel Laflamme
 * @version $Revision$
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
        assertEquals("forSqlTypeName", THIS_TYPE, DataType.forSqlTypeName(THIS_TYPE.toString()));
        assertEquals("getSqlType", Types.BIT, THIS_TYPE.getSqlType());
    }

    public void testForObject() throws Exception
    {
        assertEquals(THIS_TYPE, DataType.forObject(Boolean.TRUE));
    }

    /**
     *
     */
    public void testAsString() throws Exception
    {
        Boolean[] values = {
            Boolean.TRUE,
            Boolean.FALSE,
        };

        String[] expected = {
            "true",
            "false",
        };

        assertEquals("actual vs expected count", values.length, expected.length);

        for (int i = 0; i < values.length; i++)
        {
            assertEquals("asString " + i, expected[i], DataType.asString(values[i]));
        }
    }

}




