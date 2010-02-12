package org.dbunit.dataset;

import java.io.FileReader;

import junit.framework.TestCase;

import org.dbunit.dataset.filter.IRowFilter;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.testutil.TestUtils;

/**
 * @author gommma
 * @since 2.3.0
 */
public class RowFilterTableTest extends TestCase
{
	
    private IDataSet getDataSet() throws Exception
    {
        return new FlatXmlDataSetBuilder().build(TestUtils.getFileReader(
                "xml/rowFilterTableTest.xml"));
    }


    public void testRowFilter_HappyPath() throws Exception
    {
    	ITable testTable = getDataSet().getTable("TEST_TABLE");
    	IRowFilter rowFilter = new IRowFilter() {
			public boolean accept(IRowValueProvider rowValueProvider) {
				try {
					String value = (String)rowValueProvider.getColumnValue("COLUMN0");
					// filter out first row
					if(value.equals("row 0 col 0")) {
						return false;
					}
					return true;
				} catch (DataSetException e) {
					throw new RuntimeException("Should not happen in this unit test",e);
				}
			}
		
		};
    	ITable rowFilterTable = new RowFilterTable(testTable, rowFilter);
    	// The first row should be filtered
    	assertEquals(3, rowFilterTable.getRowCount());
    	assertEquals("row 1 col 0", rowFilterTable.getValue(0, "COLUMN0"));
    	assertEquals("row 2 col 0", rowFilterTable.getValue(1, "COLUMN0"));
    	assertEquals("row 3 col 0", rowFilterTable.getValue(2, "COLUMN0"));
    }
}
