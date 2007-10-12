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

package org.dbunit.dataset.xml;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.dbunit.dataset.*;
import org.dbunit.dataset.stream.IDataSetConsumer;
import org.dbunit.dataset.stream.IDataSetProducer;
import org.xml.sax.InputSource;

import java.io.*;
import java.util.*;

/**
 * @author Manuel Laflamme
 * @version $Revision$
 * @since Apr 4, 2002
 */
public class FlatDtdDataSet extends AbstractDataSet implements IDataSetConsumer
{

    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(FlatDtdDataSet.class);

    private final List _tableNames = new ArrayList();
    private final Map _tableMap = new HashMap();
    private boolean _ready = false;

    public FlatDtdDataSet()
    {
    }

    public FlatDtdDataSet(InputStream in) throws DataSetException, IOException
    {
        this(new FlatDtdProducer(new InputSource(in)));
    }

    public FlatDtdDataSet(Reader reader) throws DataSetException, IOException
    {
        this(new FlatDtdProducer(new InputSource(reader)));
    }

    public FlatDtdDataSet(IDataSetProducer producer) throws DataSetException
    {
        producer.setConsumer(this);
        producer.produce();
    }

    /**
     * Write the specified dataset to the specified output stream as DTD.
     */
    public static void write(IDataSet dataSet, OutputStream out)
            throws IOException, DataSetException
    {
        logger.debug("write(dataSet=" + dataSet + ", out=" + out + ") - start");

        write(dataSet, new OutputStreamWriter(out));
    }

    /**
     * Write the specified dataset to the specified writer as DTD.
     */
    public static void write(IDataSet dataSet, Writer out)
            throws IOException, DataSetException
    {
        logger.debug("write(dataSet=" + dataSet + ", out=" + out + ") - start");

        FlatDtdWriter datasetWriter = new FlatDtdWriter(out);
        datasetWriter.write(dataSet);
    }

    ////////////////////////////////////////////////////////////////////////////
    // AbstractDataSet class

    protected ITableIterator createIterator(boolean reversed)
            throws DataSetException
    {
        logger.debug("createIterator(reversed=" + reversed + ") - start");

        // Verify producer notifications completed
        if (!_ready)
        {
            throw new IllegalStateException("Not ready!");
        }

        String[] names = (String[])_tableNames.toArray(new String[0]);
        ITable[] tables = new ITable[names.length];
        for (int i = 0; i < names.length; i++)
        {
            String tableName = names[i];
            ITable table = (ITable)_tableMap.get(tableName);
            if (table == null)
            {
                throw new NoSuchTableException(tableName);
            }

            tables[i] = table;
        }

        return new DefaultTableIterator(tables, reversed);
    }

    ////////////////////////////////////////////////////////////////////////////
    // IDataSet interface

    public String[] getTableNames() throws DataSetException
    {
        logger.debug("getTableNames() - start");

        // Verify producer notifications completed
        if (!_ready)
        {
            throw new IllegalStateException("Not ready!");
        }

        return (String[])_tableNames.toArray(new String[0]);
    }

    public ITableMetaData getTableMetaData(String tableName) throws DataSetException
    {
        logger.debug("getTableMetaData(tableName=" + tableName + ") - start");

        // Verify producer notifications completed
        if (!_ready)
        {
            throw new IllegalStateException("Not ready!");
        }

        String upperTableName = tableName.toUpperCase();
        if (_tableMap.containsKey(upperTableName))
        {
            ITable table = (ITable)_tableMap.get(upperTableName);
            return table.getTableMetaData();
        }

        throw new NoSuchTableException(tableName);
    }

    public ITable getTable(String tableName) throws DataSetException
    {
        logger.debug("getTable(tableName=" + tableName + ") - start");

        // Verify producer notifications completed
        if (!_ready)
        {
            throw new IllegalStateException("Not ready!");
        }

        String upperTableName = tableName.toUpperCase();
        if (_tableMap.containsKey(upperTableName))
        {
            return (ITable)_tableMap.get(upperTableName);
        }

        throw new NoSuchTableException(tableName);
    }

    ////////////////////////////////////////////////////////////////////////
    // IDataSetConsumer interface

    public void startDataSet() throws DataSetException
    {
        logger.debug("startDataSet() - start");

        _ready = false;
    }

    public void endDataSet() throws DataSetException
    {
        logger.debug("endDataSet() - start");

        _ready = true;
    }

    public void startTable(ITableMetaData metaData) throws DataSetException
    {
        logger.debug("startTable(metaData=" + metaData + ") - start");

        String tableName = metaData.getTableName();
        _tableNames.add(tableName);
        _tableMap.put(tableName.toUpperCase(), new DefaultTable(metaData));
    }

    public void endTable() throws DataSetException
    {
        // no op
    }

    public void row(Object[] values) throws DataSetException
    {
        // no op
    }
}