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

import org.dbunit.dataset.AbstractTest;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.ITableMetaData;
import org.dbunit.dataset.DataSetProducerAdapter;
import org.dbunit.dataset.IDataSetConsumer;
import org.dbunit.dataset.IDataSetProducer;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlProducer;

import org.xml.sax.InputSource;

import java.io.FileReader;
import java.io.File;

/**
 * @author Manuel Laflamme
 * @since Apr 17, 2003
 * @version $Revision$
 */
public class DataSetProducerAdapterTest extends AbstractProducerTest
{
    private static final File DATASET_FILE =
            new File("src/xml/flatXmlProducerTest.xml");

    public DataSetProducerAdapterTest(String s)
    {
        super(s);
    }

    protected IDataSetProducer createProducer() throws Exception
    {
        FlatXmlDataSet dataSet = new FlatXmlDataSet(DATASET_FILE);
        return new DataSetProducerAdapter(dataSet);
    }

}
