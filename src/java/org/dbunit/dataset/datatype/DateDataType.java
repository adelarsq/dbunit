/*
 * DateDataType.java   Feb 19, 2002
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

package org.dbunit.dataset.datatype;

import java.sql.Types;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;

/**
 * @author Manuel Laflamme
 * @version $Revision$
 */
public class DateDataType extends AbstractDataType
{
    DateDataType()
    {
        super("DATE", Types.DATE, java.sql.Date.class, false);
    }

    ////////////////////////////////////////////////////////////////////////////
    // DataType class

    public Object typeCast(Object value) throws TypeCastException
    {
        if (value == null)
        {
            return null;
        }

        if (value instanceof java.sql.Date)
        {
            return value;
        }

        if (value instanceof java.util.Date)
        {
            java.util.Date date = (java.util.Date)value;
            return new java.sql.Date(date.getTime());
        }

        if (value instanceof Long)
        {
            Long date = (Long)value;
            return new java.sql.Date(date.longValue());
        }

        if (value instanceof String)
        {
            try
            {
                return java.sql.Date.valueOf((String)value);
            }
            catch (IllegalArgumentException e)
            {
                throw new TypeCastException((String)value, e);
            }
        }

        throw new TypeCastException(value.toString());
    }

    public Object getSqlValue(int column, ResultSet resultSet)
            throws SQLException, TypeCastException
    {
        return resultSet.getDate(column);
    }

    public void setSqlValue(Object value, int column, PreparedStatement statement)
            throws SQLException, TypeCastException
    {
        statement.setDate(column, (java.sql.Date)typeCast(value));
    }
}





