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

import org.dbunit.dataset.datatype.ClobDataType;
import org.dbunit.dataset.datatype.TypeCastException;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Manuel Laflamme
 * @version $Revision$
 * @since Jan 12, 2004
 */
public class OracleClobDataType extends ClobDataType
{
    private static final Integer DURATION_SESSION = new Integer(1);
//    private static final Integer DURATION_CALL = new Integer(2);
//    private static final Integer MODE_READONLY = new Integer(0);
    private static final Integer MODE_READWRITE = new Integer(1);

    public Object getSqlValue(int column, ResultSet resultSet)
            throws SQLException, TypeCastException
    {
        return typeCast(resultSet.getClob(column));
    }

    public void setSqlValue(Object value, int column, PreparedStatement statement)
            throws SQLException, TypeCastException
    {
        statement.setObject(column, getClob(value, statement.getConnection()));
    }

    private Object getClob(Object value, Connection connection)
            throws TypeCastException
    {
        Object tempClob = null;
        try
        {
            Class aClobClass = Class.forName("oracle.sql.CLOB");

            // Create new temporary CLOB
            Method createTemporaryMethod = aClobClass.getMethod("createTemporary",
                    new Class[]{Connection.class, Boolean.TYPE, Integer.TYPE});
            tempClob = createTemporaryMethod.invoke(null,
                    new Object[]{connection, Boolean.TRUE, DURATION_SESSION});

            // Open the temporary CLOB in readwrite mode to enable writing
            Method openMethod = aClobClass.getMethod("open", new Class[]{Integer.TYPE});
            openMethod.invoke(tempClob, new Object[]{MODE_READWRITE});

            // Get the output stream to write
            Method getCharacterOutputStreamMethod = tempClob.getClass().getMethod(
                    "getCharacterOutputStream", new Class[0]);
            Writer tempClobWriter = (Writer)getCharacterOutputStreamMethod.invoke(
                    tempClob, new Object[0]);

            // Write the data into the temporary CLOB
            tempClobWriter.write((String)typeCast(value));

            // Flush and close the stream
            tempClobWriter.flush();
            tempClobWriter.close();

            // Close the temporary CLOB
            Method closeMethod = tempClob.getClass().getMethod(
                    "close", new Class[0]);
            closeMethod.invoke(tempClob, new Object[0]);
        }
        catch (IllegalAccessException e)
        {
            freeTemporaryClob(tempClob);
            throw new TypeCastException(value, this, e);
        }
        catch (NoSuchMethodException e)
        {
            freeTemporaryClob(tempClob);
            throw new TypeCastException(value, this, e);
        }
        catch (IOException e)
        {
            freeTemporaryClob(tempClob);
            throw new TypeCastException(value, this, e);
        }
        catch (InvocationTargetException e)
        {
            freeTemporaryClob(tempClob);
            throw new TypeCastException(value, this, e.getTargetException());
        }
        catch (ClassNotFoundException e)
        {
            freeTemporaryClob(tempClob);
            throw new TypeCastException(value, this, e);
        }

        return tempClob;
    }


    private void freeTemporaryClob(Object tempClob) throws TypeCastException
    {
        if (tempClob == null)
        {
            return;
        }

        try
        {
            Method freeTemporaryMethod = tempClob.getClass().getMethod("freeTemporary", new Class[0]);
            freeTemporaryMethod.invoke(tempClob, new Object[0]);
        }
        catch (NoSuchMethodException e)
        {
            throw new TypeCastException("Error freeing Oracle CLOB", e);
        }
        catch (IllegalAccessException e)
        {
            throw new TypeCastException("Error freeing Oracle CLOB", e);
        }
        catch (InvocationTargetException e)
        {
            throw new TypeCastException("Error freeing Oracle CLOB", e.getTargetException());
        }
    }

}
