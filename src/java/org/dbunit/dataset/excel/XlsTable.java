/*
 *
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
 *
 */
package org.dbunit.dataset.excel;

import org.dbunit.dataset.*;
import org.dbunit.dataset.datatype.DataType;
import org.dbunit.dataset.datatype.DataTypeException;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;

import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;

/**
 * @author Manuel Laflamme
 * @since Feb 21, 2003
 * @version $Revision$
 */
class XlsTable extends AbstractTable
{
    private final ITableMetaData _metaData;
    private final HSSFSheet _sheet;

    public XlsTable(String sheetName, HSSFSheet sheet) throws DataSetException
    {
        int rowCount = sheet.getLastRowNum();
        if (rowCount > 0)
        {
            _metaData = createMetaData(sheetName, sheet.getRow(0));
        }
        else
        {
            _metaData = new DefaultTableMetaData(sheetName, new Column[0]);
        }

        _sheet = sheet;
    }

    static ITableMetaData createMetaData(String tableName, HSSFRow sampleRow)
    {
        List columnList = new ArrayList();
        for (int i = 0; ; i++)
        {
            HSSFCell cell = sampleRow.getCell((short)i);
            if (cell == null)
            {
                break;
            }

            Column column = new Column(cell.getStringCellValue(),
                    DataType.UNKNOWN);
            columnList.add(column);
        }
        Column[] columns = (Column[])columnList.toArray(new Column[0]);
        return new DefaultTableMetaData(tableName, columns);
    }

    ////////////////////////////////////////////////////////////////////////////
    // ITable interface

    public int getRowCount()
    {
        return _sheet.getLastRowNum();
    }

    public ITableMetaData getTableMetaData()
    {
        return _metaData;
    }

    public Object getValue(int row, String column) throws DataSetException
    {
        assertValidRowIndex(row);

        int columnIndex = getColumnIndex(column);
        HSSFCell cell = _sheet.getRow(row + 1).getCell((short)columnIndex);
        if (cell == null)
        {
            return null;
        }

        int type = cell.getCellType();
        switch (type)
        {
            case HSSFCell.CELL_TYPE_NUMERIC:
                if (HSSFDateUtil.isCellDateFormatted(cell))
                {
                    return cell.getDateCellValue();
                }
                return new BigDecimal(cell.getNumericCellValue());

            case HSSFCell.CELL_TYPE_STRING:
                return cell.getStringCellValue();

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
}