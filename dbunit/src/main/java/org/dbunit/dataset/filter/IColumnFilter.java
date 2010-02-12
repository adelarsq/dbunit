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
 * A filter for database columns.
 * 
 * <p> Instances of this interface may be passed to the 
 * <code>{@link org.dbunit.dataset.FilteredTableMetaData#FilteredTableMetaData(org.dbunit.dataset.ITableMetaData, IColumnFilter)}</code> 
 * method of the <code>{@link org.dbunit.dataset.FilteredTableMetaData}</code> class.
 * 
 * @author Manuel Laflamme
 * @since Apr 17, 2004
 * @version $Revision$
 */
public interface IColumnFilter
{
	/**
	 * Tests whether or not the specified column of the specified tableName
	 * should be included by this filter.
	 *
	 * @param tableName The tableName to be tested
	 * @param column The column to be tested
	 * @return <code>true</code> if and only if the given parameter set should be included
	 */
	public boolean accept(String tableName, Column column);
}
