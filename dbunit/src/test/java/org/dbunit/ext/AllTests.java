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
package org.dbunit.ext;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Manuel Laflamme
 * @since Aug 13, 2003
 * @version $Revision$
 */
public class AllTests extends TestSuite
{
    public static Test suite() throws Exception
    {
        TestSuite suite = new TestSuite();
        suite.addTest(org.dbunit.ext.db2.AllTests.suite());
        suite.addTest(org.dbunit.ext.mckoi.AllTests.suite());
        suite.addTest(org.dbunit.ext.mssql.AllTests.suite());
        suite.addTest(org.dbunit.ext.mysql.AllTests.suite());
        suite.addTest(org.dbunit.ext.oracle.AllTests.suite());
        suite.addTest(org.dbunit.ext.hsqldb.AllTests.suite());
        suite.addTest(org.dbunit.ext.h2.AllTests.suite());
        suite.addTest(org.dbunit.ext.postgresql.AllTests.suite());
        return suite;
    }
}
