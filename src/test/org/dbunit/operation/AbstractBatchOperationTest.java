/*
 * AbstractBatchOperationTest.java   May 7, 2002
 *
 * Copyright 2002 Freeborders Canada Corp. All Rights Reserved.
 *
 * This software is the proprietary information of Karat Software Corp.
 * Use is subject to license terms.
 *
 */

package org.dbunit.operation;

import java.io.FileInputStream;
import java.io.InputStream;

import org.dbunit.AbstractDatabaseTest;
import org.dbunit.dataset.*;
import org.dbunit.dataset.xml.XmlDataSet;

/**
 * @author Manuel Laflamme
 * @version 1.0
 */
public class AbstractBatchOperationTest extends AbstractDatabaseTest
{
    public AbstractBatchOperationTest(String s)
    {
        super(s);
    }

    public void testGetOperationMetaDataAndMissingColumns() throws Exception
    {
        InputStream in = new FileInputStream("src/xml/missingColumnTest.xml");
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
                Column databaseColumn = DataSetUtils.getColumn(
                        resultColumn.getColumnName(), databaseMetaData.getColumns());
                Column xmlColumn = xmlMetaData.getColumns()[j];

                assertEquals("column name", xmlColumn.getColumnName(),
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
        InputStream in = new FileInputStream("src/xml/unknownColumnTest.xml");
        IDataSet xmlDataSet = new XmlDataSet(in);

        ITable xmlTable = xmlDataSet.getTable(tableName);

        try
        {
            AbstractBatchOperation.getOperationMetaData(_connection, xmlTable.getTableMetaData());
            fail("Should throw a NoSuchColumnException");
        }
        catch (NoSuchColumnException e)
        {
        }
    }

}

