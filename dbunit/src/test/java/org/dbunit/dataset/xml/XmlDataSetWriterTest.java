/*
 *
 * The DbUnit Database Testing Framework
 * Copyright (C)2005-2006, DbUnit.org
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
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.datatype.DataType;

/**
 *
 * @author Felipe Leme
 * @version $Id$
 * @since Sep 9, 2005$
 */
public class XmlDataSetWriterTest extends TestCase
{
    public XmlDataSetWriterTest(String name)
    {
        super(name);
    }
    
    public static DefaultDataSet getDefaultDataSet() throws Exception {

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

        DefaultDataSet dataSet = new DefaultDataSet(table1, table2);
        return dataSet;
     }

    public static DefaultDataSet getMinimalDataSet() throws Exception {

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

        DefaultDataSet dataSet = new DefaultDataSet(table1);
        return dataSet;
     }

    
    public void testWrite() throws Exception
    {
        String expectedOutput =
                "<dataset>\n" +
                "  <table name=\"TABLE1\">\n" +
                "    <column>COL0</column>\n" +
                "    <column>COL1</column>\n" +
                "    <row>\n" +
                "      <value>t1v1</value>\n" +
                "      <value>t1v2</value>\n" +
                "    </row>\n" +
                "  </table>\n" +
                "  <table name=\"TABLE2\">\n" +
                "    <column>COL0</column>\n" +
                "    <column>COL1</column>\n" +
                "    <row>\n" +
                "      <value>t2v1</value>\n" +
                "      <value>t2v2</value>\n" +
                "    </row>\n" +
                "  </table>\n" +
                "</dataset>\n";
        
        StringWriter stringWriter = new StringWriter();
        XmlDataSetWriter xmlWriter = new XmlDataSetWriter(stringWriter);
        IDataSet dataSet = getDefaultDataSet();
        xmlWriter.write( dataSet );

        String actualOutput = stringWriter.toString();
        assertEquals("output", expectedOutput, actualOutput);
    }
    
    public void testWriteWithComments() throws Exception
    {
        String expectedOutput =
                "<dataset>\n" +
                "  <table name=\"TABLE1\">\n" +
                "    <column>COL0</column>\n" +
                "    <column>COL1</column>\n" +
                "    <row>\n" +
                "      <value>t1v1</value>\n" +
                "      <!-- COL0 -->\n" +
                "      <value>t1v2</value>\n" +
                "      <!-- COL1 -->\n" +
                "    </row>\n" +
                "  </table>\n" +
                "  <table name=\"TABLE2\">\n" +
                "    <column>COL0</column>\n" +
                "    <column>COL1</column>\n" +
                "    <row>\n" +
                "      <value>t2v1</value>\n" +
                "      <!-- COL0 -->\n" +
                "      <value>t2v2</value>\n" +
                "      <!-- COL1 -->\n" +
                "    </row>\n" +
                "  </table>\n" +
                "</dataset>\n";
        StringWriter stringWriter = new StringWriter();
        XmlDataSetWriter xmlWriter = new XmlDataSetWriter(stringWriter);
        xmlWriter.setIncludeColumnComments(true);
        IDataSet dataSet = getDefaultDataSet();
        xmlWriter.write( dataSet );

        String actualOutput = stringWriter.toString();
        assertEquals("output", expectedOutput, actualOutput);
    }
    
    public void testWriteWithCData() throws Exception
    {
        // Setup
        Column[] columns = new Column[]{
                new Column("COL1", DataType.UNKNOWN)
        };
        DefaultTable table = new DefaultTable("TABLE1", columns);
        table.addRow(new Object[]{"<myXmlData><![CDATA[Data that itself is in a CDATA section]]></myXmlData>"});
        DefaultDataSet dataSet = new DefaultDataSet(table);
        
        // Write the XML
        StringWriter stringWriter = new StringWriter();
        XmlDataSetWriter xmlWriter = new XmlDataSetWriter(stringWriter);
        xmlWriter.write(dataSet);
        
        String actualOutput = stringWriter.toString();
        String expectedOutput = "<dataset>\n"+
                                    "  <table name=\"TABLE1\">\n" +
                                    "    <column>COL1</column>\n"+
                                    "    <row>\n"+
                                    "      <value><![CDATA[<myXmlData><![CDATA[Data that itself is in a CDATA section]]]]><![CDATA[></myXmlData>]]></value>\n"+
                                    "    </row>\n"+
                                    "  </table>\n"+
                                "</dataset>\n";
        assertEquals("output", expectedOutput, actualOutput);
        
    }
    
    public void testWritePrettyPrintDisabled() throws Exception
    {
        String expectedOutput =
                "<dataset>" +
                "<table name=\"TABLE1\">" +
                "<column>COL0</column>" +
                "<column>COL1</column>" +
                "<row>" +
                "<value>t1v1</value>" +
                "<value>t1v2</value>" +
                "</row>" +
                "</table>" +
                "</dataset>";
        
        StringWriter stringWriter = new StringWriter();
        XmlDataSetWriter xmlWriter = new XmlDataSetWriter(stringWriter);
        xmlWriter.setPrettyPrint(false);
        IDataSet dataSet = getMinimalDataSet();
        xmlWriter.write( dataSet );

        String actualOutput = stringWriter.toString();
        assertEquals("output", expectedOutput, actualOutput);
    }

}
