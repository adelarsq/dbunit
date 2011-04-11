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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.UUID;

import org.dbunit.dataset.datatype.AbstractDataType;
import org.dbunit.dataset.datatype.TypeCastException;

/**
 * <code>UniqueIdentifierType</code> provides support for the "uniqueidentifier" column in Microsoft SQLServer
 * databases. It users the {@link UUID}
 *
 * @Author Darryl L. Pierce <dpierce@redhat.com>
 * @Since 02 February 2011
 * @version $Revision$
 */
public class UniqueIdentifierType extends AbstractDataType {
    static final String UNIQUE_IDENTIFIER_TYPE = "uniqueidentifier";

    public UniqueIdentifierType() {
        super(UNIQUE_IDENTIFIER_TYPE, Types.CHAR, UUID.class, false);
    }

    public Object typeCast(Object value) throws TypeCastException {
        return value.toString();
    }

    public Object getSqlValue(int column, ResultSet resultSet) throws SQLException, TypeCastException {
        String value = resultSet.getString(column);

        try {
            return value != null && !value.isEmpty() ? UUID.fromString(value) : null;
        } catch (NumberFormatException error) {
            throw new TypeCastException("Invalid UUID: " + value, error);
        }
    }

    public void setSqlValue(Object value, int column, PreparedStatement statement) throws SQLException,
            TypeCastException {
        if (value == null)
            statement.setObject(column, null);
        else
            statement.setObject(column, value.toString());
    }
}
