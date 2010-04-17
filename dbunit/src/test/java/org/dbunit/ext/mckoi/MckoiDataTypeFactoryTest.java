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
package org.dbunit.ext.mckoi;

import org.dbunit.dataset.datatype.AbstractDataTypeFactoryTest;
import org.dbunit.dataset.datatype.DataType;
import org.dbunit.dataset.datatype.IDataTypeFactory;

import java.sql.Types;

/**
 * @author Luigi Talamona (luigitalamona AT users.sourceforge.net)
 * @author Last changed by: $Author$
 * @version $Revision$ $Date$
 * @since 2.4.8
 *
 */
public class MckoiDataTypeFactoryTest extends AbstractDataTypeFactoryTest
{
    public MckoiDataTypeFactoryTest(String s)
    {
        super(s);
    }

    public IDataTypeFactory createFactory() throws Exception
    {
        return new MckoiDataTypeFactory();
    }
    public void testCreateJavaObjectDataType() throws Exception
    {
        DataType expected = null, actual = null;
        int sqlType = Types.DISTINCT;
        expected = DataType.forSqlType(Types.JAVA_OBJECT);
        actual = this.createFactory().createDataType(sqlType,"JAVA_OBJECT");
        assertSame("type", expected, actual);
    }
}
