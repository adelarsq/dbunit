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

import java.math.BigDecimal;
import java.sql.Types;

import org.dbunit.dataset.datatype.ToleratedDeltaMap.ToleratedDelta;



/**
 * @author Manuel Laflamme
 * @since Aug 13, 2003
 * @version $Revision$
 */
public class DefaultDataTypeFactoryTest extends AbstractDataTypeFactoryTest
{
    public DefaultDataTypeFactoryTest(String s)
    {
        super(s);
    }

    public IDataTypeFactory createFactory() throws Exception
    {
        return new DefaultDataTypeFactory();
    }
    
    public void testCreateNumberTolerantDataType_Numeric() throws Exception
    {
        int sqlType = Types.NUMERIC;
        String sqlTypeName = "NUMBER";

        DefaultDataTypeFactory factory = new DefaultDataTypeFactory();
        factory.addToleratedDelta(new ToleratedDelta("TEST_TABLE", "COLUMN0", 1E-5));
        DataType actual = factory.createDataType(sqlType, sqlTypeName, "TEST_TABLE", "COLUMN0");
        assertEquals("type", NumberTolerantDataType.class, actual.getClass());
        assertEquals(new BigDecimal("1.0E-5"), ((NumberTolerantDataType)actual).getToleratedDelta().getDelta());
    }

    
    public void testCreateNumberTolerantDataType_Decimal() throws Exception
    {
        int sqlType = Types.DECIMAL;
        String sqlTypeName = "DECIMAL";

        DefaultDataTypeFactory factory = new DefaultDataTypeFactory();
        factory.addToleratedDelta(new ToleratedDelta("TEST_TABLE", "COLUMN0", 1E-5));
        DataType actual = factory.createDataType(sqlType, sqlTypeName, "TEST_TABLE", "COLUMN0");
        assertEquals("type", NumberTolerantDataType.class, actual.getClass());
        assertEquals(new BigDecimal("1.0E-5"), ((NumberTolerantDataType)actual).getToleratedDelta().getDelta());
    }

    
    public void testCreateNumberTolerantDataTypeAndNoToleranceSetForColumn_Numeric() throws Exception
    {
        int sqlType = Types.NUMERIC;
        String sqlTypeName = "NUMBER";

        DefaultDataTypeFactory factory = new DefaultDataTypeFactory();
        factory.addToleratedDelta(new ToleratedDelta("TEST_TABLE", "COLUMN0", 1E-5));
        DataType actual = factory.createDataType(sqlType, sqlTypeName, "TEST_TABLE", "COLUMNXYZ-withoutTolerance");
        assertSame("type", DataType.NUMERIC, actual);
    }

    public void testCreateNumberTolerantDataTypeAndNoToleranceSetForColumn_Decimal() throws Exception
    {
        int sqlType = Types.DECIMAL;
        String sqlTypeName = "DECIMAL";

        DefaultDataTypeFactory factory = new DefaultDataTypeFactory();
        factory.addToleratedDelta(new ToleratedDelta("TEST_TABLE", "COLUMN0", 1E-5));
        DataType actual = factory.createDataType(sqlType, sqlTypeName, "TEST_TABLE", "COLUMNXYZ-withoutTolerance");
        assertSame("type", DataType.DECIMAL, actual);
    }

}
