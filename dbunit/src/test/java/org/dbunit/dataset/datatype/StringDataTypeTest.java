/*
 *
 * The DbUnit Database Testing Framework
 * Copyright (C)2002-2004, DbUnit.org
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

import org.dbunit.database.ExtendedMockSingleRowResultSet;
import org.dbunit.dataset.ITable;

import java.sql.Types;
import java.sql.SQLException;

/**
 * @author Manuel Laflamme
 * @version $Revision$
 */

public class StringDataTypeTest extends AbstractDataTypeTest
{
    private final static DataType[] TYPES = {
        DataType.CHAR,
        DataType.VARCHAR,
        DataType.LONGVARCHAR,
//        DataType.CLOB,
    };

    public StringDataTypeTest(String name)
    {
        super(name);
    }

    public void testToString() throws Exception
    {
        String[] expected = {
            "CHAR",
            "VARCHAR",
            "LONGVARCHAR",
//            "CLOB",
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

    public void testIsDateTime() throws Exception
    {
        for (int i = 0; i < TYPES.length; i++)
        {
            assertEquals("is date/time", false, TYPES[i].isDateTime());
        }
    }

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
            new byte[]{'a', 'b', 'c', 'd'},
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
            "YWJjZA==",
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

    public void testTypeCastNone() throws Exception
    {
        for (int i = 0; i < TYPES.length; i++)
        {
            DataType type = TYPES[i];
            assertEquals("typecast " + type, null, type.typeCast(ITable.NO_VALUE));
        }
    }

    /**
     * Return a bad clob that throws SQLException on all its operations.
     */
    private Object getBadClob()
    {
        // need to use proxy / reflection to work arround Clob differences
        // in jdk 1.4+
        java.lang.reflect.InvocationHandler alwaysThrowSqlExceptionHandler =
            new java.lang.reflect.InvocationHandler()
        {
            public Object invoke(Object proxy, java.lang.reflect.Method method, Object[] args)
                throws Throwable
            {
                if ("toString".equals(method.getName()))
                {
                    return this.toString();
                }
                else if ("equals".equals(method.getName()))
                {
                    return Boolean.FALSE;
                }
                throw new SQLException();
            }
        };

        return java.lang.reflect.Proxy.newProxyInstance(
            java.sql.Clob.class.getClassLoader(), new Class[] { java.sql.Clob.class },
            alwaysThrowSqlExceptionHandler);
    }

    public void testTypeCastInvalid() throws Exception
    {
        Object[] values = {
            new Object() { public String toString() { return "ABC123";} },
            new Object() { public String toString() { return "XXXX";} },
            new Object() { public String toString() { return "X";} },
        };

        for (int i = 0; i < TYPES.length; i++)
        {
            for (int j = 0; j < values.length; j++)
            {
                assertEquals(TYPES[i].typeCast(values[j]), values[j].toString());
            }
        }

        Object badClob = getBadClob();
        for (int i = 0; i < TYPES.length; i++)
        {
            try
            {
                TYPES[i].typeCast(badClob);
                fail("Should throw TypeCastException");
            }
            catch (TypeCastException e)
            {
            }
        }
    }

    public void testCompareEquals() throws Exception
    {
        Object[] values1 = {
            null,
            "bla",
            new java.sql.Date(1234),
            new java.sql.Time(1234),
            new java.sql.Timestamp(1234),
            Boolean.TRUE,
            new Integer(1234),
            new Long(1234),
            new Double(12.34),
            new byte[]{'a', 'b', 'c', 'd'},
        };
        String[] values2 = {
            null,
            "bla",
            new java.sql.Date(1234).toString(),
            new java.sql.Time(1234).toString(),
            new java.sql.Timestamp(1234).toString(),
            "true",
            "1234",
            "1234",
            "12.34",
            "YWJjZA==",
        };

        assertEquals("values count", values1.length, values2.length);

        for (int i = 0; i < TYPES.length; i++)
        {
            for (int j = 0; j < values1.length; j++)
            {
                assertEquals("compare1 " + j, 0, TYPES[i].compare(values1[j], values2[j]));
                assertEquals("compare2 " + j, 0, TYPES[i].compare(values2[j], values1[j]));
            }
        }
    }

    public void testCompareInvalid() throws Exception
    {
        Object[] values1 = {
            getBadClob(),
        };
        Object[] values2 = {
            null,
        };

        assertEquals("values count", values1.length, values2.length);

        for (int i = 0; i < TYPES.length; i++)
        {
            for (int j = 0; j < values1.length; j++)
            {
                try
                {
                    TYPES[i].compare(values1[j], values2[j]);
                    fail("Should throw TypeCastException");
                }
                catch (TypeCastException e)
                {
                }

                try
                {
                    TYPES[i].compare(values2[j], values1[j]);
                    fail("Should throw TypeCastException");
                }
                catch (TypeCastException e)
                {
                }
            }
        }
    }

    public void testCompareDifferent() throws Exception
    {
        Object[] less = {
            null,
            "",
            "abcd",
            "123",
        };

        Object[] greater = {
            "bla",
            "bla",
            "efgh",
            "1234",
        };

        assertEquals("values count", less.length, greater.length);

        for (int i = 0; i < TYPES.length; i++)
        {
            for (int j = 0; j < less.length; j++)
            {
                assertTrue("less " + j, TYPES[i].compare(less[j], greater[j]) < 0);
                assertTrue("greater " + j, TYPES[i].compare(greater[j], less[j]) > 0);
            }
        }
    }

    public void testSqlType() throws Exception
    {
        int[] sqlTypes = {
            Types.CHAR,
            Types.VARCHAR,
            Types.LONGVARCHAR,
//            Types.CLOB,
        };

        assertEquals("count", sqlTypes.length, TYPES.length);
        for (int i = 0; i < TYPES.length; i++)
        {
            assertEquals("forSqlType", TYPES[i], DataType.forSqlType(sqlTypes[i]));
            assertEquals("forSqlTypeName", TYPES[i], DataType.forSqlTypeName(TYPES[i].toString()));
            assertEquals("getSqlType", sqlTypes[i], TYPES[i].getSqlType());
        }
    }

    public void testForObject() throws Exception
    {
        assertEquals(DataType.VARCHAR, DataType.forObject(""));
    }

    public void testAsString() throws Exception
    {
        Object[] values = {
            new String("1234"),
        };

        String[] expected = {
            "1234",
        };

        assertEquals("actual vs expected count", values.length, expected.length);

        for (int i = 0; i < values.length; i++)
        {
            assertEquals("asString " + i, expected[i], DataType.asString(values[i]));
        }
    }

    public void testGetSqlValue() throws Exception
    {
        String[] expected = {
            null,
            "bla",
        };

        ExtendedMockSingleRowResultSet resultSet = new ExtendedMockSingleRowResultSet();
        resultSet.addExpectedIndexedValues(expected);

        for (int i = 0; i < expected.length; i++)
        {
            Object expectedValue = expected[i];

            for (int j = 0; j < TYPES.length; j++)
            {
                DataType dataType = TYPES[j];
                Object actualValue = dataType.getSqlValue(i + 1, resultSet);
                assertEquals("value " + j, expectedValue, actualValue);
            }
        }
    }

}
