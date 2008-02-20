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
package org.dbunit.ext.mysql;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.dbunit.dataset.datatype.DataType;
import org.dbunit.dataset.datatype.DataTypeException;
import org.dbunit.dataset.datatype.DefaultDataTypeFactory;

import java.sql.Types;

/**
 * Specialized factory that recognizes MySql data types.

 * @author manuel.laflamme
 * @since Sep 3, 2003
 * @version $Revision$
 */
public class MySqlDataTypeFactory extends DefaultDataTypeFactory
{

    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(MySqlDataTypeFactory.class);

    public DataType createDataType(int sqlType, String sqlTypeName) throws DataTypeException
    {
        logger.debug("createDataType(sqlType=" + sqlType + ", sqlTypeName=" + sqlTypeName + ") - start");

        if (sqlType == Types.OTHER)
        {
            // CLOB
            if ("longtext".equals(sqlTypeName))
            {
                return DataType.CLOB;
            }
            // MySQL 5.0 Boolean
            else if("bit".equals(sqlTypeName))
            {
                return DataType.BOOLEAN; 
            }
            else if("point".equals(sqlTypeName))
            {
                return DataType.BINARY; 
            }
        }

        return super.createDataType(sqlType, sqlTypeName);
    }
}
