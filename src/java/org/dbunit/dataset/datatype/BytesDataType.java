/*
 * BytesDataType.java   Mar 20, 2002
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

import org.dbunit.util.Base64;

import java.sql.Blob;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.PreparedStatement;

/**
 * @author Manuel Laflamme
 * @version $Revision$
 */
public class BytesDataType extends AbstractDataType
{
    BytesDataType(String name, int sqlType)
    {
        super(name, sqlType, byte[].class, false);
    }

    ////////////////////////////////////////////////////////////////////////////
    // DataType class

    public Object typeCast(Object value) throws TypeCastException
    {
        if (value == null)
        {
            return value;
        }

        if (value instanceof byte[])
        {
            return value;
        }

        if (value instanceof String)
        {
            return Base64.decode((String)value);
        }


        if (value instanceof Blob)
        {
            try
            {
                Blob blobValue = (Blob)value;
                return blobValue.getBytes(1, (int)blobValue.length());
            }
            catch (SQLException e)
            {
                throw new TypeCastException(e);
            }
        }

        throw new TypeCastException(value.toString());
    }

    public int compare(Object o1, Object o2) throws TypeCastException
    {
        try
        {
            byte[] value1 = (byte[])typeCast(o1);
            byte[] value2 = (byte[])typeCast(o2);

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

            return compare(value1, value2);
        }
        catch (ClassCastException e)
        {
            throw new TypeCastException(e);
        }
    }

    public int compare(byte[] v1, byte[] v2) throws TypeCastException
    {
        int len1 = v1.length;
        int len2 = v2.length;
        int n = Math.min(len1, len2);
        int i = 0;
        int j = 0;

        if (i == j)
        {
            int k = i;
            int lim = n + i;
            while (k < lim)
            {
                byte c1 = v1[k];
                byte c2 = v2[k];
                if (c1 != c2)
                {
                    return c1 - c2;
                }
                k++;
            }
        }
        else
        {
            while (n-- != 0)
            {
                byte c1 = v1[i++];
                byte c2 = v2[j++];
                if (c1 != c2)
                {
                    return c1 - c2;
                }
            }
        }
        return len1 - len2;
    }

    public Object getSqlValue(int column, ResultSet resultSet)
            throws SQLException, TypeCastException
    {
        return resultSet.getBytes(column);
    }

    public void setSqlValue(Object value, int column, PreparedStatement statement)
            throws SQLException, TypeCastException
    {
        // Special BLOB handling
        if (this == DataType.BLOB)
        {
            statement.setObject(column, typeCast(value),
                    DataType.LONGVARBINARY.getSqlType());
            return;
        }

        super.setSqlValue(value, column, statement);
    }

}






