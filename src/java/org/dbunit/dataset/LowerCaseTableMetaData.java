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

package org.dbunit.dataset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Specialized ITableMetaData implementation that convert the table name and
 * column names to lower case. Used in DbUnit own test suite to verify that
 * operations are case insensitive.
 *
 * @author Manuel Laflamme
 * @version $Revision$
 * @since Feb 14, 2003
 */
public class LowerCaseTableMetaData extends AbstractTableMetaData
{

    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(LowerCaseTableMetaData.class);

    private final String _tableName;
    private final Column[] _columns;
    private final Column[] _primaryKeys;

    public LowerCaseTableMetaData(String tableName, Column[] columns)
            //throws DataSetException
    {
        this(tableName, columns, new Column[0]);
    }

    public LowerCaseTableMetaData(String tableName, Column[] columns,
            String[] primaryKeys) //throws DataSetException
    {
        this(tableName, columns, getPrimaryKeys(columns, primaryKeys));
    }

    public LowerCaseTableMetaData(ITableMetaData metaData) throws DataSetException
    {
        this(metaData.getTableName(), metaData.getColumns(),
                metaData.getPrimaryKeys());
    }

    public LowerCaseTableMetaData(String tableName, Column[] columns,
            Column[] primaryKeys) //throws DataSetException
    {
        _tableName = tableName.toLowerCase();
        _columns = createLowerColumns(columns);
        _primaryKeys = createLowerColumns(primaryKeys);
    }

    private Column[] createLowerColumns(Column[] columns)
    {
        logger.debug("createLowerColumns(columns=" + columns + ") - start");

        Column[] lowerColumns = new Column[columns.length];
        for (int i = 0; i < columns.length; i++)
        {
            lowerColumns[i] = createLowerColumn(columns[i]);
        }

        return lowerColumns;
    }

    private Column createLowerColumn(Column column)
    {
        logger.debug("createLowerColumn(column=" + column + ") - start");

        return new Column(
                column.getColumnName().toLowerCase(),
                column.getDataType(),
                column.getSqlTypeName(),
                column.getNullable());
    }

    ////////////////////////////////////////////////////////////////////////////
    // ITableMetaData interface

    public String getTableName()
    {
        logger.debug("getTableName() - start");

        return _tableName;
    }

    public Column[] getColumns()
    {
        logger.debug("getColumns() - start");

        return _columns;
    }

    public Column[] getPrimaryKeys()
    {
        logger.debug("getPrimaryKeys() - start");

        return _primaryKeys;
    }
}





