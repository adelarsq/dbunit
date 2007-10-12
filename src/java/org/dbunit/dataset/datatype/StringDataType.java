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

import org.dbunit.dataset.ITable;
import org.dbunit.util.Base64;

import java.sql.*;

/**
 * @author Manuel Laflamme
 * @version $Revision$
 */
public class StringDataType extends AbstractDataType
{

    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(StringDataType.class);

    public StringDataType(String name, int sqlType)
    {
        super(name, sqlType, String.class, false);
    }

    ////////////////////////////////////////////////////////////////////////////
    // DataType class

    public Object typeCast(Object value) throws TypeCastException
    {
        logger.debug("typeCast(value=" + value + ") - start");

        if (value == null || value == ITable.NO_VALUE)
        {
            return null;
        }

        if (value instanceof String)
        {
            return value;
        }

        if (value instanceof java.sql.Date ||
                value instanceof java.sql.Time ||
                value instanceof java.sql.Timestamp)
        {
            return value.toString();
        }

        if (value instanceof Boolean)
        {
            return value.toString();
        }

        if (value instanceof Number)
        {
            try
            {
                return value.toString();
            }
            catch (java.lang.NumberFormatException e)
            {
                logger.error("typeCast()", e);

                throw new TypeCastException(value, this, e);
            }
        }

        if (value instanceof byte[])
        {
            return Base64.encodeBytes((byte[])value);
        }

        if (value instanceof Blob)
        {
            try
            {
                Blob blob = (Blob)value;
                byte[] blobValue = blob.getBytes(1, (int)blob.length());
                return typeCast(blobValue);
            }
            catch (SQLException e)
            {
                logger.error("typeCast()", e);

                throw new TypeCastException(value, this, e);
            }
        }

        if (value instanceof Clob)
        {
            try
            {
                Clob clobValue = (Clob)value;
                int length = (int)clobValue.length();
                if (length > 0)
                {
                    return clobValue.getSubString(1, length);
                }
                return "";
            }
            catch (SQLException e)
            {
                logger.error("typeCast()", e);

                throw new TypeCastException(value, this, e);
            }
        }

        throw new TypeCastException(value, this);
    }

    public Object getSqlValue(int column, ResultSet resultSet)
            throws SQLException, TypeCastException
    {
        logger.debug("getSqlValue(column=" + column + ", resultSet=" + resultSet + ") - start");

        String value = resultSet.getString(column);
        if (value == null || resultSet.wasNull())
        {
            return null;
        }
        return value;
    }

    public void setSqlValue(Object value, int column, PreparedStatement statement)
            throws SQLException, TypeCastException
    {
        logger.debug("setSqlValue(value=" + value + ", column=" + column + ", statement=" + statement + ") - start");

        statement.setString(column, asString(value));
    }
}









