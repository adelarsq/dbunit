/*
 * FilteredDataSetTest.java   Feb 22, 2002
 *
 * The dbUnit database testing framework.
 * Copyright (C) 2002   Manuel Laflamme
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

import java.io.FileInputStream;

import org.dbunit.dataset.xml.XmlDataSet;

/**
 * @author Manuel Laflamme
 * @version 1.0
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
                new FileInputStream("src/xml/compositeDataSetTest1.xml"));
        assertTrue("count before combine (1)",
                dataSet1.getTableNames().length < getExpectedNames().length);

        IDataSet dataSet2 = new XmlDataSet(
                new FileInputStream("src/xml/compositeDataSetTest2.xml"));
        assertTrue("count before combine (2)",
                dataSet2.getTableNames().length < getExpectedNames().length);

        return new CompositeDataSet(dataSet1, dataSet2);
    }
}
