/*
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Manuel Laflamme
 * @version 1.0
 */
public class DateDataType extends DataType
{
    protected static final DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss.sss z");

    public DateDataType()
    {
    }

    /**
     *
     */
    public String getName()
    {
        return "date";
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

        if (value instanceof Date)
        {
            return ((Date)value);
        }

        if (value instanceof String)
        {
            try
            {
                return dateFormat.parse((String)value);
            }
            catch (java.text.ParseException e)
            {
                throw new TypeCastException(e);
            }
        }
        throw new TypeCastException(
                "Cannot typecast " + value.getClass().getName() + " to 'date'");
    }

    /**
     *
     */
    public Class getTypeClass()
    {
        return Date.class;
    }

    public boolean isNumber()
    {
        return false;
    }

}
