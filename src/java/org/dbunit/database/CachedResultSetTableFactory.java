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
package org.dbunit.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.ITableMetaData;

import java.sql.SQLException;

/**
 *
 * @author manuel.laflamme$
 * @since Jul 31, 2003$
 * @version $Revision$
 */
public class CachedResultSetTableFactory implements IResultSetTableFactory
{

    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(CachedResultSetTableFactory.class);

    public IResultSetTable createTable(String tableName, String selectStatement,
            IDatabaseConnection connection) throws SQLException, DataSetException
    {
        logger.debug("createTable(tableName=" + tableName + ", selectStatement=" + selectStatement + ", connection="
                + connection + ") - start");

        return new CachedResultSetTable(new ForwardOnlyResultSetTable(
                tableName, selectStatement, connection));
    }

    public IResultSetTable createTable(ITableMetaData metaData,
            IDatabaseConnection connection) throws SQLException, DataSetException
    {
        logger.debug("createTable(metaData=" + metaData + ", connection=" + connection + ") - start");

        return new CachedResultSetTable(metaData, connection);
    }
}
