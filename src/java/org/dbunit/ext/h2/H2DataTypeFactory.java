/*
 *
 * The DbUnit Database Testing Framework
 * Copyright (C)2008, DbUnit.org
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
package org.dbunit.ext.h2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.dbunit.dataset.datatype.DataType;
import org.dbunit.dataset.datatype.DataTypeException;
import org.dbunit.dataset.datatype.DefaultDataTypeFactory;
import org.dbunit.ext.hsqldb.HsqldbDataTypeFactory;

/**
 * Specialized factory that recognizes H2 data types.
 * TODO: this class is pretty much the same as {@link HsqldbDataTypeFactory}, so they should derive from a common
 * superclass - see issue 1897620

 *
 * @author Felipe Leme
 */
public class H2DataTypeFactory extends DefaultDataTypeFactory
{

    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(H2DataTypeFactory.class);

    public DataType createDataType(int sqlType, String sqlTypeName) throws DataTypeException
    {
        logger.debug("createDataType(sqlType=" + sqlType + ", sqlTypeName=" + sqlTypeName + ") - start");

        if (sqlTypeName.equals("BOOLEAN"))
        {
            return DataType.BOOLEAN;
        }

        return super.createDataType(sqlType, sqlTypeName);
    }
}