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
package org.dbunit.ext.oracle;

import org.dbunit.dataset.datatype.AbstractDataTypeFactoryTest;
import org.dbunit.dataset.datatype.IDataTypeFactory;
import org.dbunit.dataset.datatype.DataType;

import java.sql.Types;

/**
 * @author Manuel Laflamme
 * @since Aug 13, 2003
 * @version $Revision$
 */
public class OracleDataTypeFactoryTest extends AbstractDataTypeFactoryTest
{
    public OracleDataTypeFactoryTest(String s)
    {
        super(s);
    }

    public IDataTypeFactory createFactory() throws Exception
    {
        return new OracleDataTypeFactory();
    }

    public void testCreateBlobDataType() throws Exception
    {
        int sqlType = Types.OTHER;
        String sqlTypeName = "BLOB";

        DataType expected = DataType.BLOB;
        DataType actual = createFactory().createDataType(sqlType, sqlTypeName);
        assertSame("type", expected, actual);
    }

    public void testCreateClobDataType() throws Exception
    {
        int sqlType = Types.OTHER;
        String sqlTypeName = "CLOB";

        DataType expected = OracleDataTypeFactory.ORACLE_CLOB;
        DataType actual = createFactory().createDataType(sqlType, sqlTypeName);
        assertSame("type", expected, actual);
    }

    public void testCreateNClobDataType() throws Exception
    {
        int sqlType = Types.OTHER;
        String sqlTypeName = "NCLOB";

        DataType expected = OracleDataTypeFactory.ORACLE_CLOB;
        DataType actual = createFactory().createDataType(sqlType, sqlTypeName);
        assertSame("type", expected, actual);
    }

    public void testCreateTimestampDataType() throws Exception
    {
        int sqlType = Types.OTHER;
        String sqlTypeName = "TIMESTAMP(6)";

        DataType expected = DataType.TIMESTAMP;
        DataType actual = createFactory().createDataType(sqlType, sqlTypeName);
        assertSame("type", expected, actual);
    }

}
