/*
 * XmlExtenderDataType.java
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

package org.dbunit.ext.db2.xmlextender;

import org.dbunit.dataset.datatype.*;

import java.util.*;

public final class XmlExtenderDataType extends DefaultDataTypeFactory{

    protected static final int SQL_TYPE = 2001;

    protected static final DataType DB2XML_XMLVARCHAR = new StringDataType(
            "DB2XML.XMLCLOB", SQL_TYPE);
    protected static final DataType DB2XML_XMLCLOB = new StringDataType(
            "DB2XML.XMLVARCHAR", SQL_TYPE);
    protected static final DataType DB2XML_XMLFILE = new StringDataType(
            "DB2XML.XMLFILE", SQL_TYPE);

    protected static Map TYPE_MAP = new HashMap();

    static {
        TYPE_MAP.put("DB2XML.XMLCLOB", DB2XML_XMLCLOB);
        TYPE_MAP.put("DB2XML.XMLVARCHAR", DB2XML_XMLVARCHAR);
        TYPE_MAP.put("DB2XML.XMLFILE", DB2XML_XMLFILE);
    }

    /**
     * Wether or not DB2 XML Extender defines a datatype
     * @param name identifies the sql data type by name
     * (e.g.: DB2XML.XMLCLOB)
     * @param sqlType identifies the sql data type by the value
     * of the <code>TYPE_NAME</code> field returned by a call
     * like <code>connection.getDatabaseMetaData().getColumns(..)</code>.
     * @return true if both <code>sqlType</code> and <code>name</code> have
     * identify a type defined by the DB2 XML Extender - v8.
     * Currently these are the types defined:
     * <pre>
     * DATA_TYPE TYPE_NAME             COLUMN_SIZE
     *      2001 "DB2XML"."XMLVARCHAR"        3000
     *      2001 "DB2XML"."XMLCLOB"     2147483647
     *      2001 "DB2XML"."XMLFILE"            512
     * </pre>
     */
    public static boolean defineDataType (String name, int sqlType) {
        if (sqlType == SQL_TYPE) {
            return TYPE_MAP.containsKey(name);
        }
        return false;
    }

    public static DataType createDataType(String name, int sqlType) {
        return (DataType) TYPE_MAP.get(name);
    }

}
