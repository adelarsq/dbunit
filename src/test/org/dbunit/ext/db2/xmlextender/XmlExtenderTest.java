package org.dbunit.ext.db2.xmlextender;

import junit.framework.TestCase;

public class XmlExtenderTest extends TestCase {

    public XmlExtenderTest(String s) {
        super(s);
    }

    public void testDefineDataType () {
        assertTrue(XmlExtenderDataType.defineDataType("DB2XML.XMLCLOB", 2001));
        assertTrue(XmlExtenderDataType.defineDataType("DB2XML.XMLVARCHAR", 2001));
        assertTrue(XmlExtenderDataType.defineDataType("DB2XML.XMLFILE", 2001));
    }

    public void testCreateDataType () {
        assertEquals(XmlExtenderDataType.createDataType("DB2XML.XMLVARCHAR", 2001), XmlExtenderDataType.DB2XML_XMLVARCHAR);
        assertEquals(XmlExtenderDataType.createDataType("DB2XML.XMLCLOB", 2001), XmlExtenderDataType.DB2XML_XMLCLOB);
        assertEquals(XmlExtenderDataType.createDataType("DB2XML.XMLFILE", 2001), XmlExtenderDataType.DB2XML_XMLFILE);
    }

    public void testTheTypes () {
        assertEquals(3, XmlExtenderDataType.TYPE_MAP.size());
    }

}
