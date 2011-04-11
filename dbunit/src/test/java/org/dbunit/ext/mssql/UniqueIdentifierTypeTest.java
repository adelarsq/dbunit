/*
 * Copyright (C) 2011, Red Hat, Inc.
 * Written by Darryl L. Pierce <dpierce@redhat.com>.
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
package org.dbunit.ext.mssql;

import java.sql.SQLException;
import java.util.UUID;

import junit.framework.TestCase;

import org.dbunit.dataset.datatype.TypeCastException;

import com.mockobjects.sql.MockSingleRowResultSet;

/**
 * <code>UniqueIdentifierTypeTest</code> ensures that the {@link UniqueIdentifierType} works as expected.
 *
 * @author Darryl L. Pierce <dpierce@redhat.com>
 */
// TODO add tests for setSqlValue(Object, int, PreparedStatement)
public class UniqueIdentifierTypeTest extends TestCase {
    private UUID existingUuid;
    private UniqueIdentifierType uuidType;
    private MockSingleRowResultSet resultSet;

    protected void setUp() throws Exception {
        super.setUp();

        uuidType = new UniqueIdentifierType();

        resultSet = new MockSingleRowResultSet();
        existingUuid = UUID.randomUUID();
    }

    protected void tearDown() throws Exception {
        resultSet.verify();
    }

    /**
     * Ensures that an exception occurs if the UUID value is invalid.
     *
     * @throws SQLException
     */
    public void testGetSqlValueWithBadValue() throws SQLException {
        resultSet.addExpectedIndexedValues(new String[] { existingUuid.toString() + "Z" });

        try {
            uuidType.getSqlValue(1, resultSet);

            fail("Method should have throw an exception");
        } catch (TypeCastException e) {
            assertTrue(true);
        }
    }

    /**
     * Ensures that unmarshalling a UUID value works correctly.
     *
     * @throws SQLException
     * @throws TypeCastException
     */
    public void testGetValue() throws TypeCastException, SQLException {
        resultSet.addExpectedIndexedValues(new String[] { existingUuid.toString() });

        UUID result = (UUID) uuidType.getSqlValue(1, resultSet);

        assertEquals(existingUuid, result);

        resultSet.verify();
    }
}
