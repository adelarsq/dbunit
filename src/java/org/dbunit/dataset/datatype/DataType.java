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

import java.sql.Types;


/**
 * @author Manuel Laflamme
 * @version 1.0
 * @since 1.0
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

    /**
     * Returns the coresponding {@link java.sql.Types}.
     */
    public abstract int getSqlType();

    /**
     * Returns the specified value typecasted to this <code>DataType</code>
     */
    public abstract Object typeCast(Object value) throws TypeCastException;

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
     * Returns the <code>DataType</code> corresponding to the specified Sql
     * type. See {@link java.sql.Types}.
     *
     */
    public static DataType forSqlType(int sqlType) throws DataTypeException
    {
        switch (sqlType)
        {
            case Types.CHAR:
                return CHAR;

            case Types.VARCHAR:
                return VARCHAR;

            case Types.LONGVARCHAR:
                return LONGVARCHAR;

            case Types.NUMERIC:
                return NUMERIC;

            case Types.DECIMAL:
                return DECIMAL;

            case Types.BIT:
                return BOOLEAN;

            case Types.TINYINT:
                return TINYINT;

            case Types.SMALLINT:
                return SMALLINT;

            case Types.INTEGER:
                return INTEGER;

            case Types.BIGINT:
                return BIGINT;

            case Types.REAL:
                return REAL;

            case Types.FLOAT:
                return FLOAT;

            case Types.DOUBLE:
                return DOUBLE;

            case Types.DATE:
                return DataType.DATE;

            case Types.TIME:
                return DataType.TIME;

            case Types.TIMESTAMP:
                return DataType.TIMESTAMP;

//            case Types.LONGVARBINARY:
//            case Types.VARBINARY:
//            case Types.BINARY:
//                return DataType.BYTES;
        }

        // todo -> should returns UNKNOWN instead
        throw new DataTypeException("Unsuported sql data type" + sqlType);
//        return UNKNOWN;
    }

//    /**
//     * Returns the <code>DataType</code> corresponding to the specified data
//     * type name.
//     */
//    public static DataType forName(String type) throws IllegalArgumentException
//    {
//        if (type == null)
//            return UNKNOWN;
//        if (type.equals("byte"))
//            return BYTE;
//        if (type.equals("short"))
//            return SHORT;
//        if (type.equals("integer"))
//            return INTEGER;
//        if (type.equals("long"))
//            return LONG;
//        if (type.equals("float"))
//            return FLOAT;
//        if (type.equals("double"))
//            return DOUBLE;
//        if (type.equals("boolean"))
//            return BOOLEAN;
//        if (type.equals("string"))
//            return STRING;
//        if (type.equals("date"))
//            return DATE;
//        if (type.equals("object"))
//            return UNKNOWN;
//
//        throw new IllegalArgumentException(type);
//    }
//
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

//        if (value instanceof java.lang.Short ||
//                value.getClass() == Short.class)
//        {
//            return SHORT;
//        }
//
//        if (value instanceof java.lang.Byte ||
//                value.getClass() == Byte.class)
//        {
//            return BYTE;
//        }

        if (value instanceof java.lang.Integer ||
                value.getClass() == Integer.class)
        {
            return INTEGER;
        }

        if (value instanceof java.lang.Long ||
                value.getClass() == Long.class)
        {
            return BIGINT;
        }

        if (value instanceof java.lang.Float ||
                value.getClass() == Float.class)
        {
            return REAL;
        }

        if (value instanceof java.lang.Double ||
                value.getClass() == Double.class)
        {
            return DOUBLE;
        }

        if (value instanceof java.lang.Number)
        {
            return NUMERIC;
        }

        if (value instanceof java.lang.Boolean ||
                value.getClass() == Boolean.class)
        {
            return BOOLEAN;
        }

        if (value instanceof java.lang.String)
        {
            return VARCHAR;
        }

        if (value instanceof java.sql.Date)
        {
            return DATE;
        }
        if (value instanceof java.sql.Time)
        {
            return TIME;
        }
        if (value instanceof java.sql.Timestamp)
        {
            return TIMESTAMP;
        }

//        if (value instanceof java.util.Date)
//        {
//            return DATE;
//        }

        return UNKNOWN;
    }
}


