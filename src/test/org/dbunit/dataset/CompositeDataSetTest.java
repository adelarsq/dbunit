/*
 * FilteredDataSetTest.java   Feb 22, 2002
 *
 * DbUnit Database Testing Framework
 * Copyright (C)2002, Manuel Laflamme
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

import org.dbunit.dataset.xml.XmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

import java.io.FileInputStream;
import java.io.FileReader;

/**
 * @author Manuel Laflamme
 * @version $Revision$
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
                new FileReader("src/xml/compositeDataSetTest1.xml"));
        assertTrue("count before combine (1)",
                dataSet1.getTableNames().length < getExpectedNames().length);

        IDataSet dataSet2 = new XmlDataSet(
                new FileReader("src/xml/compositeDataSetTest2.xml"));
        assertTrue("count before combine (2)",
                dataSet2.getTableNames().length < getExpectedNames().length);

        return new CompositeDataSet(dataSet1, dataSet2);
    }

    protected IDataSet createDuplicateDataSet() throws Exception
    {
        IDataSet dataSet1 = new FlatXmlDataSet(
                new FileReader("src/xml/compositeDataSetDuplicateTest1.xml"));
        assertTrue("count before combine (1)",
                dataSet1.getTableNames().length < getExpectedDuplicateNames().length);

        IDataSet dataSet2 = new FlatXmlDataSet(
                new FileReader("src/xml/compositeDataSetDuplicateTest2.xml"));
        assertTrue("count before combine (2)",
                dataSet2.getTableNames().length < getExpectedDuplicateNames().length);

        return new CompositeDataSet(dataSet1, dataSet2, false);
    }

    public void testCombineTables() throws Exception
    {
        IDataSet originaldataSet = createMultipleCaseDuplicateDataSet();
        assertEquals("table count before", 3, originaldataSet.getTableNames().length);

        IDataSet combinedDataSet = new CompositeDataSet(originaldataSet, true);
        assertEquals("table count combined", 2, combinedDataSet.getTableNames().length);
    }

}




