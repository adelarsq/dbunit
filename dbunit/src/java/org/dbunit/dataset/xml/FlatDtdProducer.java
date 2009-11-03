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

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.dbunit.dataset.Column;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.DefaultTableMetaData;
import org.dbunit.dataset.datatype.DataType;
import org.dbunit.dataset.stream.DefaultConsumer;
import org.dbunit.dataset.stream.IDataSetConsumer;
import org.dbunit.dataset.stream.IDataSetProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.ext.LexicalHandler;

/**
 * Produces a DataSet from a flat DTD.
 *
 * Only external DTDs are supported and for the root element only the following
 * declarations are supported.
 * <ul>
 *   <li>ANY: like &lt;!Element dataset ANY&gt;</li>
 *   <li>sequences: like &lt;!Element dataset (first*,second,third?)gt;</li>
 *   <li>choices: like &lt;!Element dataset (first|second+|third)&gt;</li>
 * </ul>
 * Combinations of sequences and choices are not support nor are #PCDATA or
 * EMPTY declarations.
 * 
 * @author Manuel Laflamme
 * @author Last changed by: $Author$
 * @version $Revision$ $Date$
 * @since Apr 27, 2003
 */
public class FlatDtdProducer implements IDataSetProducer, EntityResolver, DeclHandler, LexicalHandler
{
    /**
     * Constant for the value {@value}
     */
    public static final String REQUIRED = "#REQUIRED";
    
    /**
     * Constant for the value {@value}
     */
    public static final String IMPLIED = "#IMPLIED";

    /**
     * Constant for the value {@value}
     */
    public static final String ANY = "ANY";

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
        logger.debug("setDeclHandler(xmlReader={}, handler={}) - start", xmlReader, handler);
        xmlReader.setProperty(DECL_HANDLER_PROPERTY_NAME, handler);
    }

    public static void setLexicalHandler(XMLReader xmlReader, LexicalHandler handler)
            throws SAXNotRecognizedException, SAXNotSupportedException
    {
        logger.debug("setLexicalHandler(xmlReader={}, handler={}) - start", xmlReader, handler);
        xmlReader.setProperty(LEXICAL_HANDLER_PROPERTY_NAME, handler);
    }

    private List createColumnList()
    {
        return new LinkedList();
    }

    ////////////////////////////////////////////////////////////////////////////
    // IDataSetProducer interface

    public void setConsumer(IDataSetConsumer consumer) throws DataSetException
    {
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
            throw new DataSetException(e);
        }
        catch (SAXException e)
        {
            Exception exception = e.getException() == null ? e : e.getException();
            if(exception instanceof DataSetException)
            {
                throw (DataSetException)exception;
            }
            else
            {
                throw new DataSetException(exception);
            }
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
        return _inputSource;
    }

    ////////////////////////////////////////////////////////////////////////////
    // DeclHandler interface

    public void elementDecl(String name, String model) throws SAXException
    {
        logger.debug("elementDecl(name={}, model={}) - start", name, model);

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
    	if (logger.isDebugEnabled())
    	{
    		logger.debug("attributeDecl(elementName={}, attributeName={}, type={}, mode={}, value={}) - start",
    				new Object[]{ elementName, attributeName, type, mode, value });
    	}

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
    	if (logger.isDebugEnabled())
    	{
    		logger.debug("startDTD(name={}, publicId={}, systemId={}) - start",
    				new Object[]{ name, publicId, systemId });
    	}

        try
        {
            _rootName = name;
            _consumer.startDataSet();
        }
        catch (DataSetException e)
        {
            throw new SAXException(e);
        }
    }

    public void endDTD() throws SAXException
    {
        logger.debug("endDTD() - start");

        try
        {
            if(_rootModel == null)
            {
                logger.info("The rootModel is null. Cannot add tables.");
            }
            else
            {
                if (ANY.equalsIgnoreCase(_rootModel))
                {
                    Iterator i = _columnListMap.keySet().iterator();
                    while (i.hasNext()) {
                        String tableName = (String) i.next();
                        addTable(tableName);
                    }
                }
                else {
                    // Remove enclosing model parenthesis
                    String rootModel = _rootModel.substring(1, _rootModel.length() - 1);
    
                    // Parse the root element model to determine the table sequence.
                    // Support all sequence or choices model but not the mix of both.
                    String delim = (rootModel.indexOf(",") != -1) ? "," : "|";
                    StringTokenizer tokenizer = new StringTokenizer(rootModel, delim);
                    while (tokenizer.hasMoreTokens()) {
                        String tableName = tokenizer.nextToken();
                        tableName = cleanupTableName(tableName);
                        addTable(tableName);
                    }
                }
            }

            _consumer.endDataSet();
        }
        catch (DataSetException e)
        {
            throw new SAXException(e);
        }
    }
    
    private void addTable(String tableName) throws DataSetException
    {
        Column[] columns = getColumns(tableName);
        _consumer.startTable(new DefaultTableMetaData(tableName, columns));
        _consumer.endTable();
    }

    private Column[] getColumns(String tableName) throws DataSetException 
    {
        List columnList = (List)_columnListMap.get(tableName);
        if(columnList==null){
            throw new DataSetException("ELEMENT/ATTRIBUTE declaration for '" + tableName + "' is missing. " +
                    "Every table must have an element describing the table.");
        }
        Column[] columns = (Column[])columnList.toArray(new Column[0]);
        return columns;
    }

    protected String cleanupTableName(String tableName)
    {
        String cleaned = tableName;
        // Remove beginning parenthesis.
        while (cleaned.startsWith("(")) {
            cleaned = cleaned.substring(1);
        }
        // Remove ending parenthesis and occurrence operators
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
