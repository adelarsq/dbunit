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
public class BooleanDataType extends DataType
{
    public BooleanDataType()
    {
    }

    /**
     *
     */
    public String getName()
    {
        return "boolean";
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

        if (value instanceof Boolean)
        {
            return value;
        }

        if (value instanceof Number)
        {
            Number number = (Number)value;
            if (number.intValue() == 0)
                return Boolean.FALSE;
            else
                return Boolean.TRUE;
        }

        if (value instanceof String)
        {
            String string = (String)value;

            if (string.equalsIgnoreCase("true") || string.equalsIgnoreCase("false"))
                return Boolean.valueOf(string);
            else
            {
                try
                {
                    return typeCast(DataType.INTEGER.typeCast(string));
                }
                catch (TypeCastException e)
                {
                    throw new TypeCastException(e);
                }
            }
        }

        throw new TypeCastException("Cannot typecast '"
                + value.getClass().getName() + "' to 'Boolean'");
    }

    /**
     *
     */
    public Class getTypeClass()
    {
        return Boolean.class;
    }

    public boolean isNumber()
    {
        return false;
    }

}
