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

/**
 * @author Manuel Laflamme
 * @version 1.0
 */
public class ByteDataType extends NumberDataType
{

    public ByteDataType()
    {
    }

    /////////////////////////////////////////////////////////////////////////////
    //  DataType class

    /**
     *
     */
    public Class getTypeClass()
    {
        return Byte.class;
    }

    /**
     *
     */
    public String getName()
    {
        return "byte";
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

        if (value instanceof Number)
        {
            return new Byte(((Number)value).byteValue());
        }

        try
        {
            return typeCast(Double.valueOf(value.toString()));
        }
        catch (java.lang.NumberFormatException e)
        {
            throw new TypeCastException(e);
        }
    }
}
