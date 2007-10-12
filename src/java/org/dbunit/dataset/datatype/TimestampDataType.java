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

import java.sql.*;

/**
 * @author Manuel Laflamme
 * @version $Revision$
 * @since Feb 19, 2002
 */
public class TimestampDataType extends AbstractDataType
{

    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(TimestampDataType.class);

    TimestampDataType()
    {
        super("TIMESTAMP", Types.TIMESTAMP, Timestamp.class, false);
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

        if (value instanceof java.sql.Timestamp)
        {
            return value;
        }

        if (value instanceof java.util.Date)
        {
            java.util.Date date = (java.util.Date)value;
            return new java.sql.Timestamp(date.getTime());
        }

        if (value instanceof Long)
        {
            Long date = (Long)value;
            return new java.sql.Timestamp(date.longValue());
        }

        if (value instanceof String)
        {
            String stringValue = (String)value;

            // Probably a java.sql.Date, try it just in case!
            if (stringValue.length() == 10)
            {
                try
                {
                    long time = java.sql.Date.valueOf(stringValue).getTime();
                    return new java.sql.Timestamp(time);
                }
                catch (IllegalArgumentException e)
                {
                    logger.error("typeCast()", e);

                    // Was not a java.sql.Date, let Timestamp handle this value
                }
            }

            try
            {
                return java.sql.Timestamp.valueOf(stringValue);
            }
            catch (IllegalArgumentException e)
            {
                logger.error("typeCast()", e);

                throw new TypeCastException(value, this, e);
            }
        }

        throw new TypeCastException(value, this);
    }

    public boolean isDateTime()
    {
        logger.debug("isDateTime() - start");

        return true;
    }

    public Object getSqlValue(int column, ResultSet resultSet)
            throws SQLException, TypeCastException
    {
        logger.debug("getSqlValue(column=" + column + ", resultSet=" + resultSet + ") - start");

        Timestamp value = resultSet.getTimestamp(column);
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

        statement.setTimestamp(column, (java.sql.Timestamp)typeCast(value));
    }
}





