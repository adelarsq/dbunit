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
package org.dbunit.dataset.xml;

import org.dbunit.dataset.AbstractProducerTest;
import org.dbunit.dataset.Column;
import org.dbunit.dataset.IDataSetProducer;
import org.dbunit.dataset.MockDataSetConsumer;
import org.dbunit.dataset.DefaultDataSet;
import org.dbunit.dataset.DefaultTable;
import org.dbunit.dataset.DataSetException;

import org.xml.sax.InputSource;
import org.xml.sax.EntityResolver;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.StringReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @author Manuel Laflamme
 * @since Apr 28, 2003
 * @version $Revision$
 */
public class FlatXmlProducerTest extends AbstractProducerTest
{
    private static final File DATASET_FILE =
            new File("src/xml/flatXmlProducerTest.xml");

    public FlatXmlProducerTest(String s)
    {
        super(s);
    }

    protected IDataSetProducer createProducer() throws Exception
    {
        String uri = DATASET_FILE.getAbsoluteFile().toURL().toString();
        InputSource source = new InputSource(uri);

        return new FlatXmlProducer(source);
    }

    public void testProduceEmptyDataSet() throws Exception
    {
        // Setup consumer
        MockDataSetConsumer consumer = new MockDataSetConsumer();
        consumer.addExpectedStartDataSet();
        consumer.addExpectedEndDataSet();

        // Setup producer
        String content =
                "<?xml version=\"1.0\"?>" +
                "<dataset/>";
        InputSource source = new InputSource(new StringReader(content));
        IDataSetProducer producer = new FlatXmlProducer(source);
        producer.setConsumer(consumer);

        // Produce and verify consumer
        producer.produce();
        consumer.verify();
    }

    public void testProduceNoDtd() throws Exception
    {
        // Setup consumer
        String tableName = "EMPTY_TABLE";
        MockDataSetConsumer consumer = new MockDataSetConsumer();
        consumer.addExpectedStartDataSet();
        Column[] expectedColumns = new Column[0];
        consumer.addExpectedEmptyTable(tableName, expectedColumns);
        consumer.addExpectedEndDataSet();

        // Setup producer
        String content =
                "<?xml version=\"1.0\"?>" +
                "<dataset>" +
                    "<EMPTY_TABLE/>" +
                "</dataset>";
        InputSource source = new InputSource(new StringReader(content));
        IDataSetProducer producer = new FlatXmlProducer(source);
        producer.setConsumer(consumer);

        // Produce and verify consumer
        producer.produce();
        consumer.verify();
    }

    public void testProduceIgnoreDtd() throws Exception
    {
        // Setup consumer
        String tableName = "EMPTY_TABLE";
        MockDataSetConsumer consumer = new MockDataSetConsumer();
        consumer.addExpectedStartDataSet();
        Column[] expectedColumns = new Column[0];
        consumer.addExpectedEmptyTable(tableName, expectedColumns);
        consumer.addExpectedEndDataSet();

        // Setup producer
        String content =
                "<?xml version=\"1.0\"?>" +
                "<!DOCTYPE dataset SYSTEM \"uri:/dummy.dtd\">" +
                "<dataset>" +
                    "<EMPTY_TABLE/>" +
                "</dataset>";
        InputSource source = new InputSource(new StringReader(content));
        IDataSetProducer producer = new FlatXmlProducer(source, false);
        producer.setConsumer(consumer);

        // Produce and verify consumer
        producer.produce();
        consumer.verify();
    }

    public void testProduceMetaDataSet() throws Exception
    {
        // Setup consumer
        String tableName = "EMPTY_TABLE";
        MockDataSetConsumer consumer = new MockDataSetConsumer();
        consumer.addExpectedStartDataSet();
        Column[] expectedColumns = createExpectedColumns(Column.NULLABLE);
        consumer.addExpectedEmptyTable(tableName, expectedColumns);
        consumer.addExpectedEndDataSet();

        // Setup producer
        String content =
                "<?xml version=\"1.0\"?>" +
                "<!DOCTYPE dataset SYSTEM \"urn:/dummy.dtd\">" +
                "<dataset>" +
                    "<EMPTY_TABLE/>" +
                "</dataset>";
        InputSource source = new InputSource(new StringReader(content));
        DefaultDataSet metaDataSet = new DefaultDataSet();
        metaDataSet.addTable(
                new DefaultTable(tableName, expectedColumns, new ArrayList()));
        IDataSetProducer producer = new FlatXmlProducer(source, metaDataSet);
        producer.setConsumer(consumer);

        // Produce and verify consumer
        producer.produce();
        consumer.verify();
    }

    public void testProduceCustomEntityResolver() throws Exception
    {
        // Setup consumer
        String tableName = "EMPTY_TABLE";
        MockDataSetConsumer consumer = new MockDataSetConsumer();
        consumer.addExpectedStartDataSet();
        Column[] expectedColumns = createExpectedColumns(Column.NULLABLE);
        consumer.addExpectedEmptyTable(tableName, expectedColumns);
        consumer.addExpectedEndDataSet();

        // Setup producer
       String dtdContent =
                "<!ELEMENT dataset (EMPTY_TABLE)>" +
                "<!ATTLIST EMPTY_TABLE " +
                    "COLUMN0 CDATA #IMPLIED " +
                    "COLUMN1 CDATA #IMPLIED " +
                    "COLUMN2 CDATA #IMPLIED " +
                    "COLUMN3 CDATA #IMPLIED>" +
                "<!ELEMENT TEST_TABLE EMPTY>";
        final InputSource dtdSource = new InputSource(new StringReader(dtdContent));

        String xmlContent =
                "<?xml version=\"1.0\"?>" +
                "<!DOCTYPE dataset SYSTEM \"urn:/dummy.dtd\">" +
                "<dataset>" +
                    "<EMPTY_TABLE/>" +
                "</dataset>";
        InputSource xmlSource = new InputSource(new StringReader(xmlContent));
        IDataSetProducer producer = new FlatXmlProducer(xmlSource, new EntityResolver(){
            public InputSource resolveEntity(String s, String s1) throws SAXException, IOException
            {
                return dtdSource;
            }
        });
        producer.setConsumer(consumer);

        // Produce and verify consumer
        producer.produce();
        consumer.verify();
    }

    public void testProduceNotWellFormedXml() throws Exception
    {
        // Setup consumer
        MockDataSetConsumer consumer = new MockDataSetConsumer();
        consumer.addExpectedStartDataSet();

        // Setup producer
        String content =
                "<?xml version=\"1.0\"?>" +
                "<dataset>";
        InputSource source = new InputSource(new StringReader(content));
        IDataSetProducer producer = new FlatXmlProducer(source);
        producer.setConsumer(consumer);

        // Produce and verify consumer
        try
        {
            producer.produce();
            fail("Should not be here!");
        }
        catch (DataSetException e)
        {
        }

        consumer.verify();
    }

}
