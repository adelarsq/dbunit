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

import org.dbunit.dataset.IRowValueProvider;

/**
 * Interface to filter out rows by checking specific column values provided by the {@link IRowValueProvider}.
 * 
 * @author gommma
 * @author Last changed by: $Author$
 * @version $Revision$ $Date$
 * @since 2.3.0
 */
public interface IRowFilter {
	/**
	 * Checks if the current row should be accepted or not
	 * @param rowValueProvider provides arbitrary column values of the current row to be checked.
	 * @return True if the given value (and so that current row) should be accepted
	 */
	public boolean accept(IRowValueProvider rowValueProvider);
}