/*
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
public class BooleanDataType extends AbstractDataType
{
    BooleanDataType()
    {
        super("BIT", Types.BIT, Boolean.class, false);
    }

    ////////////////////////////////////////////////////////////////////////////
    // DataType class

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
            {
                return Boolean.valueOf(string);
            }
            else
            {
                return typeCast(DataType.INTEGER.typeCast(string));
            }
        }

        throw new TypeCastException(value.toString());
    }

    public int compare(Object o1, Object o2) throws TypeCastException
    {
        Boolean value1 = (Boolean)typeCast(o1);
        Boolean value2 = (Boolean)typeCast(o2);

        if (value1 == null && value2 == null)
        {
            return 0;
        }

        if (value1 == null && value2 != null)
        {
            return -1;
        }

        if (value1 != null && value2 == null)
        {
            return 1;
        }

        if (value1.equals(value2))
        {
            return 0;
        }

        if (value1.equals(Boolean.FALSE))
        {
            return -1;
        }

        return 1;
    }

    public Object getSqlValue(int column, ResultSet resultSet)
            throws SQLException, TypeCastException
    {
        boolean value = resultSet.getBoolean(column);
        if (resultSet.wasNull())
        {
            return null;
        }
        return value ? Boolean.TRUE : Boolean.FALSE;
    }

    public void setSqlValue(Object value, int column, PreparedStatement statement)
            throws SQLException, TypeCastException
    {
        statement.setBoolean(column, ((Boolean)typeCast(value)).booleanValue());
    }

}





