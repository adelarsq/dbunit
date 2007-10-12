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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Types;

/**
 * Generic factory that handle standard JDBC types.
 *
 * @author Manuel Laflamme
 * @since May 17, 2003
 * @version $Revision$
 */
public class DefaultDataTypeFactory implements IDataTypeFactory
{

    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(DefaultDataTypeFactory.class);

    public DataType createDataType(int sqlType, String sqlTypeName) throws DataTypeException
    {
        logger.debug("createDataType(sqlType=" + sqlType + ", sqlTypeName=" + sqlTypeName + ") - start");

        DataType dataType = DataType.UNKNOWN;
        if (sqlType != Types.OTHER)
        {
            dataType = DataType.forSqlType(sqlType);
        }
        else
        {
            // Necessary for compatibility with DbUnit 1.5 and older
            // BLOB
            if ("BLOB".equals(sqlTypeName))
            {
                return DataType.BLOB;
            }

            // CLOB
            if ("CLOB".equals(sqlTypeName))
            {
                return DataType.CLOB;
            }
        }
        return dataType;
    }
}
