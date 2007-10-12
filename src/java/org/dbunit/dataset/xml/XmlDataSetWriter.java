/*
 *
 * The DbUnit Database Testing Framework
 * Copyright (C)2002-2006, DbUnit.org
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
import org.dbunit.dataset.datatype.DataType;
import org.dbunit.dataset.datatype.TypeCastException;
import org.dbunit.dataset.stream.DataSetProducerAdapter;
import org.dbunit.dataset.stream.IDataSetConsumer;
import org.dbunit.util.xml.XmlWriter;

import java.io.IOException;
import java.io.Writer;

/**
 * @author Manuel Laflamme
 * @since Jun 13, 2003
 * @version $Revision$
 */
public class XmlDataSetWriter implements IDataSetConsumer
{

    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(XmlDataSetWriter.class);

    private static final String DATASET = "dataset";
    private static final String TABLE = "table";
    private static final String NAME = "name";
    private static final String COLUMN = "column";
    private static final String ROW = "row";
    private static final String VALUE = "value";
    private static final String NULL = "null";
    private static final String NONE = "none";

    static char[] CDATA_DETECTION_CHARS = new char[] {
        0x20, '\n', '\r', '\t',     // whitespace
        '&', '<',                   // forbiden char
    };

    private XmlWriter _xmlWriter;
    private ITableMetaData _activeMetaData;

    public XmlDataSetWriter(Writer writer)
    {
        _xmlWriter = new XmlWriter(writer);
        _xmlWriter.enablePrettyPrint(true);
    }

    public XmlDataSetWriter(Writer writer, String encoding)
    {
        _xmlWriter = new XmlWriter(writer, encoding);
        _xmlWriter.enablePrettyPrint(true);
    }

    public void write(IDataSet dataSet) throws DataSetException
    {
        logger.debug("write(dataSet=" + dataSet + ") - start");

        DataSetProducerAdapter provider = new DataSetProducerAdapter(dataSet);
        provider.setConsumer(this);
        provider.produce();
    }

    boolean needsCData(String text)
    {
        logger.debug("needsCData(text=" + text + ") - start");

        if (text == null)
        {
            return false;
        }

        for (int i = 0; i < text.length(); i++)
        {
            char c = text.charAt(i);
            for (int j = 0; j < CDATA_DETECTION_CHARS.length; j++)
            {
                if (CDATA_DETECTION_CHARS[j] == c)
                {
                    return true;
                }
            }
        }
        return false;
    }

    ////////////////////////////////////////////////////////////////////////////
    // IDataSetConsumer interface

    public void startDataSet() throws DataSetException
    {
        logger.debug("startDataSet() - start");

        try
        {
            _xmlWriter.writeDeclaration();
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

        try
        {
            _activeMetaData = metaData;

            String tableName = _activeMetaData.getTableName();
            _xmlWriter.writeElement(TABLE);
            _xmlWriter.writeAttribute(NAME, tableName);

            Column[] columns = _activeMetaData.getColumns();
            for (int i = 0; i < columns.length; i++)
            {
                String columnName = columns[i].getColumnName();
                _xmlWriter.writeElementWithText(COLUMN, columnName);
            }
        }
        catch (IOException e)
        {
            logger.error("startTable()", e);

            throw new DataSetException(e);
        }

    }

    public void endTable() throws DataSetException
    {
        logger.debug("endTable() - start");

        try
        {
            _xmlWriter.endElement();
            _activeMetaData = null;
        }
        catch (IOException e)
        {
            logger.error("endTable()", e);

            throw new DataSetException(e);
        }
    }

    public void row(Object[] values) throws DataSetException
    {
        logger.debug("row(values=" + values + ") - start");

        try
        {
            _xmlWriter.writeElement(ROW);

            Column[] columns = _activeMetaData.getColumns();
            for (int i = 0; i < columns.length; i++)
            {
                String columnName = columns[i].getColumnName();
                Object value = values[i];
                
                // null
                if (value == null)
                {
                    _xmlWriter.writeEmptyElement(NULL);
                }
                // none
                else if (value == ITable.NO_VALUE)
                {
                    _xmlWriter.writeEmptyElement(NONE);
                }
                // values
                else
                {
                    try
                    {
                        String stringValue = DataType.asString(value);

                        _xmlWriter.writeElement(VALUE);
                        if (needsCData(stringValue))
                        {
                            _xmlWriter.writeCData(stringValue);
                        }
                        else if (stringValue.length() > 0)
                        {
                            _xmlWriter.writeText( stringValue );
                        }
                        _xmlWriter.endElement();
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
                if ( this.includeColumnComments ) {
                  _xmlWriter.writeComment( columnName );
                }
            }
            _xmlWriter.endElement();
        }
        catch (IOException e)
        {
            logger.error("row()", e);

            throw new DataSetException(e);
        }
    }
    
    private boolean includeColumnComments = false;

    public void setIncludeColumnComments(boolean b) {
        logger.debug("setIncludeColumnComments(b=" + b + ") - start");

      this.includeColumnComments = b;
    }
}
