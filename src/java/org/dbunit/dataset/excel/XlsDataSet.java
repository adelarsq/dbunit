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
package org.dbunit.dataset.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.Date;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.dbunit.dataset.AbstractDataSet;
import org.dbunit.dataset.Column;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.DefaultTableIterator;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.ITableIterator;
import org.dbunit.dataset.ITableMetaData;
import org.dbunit.dataset.OrderedTableNameMap;
import org.dbunit.dataset.datatype.DataType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    public static final String ZEROS = "0000000000000000000000000000000000000000000000000000";
    
    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(XlsDataSet.class);

    private final OrderedTableNameMap _tables;


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
        _tables = super.createTableNameMap();
        
        HSSFWorkbook workbook = new HSSFWorkbook(in);
        int sheetCount = workbook.getNumberOfSheets();
        for (int i = 0; i < sheetCount; i++)
        {
            ITable table = new XlsTable(workbook.getSheetName(i),
                    workbook.getSheetAt(i));
            _tables.add(table.getTableMetaData().getTableName(), table);            
        }
    }

    /**
     * Write the specified dataset to the specified Excel document.
     */
    public static void write(IDataSet dataSet, OutputStream out)
            throws IOException, DataSetException
    {
        logger.debug("write(dataSet={}, out={}) - start", dataSet, out);

        HSSFWorkbook workbook = new HSSFWorkbook();

        HSSFCellStyle cellStyleDate = workbook.createCellStyle();
        cellStyleDate.setDataFormat(HSSFDataFormat.getBuiltinFormat("m/d/yy h:mm"));
        
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
//                cell.setEncoding(HSSFCell.ENCODING_UTF_16); //Deprecated! As - of 3-Jan-06 POI now automatically handles Unicode without forcing the encoding.
                cell.setCellValue(new HSSFRichTextString(column.getColumnName()));
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
//                        cell.setEncoding(HSSFCell.ENCODING_UTF_16); //Deprecated! As - of 3-Jan-06 POI now automatically handles Unicode without forcing the encoding.
                        if(value instanceof Date){
                            cell.setCellValue((Date)value);
                            cell.setCellStyle(cellStyleDate);
                        }
                        else if(value instanceof BigDecimal){
                            cell.setCellValue( ((BigDecimal)value).doubleValue() );

                            HSSFCellStyle cellStyleNumber = workbook.createCellStyle();
                            HSSFDataFormat df = workbook.createDataFormat();
                            int scale = ((BigDecimal)value).scale();
                            short format;
                            if(scale <= 0){
                                format = df.getFormat("####");
                            }
                            else {
                                String zeros = createZeros(((BigDecimal)value).scale());
                                format = df.getFormat("####." + zeros);
                            }
                            if(logger.isDebugEnabled())
                                logger.debug("Using format '{}' for value '{}'.", String.valueOf(format), value);
                            
                            cellStyleNumber.setDataFormat(format);
                            cell.setCellStyle(cellStyleNumber);
                        }
                        else {
                            cell.setCellValue(new HSSFRichTextString(DataType.asString(value)));
                        }
                    }
                }
            }

            index++;
        }

        // write xls document
        workbook.write(out);
        out.flush();
    }

    private static String createZeros(int count) {
        return ZEROS.substring(0, count);
    }

    ////////////////////////////////////////////////////////////////////////////
    // AbstractDataSet class

    protected ITableIterator createIterator(boolean reversed)
            throws DataSetException
    {
    	if(logger.isDebugEnabled())
    		logger.debug("createIterator(reversed={}) - start", String.valueOf(reversed));

    	ITable[] tables = (ITable[]) _tables.orderedValues().toArray(new ITable[0]);
        return new DefaultTableIterator(tables, reversed);
    }
}
