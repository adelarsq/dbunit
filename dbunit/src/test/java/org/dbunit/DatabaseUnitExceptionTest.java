/*
 *
 * The DbUnit Database Testing Framework
 * Copyright (C)2002-2008, DbUnit.org
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
package org.dbunit;

import junit.framework.TestCase;

/**
 * @author gommma
 * @version $Revision$
 * @since 2.3.0
 */
public class DatabaseUnitExceptionTest extends TestCase 
{

    public void testNestedException()
    {
        IllegalStateException nested = new IllegalStateException("bla bla");
        DatabaseUnitException ex = new DatabaseUnitException(nested);
        assertEquals(nested, ex.getCause());
    }
    
    public void testNestedExceptionWithMessage()
    {
        String msg = "a dbunit exception message";
        IllegalStateException nested = new IllegalStateException("bla bla");
        DatabaseUnitException ex = new DatabaseUnitException(msg, nested);
        assertEquals(msg, ex.getMessage());
        assertEquals(nested, ex.getCause());
    }

}
