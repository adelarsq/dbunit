/*
 * AbstractTableMetaData.java   Mar 8, 2002
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

package org.dbunit.dataset;

import java.util.List;
import java.util.ArrayList;

/**
 * @author Manuel Laflamme
 * @version 1.0
 */
public abstract class AbstractTableMetaData implements ITableMetaData
{
    protected Column[] getPrimaryKeys(Column[] columns, String[] keyNames)
    {
        List keyList = new ArrayList();
        for (int i = 0; i < keyNames.length; i++)
        {
            Column primaryKey = DataSetUtils.getColumn(keyNames[i], columns);
            if (primaryKey != null)
            {
                keyList.add(primaryKey);
            }
//            else
//            {
//                // primary key not found in table
//                if (_primaryKeys[i] == null)
//                {
//                    throw new NoPrimaryKeyException("<" + primaryKeys[i] +
//                            "> not found in table <" + tableName + ">");
//                }
//            }
        }

        return (Column[])keyList.toArray(new Column[0]);
    }
}


