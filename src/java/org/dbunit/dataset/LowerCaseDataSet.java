/*
 * DefaultDataSet.java   Feb 14, 2003
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
 * Specialized IDataSet decorator that convert the table name and
 * column names to lower case. Used in DbUnit own test suite to verify that
 * operations are case insensitive.
 *
 * @author Manuel Laflamme
 * @version $Revision$
 */
public class LowerCaseDataSet extends AbstractDataSet
{
    private final ITable[] _tables;

    public LowerCaseDataSet(ITable table) throws DataSetException
    {
        this(new ITable[]{table});
    }

    public LowerCaseDataSet(IDataSet dataSet) throws DataSetException
    {
        this(dataSet.getTables());
    }

    public LowerCaseDataSet(ITable[] tables) throws DataSetException
    {
        ITable[] lowerTables = new ITable[tables.length];
        for (int i = 0; i < tables.length; i++)
        {
            lowerTables[i] = createLowerTable(tables[i]);
        }
        _tables = lowerTables;
    }

    private ITable createLowerTable(ITable table) throws DataSetException
    {
        return new CompositeTable(
                new LowerCaseTableMetaData(table.getTableMetaData()), table);
    }

    ////////////////////////////////////////////////////////////////////////////
    // IDataSet interface

    public ITable[] getTables() throws DataSetException
    {
        return cloneTables(_tables);
    }
}






