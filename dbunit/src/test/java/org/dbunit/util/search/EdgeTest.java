/*
 *
 * The DbUnit Database Testing Framework
 * Copyright (C)2005, DbUnit.org
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
package org.dbunit.util.search;

import com.gargoylesoftware.base.testing.EqualsTester;

import junit.framework.TestCase;

/**
 * @author gommma
 * @author Last changed by: $Author$
 * @version $Revision$ $Date$
 * @since 2.4.0
 */
public class EdgeTest extends TestCase 
{

    public void testEqualsHashCode()
    {
        Edge e1 = new Edge("table1", "table2");
        Edge e2 = new Edge("table1", "table2");
        Edge eNotEqual = new Edge("table1", "tableOther");
        Edge eEqualSubclass = new Edge("table1", "table2") {};
        
        // Use gsbase "EqualsTester" library for this - easier and less code for equals/hashCode test
        new EqualsTester(e1, e2, eNotEqual, eEqualSubclass);
    }
   
    public void testCompare()
    {
        Edge e1 = new Edge("table1", "table2");
        Edge e2 = new Edge("table1", "table2");
        Edge eNotEqual = new Edge("table1", "tableOther");
        
        assertEquals(0, e1.compareTo(e2));
        assertEquals(-29, e1.compareTo(eNotEqual));
    }
}
