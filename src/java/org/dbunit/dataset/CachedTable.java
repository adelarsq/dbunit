package org.dbunit.dataset;

import java.util.List;
import java.util.ArrayList;

/**
 *
 * <p> Copyright (c) 2002 OZ.COM.  All Rights Reserved. </p>
 * @author manuel.laflamme$
 * @since Apr 10, 2003$
 */
public class CachedTable extends DefaultTable
{
    public CachedTable(ITable table) throws DataSetException
    {
        super(table.getTableMetaData(), createRowList(table));
    }

    protected CachedTable(ITableMetaData metaData)
    {
        super(metaData);
    }

    protected static List createRowList(ITable table) throws DataSetException
    {
        List rowList = new ArrayList();
        try
        {
            Column[] columns = table.getTableMetaData().getColumns();

            for (int i = 0; ; i++)
            {
                Object[] rowValues = new Object[columns.length];
                for (int j = 0; j < columns.length; j++)
                {
                    Column column = columns[j];
                    rowValues[j] = table.getValue(i, column.getColumnName());
                }
                rowList.add(rowValues);
            }
        }
        catch(RowOutOfBoundsException e)
        {
            // end of table
        }
        return rowList;
    }
}
