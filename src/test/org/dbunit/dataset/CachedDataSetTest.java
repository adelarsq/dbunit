/*
 *
 * The DbUnit Database Testing Framework
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

import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlProducer;

import org.xml.sax.InputSource;

import java.io.FileReader;

/**
 * @author Manuel Laflamme
 * @since Apr 18, 2003
 * @version $Revision$
 */
public class CachedDataSetTest extends AbstractDataSetTest
{
    public CachedDataSetTest(String s)
    {
        super(s);
    }

    protected IDataSet createDataSet() throws Exception
    {
        FileReader reader = new FileReader("src/xml/flatXmlDataSetTest.xml");
        return new CachedDataSet(new FlatXmlProducer(new InputSource(reader)));
//        return new CachedDataSet(
//                new StreamingDataSet(new FlatXmlProducer(new InputSource(reader))));
//        return new CachedDataSet(new ForwardOnlyDataSet(new FlatXmlDataSet(reader)));
    }

    protected IDataSet createDuplicateDataSet() throws Exception
    {
        FileReader reader = new FileReader("src/xml/flatXmlDataSetDuplicateTest.xml");
        return new CachedDataSet(new ForwardOnlyDataSet(new FlatXmlDataSet(reader)));
    }

    public void testGetTable() throws Exception
    {
        super.testGetTable();
    }
}
