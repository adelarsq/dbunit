/*
 * CachedResultSetTable.java   Feb 20, 2002
 *
 * The dbUnit database testing framework.
 * Copyright (C) 2002   Manuel Laflamme
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

package org.dbunit.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.dbunit.dataset.*;

/**
 * @author Manuel Laflamme
 * @version 1.0
 */
public class CachedResultSetTable extends DefaultTable
{
    public CachedResultSetTable(ITableMetaData metaData, ResultSet resultSet)
            throws SQLException
    {
        super(metaData, createList(metaData, resultSet));
    }

    private static List createList(ITableMetaData metaData, ResultSet resultSet)
            throws SQLException
    {
        List list = new ArrayList();
        Column[] columns = metaData.getColumns();
        while (resultSet.next())
        {
            Object[] row = new Object[columns.length];
            for (int i = 0; i < columns.length; i++)
            {
                Object value = resultSet.getObject(columns[i].getColumnName());
                row[i] = value;
            }

            list.add(row);
        }
        return list;
    }

}
