/*
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

import org.dbunit.dataset.DefaultDataSet;
import org.dbunit.dataset.ITableIterator;
import org.dbunit.dataset.AbstractDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.DefaultTableIterator;
import org.dbunit.dataset.DefaultTable;

import com.mockobjects.Verifiable;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author Manuel Laflamme
 * @since Apr 12, 2003
 * @version $Revision$
 */
public class MockDataSet extends AbstractDataSet implements Verifiable
{
    private final List _tableList = new ArrayList();

    public void addTable(ITable table)
    {
        _tableList.add(table);
    }

    public void addEmptyTable(String tableName)
    {
        _tableList.add(new DefaultTable(tableName));
    }

    ////////////////////////////////////////////////////////////////////////////
    // AbstractDataSet class

    protected ITableIterator createIterator(boolean reversed)
            throws DataSetException
    {
        ITable[] tables = (ITable[])_tableList.toArray(new ITable[0]);
        return new DefaultTableIterator(tables, reversed);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Verifiable interface

    public void verify()
    {
        for (Iterator it = _tableList.iterator(); it.hasNext();)
        {
            ITable table = (ITable)it.next();
            if (table instanceof Verifiable)
            {
                ((Verifiable)table).verify();
            }
        }
    }
}
