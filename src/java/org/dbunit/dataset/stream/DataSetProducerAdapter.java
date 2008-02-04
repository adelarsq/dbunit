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
package org.dbunit.dataset.stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.dbunit.dataset.*;


/**
 * @author Manuel Laflamme
 * @since Apr 17, 2003
 * @version $Revision$
 */
public class DataSetProducerAdapter implements IDataSetProducer
{

    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(DataSetProducerAdapter.class);

    private static final IDataSetConsumer EMPTY_CONSUMER = new DefaultConsumer();

    private final ITableIterator _iterator;
    private IDataSetConsumer _consumer = EMPTY_CONSUMER;

    public DataSetProducerAdapter(ITableIterator iterator)
    {
        _iterator = iterator;
    }

    public DataSetProducerAdapter(IDataSet dataSet) throws DataSetException
    {
        _iterator = dataSet.iterator();
    }

    ////////////////////////////////////////////////////////////////////////////
    // IDataSetProducer interface

    public void setConsumer(IDataSetConsumer consumer) throws DataSetException
    {
        logger.debug("setConsumer(consumer) - start");

        _consumer = consumer;
    }

    public void produce() throws DataSetException
    {
        logger.debug("produce() - start");

        _consumer.startDataSet();
        while(_iterator.next())
        {
            ITable table = _iterator.getTable();
            ITableMetaData metaData = table.getTableMetaData();

            _consumer.startTable(metaData);
            try
            {
                Column[] columns = metaData.getColumns();
                if (columns.length == 0)
                {
                    _consumer.endTable();
                    continue;
                }

                for (int i = 0; ; i++)
                {
                    Object[] values = new Object[columns.length];
                    for (int j = 0; j < columns.length; j++)
                    {
                        Column column = columns[j];
                        values[j] = table.getValue(i, column.getColumnName());
                    }
                    _consumer.row(values);
                }
            }
            catch (RowOutOfBoundsException e)
            {
                logger.error("produce()", e);

                // end of table
                _consumer.endTable();
            }
        }
        _consumer.endDataSet();
    }
}

