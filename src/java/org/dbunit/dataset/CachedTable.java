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
package org.dbunit.dataset;

import java.util.List;
import java.util.ArrayList;

/**
 * @author Manuel Laflamme
 * @since Apr 10, 2003
 * @version $Revision$
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
            if (columns.length > 0)
            {
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
        }
        catch(RowOutOfBoundsException e)
        {
            // end of table
        }
        return rowList;
    }
}
