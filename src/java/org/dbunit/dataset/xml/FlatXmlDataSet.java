/*
 * FlatXmlDataSet.java   Mar 12, 2002
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

import org.dbunit.dataset.*;
import org.dbunit.dataset.datatype.DataType;
import org.dbunit.dataset.datatype.TypeCastException;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import electric.xml.*;

/**
 * @author Manuel Laflamme
 * @version $Revision$
 */
public class FlatXmlDataSet extends AbstractDataSet
{
    private static final String SYSTEM = "SYSTEM '";

    private final ITable[] _tables;

    /**
     * Creates an FlatXmlDataSet object with the specifed xml file.
     * Relative DOCTYPE uri are resolved from the xml file path.
     *
     * @param file the xml file
     */
    public FlatXmlDataSet(File xmlFile) throws IOException, DataSetException
    {
        try
        {
            Document document = new Document(new FileInputStream(xmlFile));

            // Create metadata from dtd if defined
            IDataSet metaDataSet = null;
            String dtdUri = getDocTypeUri(document);
            if (dtdUri != null)
            {
                File dtdFile = new File(dtdUri);
                if (!dtdFile.isAbsolute())
                {
                    dtdFile = new File(xmlFile.getParent(), dtdUri);
                }
                metaDataSet = new FlatDtdDataSet(new FileInputStream(dtdFile));
            }

            _tables = getTables(document, metaDataSet);
        }
        catch (ParseException e)
        {
            throw new DataSetException(e);
        }
    }

    /**
     * Creates an FlatXmlDataSet object with the specifed xml input stream.
     * Relative DOCTYPE uri are resolved from the current working dicrectory.
     *
     * @param stream the xml input stream
     */
    public FlatXmlDataSet(InputStream stream) throws IOException, DataSetException
    {
        try
        {
            Document document = new Document(stream);

            // Create metadata from dtd if defined
            IDataSet metaDataSet = null;
            String dtdUri = getDocTypeUri(document);
            if (dtdUri != null)
            {
                try
                {
                    URL dtdUrl = new URL(dtdUri);
                    metaDataSet = new FlatDtdDataSet(dtdUrl.openStream());
                }
                catch (MalformedURLException e)
                {
                    metaDataSet = new FlatDtdDataSet(new FileInputStream(dtdUri));
                }
            }

            _tables = getTables(document, metaDataSet);
        }
        catch (ParseException e)
        {
            throw new DataSetException(e);
        }
    }

    /**
     * Creates an FlatXmlDataSet object with the specifed xml and dtd input
     * stream.
     *
     * @param xmlStream the xml input stream
     * @param dtdStream the dtd input stream
     */
    public FlatXmlDataSet(InputStream xmlStream, InputStream dtdStream)
            throws IOException, DataSetException
    {
        this(xmlStream, new FlatDtdDataSet(dtdStream));
    }

    /**
     * Creates an FlatXmlDataSet object with the specifed xml input stream.
     *
     * @param xmlStream the xml input stream
     * @param metaDataSet the dataset used as metadata source.
     */
    public FlatXmlDataSet(InputStream xmlStream, IDataSet metaDataSet)
            throws IOException, DataSetException
    {
        try
        {
            _tables = getTables(new Document(xmlStream), metaDataSet);
        }
        catch (ParseException e)
        {
            throw new DataSetException(e);
        }
    }

    /**
     * Write the specified dataset to the specified output as xml.
     */
    public static void write(IDataSet dataSet, OutputStream out)
            throws IOException, DataSetException
    {
        Document document = buildDocument(dataSet);

        // write xml document
        document.write(out);
    }

    /**
     * Write a DTD for the specified dataset to the specified output.
     * @deprecated use {@link FlatDtdDataSet#write}
     */
    public static void writeDtd(IDataSet dataSet, OutputStream out)
            throws IOException, DataSetException
    {
        FlatDtdDataSet.write(dataSet, out);
    }

    private static Document buildDocument(IDataSet dataSet) throws DataSetException
    {
        Document document = new Document();
        ITable[] tables = dataSet.getTables();
//        String[] tableNames = dataSet.getTableNames();

        // dataset
        Element rootElem = document.addElement("dataset");

        // tables
        for (int i = 0; i < tables.length; i++)
        {
            ITable table = tables[i];
            ITableMetaData metaData = tables[i].getTableMetaData();
            String tableName = metaData.getTableName();

            Column[] columns = table.getTableMetaData().getColumns();

            // table rows
            for (int j = 0; j < table.getRowCount(); j++)
            {
                Element rowElem = rootElem.addElement(tableName);
                for (int k = 0; k < columns.length; k++)
                {
                    Column column = columns[k];
                    Object value = table.getValue(j, column.getColumnName());

                    // row values
                    if (value != null && value != ITable.NO_VALUE)
                    {
                        try
                        {
                            String stringValue = DataType.asString(value);
                            rowElem.setAttribute(column.getColumnName(), stringValue);
                        }
                        catch (TypeCastException e)
                        {
                            throw new DataSetException("table=" + tableName +
                                    ", row=" + j + ", column=" +
                                    column.getColumnName() + ", value=" +
                                    value, e);
                        }
                    }
                }
            }

            // empty table
            if (table.getRowCount() == 0)
            {
                rootElem.addElement(tableName);
            }
        }

        return document;
    }

    private ITable[] getTables(Document document, IDataSet metaDataSet)
            throws IOException, DataSetException
    {
        List tableList = new ArrayList();
        List rowList = new ArrayList();
        String lastTableName = null;

        Elements rowElems = document.getElement("dataset").getElements();
        while (rowElems.hasMoreElements())
        {
            Element rowElem = (Element)rowElems.nextElement();

            if (lastTableName != null &&
                    !lastTableName.equals(rowElem.getName()))
            {
                Element[] elems = (Element[])rowList.toArray(new Element[0]);
                rowList.clear();

                tableList.add(createTable(elems, metaDataSet));
            }

            lastTableName = rowElem.getName();
            rowList.add(rowElem);
        }

        if (rowList.size() > 0)
        {
            Element[] elems = (Element[])rowList.toArray(new Element[0]);
            tableList.add(createTable(elems, metaDataSet));
        }

        return (ITable[])tableList.toArray(new ITable[0]);
    }

    private ITable createTable(Element[] rows, IDataSet metaDataSet)
            throws DataSetException
    {
        Element sampleRow = rows[0];

        ITableMetaData metaData = null;
        if (metaDataSet != null)
        {
            String tableName = sampleRow.getName();
            metaData = metaDataSet.getTableMetaData(tableName);
        }
        else
        {
            metaData = FlatXmlTable.createMetaData(sampleRow);
        }

        // Assume empty table when only one row with no columns
        if (rows.length == 1 && sampleRow.getAttributes().size() == 0)
        {
            rows = new Element[0];
        }

        return new FlatXmlTable(rows, metaData);
    }

    /**
     * Returns this document type uri or <code>null</code> if none is defined.
     */
    private String getDocTypeUri(Document document)
    {
        DocType docType = document.getDocType();
        if (docType != null && docType.getExternalId() != null)
        {
            String externalId = docType.getExternalId();
            if (externalId.startsWith(SYSTEM))
            {
                externalId = externalId.substring(SYSTEM.length(), externalId.length() - 1);
            }

            return externalId;
        }

        return null;
    }

    ////////////////////////////////////////////////////////////////////////////
    // IDataSet interface

    public ITable[] getTables() throws DataSetException
    {
        return cloneTables(_tables);
    }

}











