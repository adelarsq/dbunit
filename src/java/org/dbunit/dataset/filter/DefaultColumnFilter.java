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
package org.dbunit.dataset.filter;

import org.dbunit.dataset.Column;

/**
 * @author Manuel Laflamme
 * @since Apr 17, 2004
 * @version $Revision$
 */
public class DefaultColumnFilter implements IColumnFilter
{
    private final PatternMatcher _includeMatcher = new PatternMatcher();
    private final PatternMatcher _excludeMatcher = new PatternMatcher();

    /**
     * Add a new accepted column name pattern for all tables.
     * The following wildcard characters are supported:
     * '*' matches zero or more characters,
     * '?' matches one character.
     */
    public void includeColumn(String columnPattern)
    {
        _includeMatcher.addPattern(columnPattern);
    }

    /**
     * Add a new refused column name pattern for all tables.
     * The following wildcard characters are supported:
     * '*' matches zero or more characters,
     * '?' matches one character.
     */
    public void excludeColumn(String columnPattern)
    {
        _excludeMatcher.addPattern(columnPattern);
    }

    ////////////////////////////////////////////////////////////////////////////
    // IColumnFilter interface

    public boolean accept(String tableName, Column column)
    {
        if (_includeMatcher.isEmpty() ||
                _includeMatcher.accept(column.getColumnName()))
        {
            return !_excludeMatcher.accept(column.getColumnName());
        }
        return false;
    }
}
