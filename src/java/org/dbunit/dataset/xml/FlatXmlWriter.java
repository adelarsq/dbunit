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

import org.dbunit.dataset.Column;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITableMetaData;
import org.dbunit.dataset.datatype.DataType;
import org.dbunit.dataset.datatype.TypeCastException;
import org.dbunit.dataset.stream.DataSetProducerAdapter;
import org.dbunit.dataset.stream.IDataSetConsumer;
import org.dbunit.util.xml.XmlWriter;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 * @author Manuel Laflamme
 * @since Apr 19, 2003
 * @version $Revision$
 */
public class FlatXmlWriter implements IDataSetConsumer
{

    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(FlatXmlWriter.class);

    private static final String DEFAULT_ENCODING = "UTF8";
    private static final String DATASET = "dataset";

    private XmlWriter _xmlWriter;
    private ITableMetaData _activeMetaData;
    private int _activeRowCount;
    private boolean _includeEmptyTable = false;
    private String _systemId = null;

    public FlatXmlWriter(Writer writer)
    {
        _xmlWriter = new XmlWriter(writer);
        _xmlWriter.enablePrettyPrint(true);
    }

    public FlatXmlWriter(OutputStream out) throws IOException
    {
        _xmlWriter = new XmlWriter(new OutputStreamWriter(out, DEFAULT_ENCODING));
        _xmlWriter.enablePrettyPrint(true);
    }

    public FlatXmlWriter(Writer writer, String encoding)
    {
        _xmlWriter = new XmlWriter(writer, encoding);
        _xmlWriter.enablePrettyPrint(true);
    }

    public void setIncludeEmptyTable(boolean includeEmptyTable)
    {
        logger.debug("setIncludeEmptyTable(includeEmptyTable=" + includeEmptyTable + ") - start");

        _includeEmptyTable = includeEmptyTable;
    }

    public void setDocType(String systemId)
    {
        logger.debug("setDocType(systemId=" + systemId + ") - start");

        _systemId = systemId;
    }

    public void write(IDataSet dataSet) throws DataSetException
    {
        logger.debug("write(dataSet=" + dataSet + ") - start");

        DataSetProducerAdapter provider = new DataSetProducerAdapter(dataSet);
        provider.setConsumer(this);
        provider.produce();
    }

    ////////////////////////////////////////////////////////////////////////////
    // IDataSetConsumer interface

    public void startDataSet() throws DataSetException
    {
        logger.debug("startDataSet() - start");

        try
        {
            _xmlWriter.writeDeclaration();
            _xmlWriter.writeDoctype(_systemId, null);
            _xmlWriter.writeElement(DATASET);
        }
        catch (IOException e)
        {
            logger.error("startDataSet()", e);

            throw new DataSetException(e);
        }
    }

    public void endDataSet() throws DataSetException
    {
        logger.debug("endDataSet() - start");

        try
        {
            _xmlWriter.endElement();
            _xmlWriter.close();
        }
        catch (IOException e)
        {
            logger.error("endDataSet()", e);

            throw new DataSetException(e);
        }
    }

    public void startTable(ITableMetaData metaData) throws DataSetException
    {
        logger.debug("startTable(metaData=" + metaData + ") - start");

        _activeMetaData = metaData;
        _activeRowCount = 0;
    }

    public void endTable() throws DataSetException
    {
        logger.debug("endTable() - start");

        if (_includeEmptyTable && _activeRowCount == 0)
        {
            try
            {
                String tableName = _activeMetaData.getTableName();
                _xmlWriter.writeEmptyElement(tableName);
            }
            catch (IOException e)
            {
                logger.error("endTable()", e);

                throw new DataSetException(e);
            }
        }

        _activeMetaData = null;
    }

    public void row(Object[] values) throws DataSetException
    {
        logger.debug("row(values=" + values + ") - start");

        try
        {
            String tableName = _activeMetaData.getTableName();
            _xmlWriter.writeElement(tableName);

            Column[] columns = _activeMetaData.getColumns();
            for (int i = 0; i < columns.length; i++)
            {
                String columnName = columns[i].getColumnName();
                Object value = values[i];

                // Skip null value
                if (value == null)
                {
                    continue;
                }

                try
                {
                    String stringValue = DataType.asString(value);
                    _xmlWriter.writeAttribute(columnName, stringValue);
                }
                catch (TypeCastException e)
                {
                    logger.error("row()", e);

                    throw new DataSetException("table=" +
                            _activeMetaData.getTableName() + ", row=" + i +
                            ", column=" + columnName +
                            ", value=" + value, e);
                }
            }

            _activeRowCount++;
            _xmlWriter.endElement();
        }
        catch (IOException e)
        {
            logger.error("row()", e);

            throw new DataSetException(e);
        }
    }
}
