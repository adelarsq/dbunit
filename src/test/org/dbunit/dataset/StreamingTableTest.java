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

import org.dbunit.dataset.xml.FlatXmlProducer;

import org.xml.sax.InputSource;

import java.io.FileReader;

/**
 * @author Manuel Laflamme
 * @since Apr 11, 2003
 * @version $Revision$
 */
public class StreamingTableTest extends ForwardOnlyTableTest
{
    private static final String TEST_TABLE = "TEST_TABLE";

    public StreamingTableTest(String s)
    {
        super(s);
    }

    protected ITable createTable() throws Exception
    {
        FileReader reader = new FileReader("src/xml/flatXmlDataSetTest.xml");

//        IDataSetProducer source = new DataSetProducerAdapter(new FlatXmlDataSet(reader));
        IDataSetProducer source = new FlatXmlProducer(new InputSource(reader));
        ITableIterator iterator = new StreamingDataSet(source).iterator();
        while(iterator.next())
        {
            ITable table = iterator.getTable();
            String tableName = table.getTableMetaData().getTableName();
            if (tableName.equals(TEST_TABLE))
            {
                return table;
            }
        }

        throw new IllegalStateException();
    }
}
