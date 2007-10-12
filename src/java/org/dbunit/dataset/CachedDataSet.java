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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.dbunit.dataset.stream.IDataSetConsumer;
import org.dbunit.dataset.stream.IDataSetProducer;

import java.util.ArrayList;
import java.util.List;

/**
 * Hold copy of another dataset or a consumed provider content.
 *
 * @author Manuel Laflamme
 * @since Apr 18, 2003
 * @version $Revision$
 */
public class CachedDataSet extends AbstractDataSet implements IDataSetConsumer
{

    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(CachedDataSet.class);

    private ITable[] _tables;

    private List _tableList;
    private DefaultTable _activeTable;

    /**
     * Default constructor.
     */
    public CachedDataSet()
    {
    }

    /**
     * Creates a copy of the specified dataset.
     */
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

    /**
     * Creates a CachedDataSet that syncronously consume the specified producer.
     */
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
        logger.debug("createIterator(reversed=" + reversed + ") - start");

        return new DefaultTableIterator(_tables, reversed);
    }

    ////////////////////////////////////////////////////////////////////////
    // IDataSetConsumer interface

    public void startDataSet() throws DataSetException
    {
        logger.debug("startDataSet() - start");

        _tableList = new ArrayList();
        _tables = null;
    }

    public void endDataSet() throws DataSetException
    {
        logger.debug("endDataSet() - start");

        _tables = (ITable[])_tableList.toArray(new ITable[0]);
        _tableList = null;
    }

    public void startTable(ITableMetaData metaData) throws DataSetException
    {
        logger.debug("startTable(metaData=" + metaData + ") - start");

        _activeTable = new DefaultTable(metaData);
//        System.out.println("START " + _activeMetaData.getTableName());
    }

    public void endTable() throws DataSetException
    {
        logger.debug("endTable() - start");

//         System.out.println("END " + _activeMetaData.getTableName());
        _tableList.add(_activeTable);
        _activeTable = null;
    }

    public void row(Object[] values) throws DataSetException
    {
        logger.debug("row(values=" + values + ") - start");

        _activeTable.addRow(values);
    }
}
