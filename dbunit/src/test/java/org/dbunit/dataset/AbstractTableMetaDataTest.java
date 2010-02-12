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
package org.dbunit.dataset;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import junit.framework.TestCase;

import org.dbunit.dataset.datatype.IDataTypeFactory;
import org.dbunit.ext.mssql.MsSqlDataTypeFactory;

import com.mockobjects.sql.MockDatabaseMetaData;

/**
 * @author Manuel Laflamme
 * @author Last changed by: $Author: $
 * @version $Revision: $ $Date: $
 * @since 2.4.6
 */
public class AbstractTableMetaDataTest extends TestCase 
{

    public void testValidator() throws Exception
    {
        AbstractTableMetaData metaData = new AbstractTableMetaData(){
            public Column[] getColumns() throws DataSetException {
                return null;
            }

            public Column[] getPrimaryKeys() throws DataSetException {
                return null;
            }

            public String getTableName() {
                return null;
            }};
        
//        DataTypeFactoryValidator validator = new DataTypeFactoryValidator();
        IDataTypeFactory dataTypeFactory = new MsSqlDataTypeFactory();
        DatabaseMetaData databaseMetaData = new MockDatabaseMetaData(){
            public String getDatabaseProductName() throws SQLException {
                return "Microsoft SQL Server";
            }
        };
        String validationMessage = metaData.validateDataTypeFactory(dataTypeFactory, databaseMetaData);
        assertEquals("Validation message should be null because DB product should be supported", null, validationMessage);
    }
    
    
    
    
}
