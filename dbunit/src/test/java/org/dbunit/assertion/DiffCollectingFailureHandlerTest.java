/*
 *
 *  The DbUnit Database Testing Framework
 *  Copyright (C)2002-2008, DbUnit.org
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package org.dbunit.assertion;

import java.util.List;

import junit.framework.TestCase;

import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.testutil.TestUtils;

/**
 * @author gommma (gommma AT users.sourceforge.net)
 * @author Last changed by: $Author$
 * @version $Revision$ $Date$
 * @since 2.4.0
 */
public class DiffCollectingFailureHandlerTest extends TestCase
{
    private DbUnitAssert assertion = new DbUnitAssert();

    public DiffCollectingFailureHandlerTest(String s)
    {
        super(s);
    }

    private IDataSet getDataSet() throws Exception
    {
        return new FlatXmlDataSet(TestUtils.getFileReader(DbUnitAssertIT.FILE_PATH));
    }

    public void testAssertTablesWithDifferentValues() throws Exception
    {
        IDataSet dataSet = getDataSet();

        DiffCollectingFailureHandler myHandler = new DiffCollectingFailureHandler();
        
        assertion.assertEquals(dataSet.getTable("TEST_TABLE"),
                                dataSet.getTable("TEST_TABLE_WITH_WRONG_VALUE"), 
                                myHandler);
        
        List diffList = myHandler.getDiffList();
        assertEquals(1, diffList.size());
        Difference diff = (Difference)diffList.get(0);
        assertEquals("COLUMN2", diff.getColumnName());
        assertEquals("row 1 col 2", diff.getExpectedValue());
        assertEquals("wrong value", diff.getActualValue());
    }

}
