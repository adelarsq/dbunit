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
import org.dbunit.dataset.ITableMetaData;
import org.dbunit.dataset.datatype.DataType;
import org.dbunit.dataset.datatype.TypeCastException;

import org.dbunit.util.xml.XmlWriter;

import java.io.IOException;
import java.io.Writer;

/**
 * @author Manuel Laflamme
 * @since Apr 19, 2003
 * @version $Revision$
 */
public class FlatXmlWriter implements IDataSetConsumer
{
    private static final String DATASET = "dataset";

    private XmlWriter _xmlWriter;
    private ITableMetaData _activeMetaData;
    private int _activeRowCount;
    private boolean _includeEmptyTable = false;

    public FlatXmlWriter(Writer writer)
    {
        _xmlWriter = new XmlWriter(writer);
        _xmlWriter.enablePrettyPrint(true);
    }

    public FlatXmlWriter(Writer writer, String encoding)
    {
        _xmlWriter = new XmlWriter(writer, encoding);
        _xmlWriter.enablePrettyPrint(true);
    }

    public void setIncludeEmptyTable(boolean includeEmptyTable)
    {
        _includeEmptyTable = includeEmptyTable;
    }

    public void write(IDataSet dataSet) throws DataSetException
    {
        DataSetProducerAdapter provider = new DataSetProducerAdapter(dataSet);
        provider.setConsumer(this);
        provider.produce();
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
        _activeMetaData = metaData;
        _activeRowCount = 0;
    }

    public void endTable() throws DataSetException
    {
        if (_includeEmptyTable && _activeRowCount == 0)
        {
            try
            {
                String tableName = _activeMetaData.getTableName();
                _xmlWriter.writeEmptyElement(tableName);
            }
            catch (IOException e)
            {
                throw new DataSetException(e);
            }
        }

        _activeMetaData = null;
    }

    public void row(Object[] values) throws DataSetException
    {
        try
        {
            String tableName = _activeMetaData.getTableName();
            _xmlWriter.writeElement(tableName);

            Column[] columns = _activeMetaData.getColumns();
            for (int i = 0; i < columns.length; i++)
            {
                String columnName = columns[i].getColumnName();
                Object value = values[i];
                try
                {
                    String stringValue = DataType.asString(value);
                    _xmlWriter.writeAttribute(columnName, stringValue);
                }
                catch (TypeCastException e)
                {
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
            throw new DataSetException(e);
        }
    }
}
