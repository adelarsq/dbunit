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

import java.util.ArrayList;
import java.util.List;

/**
 * @author Manuel Laflamme
 * @since Apr 18, 2003
 * @version $Revision$
 */
public class CachedDataSet extends AbstractDataSet implements IDataSetConsumer
{
    private ITable[] _tables;

    private List _tableList;
    private ITableMetaData _activeMetaData;
    private List _activeRowList;

    public CachedDataSet()
    {
    }

    public CachedDataSet(IDataSet dataSet) throws DataSetException
    {
        List tableList = new ArrayList();
        ITableIterator iterator = dataSet.iterator();
        while (iterator.next())
        {
            tableList.add(new CachedTable(iterator.getTable()));
        }
        _tables = (ITable[])tableList.toArray(new ITable[0]);
    }

    public CachedDataSet(IDataSetProducer producer) throws DataSetException
    {
        producer.setConsumer(this);
        producer.produce();
    }

    ////////////////////////////////////////////////////////////////////////////
    // AbstractDataSet class

    protected ITableIterator createIterator(boolean reversed)
            throws DataSetException
    {
        return new DefaultTableIterator(_tables, reversed);
    }

    ////////////////////////////////////////////////////////////////////////
    // IDataSetConsumer interface

    public void startDataSet() throws DataSetException
    {
        _tableList = new ArrayList();
        _tables = null;
    }

    public void endDataSet() throws DataSetException
    {
        _tables = (ITable[])_tableList.toArray(new ITable[0]);
        _tableList = null;
    }

    public void startTable(ITableMetaData metaData) throws DataSetException
    {
        _activeMetaData = metaData;
        _activeRowList = new ArrayList();
//        System.out.println("START " + _activeMetaData.getTableName());
    }

    public void endTable() throws DataSetException
    {
//         System.out.println("END " + _activeMetaData.getTableName());
        _tableList.add(new DefaultTable(_activeMetaData, _activeRowList));
        _activeRowList = null;
        _activeMetaData = null;
    }

    public void row(Object[] values) throws DataSetException
    {
        _activeRowList.add(values);
    }
}
