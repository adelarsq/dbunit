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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.dbunit.dataset.datatype.DataType;
import org.dbunit.dataset.datatype.DataTypeException;
import org.dbunit.dataset.datatype.DefaultDataTypeFactory;
import org.dbunit.dataset.datatype.BinaryStreamDataType;

import java.sql.Types;

/**
 * Specialized factory that recognizes Oracle data types.
 * @author manuel.laflamme
 * @since Jul 17, 2003
 * @version $Revision$
 */
public class OracleDataTypeFactory extends DefaultDataTypeFactory
{

    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(OracleDataTypeFactory.class);

    public static final DataType ORACLE_BLOB = new OracleBlobDataType();
    public static final DataType ORACLE_CLOB = new OracleClobDataType();
    public static final DataType ORACLE_NCLOB = new OracleNClobDataType();
    public static final DataType LONG_RAW = new BinaryStreamDataType(
            "LONG RAW", Types.LONGVARBINARY);

    public DataType createDataType(int sqlType, String sqlTypeName) throws DataTypeException
    {
        logger.debug("createDataType(sqlType=" + sqlType + ", sqlTypeName=" + sqlTypeName + ") - start");

        // Map Oracle DATE to TIMESTAMP
        if (sqlType == Types.DATE)
        {
            return DataType.TIMESTAMP;
        }

        // TIMESTAMP
        if (sqlTypeName.startsWith("TIMESTAMP"))
        {
            return DataType.TIMESTAMP;
        }

        // BLOB
        if ("BLOB".equals(sqlTypeName))
        {
            return ORACLE_BLOB;
        }

        // CLOB
        if ("CLOB".equals(sqlTypeName))
        {
            return ORACLE_CLOB;
        }
        
        // NCLOB
        if  ("NCLOB".equals(sqlTypeName))
        {
            return ORACLE_NCLOB;
        }

        // NVARCHAR2
        if ("NVARCHAR2".equals(sqlTypeName))
        {
            return DataType.VARCHAR;
        }

		// NCHAR
        if (sqlTypeName.startsWith("NCHAR"))
        {
            return DataType.CHAR;
        }

        // FLOAT
        if ("FLOAT".equals(sqlTypeName))
        {
            return DataType.FLOAT;
        }

        // LONG RAW
        if (LONG_RAW.toString().equals(sqlTypeName))
        {
            return LONG_RAW;
        }

        return super.createDataType(sqlType, sqlTypeName);
    }
}
