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

import java.net.URLEncoder;
import java.net.URLDecoder;
import java.sql.Blob;
import java.sql.SQLException;

import org.dbunit.util.Base64;

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
}





