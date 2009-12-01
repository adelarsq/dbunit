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

package org.dbunit.ext.oracle;

import java.math.BigDecimal;

/**
 * This class provides some basic functionality shared among the OracleSdo*
 * objects.
 *
 * @author clucas@e-miles.com
 * @author Last changed by: $Author$
 * @version $Revision$ $Date$
 * @since ?
 */
class OracleSdoHelper
{
    public static boolean objectsEqual(Object object1, Object object2)
    {
        return
            (object1 != null && object1.equals(object2)) ||
            (object1 == null && object2 == null) ||
            // special case for BigDecimal support
            (object1 != null && object2 != null &&
            object1 instanceof BigDecimal && object2 instanceof BigDecimal &&
            ((BigDecimal)object1).compareTo((BigDecimal)object2) == 0);
    }

    public static boolean objectArraysEquals(Object [] objects1, Object [] objects2)
    {
        if (objects1 == objects2)
        {
            return true;
        }

        if (objects1 == null || objects2 == null || objects1.length != objects2.length)
        {
            return false;
        }

        for (int index = 0; index<objects1.length; index++)
        {
            if (! objectsEqual(objects1[index], objects2[index]))
            {
                return false;
            }
        }

        return true;
    }

    public static int objectArrayHashCode(Object [] objects)
    {
        int hash = 7;
        if (objects != null)
        {
            for (int index = 0; index<objects.length; index++)
            {
                hash = 31 * hash + (null == objects[index] ? 0 : objects[index].hashCode());
            }
        }
        return hash;
    }
}
