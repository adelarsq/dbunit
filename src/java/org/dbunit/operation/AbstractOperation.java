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
package org.dbunit.operation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Manuel Laflamme
 * @since Jan 17, 2004
 * @version $Revision$
 */
public abstract class AbstractOperation extends DatabaseOperation
{

    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(AbstractOperation.class);

    protected String getQualifiedName(String prefix, String name, IDatabaseConnection connection)
    {
        logger.debug("getQualifiedName(prefix=" + prefix + ", name=" + name + ", connection=" + connection
                + ") - start");

        String escapePattern = (String)connection.getConfig().getProperty(
                DatabaseConfig.PROPERTY_ESCAPE_PATTERN);

        return DataSetUtils.getQualifiedName(prefix, name, escapePattern);
    }

    /**
     * Returns the metadata to use in this operation.
     *
     * @param connection the database connection
     * @param metaData the xml table metadata
     */
    static ITableMetaData getOperationMetaData(IDatabaseConnection connection,
            ITableMetaData metaData) throws DatabaseUnitException, SQLException
    {
        logger.debug("getOperationMetaData(connection=" + connection + ", metaData=" + metaData + ") - start");

        IDataSet databaseDataSet = connection.createDataSet();
        String tableName = metaData.getTableName();

        ITableMetaData databaseMetaData = databaseDataSet.getTableMetaData(tableName);
        Column[] databaseColumns = databaseMetaData.getColumns();
        Column[] columns = metaData.getColumns();

        List columnList = new ArrayList();
        for (int j = 0; j < columns.length; j++)
        {
            String columnName = columns[j].getColumnName();
            Column column = DataSetUtils.getColumn(
                    columnName, databaseColumns);
            if (column == null)
            {
                throw new NoSuchColumnException(tableName + "." + columnName);
            }
            columnList.add(column);
        }

        return new DefaultTableMetaData(databaseMetaData.getTableName(),
                (Column[])columnList.toArray(new Column[0]),
                databaseMetaData.getPrimaryKeys());
    }
}
