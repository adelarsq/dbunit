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
 */
public abstract class DataType
{
    public static final DataType OBJECT = new ObjectDataType();
    public static final DataType INTEGER = new IntegerDataType();
    public static final DataType LONG = new LongDataType();
    public static final DataType FLOAT = new FloatDataType();
    public static final DataType DOUBLE = new DoubleDataType();
    public static final DataType BOOLEAN = new BooleanDataType();
    public static final DataType STRING = new StringDataType();
    public static final DataType SHORT = new ShortDataType();
    public static final DataType BYTE = new ByteDataType();
    public static final DataType NUMBER = new NumberDataType();
//    public static final DateDataType DATE = new DateDataType();
    public static final DataType DATE = new SqlDateDataType();
    public static final DataType TIME = new TimeDataType();
    public static final DataType TIMESTAMP = new TimestampDataType();

    /**
     * Returns the name of this <code>DataType</code>
     */
    public abstract String getName();

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
            case Types.BIT:
                return DataType.BOOLEAN;

            case Types.TINYINT:
                return DataType.BYTE;

            case Types.BIGINT:
                return DataType.LONG;

            case Types.SMALLINT:
                return DataType.SHORT;

            case Types.INTEGER:
                return DataType.INTEGER;

            case Types.REAL:
                return DataType.FLOAT;

            case Types.FLOAT:
            case Types.DOUBLE:
                return DataType.DOUBLE;

            case Types.NUMERIC:
            case Types.DECIMAL:
                return DataType.NUMBER;

//            case Types.LONGVARBINARY:
//            case Types.VARBINARY:
//            case Types.BINARY:
//                return DataType.BYTES;

            case Types.LONGVARCHAR:
            case Types.CHAR:
            case Types.VARCHAR:
//            case Types.OTHER:
                return DataType.STRING;

            case Types.DATE:
                return DataType.DATE;

            case Types.TIME:
                return DataType.TIME;

            case Types.TIMESTAMP:
                return DataType.TIMESTAMP;

//            case Types.NULL:
//                return DataType.NULL;

        }

        throw new DataTypeException("Unknown sql data type" + sqlType);
    }

//    /**
//     * Returns the <code>DataType</code> corresponding to the specified data
//     * type name.
//     */
//    public static DataType forName(String type) throws IllegalArgumentException
//    {
//        if (type == null)
//            return OBJECT;
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
//            return OBJECT;
//
//        throw new IllegalArgumentException(type);
//    }
//
//    /**
//     * Returns the <code>DataType</code> corresponding to the specified value
//     * runtime class. This method returns <code>DataType.OBJECT</code>
//     * if the value is <code>null</code> or runtime class not recognized.
//     */
//    public static DataType forObject(Object value)
//    {
//        if (value == null)
//            return OBJECT;
//
//        if (value instanceof java.lang.Short ||
//                value.getClass() == Short.class)
//            return SHORT;
//        if (value instanceof java.lang.Byte ||
//                value.getClass() == Byte.class)
//            return BYTE;
//        if (value instanceof java.lang.Integer ||
//                value.getClass() == Integer.class)
//            return INTEGER;
//        if (value instanceof java.lang.Long ||
//                value.getClass() == Long.class)
//            return LONG;
//        if (value instanceof java.lang.Float ||
//                value.getClass() == Float.class)
//            return FLOAT;
//        if (value instanceof java.lang.Double ||
//                value.getClass() == Double.class)
//            return DOUBLE;
//        if (value instanceof java.lang.Boolean ||
//                value.getClass() == Boolean.class)
//            return BOOLEAN;
//        if (value instanceof java.lang.String)
//            return STRING;
//        if (value instanceof java.util.Date)
//            return DATE;
//
//        // Since the class have not been found we return Object
//        return OBJECT;
//    }
}
