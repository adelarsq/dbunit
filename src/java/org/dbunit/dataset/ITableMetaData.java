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

/**
 * Represents table metadata.
 *
 * @author Manuel Laflamme
 * @version $Revision$
 * @since Feb 17, 2002
 */
public interface ITableMetaData
{
    /**
     * Returns this table name.
     */
    public String getTableName();

    /**
     * Returns this table columns.
     */
    public Column[] getColumns() throws DataSetException;

    /**
     * Returns this table primary key columns.
     */
    public Column[] getPrimaryKeys() throws DataSetException;
}






