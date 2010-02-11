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

package org.dbunit.dataset.csv;


import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import junit.framework.TestCase;

import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.ITable;
import org.dbunit.testutil.TestUtils;

/**
 * @author Lenny Marks (lenny@aps.org)
 * @author dIon gillard (diongillard@users.sourceforge.net)
 * @version $Revision$
 * @since 2.1.0
 */
public class CsvURLDataSetTest extends TestCase {
    
    public CsvURLDataSetTest(String s) {
        super(s);
    }

    public void testNullColumns() throws DataSetException, MalformedURLException {
		URL csvDir = TestUtils.getFile("csv/orders/").toURL();
    	CsvURLDataSet dataSet = new CsvURLDataSet(csvDir);
    	
    	ITable table = dataSet.getTable("orders");
    	assertNull(table.getValue(4, "description"));
    }

    public void testSpacesInColumns() throws DataSetException, MalformedURLException {
		URL csvDir = TestUtils.getFile("csv/accounts/").toURL();
    	CsvURLDataSet dataSet = new CsvURLDataSet(csvDir);
    	
    	ITable table = dataSet.getTable("accounts");
    	assertEquals("   123", table.getValue(0, "acctid"));
    	assertEquals("  2", table.getValue(1, "acctid"));
    	assertEquals("   3spaces", table.getValue(2, "acctid"));
    	assertEquals("    -4", table.getValue(3, "acctid"));
    	assertEquals("     5     ", table.getValue(4, "acctid"));
    }

}




