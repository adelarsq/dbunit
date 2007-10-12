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
package org.dbunit.ext.oracle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.dbunit.dataset.datatype.BlobDataType;
import org.dbunit.dataset.datatype.TypeCastException;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Manuel Laflamme
 * @version $Revision$
 * @since Feb 2, 2004
 */
public class OracleBlobDataType extends BlobDataType
{

    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(OracleBlobDataType.class);

    private static final Integer DURATION_SESSION = new Integer(1);
//    private static final Integer DURATION_CALL = new Integer(2);
//    private static final Integer MODE_READONLY = new Integer(0);
    private static final Integer MODE_READWRITE = new Integer(1);

    public Object getSqlValue(int column, ResultSet resultSet)
            throws SQLException, TypeCastException
    {
        logger.debug("getSqlValue(column=" + column + ", resultSet=" + resultSet + ") - start");

        return typeCast(resultSet.getBlob(column));
    }

    public void setSqlValue(Object value, int column, PreparedStatement statement)
            throws SQLException, TypeCastException
    {
        logger.debug("setSqlValue(value=" + value + ", column=" + column + ", statement=" + statement + ") - start");

        statement.setObject(column, getBlob(value, statement.getConnection()));
    }

    private Object getBlob(Object value, Connection connection)
            throws TypeCastException
    {
        logger.debug("getBlob(value=" + value + ", connection=" + connection + ") - start");

        Object tempBlob = null;
        try
        {
            Class aBlobClass = Class.forName("oracle.sql.BLOB");

            // Create new temporary Blob
            Method createTemporaryMethod = aBlobClass.getMethod("createTemporary",
                    new Class[]{Connection.class, Boolean.TYPE, Integer.TYPE});
            tempBlob = createTemporaryMethod.invoke(null,
                    new Object[]{connection, Boolean.TRUE, DURATION_SESSION});

            // Open the temporary Blob in readwrite mode to enable writing
            Method openMethod = aBlobClass.getMethod("open", new Class[]{Integer.TYPE});
            openMethod.invoke(tempBlob, new Object[]{MODE_READWRITE});

            // Get the output stream to write
            Method getOutputStreamMethod = tempBlob.getClass().getMethod(
                    "getBinaryOutputStream", new Class[0]);
            OutputStream tempBlobOutputStream = (OutputStream)getOutputStreamMethod.invoke(
                    tempBlob, new Object[0]);

            // Write the data into the temporary BLOB
            tempBlobOutputStream.write((byte[])typeCast(value));

            // Flush and close the stream
            tempBlobOutputStream.flush();
            tempBlobOutputStream.close();

            // Close the temporary Blob
            Method closeMethod = tempBlob.getClass().getMethod(
                    "close", new Class[0]);
            closeMethod.invoke(tempBlob, new Object[0]);
        }
        catch (IllegalAccessException e)
        {
            logger.error("getBlob()", e);

            freeTemporaryBlob(tempBlob);
            throw new TypeCastException(value, this, e);
        }
        catch (NoSuchMethodException e)
        {
            logger.error("getBlob()", e);

            freeTemporaryBlob(tempBlob);
            throw new TypeCastException(value, this, e);
        }
        catch (IOException e)
        {
            logger.error("getBlob()", e);

            freeTemporaryBlob(tempBlob);
            throw new TypeCastException(value, this, e);
        }
        catch (InvocationTargetException e)
        {
            logger.error("getBlob()", e);

            freeTemporaryBlob(tempBlob);
            throw new TypeCastException(value, this, e);
        }
        catch (ClassNotFoundException e)
        {
            logger.error("getBlob()", e);

            freeTemporaryBlob(tempBlob);
            throw new TypeCastException(value, this, e);
        }

        return tempBlob;
    }


    private void freeTemporaryBlob(Object tempBlob) throws TypeCastException
    {
        logger.debug("freeTemporaryBlob(tempBlob=" + tempBlob + ") - start");

        if (tempBlob == null)
        {
            return;
        }

        try
        {
            Method freeTemporaryMethod = tempBlob.getClass().getMethod("freeTemporary", new Class[0]);
            freeTemporaryMethod.invoke(tempBlob, new Object[0]);
        }
        catch (NoSuchMethodException e)
        {
            logger.error("freeTemporaryBlob()", e);

            throw new TypeCastException("Error freeing Oracle BLOB", e);
        }
        catch (IllegalAccessException e)
        {
            logger.error("freeTemporaryBlob()", e);

            throw new TypeCastException("Error freeing Oracle BLOB", e);
        }
        catch (InvocationTargetException e)
        {
            logger.error("freeTemporaryBlob()", e);

            throw new TypeCastException("Error freeing Oracle BLOB", e.getTargetException());
        }
    }

}
