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
package org.dbunit.dataset.datatype;

import junit.framework.TestCase;

import java.sql.Types;

/**
 * @author Manuel Laflamme
 * @since Aug 13, 2003
 * @version $Revision$
 */
public class AbstractDataTypeFactoryTest extends TestCase
{
    public AbstractDataTypeFactoryTest(String s)
    {
        super(s);
    }

    public IDataTypeFactory createFactory() throws Exception
    {
        return new DefaultDataTypeFactory();
    }

    public void testCreateDataType() throws Exception
    {
        DataType[] expectedTypes = new DataType[] {
            DataType.UNKNOWN,
            DataType.CHAR,
            DataType.VARCHAR,
            DataType.LONGVARCHAR,
//            DataType.CLOB,
            DataType.NUMERIC,
            DataType.DECIMAL,
            DataType.BOOLEAN,
            DataType.TINYINT,
            DataType.SMALLINT,
            DataType.INTEGER,
            DataType.BIGINT,
            DataType.REAL,
            DataType.FLOAT,
            DataType.DOUBLE,
//            DataType.DATE,
            DataType.TIME,
            DataType.TIMESTAMP,
            DataType.BINARY,
            DataType.VARBINARY,
            DataType.LONGVARBINARY,
//            DataType.BLOB,
        };

        IDataTypeFactory factory = createFactory();
        for (int i = 0; i < expectedTypes.length; i++)
        {
            DataType expected = expectedTypes[i];
            DataType actual = factory.createDataType(expected.getSqlType(), expected.toString());
            assertSame("type", expected,  actual);
        }
    }

    public void testCreateDateDataType() throws Exception
    {
        int sqlType = Types.DATE;
        String sqlTypeName = "DATE";

        DataType expected = DataType.DATE;
        DataType actual = createFactory().createDataType(sqlType, sqlTypeName);
        assertSame("type", expected, actual);
    }

    public void testCreateBlobDataType() throws Exception
    {
        int sqlType = Types.BLOB;
        String sqlTypeName = "BLOB";

        DataType expected = DataType.BLOB;
        DataType actual = createFactory().createDataType(sqlType, sqlTypeName);
        assertSame("type", expected, actual);
    }

    public void testCreateClobDataType() throws Exception
    {
        int sqlType = Types.CLOB;
        String sqlTypeName = "CLOB";

        DataType expected = DataType.CLOB;
        DataType actual = createFactory().createDataType(sqlType, sqlTypeName);
        assertSame("type", expected, actual);
    }

}
