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

package org.dbunit.dataset.xml;

import java.io.StringWriter;

import junit.framework.TestCase;

import org.dbunit.dataset.Column;
import org.dbunit.dataset.DefaultDataSet;
import org.dbunit.dataset.DefaultTable;
import org.dbunit.dataset.datatype.DataType;

/**
 *
 * @author Manuel Laflamme
 * @version $Revision$
 * @since Sep 8, 2003$
 */
public class FlatXmlWriterTest extends TestCase
{
    public FlatXmlWriterTest(String name)
    {
        super(name);
    }

    public void testWrite() throws Exception
    {
        String expectedOutput =
                "<dataset>\n" +
                "  <TABLE1 COL0=\"t1v1\" COL1=\"t1v2\"/>\n" +
                "  <TABLE2 COL0=\"t2v1\" COL1=\"t2v2\"/>\n" +
                "</dataset>\n";

        String col0 = "COL0";
        String col1 = "COL1";
        Column[] columns = new Column[]{
            new Column(col0, DataType.UNKNOWN),
            new Column(col1, DataType.UNKNOWN)
        };

        DefaultTable table1 = new DefaultTable("TABLE1", columns);
        table1.addRow();
        table1.setValue(0, col0, "t1v1");
        table1.setValue(0, col1, "t1v2");

        DefaultTable table2 = new DefaultTable("TABLE2", columns);
        table2.addRow();
        table2.setValue(0, col0, "t2v1");
        table2.setValue(0, col1, "t2v2");

        StringWriter stringWriter = new StringWriter();
        FlatXmlWriter xmlWriter = new FlatXmlWriter(stringWriter);
        xmlWriter.write(new DefaultDataSet(table1, table2));

        String actualOutput = stringWriter.toString();
        assertEquals("output", expectedOutput, actualOutput);
    }

    public void testWriteWithDocType() throws Exception
    {
        String expectedOutput =
                "<!DOCTYPE dataset SYSTEM \"dataset.dtd\">\n" +
                "<dataset>\n" +
                "  <TABLE1 COL0=\"v1\" COL1=\"v2\"/>\n" +
                "</dataset>\n";

        String col0 = "COL0";
        String col1 = "COL1";
        Column[] columns = new Column[]{
            new Column(col0, DataType.UNKNOWN),
            new Column(col1, DataType.UNKNOWN)
        };

        DefaultTable table1 = new DefaultTable("TABLE1", columns);
        table1.addRow();
        table1.setValue(0, col0, "v1");
        table1.setValue(0, col1, "v2");

        StringWriter stringWriter = new StringWriter();
        FlatXmlWriter xmlWriter = new FlatXmlWriter(stringWriter);
        xmlWriter.setDocType("dataset.dtd");
        xmlWriter.write(new DefaultDataSet(table1));

        String actualOutput = stringWriter.toString();
        assertEquals("output", expectedOutput, actualOutput);
    }

    public void testWriteExcludeEmptyTable() throws Exception
    {
        String expectedOutput =
                "<dataset>\n" +
                "  <TEST_TABLE COL0=\"value\"/>\n" +
                "</dataset>\n";

        String col0 = "COL0";
        Column[] columns = new Column[]{
            new Column(col0, DataType.UNKNOWN),
        };

        DefaultTable table1 = new DefaultTable("TEST_TABLE", columns);
        table1.addRow();
        table1.setValue(0, col0, "value");
        DefaultTable table2 = new DefaultTable("EMPTY_TABLE", columns);

        StringWriter stringWriter = new StringWriter();
        FlatXmlWriter datasetWriter = new FlatXmlWriter(stringWriter);
        datasetWriter.setIncludeEmptyTable(false);
        datasetWriter.write(new DefaultDataSet(table1, table2));

        String actualOutput = stringWriter.toString();
        assertEquals("output", expectedOutput, actualOutput);
    }
               
    public void testWriteIncludeEmptyTable() throws Exception
    {
        String expectedOutput =
                "<dataset>\n" +
                "  <TEST_TABLE COL0=\"value\"/>\n" +
                "  <EMPTY_TABLE/>\n" +
                "</dataset>\n";

        String col0 = "COL0";
        Column[] columns = new Column[]{
            new Column(col0, DataType.UNKNOWN),
        };

        DefaultTable table1 = new DefaultTable("TEST_TABLE", columns);
        table1.addRow();
        table1.setValue(0, col0, "value");
        DefaultTable table2 = new DefaultTable("EMPTY_TABLE", columns);

        StringWriter stringWriter = new StringWriter();
        FlatXmlWriter datasetWriter = new FlatXmlWriter(stringWriter);
        datasetWriter.setIncludeEmptyTable(true);
        datasetWriter.write(new DefaultDataSet(table1, table2));

        String actualOutput = stringWriter.toString();
        assertEquals("output", expectedOutput, actualOutput);
    }

    public void testWriteNullValue() throws Exception
    {
        String expectedOutput =
                "<dataset>\n" +
                "  <TEST_TABLE COL0=\"c0r0\" COL1=\"c1r0\"/>\n" +
                "  <TEST_TABLE COL0=\"c0r1\"/>\n" +
                "</dataset>\n";

        String col0 = "COL0";
        String col1 = "COL1";
        Column[] columns = new Column[]{
            new Column(col0, DataType.UNKNOWN),
            new Column(col1, DataType.UNKNOWN)
        };

        DefaultTable table = new DefaultTable("TEST_TABLE", columns);
        table.addRow();
        table.setValue(0, col0, "c0r0");
        table.setValue(0, col1, "c1r0");
        table.addRow();
        table.setValue(1, col0, "c0r1");
        table.setValue(1, col1, null);

        StringWriter stringWriter = new StringWriter();
        FlatXmlWriter xmlWriter = new FlatXmlWriter(stringWriter);
        xmlWriter.write(new DefaultDataSet(table));

        String actualOutput = stringWriter.toString();
        assertEquals("output", expectedOutput, actualOutput);
    }
}
