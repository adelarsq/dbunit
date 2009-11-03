/*
 *
 * The DbUnit Database Testing Framework
 * Copyright (C)2008, DbUnit.org
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
package org.dbunit.util.xml;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.io.Writer;

import junit.framework.TestCase;

/**
 * @author gommma
 * @author Last changed by: $Author$
 * @version $Revision$ $Date$
 * @since 2.3.0
 */
public class XmlWriterTest extends TestCase 
{

	public void testLiterallyFalse() throws Exception
	{
		String text = "text1\ntext2\rtext3";
		String expectedXml = "<COLUMN1 ATTR=\"" + text + "\">" + text + "</COLUMN1>\n";
        Writer writer = new StringWriter();
        XmlWriter xmlWriter = new XmlWriter(writer);
		xmlWriter.writeElement("COLUMN1");
		xmlWriter.writeAttribute("ATTR", text);
		xmlWriter.writeText(text);
		xmlWriter.endElement();
		xmlWriter.close();
		String actualXml = writer.toString();
		assertEquals(expectedXml, actualXml);
	}
	
	public void testLiterallyTrue() throws Exception
	{
		String expectedText = "text1&#xA;text2&#xD;text3";
		String expectedXml = "<COLUMN1 ATTR=\"" + expectedText + "\">" + expectedText + "</COLUMN1>\n";

		boolean literally = true;
		String text = "text1\ntext2\rtext3";
        Writer writer = new StringWriter();
        XmlWriter xmlWriter = new XmlWriter(writer);
		xmlWriter.writeElement("COLUMN1");
		xmlWriter.writeAttribute("ATTR", text, literally);
		xmlWriter.writeText(text, literally);
		xmlWriter.endElement();
		xmlWriter.close();
		String actualXml = writer.toString();
		assertEquals(expectedXml, actualXml);
	}
	
	public void testWriteAttributesAfterText() throws Exception
	{
		String text = "bla";
        Writer writer = new StringWriter();
        XmlWriter xmlWriter = new XmlWriter(writer);
		xmlWriter.writeElement("COLUMN1");
		xmlWriter.writeText(text);
		try {
			xmlWriter.writeAttribute("ATTR", text);
			fail("Should not be able to add attributes afterwards with the current XmlWriter implementation (which could be better...)");
		}
		catch(IllegalStateException expected) {
			// all right
		}
	}
	
	public void testWriteNestedCDATAWithoutSurrounder() throws Exception
	{
	    String text = "<![CDATA[Text that itself is in a CDATA section]]>";
        Writer writer = new StringWriter();
        XmlWriter xmlWriter = new XmlWriter(writer);
        xmlWriter.writeElement("COLUMN1");
        xmlWriter.writeCData(text);
        xmlWriter.endElement();
        xmlWriter.close();
        String actualXml = writer.toString();
        
        // Input should be equal to output because the text already starts with a CDATA section
        assertEquals("<COLUMN1>" + text + "</COLUMN1>\n", actualXml);
	}

	public void testWriteNestedCDATAWithSurrounder() throws Exception
	{
	    String text = "<myXmlText>"+XmlWriter.CDATA_START+"Text that itself is in a CDATA section"+XmlWriter.CDATA_END+"</myXmlText>";
        String expectedResultText = "<myXmlText>"+XmlWriter.CDATA_START+"Text that itself is in a CDATA section]]"+XmlWriter.CDATA_END+XmlWriter.CDATA_START+"></myXmlText>";
	    Writer writer = new StringWriter();
	    XmlWriter xmlWriter = new XmlWriter(writer);
	    xmlWriter.writeElement("COLUMN1");
	    xmlWriter.writeCData(text);
	    xmlWriter.endElement();
	    xmlWriter.close();
	    String actualXml = writer.toString();

	    String expectedXml = "<COLUMN1>" + XmlWriter.CDATA_START + expectedResultText + XmlWriter.CDATA_END + "</COLUMN1>\n";
	    assertEquals(expectedXml, actualXml);
	}

	   public void testOutputStreamWithNullEncoding() throws Exception
	    {
	        ByteArrayOutputStream out = new ByteArrayOutputStream();
	        // Use a different encoding than the default
	        XmlWriter xmlWriter = new XmlWriter(out, null);
	        xmlWriter.writeDeclaration();
	        xmlWriter.writeEmptyElement("COLUMN1");
	        xmlWriter.close();
	        
	           String expected = "<?xml version='1.0' encoding='UTF-8'?>\n" +
	           		"<COLUMN1/>\n";
	        assertEquals(expected, out.toString("UTF-8"));
	    }

       public void testOutputStreamWithNonDefaultEncoding() throws Exception
       {
           ByteArrayOutputStream out = new ByteArrayOutputStream();
           // Use a different encoding than the default
           XmlWriter xmlWriter = new XmlWriter(out, "ISO-8859-1");
           xmlWriter.writeDeclaration();
           xmlWriter.writeEmptyElement("COLUMN1");
           xmlWriter.close();
           
           String expected = "<?xml version='1.0' encoding='ISO-8859-1'?>\n" +
           		"<COLUMN1/>\n";
           assertEquals(expected, out.toString("ISO-8859-1"));
       }
	   
	public void testEncodedXmlChar() throws Exception
	{
		String expectedText = "&#174;text1&#xA;text2&#xD;text3&#174;";
		String expectedXml = "<COLUMN1 ATTR=\"" + expectedText + "\">" + expectedText + "</COLUMN1>\n";

		boolean literally = true;
        StringBuffer textBuilder = new StringBuffer();
        String registeredSymbol = new String(new char[] { 174 });
        textBuilder.append(registeredSymbol);
        textBuilder.append("text1\ntext2\rtext3");
        textBuilder.append(registeredSymbol);
        String text = textBuilder.toString();
        Writer writer = new StringWriter();
        XmlWriter xmlWriter = new XmlWriter(writer);
		xmlWriter.writeElement("COLUMN1");
		xmlWriter.writeAttribute("ATTR", text, literally);
		xmlWriter.writeText(text, literally);
		xmlWriter.endElement();
		xmlWriter.close();
		String actualXml = writer.toString();
		assertEquals(expectedXml, actualXml);
	}
	
}
