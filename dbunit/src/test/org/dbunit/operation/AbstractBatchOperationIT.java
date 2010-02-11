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

package org.dbunit.operation;

import org.dbunit.AbstractDatabaseIT;
import org.dbunit.dataset.*;
import org.dbunit.dataset.xml.XmlDataSet;
import org.dbunit.testutil.TestUtils;

import java.io.FileReader;
import java.io.Reader;

/**
 * @author Manuel Laflamme
 * @version $Revision$
 * @since May 7, 2002
 */
public class AbstractBatchOperationIT extends AbstractDatabaseIT
{
    public AbstractBatchOperationIT(String s)
    {
        super(s);
    }

    public void testGetOperationMetaDataAndMissingColumns() throws Exception
    {
        Reader in = TestUtils.getFileReader("xml/missingColumnTest.xml");
        IDataSet xmlDataSet = new XmlDataSet(in);

        ITable[] xmlTables = DataSetUtils.getTables(xmlDataSet);
        for (int i = 0; i < xmlTables.length; i++)
        {
            ITable xmlTable = xmlTables[i];
            ITableMetaData xmlMetaData = xmlTable.getTableMetaData();
            String tableName = xmlMetaData.getTableName();

            ITable databaseTable = _connection.createDataSet().getTable(tableName);
            ITableMetaData databaseMetaData = databaseTable.getTableMetaData();

            // ensure xml table is missing some columns present in database table
            assertTrue(tableName + " missing columns", xmlMetaData.getColumns().length <
                    databaseMetaData.getColumns().length);

            ITableMetaData resultMetaData =
                    AbstractBatchOperation.getOperationMetaData(_connection, xmlMetaData);

            // result metadata must contains database columns matching the xml columns
            Column[] resultColumns = resultMetaData.getColumns();
            assertEquals("result columns count", xmlMetaData.getColumns().length,
                    resultColumns.length);
            for (int j = 0; j < resultColumns.length; j++)
            {
                Column resultColumn = resultColumns[j];
                Column databaseColumn = Columns.getColumn(resultColumn.getColumnName(), databaseMetaData.getColumns());
                Column xmlColumn = xmlMetaData.getColumns()[j];

                assertEquals("column name", convertString(xmlColumn.getColumnName()),
                        resultColumn.getColumnName());
                assertSame("column instance", resultColumn, databaseColumn);
            }

            // result metadata must contains database primary keys
            Column[] resultPrimaryKeys = resultMetaData.getPrimaryKeys();
            assertEquals("key count", databaseMetaData.getPrimaryKeys().length,
                    resultPrimaryKeys.length);
            for (int j = 0; j < resultPrimaryKeys.length; j++)
            {
                Column resultPrimaryKey = resultPrimaryKeys[j];
                Column databasePrimaryKey = databaseMetaData.getPrimaryKeys()[j];
                assertSame("key instance", databasePrimaryKey, resultPrimaryKey);
            }
        }
    }

    public void testGetOperationMetaDataAndUnknownColumns() throws Exception
    {
        String tableName = "PK_TABLE";
        Reader in = TestUtils.getFileReader("xml/unknownColumnTest.xml");
        IDataSet xmlDataSet = new XmlDataSet(in);

        ITable xmlTable = xmlDataSet.getTable(tableName);

        try
        {
            AbstractBatchOperation.getOperationMetaData(_connection,
                    xmlTable.getTableMetaData());
            fail("Should throw a NoSuchColumnException");
        }
        catch (NoSuchColumnException e)
        {
        }
    }

}


