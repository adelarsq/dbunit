/*
 * TimestampDataType.java   Feb 19, 2002
 *
 * The dbUnit database testing framework.
 * Copyright (C) 2002   Manuel Laflamme
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

/**
 * @author Manuel Laflamme
 * @version 1.0
 */
public class TimestampDataType extends DateDataType
{
    public TimestampDataType()
    {
    }

    /**
     *
     */
    public String getName()
    {
        return "timestamp";
    }

    /**
     *
     */
    public Object typeCast(Object value) throws TypeCastException
    {
        if (value == null)
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
            try
            {
                return java.sql.Timestamp.valueOf((String)value);
            }
            catch (IllegalArgumentException e)
            {
                throw new TypeCastException((String)value, e);
            }
        }

        throw new TypeCastException(
                "Cannot typecast " + value + " to 'timestamp'");
    }

    /**
     *
     */
    public Class getTypeClass()
    {
        return java.sql.Timestamp.class;
    }

}
