/*
 *
 * The DbUnit Database Testing Framework
 * Copyright (C)2002-2008, DbUnit.org
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
package org.dbunit;

/**
 * Defines a database table to verify (assert on data), specifying include and
 * exclude column filters.
 * 
 * @author Jeff Jensen jeffjensen AT users.sourceforge.net
 * @author Last changed by: $Author$
 * @version $Revision$ $Date$
 * @since 2.4.8
 */
public class VerifyTableDefinition {
    /** The name of the table. */
    private final String tableName;

    /** The columns to exclude in table comparisons. */
    private final String[] columnExclusionFilters;

    /** The columns to include in table comparisons. */
    private final String[] columnInclusionFilters;

    /**
     * Create a valid instance with no include columns specified (meaning
     * include all columns).
     * 
     * @param table
     *            The name of the table.
     * @param excludeColumns
     *            The columns in the table to ignore (filter out) in expected vs
     *            actual comparisons.
     */
    public VerifyTableDefinition(String table, String[] excludeColumns) {
        this(table, excludeColumns, new String[] {});
    }

    /**
     * Create a valid instance specifying exclude and include columns.
     * 
     * @param table
     *            The name of the table.
     * @param excludeColumns
     *            The columns in the table to ignore (filter out) in expected vs
     *            actual comparisons.
     * @param includeColumns
     *            The columns in the table to include in expected vs actual
     *            comparisons.
     */
    public VerifyTableDefinition(String table, String[] excludeColumns,
            String[] includeColumns) {
        if (table == null) {
            throw new IllegalArgumentException("table is null.");
        }

        if (excludeColumns == null) {
            throw new IllegalArgumentException("excludeColumns is null.");
        }

        if (includeColumns == null) {
            throw new IllegalArgumentException("includeColumns is null.");
        }

        tableName = table;
        columnExclusionFilters = excludeColumns;
        columnInclusionFilters = includeColumns;
    }

    public String getTableName() {
        return tableName;
    }

    public String[] getColumnExclusionFilters() {
        return columnExclusionFilters;
    }

    public String[] getColumnInclusionFilters() {
        return columnInclusionFilters;
    }
}
