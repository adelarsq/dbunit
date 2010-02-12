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

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.dataset.xml.XmlDataSet;
import org.dbunit.testutil.TestUtils;

/**
 * @author Manuel Laflamme
 * @author Last changed by: $Author$
 * @version $Revision$ $Date$
 * @since 1.0 (Feb 22, 2002)
 */
public class CompositeDataSetTest extends AbstractDataSetTest
{
    public CompositeDataSetTest(String s)
    {
        super(s);
    }

    protected IDataSet createDataSet() throws Exception
    {
        IDataSet dataSet1 = new XmlDataSet(
                TestUtils.getFileReader("xml/compositeDataSetTest1.xml"));
        assertTrue("count before combine (1)",
                dataSet1.getTableNames().length < getExpectedNames().length);

        IDataSet dataSet2 = new XmlDataSet(
                TestUtils.getFileReader("xml/compositeDataSetTest2.xml"));
        assertTrue("count before combine (2)",
                dataSet2.getTableNames().length < getExpectedNames().length);

        return new CompositeDataSet(dataSet1, dataSet2);
    }

    protected IDataSet createDuplicateDataSet() throws Exception
    {
        return createCompositeDataSet(false, false);
    }

    protected IDataSet createMultipleCaseDuplicateDataSet() throws Exception 
    {
        return createCompositeDataSet(false, true);
    }

    
    public void testCombineTables() throws Exception
    {
        CompositeDataSet combinedDataSet = createCompositeDataSet(true, false);
        String[] tableNames = combinedDataSet.getTableNames();
        assertEquals("table count combined", 2, tableNames.length);
        assertEquals("DUPLICATE_TABLE", tableNames[0]);
        assertEquals("EMPTY_TABLE", tableNames[1]);
    }

    
    private CompositeDataSet createCompositeDataSet(boolean combined, boolean multipleCase) 
    throws DataSetException, FileNotFoundException, IOException 
    {
        IDataSet dataSet1 = new FlatXmlDataSetBuilder().build(
                TestUtils.getFileReader("xml/compositeDataSetDuplicateTest1.xml"));
        assertTrue("count before combine (1)",
                dataSet1.getTableNames().length < getExpectedDuplicateNames().length);

        IDataSet dataSet2 = new FlatXmlDataSetBuilder().build(
                TestUtils.getFileReader("xml/compositeDataSetDuplicateTest2.xml"));
        assertTrue("count before combine (2)",
                dataSet2.getTableNames().length < getExpectedDuplicateNames().length);

        if(multipleCase){
            dataSet2 = new LowerCaseDataSet(dataSet2);
        }
        
        CompositeDataSet dataSet = new CompositeDataSet(dataSet1, dataSet2, combined);
        return dataSet;
    }

}




