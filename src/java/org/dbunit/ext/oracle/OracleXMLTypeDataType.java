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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.dbunit.dataset.datatype.BlobDataType;
import org.dbunit.dataset.datatype.TypeCastException;

/**
 * 
 * TODO UnitTests are completely missing
 * @author Phil Barr
 * @author Last changed by: $Author$
 * @version $Revision$ $Date$
 * @since 2.4.0
 */
public class OracleXMLTypeDataType extends BlobDataType
{

    public Object getSqlValue(int column, ResultSet resultSet) throws SQLException, TypeCastException
    {
        Object data = new byte[0];
        try
        {
            ClassLoader classLoader = resultSet.getClass().getClassLoader();
            // Classes
            Class cOracleResultSet = super.loadClass("oracle.jdbc.OracleResultSet", classLoader);
            Class cOPAQUE = super.loadClass("oracle.sql.OPAQUE", classLoader);

            // Methods
            Method mGetOPAQUE = cOracleResultSet.getMethod("getOPAQUE", new Class[]{ Integer.TYPE });
            Method mGetBytes = cOPAQUE.getMethod("getBytes", null);

            // cast resultSet to an OracleResultSet
//            Object ors = cOracleResultSet.cast(resultSet); // TODO activate this with java 1.5
            Object ors = resultSet;

            // call ors.getOPAQUE(column)
            Object opaque = mGetOPAQUE.invoke(ors, new Object[]{ new Integer(column) });

            // if there is any data for this column call opaque.getBytes() to get it.
            if (opaque != null)
            {
                data = mGetBytes.invoke(opaque, null);
            }
        }
        catch (SecurityException e)
        {
            throw new TypeCastException(data, this, e);
        }
        catch (IllegalArgumentException e)
        {
            throw new TypeCastException(data, this, e);
        }
        catch (ClassNotFoundException e)
        {
            throw new TypeCastException(data, this, e);
        }
        catch (NoSuchMethodException e)
        {
            throw new TypeCastException(data, this, e);
        }
        catch (IllegalAccessException e)
        {
            throw new TypeCastException(data, this, e);
        }
        catch (InvocationTargetException e)
        {
            throw new TypeCastException(data, this, e);
        }

        // return the byte data (using typeCast to cast it to Base64 notation)
        return typeCast((byte[]) data);
    }

    public void setSqlValue(Object value, int column, PreparedStatement statement) throws SQLException, TypeCastException
    {
        try
        {
            ClassLoader classLoader = statement.getClass().getClassLoader();
            // Classes
            Class cOraclePreparedStatement = super.loadClass("oracle.jdbc.OraclePreparedStatement", classLoader);
            Class cOpaqueDescriptor = super.loadClass("oracle.sql.OpaqueDescriptor", classLoader);
            Class cOPAQUE = super.loadClass("oracle.sql.OPAQUE", classLoader);

            // Methods (inc. the constructor for the class OPAQUE)
            Constructor mOPAQUEConstructor = cOPAQUE.getConstructor(new Class[]{ cOpaqueDescriptor, new byte[0].getClass(), Connection.class });
            Method mCreateDescriptor = cOpaqueDescriptor.getMethod("createDescriptor", new Class[]{ String.class, Connection.class });
            Method mSetOPAQUE = cOraclePreparedStatement.getMethod("setOPAQUE", new Class[]{ Integer.TYPE, cOPAQUE });

            // Cast statement to OraclePreparedStatement
//            Object oraclePreparedStatement = cOraclePreparedStatement.cast(statement); // TODO activate this with java 1.5
            Object oraclePreparedStatement = statement;

            // Create the OpaqueDescriptor for type SYS.XMLTYPE
            Object opaqueDescriptor = mCreateDescriptor.invoke(null, new Object[]{ "SYS.XMLTYPE", statement.getConnection() });

            // Create the OPAQUE object
            Object opaque = mOPAQUEConstructor.newInstance(new Object[]{ opaqueDescriptor, typeCast(value), statement.getConnection() });

            // call oraclePreparedStatement.setOPAQUE(column, opaque)
            mSetOPAQUE.invoke(oraclePreparedStatement, new Object[]{ new Integer(column), opaque });
        }
        catch (TypeCastException e)
        {
            throw new TypeCastException(value, this, e);
        }
        catch (SecurityException e)
        {
            throw new TypeCastException(value, this, e);
        }
        catch (IllegalArgumentException e)
        {
            throw new TypeCastException(value, this, e);
        }
        catch (ClassNotFoundException e)
        {
            throw new TypeCastException(value, this, e);
        }
        catch (NoSuchMethodException e)
        {
            throw new TypeCastException(value, this, e);
        }
        catch (IllegalAccessException e)
        {
            throw new TypeCastException(value, this, e);
        }
        catch (InvocationTargetException e)
        {
            throw new TypeCastException(value, this, e);
        }
        catch (InstantiationException e)
        {
            throw new TypeCastException(value, this, e);
        }
    }
}
