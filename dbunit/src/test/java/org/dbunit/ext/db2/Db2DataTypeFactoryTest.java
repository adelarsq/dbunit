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
package org.dbunit.ext.db2;

import org.dbunit.dataset.datatype.AbstractDataTypeFactoryTest;
import org.dbunit.dataset.datatype.DataType;
import org.dbunit.dataset.datatype.IDataTypeFactory;

import java.sql.Types;

/**
 * @author Manuel Laflamme
 * @since Aug 13, 2003
 * @version $Revision$
 */
public class Db2DataTypeFactoryTest extends AbstractDataTypeFactoryTest
{
    public Db2DataTypeFactoryTest(String s)
    {
        super(s);
    }

    public IDataTypeFactory createFactory() throws Exception
    {
        return new Db2DataTypeFactory();
    }

    public void testCreateXmlVarcharDataType() throws Exception
    {
        DataType expected = Db2DataTypeFactory.DB2XML_XMLVARCHAR;
        int sqlType = Types.DISTINCT;
        String sqlTypeName = "DB2XML.XMLVARCHAR";

        DataType actual = createFactory().createDataType(sqlType, sqlTypeName);
        assertSame("type", expected, actual);
    }

    public void testCreateXmlClobDataType() throws Exception
    {
        DataType expected = Db2DataTypeFactory.DB2XML_XMLCLOB;
        int sqlType = Types.DISTINCT;
        String sqlTypeName = "DB2XML.XMLCLOB";

        DataType actual = createFactory().createDataType(sqlType, sqlTypeName);
        assertSame("type", expected, actual);
    }

    public void testCreateXmlFileDataType() throws Exception
    {
        DataType expected = Db2DataTypeFactory.DB2XML_XMLFILE;
        int sqlType = Types.DISTINCT;
        String sqlTypeName = "DB2XML.XMLFILE";

        DataType actual = createFactory().createDataType(sqlType, sqlTypeName);
        assertSame("type", expected, actual);
    }
}
