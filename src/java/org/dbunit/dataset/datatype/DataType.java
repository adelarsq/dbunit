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


/**
 * @author Manuel Laflamme
 * @version $Revision$
 */
public abstract class DataType
{
    public static final DataType UNKNOWN = new UnkownDataType();

    public static final DataType CHAR = new StringDataType(
            "CHAR", Types.CHAR);
    public static final DataType VARCHAR = new StringDataType(
            "VARCHAR", Types.VARCHAR);
    public static final DataType LONGVARCHAR = new StringDataType(
            "LONGVARCHAR", Types.LONGVARCHAR);
    public static final DataType CLOB = new StringDataType(
            "CLOB", Types.CLOB);

    public static final DataType NUMERIC = new NumberDataType(
            "NUMERIC", Types.NUMERIC);
    public static final DataType DECIMAL = new NumberDataType(
            "DECIMAL", Types.DECIMAL);

    public static final DataType BOOLEAN = new BooleanDataType();

    public static final DataType TINYINT = new IntegerDataType(
            "TINYINT", Types.TINYINT);
    public static final DataType SMALLINT = new IntegerDataType(
            "SMALLINT", Types.SMALLINT);
    public static final DataType INTEGER = new IntegerDataType(
            "INTEGER", Types.INTEGER);

    public static final DataType BIGINT = new LongDataType();

    public static final DataType REAL = new FloatDataType();

    public static final DataType FLOAT = new DoubleDataType(
            "FLOAT", Types.FLOAT);
    public static final DataType DOUBLE = new DoubleDataType(
            "DOUBLE", Types.DOUBLE);

    public static final DataType DATE = new DateDataType();
    public static final DataType TIME = new TimeDataType();
    public static final DataType TIMESTAMP = new TimestampDataType();

    public static final DataType BINARY = new BytesDataType(
            "BINARY", Types.BINARY);
    public static final DataType VARBINARY = new BytesDataType(
            "VARBINARY", Types.VARBINARY);
    public static final DataType LONGVARBINARY = new BytesDataType(
            "LONGVARBINARY", Types.LONGVARBINARY);
    public static final DataType BLOB = new BytesDataType(
            "BLOB", Types.BLOB);

    private static final DataType[] TYPES = {
        VARCHAR, CHAR, LONGVARCHAR, CLOB, NUMERIC, DECIMAL, BOOLEAN, INTEGER,
        TINYINT, SMALLINT, BIGINT, REAL, DOUBLE, FLOAT, DATE, TIME, TIMESTAMP,
        VARBINARY, BINARY, LONGVARBINARY, BLOB,
    };

    /**
     * Returns the specified value typecasted to this <code>DataType</code>
     */
    public abstract Object typeCast(Object value) throws TypeCastException;

    /**
     * Returns the coresponding {@link java.sql.Types}.
     */
    public abstract int getSqlType();

    /**
     * Returns the runtime class of the typecast result.
     */
    public abstract Class getTypeClass();

    /**
     * Returns <code>true</code> if this <code>DataType</code> represents a
     * number.
     */
    public abstract boolean isNumber();

    /**
     * Typecast the specified value to string.
     */
    public static String asString(Object value) throws TypeCastException
    {
        return (String)DataType.VARCHAR.typeCast(value);
    }

    /**
     * Returns the <code>DataType</code> corresponding to the specified Sql
     * type. See {@link java.sql.Types}.
     *
     */
    public static DataType forSqlType(int sqlType) throws DataTypeException
    {
        for (int i = 0; i < TYPES.length; i++)
        {
            if (sqlType == TYPES[i].getSqlType())
            {
                return TYPES[i];
            }
        }

        return UNKNOWN;
    }

    /**
     * Returns the <code>DataType</code> corresponding to the specified Sql
     * type name.
     *
     */
    public static DataType forSqlTypeName(String sqlTypeName) throws DataTypeException
    {
        for (int i = 0; i < TYPES.length; i++)
        {
            if (sqlTypeName.equals(TYPES[i].toString()))
            {
                return TYPES[i];
            }
        }

        return UNKNOWN;
    }

    /**
     * Returns the <code>DataType</code> corresponding to the specified value
     * runtime class. This method returns <code>DataType.UNKNOWN</code>
     * if the value is <code>null</code> or runtime class not recognized.
     */
    public static DataType forObject(Object value)
    {
        if (value == null)
        {
            return UNKNOWN;
        }

        for (int i = 0; i < TYPES.length; i++)
        {
            Class typeClass = TYPES[i].getTypeClass();
            if (typeClass.isInstance(value))
            {
                return TYPES[i];
            }
        }

        return UNKNOWN;
    }
}









