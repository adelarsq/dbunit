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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.dbunit.dataset.Column;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.CompositeTable;
import org.dbunit.dataset.FilteredTableMetaData;
import org.dbunit.dataset.DataSetException;

/**
 * Implementation of the IColumnFilter interface that exposes columns matching
 * include patterns and not matching exclude patterns.
 *
 * @author Manuel Laflamme
 * @since Apr 17, 2004
 * @version $Revision$
 */
public class DefaultColumnFilter implements IColumnFilter
{

    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(DefaultColumnFilter.class);

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
        logger.debug("includeColumn(columnPattern=" + columnPattern + ") - start");

        _includeMatcher.addPattern(columnPattern);
    }

    /**
     * Add specified columns to accepted column name list.
     */
    public void includeColumns(Column[] columns)
    {
        logger.debug("includeColumns(columns=" + columns + ") - start");

        for (int i = 0; i < columns.length; i++)
        {
            _includeMatcher.addPattern(columns[i].getColumnName());
        }
    }

    /**
     * Add a new refused column name pattern for all tables.
     * The following wildcard characters are supported:
     * '*' matches zero or more characters,
     * '?' matches one character.
     */
    public void excludeColumn(String columnPattern)
    {
        logger.debug("excludeColumn(columnPattern=" + columnPattern + ") - start");

        _excludeMatcher.addPattern(columnPattern);
    }

    /**
     * Add specified columns to excluded column name list.
     */
    public void excludeColumns(Column[] columns)
    {
        logger.debug("excludeColumns(columns=" + columns + ") - start");

        for (int i = 0; i < columns.length; i++)
        {
            _excludeMatcher.addPattern(columns[i].getColumnName());
        }
    }

    /**
     * Returns a table backed by the specified table that only exposes specified
     * columns.
     */
    public static ITable includedColumnsTable(ITable table, String[] columnNames)
            throws DataSetException
    {
        logger.debug("includedColumnsTable(table=" + table + ", columnNames=" + columnNames + ") - start");

        DefaultColumnFilter columnFilter = new DefaultColumnFilter();
        for (int i = 0; i < columnNames.length; i++)
        {
            String columnName = columnNames[i];
            columnFilter.includeColumn(columnName);
        }

        FilteredTableMetaData metaData = new FilteredTableMetaData(
                table.getTableMetaData(), columnFilter);
        return new CompositeTable(metaData, table);
    }

    /**
     * Returns a table backed by the specified table that only exposes specified
     * columns.
     */
    public static ITable includedColumnsTable(ITable table, Column[] columns)
            throws DataSetException
    {
        logger.debug("includedColumnsTable(table=" + table + ", columns=" + columns + ") - start");

        DefaultColumnFilter columnFilter = new DefaultColumnFilter();
        columnFilter.includeColumns(columns);

        FilteredTableMetaData metaData = new FilteredTableMetaData(
                table.getTableMetaData(), columnFilter);
        return new CompositeTable(metaData, table);
    }

    /**
     * Returns a table backed by the specified table but with specified
     * columns excluded.
     */
    public static ITable excludedColumnsTable(ITable table, String[] columnNames)
            throws DataSetException
    {
        logger.debug("excludedColumnsTable(table=" + table + ", columnNames=" + columnNames + ") - start");

        DefaultColumnFilter columnFilter = new DefaultColumnFilter();
        for (int i = 0; i < columnNames.length; i++)
        {
            String columnName = columnNames[i];
            columnFilter.excludeColumn(columnName);
        }

        FilteredTableMetaData metaData = new FilteredTableMetaData(
                table.getTableMetaData(), columnFilter);
        return new CompositeTable(metaData, table);
    }

    /**
     * Returns a table backed by the specified table but with specified
     * columns excluded.
     */
    public static ITable excludedColumnsTable(ITable table, Column[] columns)
            throws DataSetException
    {
        logger.debug("excludedColumnsTable(table=" + table + ", columns=" + columns + ") - start");

        DefaultColumnFilter columnFilter = new DefaultColumnFilter();
        columnFilter.excludeColumns(columns);

        FilteredTableMetaData metaData = new FilteredTableMetaData(
                table.getTableMetaData(), columnFilter);
        return new CompositeTable(metaData, table);
    }

    ////////////////////////////////////////////////////////////////////////////
    // IColumnFilter interface

    public boolean accept(String tableName, Column column)
    {
        logger.debug("accept(tableName=" + tableName + ", column=" + column + ") - start");

        if (_includeMatcher.isEmpty() ||
                _includeMatcher.accept(column.getColumnName()))
        {
            return !_excludeMatcher.accept(column.getColumnName());
        }
        return false;
    }
}
