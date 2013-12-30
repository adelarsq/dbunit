/*
 *
 *  The DbUnit Database Testing Framework
 *  Copyright (C)2002-2008, DbUnit.org
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package org.dbunit.assertion;

import java.util.Arrays;

import org.dbunit.dataset.Column;
import org.dbunit.dataset.ColumnFilterTable;
import org.dbunit.dataset.Columns;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.ITableMetaData;
import org.dbunit.dataset.NoSuchColumnException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default implementation of the {@link FailureHandler}.
 * 
 * @author gommma (gommma AT users.sourceforge.net)
 * @since 2.4.0
 */
public class DefaultFailureHandler implements FailureHandler
{
    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(DefaultFailureHandler.class);

    private String[] _additionalColumnInfo;

    private FailureFactory failureFactory = new DefaultFailureFactory();

    /**
     * Default constructor which does not provide any additional column information.
     */
    public DefaultFailureHandler()
    {
    }

    /**
     * Create a default failure handler
     * @param additionalColumnInfo the column names of the columns for which additional
     * information should be printed when an assertion failed.
     */
    public DefaultFailureHandler(Column[] additionalColumnInfo)
    {
        // Null-safe access
        if (additionalColumnInfo != null) {
            this._additionalColumnInfo = Columns.getColumnNames(additionalColumnInfo);
        }
    }

    /**
     * Create a default failure handler
     * @param additionalColumnInfo the column names of the columns for which additional
     * information should be printed when an assertion failed.
     */
    public DefaultFailureHandler(String[] additionalColumnInfo)
    {
        this._additionalColumnInfo = additionalColumnInfo;
    }

    /**
     * @param failureFactory The {@link FailureFactory} to be used for creating assertion
     * errors.
     */
    public void setFailureFactory(FailureFactory failureFactory)
    {
        if (failureFactory == null) {
            throw new NullPointerException(
                    "The parameter 'failureFactory' must not be null");
        }
        this.failureFactory = failureFactory;
    }

    public Error createFailure(String message, String expected, String actual)
    {
        return this.failureFactory.createFailure(message, expected, actual);
    }

    public Error createFailure(String message)
    {
        return this.failureFactory.createFailure(message);
    }

    public String getAdditionalInfo(ITable expectedTable, ITable actualTable,
            int row, String columnName)
    {
        // add custom column values information for better identification of mismatching rows
        String additionalInfo = buildAdditionalColumnInfo(expectedTable, actualTable, row);
        return additionalInfo;
    }

    private String buildAdditionalColumnInfo(ITable expectedTable, ITable actualTable, int rowIndex)
    {
        if(logger.isDebugEnabled())
        {
            logger.debug("buildAdditionalColumnInfo(expectedTable={}, actualTable={}, rowIndex={}, " +
                    "additionalColumnInfo={}) - start",
                    new Object[] {expectedTable, actualTable, new Integer(rowIndex), _additionalColumnInfo} );
        }

        // No columns specified
        if(_additionalColumnInfo == null || _additionalColumnInfo.length <= 0) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Additional row info:");
        for (int j = 0; j < _additionalColumnInfo.length; j++) {
            String columnName = _additionalColumnInfo[j];

            Object expectedKeyValue =
                    getColumnValue(expectedTable, rowIndex, columnName);
            Object actualKeyValue =
                    getColumnValue(actualTable, rowIndex, columnName);

            sb.append(" ('");
            sb.append(columnName);
            sb.append("': expected=<");
            sb.append(expectedKeyValue);
            sb.append(">, actual=<");
            sb.append(actualKeyValue);
            sb.append(">)");
        }

        return sb.toString();
    }

    protected Object getColumnValue(ITable table, int rowIndex,
            String columnName)
    {
        Object value = null;
        try
        {
            // Get the ITable object to be used for showing the column values
            // (needed in case of Filtered tables)
            ITable tableForCol = getTableForColumn(table, columnName);
            value = tableForCol.getValue(rowIndex, columnName);
        }
        catch (DataSetException e)
        {
            value = makeAdditionalColumnInfoErrorMessage(columnName, e);
        }
        return value;
    }

    protected String makeAdditionalColumnInfoErrorMessage(String columnName,
            DataSetException e)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("Exception creating more info for column '");
        sb.append(columnName);
        sb.append("': ");
        sb.append(e.getClass().getName());
        sb.append(": ");
        sb.append(e.getMessage());
        String msg = sb.toString();

        logger.warn(msg, e);

        return " (!!!!! " + msg + ")";
    }

    /**
     * @param table The table which might be a decorated table
     * @param columnName The column name for which a table is searched
     * @return The table that as a column with the given name
     * @throws DataSetException If no table could be found having a column with the given name
     */
    private ITable getTableForColumn(ITable table, String columnName) throws DataSetException
    {
        ITableMetaData tableMetaData = table.getTableMetaData();
        try
        {
            tableMetaData.getColumnIndex(columnName);
            // if the column index was resolved the table contains the given column.
            // So just use this table
            return table;
        }
        catch(NoSuchColumnException e)
        {
            // If the column was not found check for filtered table
            if(table instanceof ColumnFilterTable)
            {
                ITableMetaData originalMetaData = ((ColumnFilterTable)table).getOriginalMetaData();
                originalMetaData.getColumnIndex(columnName);
                // If we get here the column exists - return the table since it is not filtered
                // in the CompositeTable.
                return table;
            }
            else
            {
                // Column not available in the table - rethrow the exception
                throw e;
            }
        }
    }

    public void handle(Difference diff)
    {
        String msg = buildMessage(diff);

        Error err = this.createFailure(msg,
                String.valueOf(diff.getExpectedValue()), String.valueOf(diff.getActualValue()));
        // Throw the assertion error
        throw err;
    }

    protected String buildMessage(Difference diff)
    {
        int row = diff.getRowIndex();
        String columnName = diff.getColumnName();
        String tableName = diff.getExpectedTable().getTableMetaData().getTableName();

        // example message:
        // "value (table=MYTAB, row=232, column=MYCOL, Additional row info: (column=MyIdCol, expected=444, actual=555)): expected:<123> but was:<1234>"
        String msg = "value (table=" + tableName + ", row=" + row + ", col=" + columnName;

        String additionalInfo = this.getAdditionalInfo(
                diff.getExpectedTable(), diff.getActualTable(), row, columnName);
        if (additionalInfo != null && !additionalInfo.trim().equals(""))
        {
            msg += ", " + additionalInfo;
        }
        msg += ")";

        return msg;
    }

    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append(DefaultFailureHandler.class.getName()).append("[");
        sb.append("_additionalColumnInfo=").append(
                _additionalColumnInfo==null ? "null" : Arrays.asList(_additionalColumnInfo).toString());
        sb.append("]");
        return sb.toString();
    }

    /**
     * Default failure factory which returns DBUnits own assertion error instances.
     * 
     * @author gommma (gommma AT users.sourceforge.net)
     * @author Last changed by: $Author: gommma $
     * @version $Revision: 872 $ $Date: 2008-11-08 09:45:52 -0600 (Sat, 08 Nov 2008) $
     * @since 2.4.0
     */
    public static class DefaultFailureFactory implements FailureFactory
    {
        public Error createFailure(String message, String expected, String actual)
        {
            // Return dbunit's own comparison failure object
            return new DbComparisonFailure(message, expected, actual);
        }

        public Error createFailure(String message)
        {
            // Return dbunit's own failure object
            return new DbAssertionFailedError(message);
        }
    }
}
