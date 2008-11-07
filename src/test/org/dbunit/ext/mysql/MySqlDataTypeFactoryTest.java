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
package org.dbunit.ext.mysql;

import org.dbunit.dataset.datatype.AbstractDataTypeFactoryTest;
import org.dbunit.dataset.datatype.DataType;
import org.dbunit.dataset.datatype.IDataTypeFactory;

import java.sql.Types;

/**
 * @author Manuel Laflamme
 * @since Sep 3, 2003
 * @version $Revision$
 */
public class MySqlDataTypeFactoryTest extends AbstractDataTypeFactoryTest
{
    public MySqlDataTypeFactoryTest(String s)
    {
        super(s);
    }

    public IDataTypeFactory createFactory() throws Exception
    {
        return new MySqlDataTypeFactory();
    }

    public void testCreateLongtextDataType() throws Exception
    {
        DataType actual = createFactory().createDataType(Types.OTHER, "longtext");
        DataType expected = DataType.CLOB;
        assertSame("type", expected, actual);
    }

    public void testCreateLongtextUpperCaseDataType() throws Exception
    {
        // MySql 5 reports the datatypes in uppercase, so this here must also work
        DataType actual = createFactory().createDataType(Types.OTHER, "LONGTEXT");
        DataType expected = DataType.CLOB;
        assertSame("type", expected, actual);
    }

    public void testCreateBooleanDataType() throws Exception
    {
        DataType actual = createFactory().createDataType(Types.OTHER, "bit");
        DataType expected = DataType.BOOLEAN;
        assertSame("type", expected, actual);
    }
    
    public void testCreateBooleanUpperCaseDataType() throws Exception
    {
        // MySql 5 reports the datatypes in uppercase, so this here must also work
        DataType actual = createFactory().createDataType(Types.OTHER, "BIT");
        DataType expected = DataType.BOOLEAN;
        assertSame("type", expected, actual);
    }

    public void testCreatePointDataType() throws Exception
    {
        DataType actual = createFactory().createDataType(Types.OTHER, "point");
        DataType expected = DataType.BINARY;
        assertSame("type", expected, actual);
    }

    public void testCreatePointUpperCaseDataType() throws Exception
    {
        // MySql 5 reports the datatypes in uppercase, so this here must also work
        DataType actual = createFactory().createDataType(Types.OTHER, "POINT");
        DataType expected = DataType.BINARY;
        assertSame("type", expected, actual);
    }

    public void testCreateTinyintUnsignedDatatype() throws Exception
    {
        int sqlType = Types.BIT; // MySqlConnector/J reports "TINYINT UNSIGNED" columns as SQL type "BIT".
        String sqlTypeName = MySqlDataTypeFactory.SQL_TYPE_NAME_TINYINT_UNSIGNED;

        DataType expected = DataType.TINYINT;
        DataType actual = createFactory().createDataType(sqlType, sqlTypeName);
        assertSame("type", expected, actual);
    }

    public void testCreateIntegerUnsignedDatatype() throws Exception
    {
        int sqlType = Types.INTEGER;
        String sqlTypeName = "INTEGER" + MySqlDataTypeFactory.UNSIGNED_SUFFIX;

        DataType expected = DataType.BIGINT;
        DataType actual = createFactory().createDataType(sqlType, sqlTypeName);
        assertSame("type", expected, actual);
    }

}
