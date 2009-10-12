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
package org.dbunit.ext.oracle;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import oracle.jdbc.OraclePreparedStatement;
import oracle.jdbc.OracleResultSet;
import oracle.sql.OPAQUE;
import oracle.sql.OpaqueDescriptor;

import org.dbunit.dataset.datatype.BlobDataType;
import org.dbunit.dataset.datatype.TypeCastException;

/**
 * 
 * TODO UnitTests are completely missing
 * @author Phil Barr
 * @author Last changed by: $Author$
 * @version $Revision$ $Date$
 * @since 2.4.0
 */
public class OracleXMLTypeDataType extends BlobDataType
{

    public Object getSqlValue(int column, ResultSet resultSet) throws SQLException, TypeCastException
    {
        byte[] data = new byte[0];
        OracleResultSet oracleResultSet = (OracleResultSet) resultSet;
        OPAQUE opaque = oracleResultSet.getOPAQUE(column);
        if (opaque != null) 
        {
            data = opaque.getBytes();
        }

        // return the byte data (using typeCast to cast it to Base64 notation)
        return typeCast(data);
    }

    public void setSqlValue(Object value, int column, PreparedStatement statement) throws SQLException, TypeCastException
    {
        OraclePreparedStatement oraclePreparedStatement = (OraclePreparedStatement) statement;
        OpaqueDescriptor opaqueDescriptor = OpaqueDescriptor.createDescriptor("SYS.XMLTYPE", statement.getConnection());
        OPAQUE opaque = new OPAQUE(opaqueDescriptor, (byte[]) typeCast(value), statement.getConnection());
        oraclePreparedStatement.setOPAQUE(column, opaque);
    }
}
