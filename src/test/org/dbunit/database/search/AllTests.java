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

package org.dbunit.database.search;

import junit.framework.Test;
import junit.framework.TestSuite;

/**  
 * @author Felipe Leme (dbunit@felipeal.net)
 * @version $Revision$
 * @since Aug 28, 2005
 */
public class AllTests extends TestSuite
{
    public static Test suite() throws Exception
    {
        TestSuite suite = new TestSuite();
        suite.addTest(new TestSuite(ForeignKeyRelationshipEdgeTest.class));        
        suite.addTest(new TestSuite(ImportAndExportNodesFilterSearchCallbackTest.class));        
        suite.addTest(new TestSuite(ImportNodesFilterSearchCallbackTest.class));        
        suite.addTest(new TestSuite(ImportAndExportKeysSearchCallbackOwnFileTest.class));                
        suite.addTest(new TestSuite(ImportedKeysFilteredByPKsCyclicTest.class));        
        suite.addTest(new TestSuite(ImportedKeysFilteredByPKsSingleTest.class));        
        suite.addTest(new TestSuite(ImportedKeysFilteredByPKsTest.class));        
        suite.addTest(new TestSuite(ImportedAndExportedKeysFilteredByPKsCyclicTest.class));        
        suite.addTest(new TestSuite(ImportedAndExportedKeysFilteredByPKsSingleTest.class));        
        suite.addTest(new TestSuite(ImportedAndExportedKeysFilteredByPKsTest.class));        
        suite.addTest(new TestSuite(TablesDependencyHelperTest.class));        
        return suite;
    }
}






