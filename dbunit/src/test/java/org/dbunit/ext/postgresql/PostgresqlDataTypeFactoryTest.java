/*
 *
 * The DbUnit Database Testing Framework
 * Copyright (C)2002-2009, DbUnit.org
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
package org.dbunit.ext.postgresql;

import java.sql.Types;
import junit.framework.TestCase;
import org.dbunit.dataset.datatype.DataType;
import org.dbunit.dataset.datatype.IntegerDataType;

/**
 *
 * @author Jarvis Cochrane (jarvis@cochrane.com.au)
 * @author Roberto Lo Giacco (rlogiacco@users.sourceforge.ent)
 * @since 2.4.5 (Apr 27, 2009)
 */
public class PostgresqlDataTypeFactoryTest extends TestCase {

    public PostgresqlDataTypeFactoryTest(String testName) {
        super(testName);
    }

    /**
     * Test of createDataType method, of class PostgresqlDataTypeFactory.
     */
    public void testCreateUuidType() throws Exception {

        PostgresqlDataTypeFactory instance = new PostgresqlDataTypeFactory();

        // Test UUID type created properly
        int sqlType = Types.OTHER;
        String sqlTypeName = "uuid";

        DataType result = instance.createDataType(sqlType, sqlTypeName);
        assertTrue(result instanceof UuidType);
    }
    
    public void testCreateIntervalType() throws Exception {

        PostgresqlDataTypeFactory instance = new PostgresqlDataTypeFactory();

        // Test interval type created properly
        int sqlType = Types.OTHER;
        String sqlTypeName = "interval";

        DataType result = instance.createDataType(sqlType, sqlTypeName);
        assertTrue(result instanceof IntervalType);
    }

    public void testCreateInetType() throws Exception {

        PostgresqlDataTypeFactory instance = new PostgresqlDataTypeFactory();

        // Test inet type created properly
        int sqlType = Types.OTHER;
        String sqlTypeName = "inet";

        DataType result = instance.createDataType(sqlType, sqlTypeName);
        assertTrue(result instanceof InetType);
    }

    public void testCreateEnumType() throws Exception {

        PostgresqlDataTypeFactory instance = new PostgresqlDataTypeFactory(){
            public boolean isEnumType(String sqlTypeName) {
                if(sqlTypeName.equalsIgnoreCase("abc_enum")){
                    return true;
                }
                return false;
            }
        };

        // Test Enum type created properly
        int sqlType = Types.OTHER;
        String sqlTypeName = "abc_enum";

        DataType result = instance.createDataType(sqlType, sqlTypeName);
        assertTrue(result instanceof GenericEnumType);
        assertEquals("abc_enum", ((GenericEnumType)result).getSqlTypeName());
    }

    public void testCreateDefaultType() throws Exception {

        PostgresqlDataTypeFactory instance = new PostgresqlDataTypeFactory();

        int sqlType = Types.INTEGER;
        String sqlTypeName = "int";

        DataType result = instance.createDataType(sqlType, sqlTypeName);
        assertTrue(result instanceof IntegerDataType);
    }

}
