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

import org.dbunit.dataset.AbstractDataSet;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.ITableIterator;
import org.dbunit.dataset.ITableMetaData;
import org.dbunit.dataset.IDataSetProducer;

/**
 * @author Manuel Laflamme
 * @since Apr 18, 2003
 * @version $Revision$
 */
public class StreamingDataSet extends AbstractDataSet
{
    private IDataSetProducer _source;
    private int _iteratorCount;

    public StreamingDataSet(IDataSetProducer source)
    {
        _source = source;
    }

    ////////////////////////////////////////////////////////////////////////////
    // AbstractDataSet class

    protected ITableIterator createIterator(boolean reversed)
            throws DataSetException
    {
        if (reversed)
        {
            throw new UnsupportedOperationException(
                    "Reverse iterator not supported!");
        }

        if (_iteratorCount > 0)
        {
            throw new UnsupportedOperationException(
                    "Only one iterator allowed!");
        }

        _iteratorCount++;
        return new StreamingIterator(_source);
    }

    ////////////////////////////////////////////////////////////////////////////
    // IDataSet interface

    public String[] getTableNames() throws DataSetException
    {
        throw new UnsupportedOperationException();
    }

    public ITableMetaData getTableMetaData(String tableName) throws DataSetException
    {
        throw new UnsupportedOperationException();
    }

    public ITable getTable(String tableName) throws DataSetException
    {
        throw new UnsupportedOperationException();
    }

}
