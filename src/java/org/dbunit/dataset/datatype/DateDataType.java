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

import org.dbunit.dataset.ITable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

/**
 * @author Manuel Laflamme
 * @version $Revision$
 * @since Feb 19, 2002
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
        if (value == null || value == ITable.NO_VALUE)
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
            String stringValue = (String)value;

            // Probably a Timestamp, try it just in case!
            if (stringValue.length() > 10)
            {
                try
                {
                    long time = java.sql.Timestamp.valueOf(stringValue).getTime();
                    return new java.sql.Date(time);
//                    return java.sql.Date.valueOf(new java.sql.Date(time).toString());
                }
                catch (IllegalArgumentException e)
                {
                    // Was not a Timestamp, let java.sql.Date handle this value
                }
            }

            try
            {
                return java.sql.Date.valueOf(stringValue);
            }
            catch (IllegalArgumentException e)
            {
                throw new TypeCastException(stringValue, e);
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





