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
package org.dbunit.ext.h2;

import org.dbunit.dataset.datatype.AbstractDataTypeFactoryTest;
import org.dbunit.dataset.datatype.DataType;
import org.dbunit.dataset.datatype.IDataTypeFactory;

import java.sql.Types;

/**
 * @author Felipe Leme
 * @author Last changed by: $Author$
 * @version $Revision$ $Date$
 * @since 2.2.1
 */
public class H2DataTypeFactoryTest extends AbstractDataTypeFactoryTest
{
    public H2DataTypeFactoryTest(String s)
    {
        super(s);
    }

    public IDataTypeFactory createFactory() throws Exception
    {
        return new H2DataTypeFactory();
    }

    public void testCreateBooleanDataType() throws Exception
    {
        int sqlType = Types.BIT;
        String sqlTypeName = "BOOLEAN";

        DataType expected = DataType.BOOLEAN;
        DataType actual = createFactory().createDataType(sqlType, sqlTypeName);
        assertSame("type", expected, actual);
    }

}

