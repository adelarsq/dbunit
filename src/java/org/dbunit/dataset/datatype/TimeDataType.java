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
public class TimeDataType extends AbstractDataType
{

    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(TimeDataType.class);

    TimeDataType()
    {
        super("TIME", Types.TIME, Time.class, false);
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

        if (value instanceof java.sql.Time)
        {
            return value;
        }

        if (value instanceof java.util.Date)
        {
            java.util.Date date = (java.util.Date)value;
            return new java.sql.Time(date.getTime());
        }

        if (value instanceof Long)
        {
            Long date = (Long)value;
            return new java.sql.Time(date.longValue());
        }

        if (value instanceof String)
        {
            try
            {
                return java.sql.Time.valueOf((String)value);
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

        Time value = resultSet.getTime(column);
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

        statement.setTime(column, (java.sql.Time)typeCast(value));
    }
}





