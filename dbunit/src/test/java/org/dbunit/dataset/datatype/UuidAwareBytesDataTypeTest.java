/*
 *
 * The DbUnit Database Testing Framework
 * Copyright (C)2002-2008, DbUnit.org
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
import java.util.Arrays;

import org.dbunit.database.statement.MockPreparedStatement;

/**
 * @author Timur Strekalov
 */
public class UuidAwareBytesDataTypeTest extends BytesDataTypeTest
{
    private final static DataType[] TYPES = {DataType.BINARY,
        DataType.VARBINARY, DataType.LONGVARBINARY};

    public UuidAwareBytesDataTypeTest(final String name)
    {
        super(name);
    }

    @Override
    public void testTypeCast() throws Exception
    {
        final Object[] values =
            {null, "uuid'2aad615a-d8e1-11e2-b8ed-50e549c9b654'"};

        final byte[][] expected =
            {
                null,
                new byte[] {(byte) 0x2a, (byte) 0xad, (byte) 0x61,
                        (byte) 0x5a, (byte) 0xd8, (byte) 0xe1,
                        (byte) 0x11, (byte) 0xe2, (byte) 0xb8,
                        (byte) 0xed, (byte) 0x50, (byte) 0xe5,
                        (byte) 0x49, (byte) 0xc9, (byte) 0xb6,
                        (byte) 0x54}};

        assertEquals("actual vs expected count", values.length, expected.length);

        for (DataType element : TYPES)
        {
            for (int j = 0; j < values.length; j++)
            {
                byte[] actual = (byte[]) element.typeCast(values[j]);
                assertTrue("typecast " + j, Arrays.equals(expected[j], actual));
            }
        }
    }

    public void testCompareEqualsUuidAware() throws Exception
    {
        final Object[] values1 =
            {null, "uuid'2aad615a-d8e1-11e2-b8ed-50e549c9b654'"};

        final byte[][] values2 =
            {
                null,
                new byte[] {(byte) 0x2a, (byte) 0xad, (byte) 0x61,
                        (byte) 0x5a, (byte) 0xd8, (byte) 0xe1,
                        (byte) 0x11, (byte) 0xe2, (byte) 0xb8,
                        (byte) 0xed, (byte) 0x50, (byte) 0xe5,
                        (byte) 0x49, (byte) 0xc9, (byte) 0xb6,
                        (byte) 0x54}};

        assertEquals("values count", values1.length, values2.length);

        for (DataType element : TYPES)
        {
            for (int j = 0; j < values1.length; j++)
            {
                assertEquals("compare1 " + j, 0,
                        element.compare(values1[j], values2[j]));
                assertEquals("compare2 " + j, 0,
                        element.compare(values2[j], values1[j]));
            }
        }
    }

    public void testSetSqlValueWithUuid() throws Exception
    {
        final MockPreparedStatement preparedStatement =
                new MockPreparedStatement();

        final String[] given = {"uuid'2aad615a-d8e1-11e2-b8ed-50e549c9b654'"};

        final byte[][] expected =
            {new byte[] {(byte) 0x2a, (byte) 0xad, (byte) 0x61,
                    (byte) 0x5a, (byte) 0xd8, (byte) 0xe1, (byte) 0x11,
                    (byte) 0xe2, (byte) 0xb8, (byte) 0xed, (byte) 0x50,
                    (byte) 0xe5, (byte) 0x49, (byte) 0xc9, (byte) 0xb6,
                    (byte) 0x54}};

        final int[] expectedSqlTypesForDataType =
            {Types.BINARY, Types.VARBINARY, Types.LONGVARBINARY};

        for (int i = 0; i < expected.length; i++)
        {
            final String givenValue = given[i];
            final byte[] expectedValue = expected[i];

            for (int j = 0; j < TYPES.length; j++)
            {
                final DataType dataType = TYPES[j];
                final int expectedSqlType = expectedSqlTypesForDataType[j];

                dataType.setSqlValue(givenValue, 1, preparedStatement);

                assertEquals("Loop " + i + " Type " + dataType, 1,
                        preparedStatement.getLastSetObjectParamIndex());
                assertEquals("Loop " + i + " Type " + dataType,
                        expectedSqlType,
                        preparedStatement.getLastSetObjectTargetSqlType());

                final byte[] actualValue =
                        (byte[]) preparedStatement.getLastSetObjectParamValue();

                assertTrue("Loop " + i + " Type " + dataType,
                        Arrays.equals(expectedValue, actualValue));
            }
        }
    }

    public void testSetSqlValueWithSomethingThatLooksLikeUuidButIsNot()
            throws Exception
            {
        final MockPreparedStatement preparedStatement =
                new MockPreparedStatement();

        final String[] given =
            {"2aad615a-d8e1-11e2-b8ed-50e549c9b654",
            "uuid'2aad615a-d8e1-11e2-b8ed-50e549c9b65'"};

        final Object[] expected = {null, null};

        final int[] expectedSqlTypesForDataType =
            {Types.BINARY, Types.VARBINARY, Types.LONGVARBINARY};

        for (int i = 0; i < expected.length; i++)
        {
            final String givenValue = given[i];
            final Object expectedValue = expected[i];

            for (int j = 0; j < TYPES.length; j++)
            {
                final DataType dataType = TYPES[j];
                final int expectedSqlType = expectedSqlTypesForDataType[j];

                dataType.setSqlValue(givenValue, 1, preparedStatement);

                assertEquals("Loop " + i + " Type " + dataType, 1,
                        preparedStatement.getLastSetObjectParamIndex());
                assertEquals("Loop " + i + " Type " + dataType,
                        expectedSqlType,
                        preparedStatement.getLastSetObjectTargetSqlType());

                final Object actualValue =
                        preparedStatement.getLastSetObjectParamValue();

                assertEquals("Loop " + i + " Type " + dataType, expectedValue,
                        actualValue);
            }
        }
    }
}
