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
package org.dbunit.dataset.xml;

import org.dbunit.dataset.Column;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.DataSetProducerAdapter;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.IDataSetConsumer;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.ITableMetaData;
import org.dbunit.dataset.datatype.DataType;
import org.dbunit.dataset.datatype.TypeCastException;
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
        DataSetProducerAdapter provider = new DataSetProducerAdapter(dataSet);
        provider.setConsumer(this);
        provider.produce();
    }

    boolean needsCData(String text)
    {
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
        try
        {
            _xmlWriter.writeDeclaration();
            _xmlWriter.writeElement(DATASET);
        }
        catch (IOException e)
        {
            throw new DataSetException(e);
        }
    }

    public void endDataSet() throws DataSetException
    {
        try
        {
            _xmlWriter.endElement();
            _xmlWriter.close();
        }
        catch (IOException e)
        {
            throw new DataSetException(e);
        }
    }

    public void startTable(ITableMetaData metaData) throws DataSetException
    {
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
            throw new DataSetException(e);
        }

    }

    public void endTable() throws DataSetException
    {
        try
        {
            _xmlWriter.endElement();
            _activeMetaData = null;
        }
        catch (IOException e)
        {
            throw new DataSetException(e);
        }
    }

    public void row(Object[] values) throws DataSetException
    {
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
                            _xmlWriter.writeText(stringValue);
                        }
                        _xmlWriter.endElement();
                    }
                    catch (TypeCastException e)
                    {
                        throw new DataSetException("table=" +
                                _activeMetaData.getTableName() + ", row=" + i +
                                ", column=" + columnName +
                                ", value=" + value, e);
                    }
                }
            }

            _xmlWriter.endElement();
        }
        catch (IOException e)
        {
            throw new DataSetException(e);
        }
    }
}
