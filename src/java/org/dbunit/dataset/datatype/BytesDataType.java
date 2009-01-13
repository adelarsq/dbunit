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

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.dbunit.dataset.ITable;
import org.dbunit.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Manuel Laflamme
 * @author Last changed by: $Author$
 * @version $Revision$ $Date$
 * @since 1.0 (Mar 20, 2002)
 */
public class BytesDataType extends AbstractDataType
{

    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(BytesDataType.class);

    private static final int MAX_URI_LENGTH = 256;

    BytesDataType(String name, int sqlType)
    {
        super(name, sqlType, byte[].class, false);
    }

    private byte[] toByteArray(InputStream in, int length) throws IOException
    {
        if (logger.isDebugEnabled())
		{
			logger.debug("toByteArray(in={}, length={}) - start", in, Integer.toString(length));
		}

        ByteArrayOutputStream out = new ByteArrayOutputStream(length);
        in = new BufferedInputStream(in);
        int i = in.read();
        while (i != -1)
        {
            out.write(i);
            i = in.read();
        }
        return out.toByteArray();
    }

    ////////////////////////////////////////////////////////////////////////////
    // DataType class

    /**
     * Casts the given value into a byte[] using different strategies. Note
     * that this might sometimes result in undesired behavior when character
     * data (Strings) are used.
     * 
     * @see org.dbunit.dataset.datatype.DataType#typeCast(java.lang.Object)
     */
    public Object typeCast(Object value) throws TypeCastException
    {
        logger.debug("typeCast(value={}) - start", value);

        if (value == null || value == ITable.NO_VALUE)
        {
            return null;
        }

        if (value instanceof byte[])
        {
            return value;
        }

        if (value instanceof String)
        {
            String stringValue = (String)value;

            // Assume not an uri if length greater than max uri length
            if (stringValue.length() == 0 || stringValue.length() > MAX_URI_LENGTH)
            {
            	logger.debug("Assuming given string to be Base64 and not a URI");
                return Base64.decode((String)value);
            }

            try
            {
            	logger.debug("Assuming given string to be a URI");
                try
                {
                    // Try value as URL
                    URL url = new URL(stringValue);
                    return toByteArray(url.openStream(), 0);
                }
                catch (MalformedURLException e1)
                {
                	logger.debug("Given string is not a valid URI - trying to resolve it as file...");
                    try
                    {
                        // Not an URL, try as file name
                        File file = new File(stringValue);
                        return toByteArray(new FileInputStream(file), (int)file.length());
                    }
                    catch (FileNotFoundException e2)
                    {
                        logger.info("Assuming given string to be Base64 and not a URI or File");
                        // Not a file name either
                        return Base64.decode(stringValue);
                    }
                }
            }
            catch (IOException e)
            {
                throw new TypeCastException(value, this, e);
            }
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
                throw new TypeCastException(value, this, e);
            }
        }

        if (value instanceof URL)
        {
            try
            {
                return toByteArray(((URL)value).openStream(), 0);
            }
            catch (IOException e)
            {
                throw new TypeCastException(value, this, e);
            }
        }

        if (value instanceof File)
        {
            try
            {
                File file = (File)value;
                return toByteArray(new FileInputStream(file), (int)file.length());
            }
            catch (IOException e)
            {
                throw new TypeCastException(value, this, e);
            }
        }

        throw new TypeCastException(value, this);
    }


    protected int compareNonNulls(Object value1, Object value2) throws TypeCastException
    {
        logger.debug("compareNonNulls(value1={}, value2={}) - start", value1, value2);

        try
        {
            byte[] value1cast = (byte[])typeCast(value1);
            byte[] value2cast = (byte[])typeCast(value2);
    
            return compare(value1cast, value2cast);
        }
        catch (ClassCastException e)
        {
            throw new TypeCastException(e);
        }
    }

    public int compare(byte[] v1, byte[] v2) throws TypeCastException
    {
        if (logger.isDebugEnabled())
		{
			logger.debug("compare(v1={}, v2={}) - start", v1, v2);
		}

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
    	if(logger.isDebugEnabled())
    		logger.debug("getSqlValue(column={}, resultSet={}) - start", new Integer(column), resultSet);

        byte[] value = resultSet.getBytes(column);
        if (value == null || resultSet.wasNull())
        {
            return null;
        }
        return value;
    }

    public void setSqlValue(Object value, int column, PreparedStatement statement)
            throws SQLException, TypeCastException
    {
    	if (logger.isDebugEnabled()) 
    	{
    		logger.debug("setSqlValue(value={}, column={}, statement={}) - start",
        		new Object[]{value, new Integer(column), statement} );
    	}

        super.setSqlValue(value, column, statement);
    }

}
