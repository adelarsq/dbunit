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

}
