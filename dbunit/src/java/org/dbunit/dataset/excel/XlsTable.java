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

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.dbunit.dataset.AbstractTable;
import org.dbunit.dataset.Column;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.DefaultTableMetaData;
import org.dbunit.dataset.ITableMetaData;
import org.dbunit.dataset.datatype.DataType;
import org.dbunit.dataset.datatype.DataTypeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Manuel Laflamme
 * @author Last changed by: $Author$
 * @version $Revision$ $Date$
 * @since Feb 21, 2003
 */
class XlsTable extends AbstractTable
{

    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(XlsTable.class);

    private final ITableMetaData _metaData;
    private final HSSFSheet _sheet;
    
    private final DecimalFormatSymbols symbols = new DecimalFormatSymbols();
    

    public XlsTable(String sheetName, HSSFSheet sheet) throws DataSetException
    {
        int rowCount = sheet.getLastRowNum();
        if (rowCount >= 0 && sheet.getRow(0) != null)
        {
            _metaData = createMetaData(sheetName, sheet.getRow(0));
        }
        else
        {
            _metaData = new DefaultTableMetaData(sheetName, new Column[0]);
        }

        _sheet = sheet;
        
        // Needed for later "BigDecimal"/"Number" conversion
        symbols.setDecimalSeparator('.');
    }

    static ITableMetaData createMetaData(String tableName, HSSFRow sampleRow)
    {
        logger.debug("createMetaData(tableName={}, sampleRow={}) - start", tableName, sampleRow);

        List columnList = new ArrayList();
        for (int i = 0; ; i++)
        {
            HSSFCell cell = sampleRow.getCell(i);
            if (cell == null)
            {
                break;
            }

            String columnName = cell.getRichStringCellValue().getString();
            if (columnName != null)
            {
            	columnName = columnName.trim();
            }
            
            // Bugfix for issue ID 2818981 - if a cell has a formatting but no name also ignore it
            if(columnName.length()<=0)
            {
                logger.debug("The column name of column # {} is empty - will skip here assuming the last column was reached", String.valueOf(i));
                break;
            }
            
            Column column = new Column(columnName, DataType.UNKNOWN);
            columnList.add(column);
        }
        Column[] columns = (Column[])columnList.toArray(new Column[0]);
        return new DefaultTableMetaData(tableName, columns);
    }

    ////////////////////////////////////////////////////////////////////////////
    // ITable interface

    public int getRowCount()
    {
        logger.debug("getRowCount() - start");

        return _sheet.getLastRowNum();
    }

    public ITableMetaData getTableMetaData()
    {
        logger.debug("getTableMetaData() - start");

        return _metaData;
    }

    public Object getValue(int row, String column) throws DataSetException
    {
        if(logger.isDebugEnabled())
            logger.debug("getValue(row={}, columnName={}) - start", Integer.toString(row), column);

        assertValidRowIndex(row);

        int columnIndex = getColumnIndex(column);
        HSSFCell cell = _sheet.getRow(row + 1).getCell(columnIndex);
        if (cell == null)
        {
            return null;
        }

        int type = cell.getCellType();
        switch (type)
        {
            case HSSFCell.CELL_TYPE_NUMERIC:
                HSSFCellStyle style = cell.getCellStyle();
                if (HSSFDateUtil.isCellDateFormatted(cell))
                {
                    return getDateValue(cell);
                }
                else if(XlsDataSetWriter.DATE_FORMAT_AS_NUMBER_DBUNIT.equals(style.getDataFormatString()))
                {
                    // The special dbunit date format
                    return getDateValueFromJavaNumber(cell);
                }
                else 
                {
                    return getNumericValue(cell);
                }

            case HSSFCell.CELL_TYPE_STRING:
                return cell.getRichStringCellValue().getString();

            case HSSFCell.CELL_TYPE_FORMULA:
                throw new DataTypeException("Formula not supported at row=" +
                        row + ", column=" + column);

            case HSSFCell.CELL_TYPE_BLANK:
                return null;

            case HSSFCell.CELL_TYPE_BOOLEAN:
                return cell.getBooleanCellValue() ? Boolean.TRUE : Boolean.FALSE;

            case HSSFCell.CELL_TYPE_ERROR:
                throw new DataTypeException("Error at row=" + row +
                        ", column=" + column);

            default:
                throw new DataTypeException("Unsupported type at row=" + row +
                        ", column=" + column);
        }
    }
    
    protected Object getDateValueFromJavaNumber(HSSFCell cell) 
    {
        logger.debug("getDateValueFromJavaNumber(cell={}) - start", cell);
        
        double numericValue = cell.getNumericCellValue();
        BigDecimal numericValueBd = new BigDecimal(String.valueOf(numericValue));
        numericValueBd = stripTrailingZeros(numericValueBd);
        return new Long(numericValueBd.longValue());
//        return new Long(numericValueBd.unscaledValue().longValue());
    }
    
    protected Object getDateValue(HSSFCell cell) 
    {
        logger.debug("getDateValue(cell={}) - start", cell);
        
        double numericValue = cell.getNumericCellValue();
        Date date = HSSFDateUtil.getJavaDate(numericValue);
        // Add the timezone offset again because it was subtracted automatically by Apache-POI (we need UTC)
        long tzOffset = TimeZone.getDefault().getOffset(date.getTime());
        date = new Date(date.getTime() + tzOffset);
        return new Long(date.getTime());
        
        //TODO use a calendar for XLS Date objects when it is supported better by POI
//        HSSFCellStyle style = cell.getCellStyle();
//        HSSFDataFormatter formatter = new HSSFDataFormatter();
//        Format f = formatter.createFormat(cell);
//      String formatted = fomatter.formatCellValue(cell);
//System.out.println("###"+formatted);
//        Date dateValue = cell.getDateCellValue();
    }

    /**
     * Removes all trailing zeros from the end of the given BigDecimal value
     * up to the decimal point.
     * @param value The value to be stripped
     * @return The value without trailing zeros
     */
    private BigDecimal stripTrailingZeros(BigDecimal value)
    {
        if(value.scale()<=0){
            return value;
        }
        
        String valueAsString = String.valueOf(value);
        int idx = valueAsString.indexOf(".");
        if(idx==-1){
            return value;
        }
        
        for(int i=valueAsString.length()-1; i>idx; i--){
            if(valueAsString.charAt(i)=='0'){
                valueAsString = valueAsString.substring(0, i);
            }
            else if(valueAsString.charAt(i)=='.'){
                valueAsString = valueAsString.substring(0, i);
                // Stop when decimal point is reached
                break;
            }
            else{
                break;
            }
        }
        BigDecimal result = new BigDecimal(valueAsString);
        return result;
    }
    
    protected BigDecimal getNumericValue(HSSFCell cell)
    {
        logger.debug("getNumericValue(cell={}) - start", cell);

        String formatString = cell.getCellStyle().getDataFormatString();
        String resultString = null;
        double cellValue = cell.getNumericCellValue();

        if((formatString != null))
        {
            if(!formatString.equals("General") && !formatString.equals("@")) {
                logger.debug("formatString={}", formatString);
                DecimalFormat nf = new DecimalFormat(formatString, symbols);
                resultString = nf.format(cellValue);
            }
        }
        
        BigDecimal result;
        if(resultString != null) {
            try {
                result = new BigDecimal(resultString);
            }
            catch(NumberFormatException e) {
                logger.debug("Exception occurred while trying create a BigDecimal. value={}", resultString);
                // Probably was not a BigDecimal format retrieved from the excel. Some
                // date formats are not yet recognized by HSSF as DateFormats so that
                // we could get here.
                result = toBigDecimal(cellValue);
            }
        }
        else {
            result = toBigDecimal(cellValue);
        }
        return result;
    }

    /**
     * @param cellValue
     * @return
     * @since 2.4.6
     */
    private BigDecimal toBigDecimal(double cellValue) 
    {
        String resultString = String.valueOf(cellValue);
        // To ensure that intergral numbers do not have decimal point and trailing zero
        // (to restore backward compatibility and provide a string representation consistent with Excel)
        if (resultString.endsWith(".0")) {
            resultString=resultString.substring(0,resultString.length()-2);
        }
        BigDecimal result = new BigDecimal(resultString);
        return result;
        
    }
    
}

