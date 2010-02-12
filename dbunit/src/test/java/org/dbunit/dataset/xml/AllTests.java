/*
 *
 * The DbUnit Database Testing Framework
 * Copyright (C)2002-2006, DbUnit.org
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

package org.dbunit.dataset.xml;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Manuel Laflamme
 * @version $Revision$
 */
public class AllTests extends TestSuite
{
    public static Test suite()
    {
        TestSuite suite = new TestSuite();
        suite.addTest(new TestSuite(FlatDtdDataSetIT.class));
        suite.addTest(new TestSuite(FlatDtdProducerTest.class));
        suite.addTest(new TestSuite(FlatDtdWriterTest.class));
        suite.addTest(new TestSuite(FlatXmlDataSetTest.class));
        suite.addTest(new TestSuite(FlatXmlProducerTest.class));
        suite.addTest(new TestSuite(FlatXmlTableTest.class));
        suite.addTest(new TestSuite(FlatXmlTableWriteTest.class));
        suite.addTest(new TestSuite(FlatXmlWriterTest.class));
        suite.addTest(new TestSuite(XmlDataSetTest.class));
        suite.addTest(new TestSuite(XmlDataSetWriterTest.class));
        suite.addTest(new TestSuite(XmlProducerTest.class));
        suite.addTest(new TestSuite(XmlTableTest.class));
        suite.addTest(new TestSuite(XmlTableWriteTest.class));

        return suite;
    }
}






