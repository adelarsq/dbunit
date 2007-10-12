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

import java.util.Arrays;


/**
 * @author Manuel Laflamme
 * @version $Revision$
 * @since Feb 17, 2002
 */
public class DefaultTableMetaData extends AbstractTableMetaData
{

    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(DefaultTableMetaData.class);

    private final String _tableName;
    private final Column[] _columns;
    private final Column[] _primaryKeys;

    public DefaultTableMetaData(String tableName, Column[] columns)
            //throws DataSetException
    {
        this(tableName, columns, new String[0]);
    }

    public DefaultTableMetaData(String tableName, Column[] columns,
            String[] primaryKeys) //throws DataSetException
    {
        _tableName = tableName;
        _columns = columns;
        _primaryKeys = getPrimaryKeys(columns, primaryKeys);
    }

    public DefaultTableMetaData(String tableName, Column[] columns,
            Column[] primaryKeys) //throws DataSetException
    {
        _tableName = tableName;
        _columns = columns;
        _primaryKeys = primaryKeys;
    }

    public String toString()
    {
        logger.debug("toString() - start");

        return "tableName=" + _tableName +
                ", columns=" + Arrays.asList(_columns) +
                ", keys=" + Arrays.asList(_primaryKeys) + "";
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





