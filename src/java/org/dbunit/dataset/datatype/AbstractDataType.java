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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Manuel Laflamme
 * @version $Revision$
 * @since Mar 19, 2002
 */
public abstract class AbstractDataType extends DataType
{

    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(AbstractDataType.class);

    private final String _name;
    private final int _sqlType;
    private final Class _classType;
    private final boolean _isNumber;

    public AbstractDataType(String name, int sqlType, Class classType,
            boolean isNumber)
    {
        _sqlType = sqlType;
        _name = name;
        _classType = classType;
        _isNumber = isNumber;
    }

    ////////////////////////////////////////////////////////////////////////////
    // DataType class

    public int compare(Object o1, Object o2) throws TypeCastException
    {
        logger.debug("compare(o1=" + o1 + ", o2=" + o2 + ") - start");

        try
        {
            Comparable value1 = (Comparable)typeCast(o1);
            Comparable value2 = (Comparable)typeCast(o2);

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

            return value1.compareTo(value2);
        }
        catch (ClassCastException e)
        {
            logger.error("compare()", e);

            throw new TypeCastException(e);
        }
    }

    public int getSqlType()
    {
        logger.debug("getSqlType() - start");

        return _sqlType;
    }

    public Class getTypeClass()
    {
        logger.debug("getTypeClass() - start");

        return _classType;
    }

    public boolean isNumber()
    {
        logger.debug("isNumber() - start");

        return _isNumber;
    }

    public boolean isDateTime()
    {
        logger.debug("isDateTime() - start");

        return false;
    }

    public Object getSqlValue(int column, ResultSet resultSet)
            throws SQLException, TypeCastException
    {
        logger.debug("getSqlValue(column=" + column + ", resultSet=" + resultSet + ") - start");

        Object value = resultSet.getObject(column);
        if (value == null || resultSet.wasNull())
        {
            return null;
        }
        return value;
    }

    public void setSqlValue(Object value, int column, PreparedStatement statement)
            throws SQLException, TypeCastException
    {
        logger.debug("setSqlValue(value=" + value + ", column=" + column + ", statement=" + statement + ") - start");

        statement.setObject(column, typeCast(value), getSqlType());
    }

    ////////////////////////////////////////////////////////////////////////////
    // Object class

    public String toString()
    {
        logger.debug("toString() - start");

        return _name;
    }
}



