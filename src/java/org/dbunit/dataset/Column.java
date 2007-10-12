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

package org.dbunit.dataset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.dbunit.dataset.datatype.DataType;

import java.sql.DatabaseMetaData;

/**
 * Represents a table column.
 *
 * @author Manuel Laflamme
 * @version $Revision$
 * @since Feb 17, 2002
 */
public class Column
{

    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(Column.class);

    /**
     * Indicates that the column might not allow <code>NULL</code> values.
     */
    public static final Nullable NO_NULLS = new Nullable("noNulls");
    /**
     * Indicates that the column definitely allows <code>NULL</code> values.
     */
    public static final Nullable NULLABLE = new Nullable("nullable");
    /**
     * Indicates that the nullability of columns is unknown.
     */
    public static final Nullable NULLABLE_UNKNOWN = new Nullable("nullableUnknown");

    private final String _columnName;
    private final DataType _dataType;
    private final String _sqlTypeName;
    private final Nullable _nullable;

    /**
     * Creates a Column object. This contructor set nullable to true.
     *
     * @param columnName the column name
     * @param dataType the data type
     */
    public Column(String columnName, DataType dataType)
    {
        _columnName = columnName;
        _dataType = dataType;
        _nullable = NULLABLE_UNKNOWN;
        _sqlTypeName = dataType.toString();
    }

    /**
     * Creates a Column object.
     */
    public Column(String columnName, DataType dataType, Nullable nullable)
    {
        _columnName = columnName;
        _dataType = dataType;
        _sqlTypeName = dataType.toString();
        _nullable = nullable;
    }

    /**
     * Creates a Column object.
     */
    public Column(String columnName, DataType dataType, String sqlTypeName,
            Nullable nullable)
    {
        _columnName = columnName;
        _dataType = dataType;
        _sqlTypeName = sqlTypeName;
        _nullable = nullable;
    }

    /**
     * Returns this column name.
     */
    public String getColumnName()
    {
        logger.debug("getColumnName() - start");

        return _columnName;
    }

    /**
     * Returns this column data type.
     */
    public DataType getDataType()
    {
        logger.debug("getDataType() - start");

        return _dataType;
    }

    /**
     * Returns this column sql data type name.
     */
    public String getSqlTypeName()
    {
        logger.debug("getSqlTypeName() - start");

        return _sqlTypeName;
    }

    /**
     * Returns <code>true</code> if this column is nullable.
     */
    public Nullable getNullable()
    {
        logger.debug("getNullable() - start");

        return _nullable;
    }

    /**
     * Returns the appropriate Nullable constant according specified JDBC
     * DatabaseMetaData constant.
     *
     * @param nullable one of the following constants
     * {@link java.sql.DatabaseMetaData#columnNoNulls},
     * {@link java.sql.DatabaseMetaData#columnNullable},
     * {@link java.sql.DatabaseMetaData#columnNullableUnknown}
     */
    public static Nullable nullableValue(int nullable)
    {
        logger.debug("nullableValue(nullable=" + nullable + ") - start");

        switch (nullable)
        {
            case DatabaseMetaData.columnNoNulls:
                return NO_NULLS;

            case DatabaseMetaData.columnNullable:
                return NULLABLE;

            case DatabaseMetaData.columnNullableUnknown:
                return NULLABLE_UNKNOWN;

            default:
                throw new IllegalArgumentException("Unknown constant value "
                        + nullable);
        }
    }

    /**
     * Returns the appropriate Nullable constant.
     *
     * @param nullable <code>true</code> if null is allowed
     */
    public static Nullable nullableValue(boolean nullable)
    {
        logger.debug("nullableValue(nullable=" + nullable + ") - start");

        return nullable ? NULLABLE : NO_NULLS;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Object class

    public String toString()
    {
        logger.debug("toString() - start");

        return "(" + _columnName + ", " + _dataType + ", " + _nullable + ")";
//        return _columnName;
    }

    public boolean equals(Object o)
    {
        logger.debug("equals(o=" + o + ") - start");

        if (this == o) return true;
        if (!(o instanceof Column)) return false;

        final Column column = (Column)o;

        if (!_columnName.equals(column._columnName)) return false;
        if (!_dataType.equals(column._dataType)) return false;
        if (!_nullable.equals(column._nullable)) return false;
        if (!_sqlTypeName.equals(column._sqlTypeName)) return false;

        return true;
    }

    public int hashCode()
    {
        logger.debug("hashCode() - start");

        int result;
        result = _columnName.hashCode();
        result = 29 * result + _dataType.hashCode();
        result = 29 * result + _sqlTypeName.hashCode();
        result = 29 * result + _nullable.hashCode();
        return result;
    }

    public static class Nullable
    {

        /**
         * Logger for this class
         */
        private static final Logger logger = LoggerFactory.getLogger(Nullable.class);

        private final String _name;

        private Nullable(String name)
        {
            _name = name;
        }

        ////////////////////////////////////////////////////////////////////////////
        // Object class

        public String toString()
        {
            logger.debug("toString() - start");

            return _name;
        }
    }

}








