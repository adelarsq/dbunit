/*
 * AbstractDataType.java   Mar 19, 2002
 *
 * Copyright 2002 Freeborders Canada Corp. All Rights Reserved.
 *
 * This software is the proprietary information of Karat Software Corp.
 * Use is subject to license terms.
 *
 */

package org.dbunit.dataset.datatype;

/**
 * @author Manuel Laflamme
 * @version 1.0
 * @since 1.3
 */
abstract class AbstractDataType extends DataType
{
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

    public int getSqlType()
    {
        return _sqlType;
    }

    public Class getTypeClass()
    {
        return _classType;
    }

    public boolean isNumber()
    {
        return _isNumber;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Object class

    public String toString()
    {
        return _name;
    }
}
