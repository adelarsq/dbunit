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

import org.xml.sax.InputSource;

import java.io.File;
import java.io.FileInputStream;
import java.io.StringReader;

/**
 * @author Manuel Laflamme
 * @since Apr 29, 2003
 * @version $Revision$
 */
public class FlatDtdProducerTest extends AbstractProducerTest
{
    private static final File DTD_FILE =
            new File("src/dtd/flatDtdProducerTest.dtd");

    public FlatDtdProducerTest(String s)
    {
        super(s);
    }

    protected IDataSetProducer createProducer() throws Exception
    {
        InputSource source = new InputSource(new FileInputStream(DTD_FILE));
        return new FlatDtdProducer(source);
    }

    protected int[] getExpectedRowCount() throws Exception
    {
        return new int[] {0, 0, 0, 0, 0, 0};
    }

    public void testSequenceModel() throws Exception
    {
        // Setup consumer
        MockDataSetConsumer consumer = new MockDataSetConsumer();
        consumer.addExpectedStartDataSet();
        consumer.addExpectedEmptyTableIgnoreColumns("DUPLICATE_TABLE");
        consumer.addExpectedEmptyTableIgnoreColumns("TEST_TABLE");
        consumer.addExpectedEmptyTableIgnoreColumns("DUPLICATE_TABLE");
        consumer.addExpectedEndDataSet();

        // Setup producer
        String content =
                "<!ELEMENT dataset (DUPLICATE_TABLE*,TEST_TABLE+,DUPLICATE_TABLE?)>" +
                "<!ELEMENT TEST_TABLE EMPTY>" +
                "<!ELEMENT DUPLICATE_TABLE EMPTY>";
        InputSource source = new InputSource(new StringReader(content));
        FlatDtdProducer producer = new FlatDtdProducer(source);
        producer.setConsumer(consumer);

        // Produce and verify consumer
        producer.produce();
        consumer.verify();
    }

    public void testChoicesModel() throws Exception
    {
        // Setup consumer
        MockDataSetConsumer consumer = new MockDataSetConsumer();
        consumer.addExpectedStartDataSet();
        consumer.addExpectedEmptyTableIgnoreColumns("TEST_TABLE");
        consumer.addExpectedEmptyTableIgnoreColumns("SECOND_TABLE");
        consumer.addExpectedEndDataSet();

        // Setup producer
        String content =
                "<!ELEMENT dataset (TEST_TABLE|SECOND_TABLE)>" +
                "<!ELEMENT TEST_TABLE EMPTY>" +
                "<!ELEMENT SECOND_TABLE EMPTY>";
        InputSource source = new InputSource(new StringReader(content));
        FlatDtdProducer producer = new FlatDtdProducer(source);
        producer.setConsumer(consumer);

        // Produce and verify consumer
        producer.produce();
        consumer.verify();
    }

    public void testAttrListBeforeParentElement() throws Exception
    {
        // Setup consumer
        MockDataSetConsumer consumer = new MockDataSetConsumer();
        consumer.addExpectedStartDataSet();
        Column[] expectedColumns = createExpectedColumns(Column.NULLABLE);
        consumer.addExpectedEmptyTable("TEST_TABLE", expectedColumns);
        consumer.addExpectedEndDataSet();

        // Setup producer
        String content =
                "<!ELEMENT dataset (TEST_TABLE)>" +
                "<!ATTLIST TEST_TABLE " +
                    "COLUMN0 CDATA #IMPLIED " +
                    "COLUMN1 CDATA #IMPLIED " +
                    "COLUMN2 CDATA #IMPLIED " +
                    "COLUMN3 CDATA #IMPLIED>" +
                "<!ELEMENT TEST_TABLE EMPTY>";

        InputSource source = new InputSource(new StringReader(content));
        FlatDtdProducer producer = new FlatDtdProducer(source);
        producer.setConsumer(consumer);

        // Produce and verify consumer
        producer.produce();
        consumer.verify();
    }
}
