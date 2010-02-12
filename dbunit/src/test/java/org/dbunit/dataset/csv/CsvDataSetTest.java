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
import java.io.IOException;

import junit.framework.TestCase;

import org.dbunit.Assertion;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.DataSetUtils;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.testutil.TestUtils;
import org.dbunit.util.FileHelper;

/**
 * @author Lenny Marks (lenny@aps.org)
 * @version $Revision$
 * @since Sep 12, 2004 (pre 2.3)
 */
public class CsvDataSetTest extends TestCase {
	protected static final File DATASET_DIR = TestUtils.getFile("csv/orders");
    
    public CsvDataSetTest(String s) {
        super(s);
    }

    public void testNullColumns() throws DataSetException {
		File csvDir = DATASET_DIR;
		
    	CsvDataSet dataSet = new CsvDataSet(csvDir);
    	
    	ITable table = dataSet.getTable("orders");
    	
    	assertNull(table.getValue(4, "description"));
    	
    }

	public void testWrite() throws Exception {
		
		IDataSet expectedDataSet = new CsvDataSet(DATASET_DIR);
			
		File tempDir = createTmpDir();
		try {
			//modified this test from FlatXmlDataSetTest
			CsvDataSetWriter.write(expectedDataSet, tempDir);
			
			File tableOrderingFile = new File(tempDir, CsvDataSet.TABLE_ORDERING_FILE);
			assertTrue(tableOrderingFile.exists());
			
			IDataSet actualDataSet = new CsvDataSet(tempDir);
			
			//verify table count
			assertEquals("table count", expectedDataSet.getTableNames().length,
			actualDataSet.getTableNames().length);
			
			//verify each table
			ITable[] expected = DataSetUtils.getTables(expectedDataSet);
			ITable[] actual = DataSetUtils.getTables(actualDataSet);
			assertEquals("table count", expected.length, actual.length);
			for (int i = 0; i < expected.length; i++) {
				String expectedName = expected[i].getTableMetaData().getTableName();
				String actualName = actual[i].getTableMetaData().getTableName();
				assertEquals("table name", expectedName, actualName);
				
				assertTrue("not same instance", expected[i] != actual[i]);
				Assertion.assertEquals(expected[i], actual[i]);
			}
			
		} finally {
			FileHelper.deleteDirectory(tempDir, true);
			
		}
		
		//assertFalse("temporary directory was not deleted", tempDir.exists());
	}
	
	private File createTmpDir() throws IOException {
		File tmpFile = File.createTempFile("CsvDataSetTest", "-csv");
		String fullPath = tmpFile.getAbsolutePath();
		tmpFile.delete();
		
		File tmpDir = new File(fullPath);
		if(!tmpDir.mkdir()) {
			throw new IOException("Failed to create tmpDir: " + fullPath);
		}
		
		return tmpDir;
	}
	
}




