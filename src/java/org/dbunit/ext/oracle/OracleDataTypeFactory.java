/*
 *
 * The DbUnit Database Testing Framework
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
package org.dbunit.ext.oracle;

import org.dbunit.dataset.datatype.DataType;
import org.dbunit.dataset.datatype.DataTypeException;
import org.dbunit.dataset.datatype.DefaultDataTypeFactory;

import java.sql.Types;

/**
 * Specialized factory that recognizes Oracle data types.

 * @author manuel.laflamme
 * @since Jul 17, 2003
 * @version $Revision$
 */
public class OracleDataTypeFactory extends DefaultDataTypeFactory
{
    public static final DataType ORACLE_CLOB = new OracleClobDataType();

    public DataType createDataType(int sqlType, String sqlTypeName) throws DataTypeException
    {
        if (sqlType == Types.OTHER)
        {
            // BLOB
            if ("BLOB".equals(sqlTypeName))
            {
                return DataType.BLOB;
            }

            // CLOB
            if ("CLOB".equals(sqlTypeName) || "NCLOB".equals(sqlTypeName))
            {
                return ORACLE_CLOB;
            }

            // NVARCHAR2
            if ("NVARCHAR2".equals(sqlTypeName))
            {
                return DataType.VARCHAR;
            }

            // TIMESTAMP
            if (sqlType == Types.DATE || sqlTypeName.startsWith("TIMESTAMP"))
            {
                return DataType.TIMESTAMP;
            }
        }

        return super.createDataType(sqlType, sqlTypeName);
    }
}
