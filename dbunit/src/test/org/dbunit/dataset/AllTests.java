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

package org.dbunit.dataset;

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
        suite.addTest(org.dbunit.dataset.common.handlers.AllTests.suite());
        suite.addTest(org.dbunit.dataset.datatype.AllTests.suite());
        suite.addTest(org.dbunit.dataset.excel.AllTests.suite());
        suite.addTest(org.dbunit.dataset.filter.AllTests.suite());
        suite.addTest(org.dbunit.dataset.stream.AllTests.suite());
        suite.addTest(org.dbunit.dataset.sqlloader.AllTests.suite());
        suite.addTest(org.dbunit.dataset.xml.AllTests.suite());
        suite.addTest(org.dbunit.dataset.csv.AllTests.suite());
        suite.addTest(new TestSuite(CaseInsensitiveDataSetTest.class));
        suite.addTest(new TestSuite(CaseInsensitiveTableTest.class));
        suite.addTest(new TestSuite(ColumnTest.class));
        suite.addTest(new TestSuite(ColumnsTest.class));
        suite.addTest(new TestSuite(CompositeDataSetTest.class));
        suite.addTest(new TestSuite(CompositeTableTest.class));
        suite.addTest(new TestSuite(DataSetProducerAdapterTest.class));
        suite.addTest(new TestSuite(DataSetUtilsTest.class));
        suite.addTest(new TestSuite(DefaultDataSetTest.class));
        suite.addTest(new TestSuite(DefaultReverseTableIteratorTest.class));
        suite.addTest(new TestSuite(DefaultTableIteratorTest.class));
        suite.addTest(new TestSuite(DefaultTableMetaDataTest.class));
        suite.addTest(new TestSuite(DefaultTableTest.class));
        suite.addTest(new TestSuite(FilteredDataSetTest.class));
        suite.addTest(new TestSuite(FilteredTableMetaDataTest.class));
        suite.addTest(new TestSuite(ForwardOnlyDataSetTest.class));
        suite.addTest(new TestSuite(ForwardOnlyTableTest.class));
        suite.addTest(new TestSuite(LowerCaseDataSetTest.class));
        suite.addTest(new TestSuite(LowerCaseTableMetaDataTest.class));
        suite.addTest(new TestSuite(ReplacementDataSetTest.class));
        suite.addTest(new TestSuite(ReplacementTableTest.class));
        suite.addTest(new TestSuite(SortedDataSetTest.class));
        suite.addTest(new TestSuite(SortedTableTest.class));

        return suite;
    }
}





