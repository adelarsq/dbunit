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
import org.dbunit.dataset.datatype.DataType;
import org.dbunit.dataset.stream.DefaultConsumer;
import org.dbunit.dataset.stream.IDataSetConsumer;
import org.dbunit.dataset.stream.IDataSetProducer;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
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

    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(FlatXmlProducer.class);

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
        logger.debug("createTableMetaData(tableName=" + tableName + ", attributes=" + attributes + ") - start");

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
        logger.debug("setValidating(validating=" + validating + ") - start");

        _validating = validating;
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
            xmlReader.setErrorHandler(this);
            xmlReader.setEntityResolver(_resolver);
            xmlReader.parse(_inputSource);
        }
        catch (ParserConfigurationException e)
        {
            logger.error("produce()", e);

            throw new DataSetException(e);
        }
        catch (SAXException e)
        {
            logger.error("produce()", e);

            int lineNumber = -1;
            if (e instanceof SAXParseException)
            {
                lineNumber = ((SAXParseException)e).getLineNumber();
            }
            Exception exception = e.getException() == null ? e : e.getException();

            if (lineNumber >= 0)
            {
                String message = "Line " + lineNumber + ": " + exception.getMessage();
                throw new DataSetException(message, e);
            }
            throw new DataSetException(exception);
        }
        catch (IOException e)
        {
            logger.error("produce()", e);

            throw new DataSetException(e);
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    // EntityResolver interface

    public InputSource resolveEntity(String publicId, String systemId)
            throws SAXException
    {
        logger.debug("resolveEntity(publicId=" + publicId + ", systemId=" + systemId + ") - start");

        if (!_dtdMetadata)
        {
            return new InputSource(new StringReader(""));
        }
        return null;
    }

    ////////////////////////////////////////////////////////////////////////////
    // ErrorHandler interface

    public void error(SAXParseException e) throws SAXException
    {
        logger.debug("error(e=" + e + ") - start");

        throw e;

    }

    ////////////////////////////////////////////////////////////////////////
    // ContentHandler interface

    public void startElement(String uri, String localName, String qName,
            Attributes attributes) throws SAXException
    {
        logger.debug("startElement(uri=" + uri + ", localName=" + localName + ", qName=" + qName + ", attributes="
                + attributes + ") - start");

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
            logger.error("startElement()", e);

            throw new SAXException(e);
        }
    }

    public void endElement(String uri, String localName, String qName) throws SAXException
    {
        logger.debug("endElement(uri=" + uri + ", localName=" + localName + ", qName=" + qName + ") - start");

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
                logger.error("endElement()", e);

                throw new SAXException(e);
            }
        }
    }

    private class FlatDtdHandler extends FlatDtdProducer
    {

        /**
         * Logger for this class
         */
        private final Logger logger = LoggerFactory.getLogger(FlatDtdHandler.class);

        public FlatDtdHandler()
        {
        }

        ////////////////////////////////////////////////////////////////////////////
        // LexicalHandler interface

        public void startDTD(String name, String publicId, String systemId)
                throws SAXException
        {
            logger.debug("startDTD(name=" + name + ", publicId=" + publicId + ", systemId=" + systemId + ") - start");

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
                logger.error("startDTD()", e);

                throw new SAXException(e);
            }
        }
    }

}
