/*
 * BytesDataType.java   Mar 20, 2002
 *
 * Copyright 2002 Freeborders Canada Corp. All Rights Reserved.
 *
 * This software is the proprietary information of Karat Software Corp.
 * Use is subject to license terms.
 *
 */

package org.dbunit.dataset.datatype;

import java.net.URLEncoder;
import java.net.URLDecoder;

import HTTPClient.Codecs;

/**
 * @author Manuel Laflamme
 * @version 1.0
 */
public class BytesDataType extends AbstractDataType
{
    public BytesDataType(String name, int sqlType)
    {
        super(name, sqlType, byte[].class, false);
    }

    ////////////////////////////////////////////////////////////////////////////
    // DataType class

    public Object typeCast(Object value) throws TypeCastException
    {
        if (value == null)
        {
            return value;
        }

        if (value instanceof byte[])
        {
            return value;
        }

        if (value instanceof String)
        {
            String stringValue = value + "\n\r";
            return Codecs.uudecode(stringValue.toCharArray());
        }

        throw new TypeCastException(value.toString());
    }
}
