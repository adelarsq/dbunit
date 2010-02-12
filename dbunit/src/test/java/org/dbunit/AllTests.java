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

package org.dbunit;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Manuel Laflamme
 * @version $Revision$
 */
public class AllTests extends TestSuite
{
    public static Test suite() throws Exception
    {
        TestSuite suite = new TestSuite();
        suite.addTest(org.dbunit.ant.AllTests.suite());
        suite.addTest(org.dbunit.assertion.AllTests.suite());
        suite.addTest(org.dbunit.database.AllTests.suite());
        suite.addTest(org.dbunit.database.search.AllTests.suite());
        suite.addTest(org.dbunit.dataset.AllTests.suite());
        suite.addTest(org.dbunit.ext.AllTests.suite());
        suite.addTest(org.dbunit.operation.AllTests.suite());
        suite.addTest(org.dbunit.util.AllTests.suite());
        suite.addTest(org.dbunit.util.search.AllTests.suite());
        suite.addTest(new TestSuite(DatabaseUnitExceptionTest.class));
        suite.addTest(new TestSuite(DatabaseProfileTest.class));
        suite.addTest(new TestSuite(DatabaseTestCaseIT.class));
        suite.addTest(new TestSuite(DBTestCaseIT.class));
        return suite;
    }

    public static void main(String args[]) throws Exception
    {
        junit.textui.TestRunner.run(suite());
        System.exit(0);
    }
}







