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
package org.dbunit.dataset.filter;

import java.util.HashSet;
import java.util.Set;

/**
 * This filter hides specified tables from the filtered dataset. This
 * implementation do not modify the original table sequence from the filtered
 * dataset and support duplicate table names.
 *
 * @author Manuel Laflamme
 * @since Mar 7, 2003
 * @version $Revision$
 */
public class ExcludeTableFilter extends AbstractTableFilter implements ITableFilter
{
    private Set _forbidenNames = new HashSet();

    /**
     * Create a new empty ExcludeTableFilter. Use {@link #addTableName} to hide
     * some tables.
     */
    public ExcludeTableFilter()
    {
    }

    /**
     * Create a new ExcludeTableFilter which prevent access to specified tables.
     */
    public ExcludeTableFilter(String[] tableNames)
    {
        for (int i = 0; i < tableNames.length; i++)
        {
            String tableName = tableNames[i];
            addTableName(tableName);
        }
    }

    /**
     * Add a new table name to exclude in this filter.
     */
    public void addTableName(String tableName)
    {
        _forbidenNames.add(tableName.toUpperCase());
    }

    ////////////////////////////////////////////////////////////////////////////
    // ITableFilter interface

    public boolean isValidName(String tableName)
    {
        return !_forbidenNames.contains(tableName.toUpperCase());
    }
}
