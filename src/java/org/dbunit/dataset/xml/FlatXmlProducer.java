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
import org.dbunit.dataset.DefaultTableMetaData;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.IDataSetConsumer;
import org.dbunit.dataset.IDataSetProducer;
import org.dbunit.dataset.ITableMetaData;
import org.dbunit.dataset.DefaultConsumer;
import org.dbunit.dataset.datatype.DataType;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.EntityResolver;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.StringReader;

/**
 * @author Manuel Laflamme
 * @since Apr 18, 2003
 * @version $Revision$
 */
public class FlatXmlProducer extends DefaultHandler
        implements IDataSetProducer, ContentHandler
{
    private static final IDataSetConsumer EMPTY_CONSUMER = new DefaultConsumer();
    private static final String DATASET = "dataset";

    private final InputSource _inputSource;
    private final EntityResolver _resolver;
    private final boolean _dtdMetadata;
    private boolean _validating = false;
    private IDataSet _metaDataSet;

    private IDataSetConsumer _consumer = EMPTY_CONSUMER;
    private ITableMetaData _activeMetaData;

    public FlatXmlProducer(InputSource xmlSource)
    {
        _inputSource = xmlSource;
        _resolver = this;
        _dtdMetadata = true;
        _validating = false;
    }

    public FlatXmlProducer(InputSource xmlSource, boolean dtdMetadata)
    {
        _inputSource = xmlSource;
        _resolver = this;
        _dtdMetadata = dtdMetadata;
        _validating = false;
    }

    public FlatXmlProducer(InputSource xmlSource, IDataSet metaDataSet)
    {
        _inputSource = xmlSource;
        _metaDataSet = metaDataSet;
        _resolver = this;
        _dtdMetadata = false;
        _validating = false;
    }

    public FlatXmlProducer(InputSource xmlSource, EntityResolver resolver)
    {
        _inputSource = xmlSource;
        _resolver = resolver;
        _dtdMetadata = true;
        _validating = false;
    }

//    public FlatXmlProducer(InputSource inputSource, XMLReader xmlReader)
//    {
//        _inputSource = inputSource;
//        _xmlReader = xmlReader;
//    }

    private ITableMetaData createTableMetaData(String tableName,
            Attributes attributes) throws DataSetException
    {
        if (_metaDataSet != null)
        {
            return _metaDataSet.getTableMetaData(tableName);
        }

        // Create metadata from attributes
        Column[] columns = new Column[attributes.getLength()];
        for (int i = 0; i < attributes.getLength(); i++)
        {
            columns[i] = new Column(attributes.getQName(i),
                    DataType.UNKNOWN);
        }

        return new DefaultTableMetaData(tableName, columns);
    }

    public void setValidating(boolean validating)
    {
        _validating = validating;
    }

    ////////////////////////////////////////////////////////////////////////////
    // IDataSetProducer interface

    public void setConsumer(IDataSetConsumer consumer) throws DataSetException
    {
        _consumer = consumer;
    }

    public void produce() throws DataSetException
    {
        try
        {
            SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
            saxParserFactory.setValidating(_validating);
            XMLReader xmlReader = saxParserFactory.newSAXParser().getXMLReader();

            if (_dtdMetadata)
            {
                FlatDtdHandler dtdHandler = new FlatDtdHandler();
                FlatDtdHandler.setLexicalHandler(xmlReader, dtdHandler);
                FlatDtdHandler.setDeclHandler(xmlReader, dtdHandler);
            }

            xmlReader.setContentHandler(this);
            xmlReader.setEntityResolver(_resolver);
            xmlReader.parse(_inputSource);
        }
        catch (ParserConfigurationException e)
        {
            throw new DataSetException(e);
        }
        catch (SAXException e)
        {
            Exception exception = e.getException() == null ? e : e.getException();
            throw new DataSetException(exception);
        }
        catch (IOException e)
        {
            throw new DataSetException(e);
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    // EntityResolver interface

    public InputSource resolveEntity(String publicId, String systemId)
            throws SAXException
    {
        if (!_dtdMetadata)
        {
            return new InputSource(new StringReader(""));
        }
        return null;
    }

    ////////////////////////////////////////////////////////////////////////
    // ContentHandler interface

    public void startElement(String uri, String localName, String qName,
            Attributes attributes) throws SAXException
    {
        try
        {
            // Start of dataset
            if (_activeMetaData == null && qName.equals(DATASET))
            {
                _consumer.startDataSet();
                return;
            }

            // New table
            if (_activeMetaData == null ||
                    !_activeMetaData.getTableName().equals(qName))
            {
                // If not first table, notify end of previous table to consumer
                if (_activeMetaData != null)
                {
                    _consumer.endTable();
                }

                // Notify start of new table to consumer
                _activeMetaData = createTableMetaData(qName, attributes);
                _consumer.startTable(_activeMetaData);
            }

            // Row notification
            if (attributes.getLength() > 0)
            {
                Column[] columns = _activeMetaData.getColumns();
                Object[] rowValues = new Object[columns.length];
                for (int i = 0; i < columns.length; i++)
                {
                    Column column = columns[i];
                    rowValues[i] = attributes.getValue(column.getColumnName());
                }
                _consumer.row(rowValues);
            }
        }
        catch (DataSetException e)
        {
            throw new SAXException(e);
        }
    }

    public void endElement(String uri, String localName, String qName) throws SAXException
    {
        // End of dataset
        if (qName.equals(DATASET))
        {
            try
            {
                // Notify end of active table to consumer
                if (_activeMetaData != null)
                {
                    _consumer.endTable();
                }

                // Notify end of dataset to consumer
                _consumer.endDataSet();
            }
            catch (DataSetException e)
            {
                throw new SAXException(e);
            }
        }
    }

    private class FlatDtdHandler extends FlatDtdProducer
    {
        public FlatDtdHandler()
        {
        }

        ////////////////////////////////////////////////////////////////////////////
        // LexicalHandler interface

        public void startDTD(String name, String publicId, String systemId)
                throws SAXException
        {
            try
            {
                // Cache the DTD content to use it as metadata
                FlatDtdDataSet metaDataSet = new FlatDtdDataSet();
                this.setConsumer(metaDataSet);
                _metaDataSet = metaDataSet;

                super.startDTD(name, publicId, systemId);
            }
            catch (DataSetException e)
            {
                throw new SAXException(e);
            }
        }
    }

}
