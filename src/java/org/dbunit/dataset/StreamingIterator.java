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

import org.dbunit.DatabaseUnitRuntimeException;
import org.dbunit.dataset.AbstractTable;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.ITableIterator;
import org.dbunit.dataset.ITableMetaData;
import org.dbunit.dataset.RowOutOfBoundsException;
import org.dbunit.dataset.IDataSetConsumer;
import org.dbunit.dataset.IDataSetProducer;

import org.dbunit.util.concurrent.BoundedBuffer;
import org.dbunit.util.concurrent.Channel;
import org.dbunit.util.concurrent.Puttable;
import org.dbunit.util.concurrent.Takable;

/**
 * @author Manuel Laflamme
 * @since Apr 17, 2003
 * @version $Revision$
 */
public class StreamingIterator implements ITableIterator
{
    private static final Object EOD = new Object(); // end of dataset marker

    private final Takable _channel;
    private StreamingTable _activeTable;
    private Object _taken = null;
    private boolean _eod = false;

    public StreamingIterator(IDataSetProducer source) throws DataSetException
    {
        Channel channel = new BoundedBuffer(30);
        _channel = channel;

        AsynchronousConsumer consumer = new AsynchronousConsumer(source, channel);
        Thread thread = new Thread(consumer);
        thread.setDaemon(true);
        thread.start();

        // Take first element from asyncronous handler
        try
        {
            _taken = _channel.take();
        }
        catch (InterruptedException e)
        {
            throw new DataSetException(e);
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    // ITableIterator interface

    public boolean next() throws DataSetException
    {
        // End of dataset has previously been reach
        if (_eod)
        {
            return false;
        }

        // Iterate to the end of current table.
        while (_activeTable != null && _activeTable.next())
            ;

        // End of dataset is reach
        if (_taken == EOD)
        {
            _eod = true;
            _activeTable = null;

//            System.out.println("End of iterator! - " + System.currentTimeMillis());
            return false;
        }

        // New table
        if (_taken instanceof ITableMetaData)
        {
            _activeTable = new StreamingTable((ITableMetaData)_taken);
            return true;
        }

        throw new IllegalStateException(
                "Unexpected object taken from asyncronous handler: " + _taken);
    }

    public ITableMetaData getTableMetaData() throws DataSetException
    {
        return _activeTable.getTableMetaData();
    }

    public ITable getTable() throws DataSetException
    {
        return _activeTable;
    }

    ////////////////////////////////////////////////////////////////////////////
    // StreamingTable class

    private class StreamingTable extends AbstractTable
    {
        private ITableMetaData _metaData;
        private int _lastRow = -1;
        private boolean _eot = false;
        private Object[] _rowValues;

        public StreamingTable(ITableMetaData metaData)
        {
            _metaData = metaData;
        }

        boolean next() throws DataSetException
        {
            // End of table has previously been reach
            if (_eot)
            {
                return false;
            }

            try
            {
                _taken = _channel.take();
                if (!(_taken instanceof Object[]))
                {
                    _eot = true;
                    return false;
                }

                _lastRow++;
                _rowValues = (Object[])_taken;
                return true;
            }
            catch (InterruptedException e)
            {
                throw new DataSetException();
            }
        }

        ////////////////////////////////////////////////////////////////////////
        // ITable interface

        public ITableMetaData getTableMetaData()
        {
            return _metaData;
        }

        public int getRowCount()
        {
            throw new UnsupportedOperationException();
        }

        public Object getValue(int row, String column) throws DataSetException
        {
            // Iterate up to specified row
            while (!_eot && row > _lastRow)
            {
                next();
            }

            if (row < _lastRow)
            {
                throw new UnsupportedOperationException("Cannot go backward!");
            }

            if (_eot || row > _lastRow)
            {
                throw new RowOutOfBoundsException(row + " > " + _lastRow);
            }

            return _rowValues[getColumnIndex(column)];
        }

    }

    ////////////////////////////////////////////////////////////////////////////
    // AsynchronousConsumer class

    private static class AsynchronousConsumer implements Runnable, IDataSetConsumer
    {
        private final IDataSetProducer _producer;
        private final Puttable _channel;

        public AsynchronousConsumer(IDataSetProducer source, Puttable channel)
        {
            _producer = source;
            _channel = channel;
        }

        ////////////////////////////////////////////////////////////////////////
        // Runnable interface

        public void run()
        {
            try
            {
                _producer.setConsumer(this);
                _producer.produce();
//                System.out.println("End of thread! - " + System.currentTimeMillis());
            }
            catch (DataSetException e)
            {
                throw new DatabaseUnitRuntimeException(e);
            }
        }

        ////////////////////////////////////////////////////////////////////////
        // IDataSetConsumer interface

        public void startDataSet() throws DataSetException
        {
        }

        public void endDataSet() throws DataSetException
        {
            try
            {
                _channel.put(EOD);
            }
            catch (InterruptedException e)
            {
                throw new DataSetException();
            }
        }

        public void startTable(ITableMetaData metaData) throws DataSetException
        {
            try
            {
                _channel.put(metaData);
            }
            catch (InterruptedException e)
            {
                throw new DataSetException();
            }
        }

        public void endTable() throws DataSetException
        {
        }

        public void row(Object[] values) throws DataSetException
        {
            try
            {
                _channel.put(values);
            }
            catch (InterruptedException e)
            {
                throw new DataSetException();
            }
        }
    }
}
