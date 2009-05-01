/*
 *
 * The DbUnit Database Testing Framework
 * Copyright (C)2002-2009, DbUnit.org
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
package org.dbunit.ext.postgresql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import org.dbunit.dataset.datatype.AbstractDataType;
import org.dbunit.dataset.datatype.TypeCastException;

/**
 * Adapter to handle conversion between Postgresql
 * native UUID type and Strings.
 * 
 * @author Jarvis Cochrane (jarvis@cochrane.com.au)
 * @since 2.4.5 (Apr 27, 2009)
 */
public class UuidType
        extends AbstractDataType {

    public class UUID
            extends org.postgresql.util.PGobject {

        public static final long serialVersionUID = 668353936136517917L;

        public UUID(String s) throws java.sql.SQLException {
            super();
            this.setType("uuid");
            this.setValue(s);
        }

    }

    public UuidType() {
        super("uuid", Types.OTHER, String.class, false);
    }

    public Object getSqlValue(int column, ResultSet resultSet) throws SQLException, TypeCastException {
        return resultSet.getString(column);
    }

    public void setSqlValue(Object uuid, int column,
                            PreparedStatement statement) throws SQLException, TypeCastException {
        statement.setObject(column, new UUID(uuid.toString()));
    }

    public Object typeCast(Object arg0) throws TypeCastException {
        return arg0.toString();
    }

}
