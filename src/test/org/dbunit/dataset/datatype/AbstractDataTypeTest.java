/*
 *
 * Copyright 2001 Karat Software Corp. All Rights Reserved.
 *
 * This software is the proprietary information of Karat Software Corp.
 * Use is subject to license terms.
 *
 */

package org.dbunit.dataset.datatype;

import junit.framework.TestCase;

/**
 * @author Manuel Laflamme
 * @version 1.0
 */

public abstract class AbstractDataTypeTest extends TestCase
{

    public AbstractDataTypeTest(String name)
    {
        super(name);
    }

    public abstract void testToString() throws Exception;

    public abstract void testGetTypeClass() throws Exception;

    public abstract void testIsNumber() throws Exception;

    public abstract void testTypeCast() throws Exception;

    public abstract void testInvalidTypeCast() throws Exception;

    public abstract void testSqlType() throws Exception;

    public abstract void testForObject() throws Exception;

}

