/*
 * IDataSet.java   Feb 17, 2002
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

package org.dbunit.dataset;

import org.dbunit.database.AmbiguousTableNameException;

/**
 * Represents a collection of tables.
 *
 * @author Manuel Laflamme
 * @version $Revision$
 */
public interface IDataSet
{
    /**
     * Returns table names this dataset contains. Multiple occurence of a name
     * may be present in the returned list if the dataset contains multiple
     * tables having the same name.
     */
    public String[] getTableNames() throws DataSetException;

    /**
     * Returns the specified table metadata.
     *
     * @throws AmbiguousTableNameException if dataset contains multiple tables
     *      having the specified name. Use {@link #getTables} to access
     *      to all tables.
     * @throws NoSuchTableException if dataset do not contains the specified
     *      table
     */
    public ITableMetaData getTableMetaData(String tableName)
            throws DataSetException;

    /**
     * Returns the specified table.
     *
     * @throws AmbiguousTableNameException if dataset contains multiple tables
     *      having the specified name. Use {@link #getTables} to access
     *      to all tables.
     * @throws NoSuchTableException if dataset do not contains the specified
     *      table
     */
    public ITable getTable(String tableName) throws DataSetException;

    /**
     * Returns tables contained by this dataset. Multiple table having the same
     * name but different data may be returned.
     */
    public ITable[] getTables() throws DataSetException;
}






