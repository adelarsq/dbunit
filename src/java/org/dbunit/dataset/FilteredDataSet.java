/*
 * FilteredDataSet.java   Feb 22, 2002
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


/**
 * Decorates a dataset and exposes only some tables from it.
 *
 * @author Manuel Laflamme
 * @version $Revision$
 */
public class FilteredDataSet extends AbstractDataSet
{
    private final IDataSet _dataSet;
    private final String[] _tableNames;

    /**
     * Creates a FilteredDataSet that decorates the specified dataset and
     * exposes only the specified tables.
     */
    public FilteredDataSet(String[] tableNames, IDataSet dataSet)
    {
        _tableNames = tableNames;
        _dataSet = dataSet;
    }

    ////////////////////////////////////////////////////////////////////////////
    // AbstractDataSet class

    protected ITable[] getTables() throws DataSetException
    {
        return DataSetUtils.getTables(_tableNames, _dataSet);
    }
}





