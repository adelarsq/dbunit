/*
 *
 * Copyright 2000 Karat Software Corp. All Rights Reserved.
 *
 * This software is the proprietary information of Karat Software Corp.
 * Use is subject to license terms.
 *
 */

package org.dbunit.dataset.datatype;


/**
 * @author Manuel Laflamme
 * @version 1.0
 */
public class TypeCastException extends DataTypeException
{
    public TypeCastException()
    {
        super();
    }

    public TypeCastException(String msg)
    {
        super(msg);
    }

    public TypeCastException(Throwable e)
    {
        super(e);
    }

    public TypeCastException(String msg, Throwable e)
    {
        super(msg, e);
    }
}
