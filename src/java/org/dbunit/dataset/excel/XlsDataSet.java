/*
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
 */
package org.dbunit.dataset.excel;

import org.dbunit.dataset.AbstractDataSet;
import org.dbunit.dataset.Column;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.ITableMetaData;
import org.dbunit.dataset.ITableIterator;
import org.dbunit.dataset.DefaultTableIterator;
import org.dbunit.dataset.datatype.DataType;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * This dataset implementation can read and write MS Excel documents. Each
 * sheet represents a table. The first row of a sheet defines the columns names
 * and remaining rows contains the data.
 *
 * @author Manuel Laflamme
 * @since Feb 21, 2003
 * @version $Revision$
 */
public class XlsDataSet extends AbstractDataSet
{
    private final ITable[] _tables;

    /**
     * Creates a new XlsDataSet object that loads the specified Excel document.
     */
    public XlsDataSet(File file) throws IOException, DataSetException
    {
        this(new FileInputStream(file));
    }

    /**
     * Creates a new XlsDataSet object that loads the specified Excel document.
     */
    public XlsDataSet(InputStream in) throws IOException, DataSetException
    {
        HSSFWorkbook workbook = new HSSFWorkbook(in);
        _tables = new ITable[workbook.getNumberOfSheets()];
        for (int i = 0; i < _tables.length; i++)
        {
            _tables[i] = new XlsTable(workbook.getSheetName(i),
                    workbook.getSheetAt(i));
        }
    }

    /**
     * Write the specified dataset to the specified Excel document.
     */
    public static void write(IDataSet dataSet, OutputStream out)
            throws IOException, DataSetException
    {
        HSSFWorkbook workbook = new HSSFWorkbook();

        int index = 0;
        ITableIterator iterator = dataSet.iterator();
        while(iterator.next())
        {
            // create the table i.e. sheet
            ITable table = iterator.getTable();
            ITableMetaData metaData = table.getTableMetaData();
            HSSFSheet sheet = workbook.createSheet(metaData.getTableName());

            // write table metadata i.e. first row in sheet
            workbook.setSheetName(index, metaData.getTableName());

            HSSFRow headerRow = sheet.createRow(0);
            Column[] columns = metaData.getColumns();
            for (int j = 0; j < columns.length; j++)
            {
                Column column = columns[j];
                HSSFCell cell = headerRow.createCell((short)j);
                cell.setCellValue(column.getColumnName());
            }

            // write table data
            for (int j = 0; j < table.getRowCount(); j++)
            {
                HSSFRow row = sheet.createRow(j + 1);
                for (int k = 0; k < columns.length; k++)
                {
                    Column column = columns[k];
                    Object value = table.getValue(j, column.getColumnName());
                    if (value != null)
                    {
                        HSSFCell cell = row.createCell((short)k);
                        cell.setCellValue(DataType.asString(value));
                    }
                }
            }

            index++;
        }

        // write xls document
        workbook.write(out);
        out.flush();
    }

    ////////////////////////////////////////////////////////////////////////////
    // AbstractDataSet class

    protected ITableIterator createIterator(boolean reversed)
            throws DataSetException
    {
        return new DefaultTableIterator(_tables, reversed);
    }
}
