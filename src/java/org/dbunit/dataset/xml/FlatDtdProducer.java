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
import org.dbunit.dataset.DefaultTableMetaData;
import org.dbunit.dataset.datatype.DataType;
import org.dbunit.dataset.stream.DefaultConsumer;
import org.dbunit.dataset.stream.IDataSetConsumer;
import org.dbunit.dataset.stream.IDataSetProducer;
import org.xml.sax.*;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.ext.LexicalHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.StringReader;
import java.util.*;

/**
 * @author Manuel Laflamme
 * @since Apr 27, 2003
 * @version $Revision$
 */
public class FlatDtdProducer implements IDataSetProducer, EntityResolver, DeclHandler, LexicalHandler
{

    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(FlatDtdProducer.class);

    private static final IDataSetConsumer EMPTY_CONSUMER = new DefaultConsumer();

    private static final String XML_CONTENT =
            "<?xml version=\"1.0\"?>" +
            "<!DOCTYPE dataset SYSTEM \"urn:/dummy.dtd\">" +
            "<dataset/>";
    private static final String DECL_HANDLER_PROPERTY_NAME =
            "http://xml.org/sax/properties/declaration-handler";
    private static final String LEXICAL_HANDLER_PROPERTY_NAME =
            "http://xml.org/sax/properties/lexical-handler";

    private static final String REQUIRED = "#REQUIRED";
    private static final String IMPLIED = "#IMPLIED";

    private InputSource _inputSource;
    private IDataSetConsumer _consumer = EMPTY_CONSUMER;

    private String _rootName;
    private String _rootModel;
    private final Map _columnListMap = new HashMap();

    public FlatDtdProducer()
    {
    }

    public FlatDtdProducer(InputSource inputSource)
    {
        _inputSource = inputSource;
    }

    public static void setDeclHandler(XMLReader xmlReader, DeclHandler handler)
            throws SAXNotRecognizedException, SAXNotSupportedException
    {
        logger.debug("setDeclHandler(xmlReader=" + xmlReader + ", handler=" + handler + ") - start");

        xmlReader.setProperty(DECL_HANDLER_PROPERTY_NAME, handler);
    }

    public static void setLexicalHandler(XMLReader xmlReader, LexicalHandler handler)
            throws SAXNotRecognizedException, SAXNotSupportedException
    {
        logger.debug("setLexicalHandler(xmlReader=" + xmlReader + ", handler=" + handler + ") - start");

        xmlReader.setProperty(LEXICAL_HANDLER_PROPERTY_NAME, handler);
    }

    private List createColumnList()
    {
        logger.debug("createColumnList() - start");

        return new LinkedList();
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
            SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
            XMLReader xmlReader = saxParser.getXMLReader();

            setDeclHandler(xmlReader, this);
            setLexicalHandler(xmlReader, this);
            xmlReader.setEntityResolver(this);
            xmlReader.parse(new InputSource(new StringReader(XML_CONTENT)));
        }
        catch (ParserConfigurationException e)
        {
            logger.error("produce()", e);

            throw new DataSetException(e);
        }
        catch (SAXException e)
        {
            logger.error("produce()", e);

            Exception exception = e.getException() == null ? e : e.getException();
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

        return _inputSource;
    }

    ////////////////////////////////////////////////////////////////////////////
    // DeclHandler interface

    public void elementDecl(String name, String model) throws SAXException
    {
        logger.debug("elementDecl(name=" + name + ", model=" + model + ") - start");

        // Root element
        if (name.equals(_rootName))
        {
            // The root model defines the table sequence. Keep it for later used!
            _rootModel = model;
        }
        else if (!_columnListMap.containsKey(name))
        {
            _columnListMap.put(name, createColumnList());
        }
    }

    public void attributeDecl(String elementName, String attributeName,
            String type, String mode, String value) throws SAXException
    {
        logger.debug("attributeDecl(elementName=" + elementName + ", attributeName=" + attributeName + ", type=" + type
                + ", mode=" + mode + ", value=" + value + ") - start");

        // Each element attribute represent a table column
        Column.Nullable nullable = (REQUIRED.equals(mode)) ?
                Column.NO_NULLS : Column.NULLABLE;
        Column column = new Column(attributeName, DataType.UNKNOWN, nullable);

        if (!_columnListMap.containsKey(elementName))
        {
            _columnListMap.put(elementName, createColumnList());
        }
        List columnList = (List)_columnListMap.get(elementName);
        columnList.add(column);
    }

    public void internalEntityDecl(String name, String value) throws SAXException
    {
        // Not used!
    }

    public void externalEntityDecl(String name, String publicId,
            String systemId) throws SAXException
    {
        // Not used!
    }

    ////////////////////////////////////////////////////////////////////////////
    // LexicalHandler interface

    public void startDTD(String name, String publicId, String systemId)
            throws SAXException
    {
        logger.debug("startDTD(name=" + name + ", publicId=" + publicId + ", systemId=" + systemId + ") - start");

        try
        {
            _rootName = name;
            _consumer.startDataSet();
        }
        catch (DataSetException e)
        {
            logger.error("startDTD()", e);

            throw new SAXException(e);
        }
    }

    public void endDTD() throws SAXException
    {
        logger.debug("endDTD() - start");

        try
        {
            if (_rootModel != null)
            {
                // Remove enclosing model parenthesis
                String rootModel = _rootModel.substring(1, _rootModel.length() - 1);

                // Parse the root element model to determine the table sequence.
                // Support all sequence or choices model but not the mix of both.
                String delim = (rootModel.indexOf(",") != -1) ? "," : "|";
                StringTokenizer tokenizer = new StringTokenizer(rootModel, delim);
                while (tokenizer.hasMoreTokens()) {
                    String tableName = tokenizer.nextToken();

                    tableName = cleanupTableName(tableName);

                    List columnList = (List)_columnListMap.get(tableName);
                    Column[] columns = (Column[])columnList.toArray(new Column[0]);

                    _consumer.startTable(new DefaultTableMetaData(tableName, columns));
                    _consumer.endTable();
                }
            }

            _consumer.endDataSet();
        }
        catch (DataSetException e)
        {
            logger.error("endDTD()", e);

            throw new SAXException(e);
        }
    }
    
    protected String cleanupTableName(String tableName)
    {
        String cleaned = tableName;
        while (cleaned.startsWith("(")) {
            cleaned = cleaned.substring(1);
        }
        while (cleaned.endsWith(")")
                || cleaned.endsWith("*")
                || cleaned.endsWith("?")
                || cleaned.endsWith("+")) {
            cleaned = cleaned.substring(0, cleaned.length() - 1);
        }
        return cleaned;
    }

    public void startEntity(String name) throws SAXException
    {
        // Not used!
    }

    public void endEntity(String name) throws SAXException
    {
        // Not used!
    }

    public void startCDATA() throws SAXException
    {
        // Not used!
    }

    public void endCDATA() throws SAXException
    {
        // Not used!
    }

    public void comment(char ch[], int start, int length) throws SAXException
    {
        // Not used!
    }
}
