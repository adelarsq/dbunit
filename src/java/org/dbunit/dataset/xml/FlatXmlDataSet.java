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

import org.dbunit.dataset.CachedDataSet;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;

import org.xml.sax.InputSource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;

/**
 * Reads and writes flat XML dataset document. Each XML element corresponds to a table row.
 *       Each XML element name corresponds to a table name. The XML attributes
 *       correspond to table columns.
 * <p>
 * Flat XML dataset document sample:
 * <p>
 * <pre>
 * &lt;!DOCTYPE dataset SYSTEM "my-dataset.dtd"&gt;
 * &lt;dataset&gt;
 *     &lt;TEST_TABLE COL0="row 0 col 0"
 *         COL1="row 0 col 1"
 *         COL2="row 0 col 2"/&gt;
 *     &lt;TEST_TABLE COL1="row 1 col 1"/&gt;
 *     &lt;SECOND_TABLE COL0="row 0 col 0"
 *           COL1="row 0 col 1" /&gt;
 *     &lt;EMPTY_TABLE/&gt;
 * &lt;/dataset&gt;</pre>
 * <p>
 * To specify null values, omit corresponding attribute.
 * In the above example, missing COL0 and COL2 attributes of TEST_TABLE second row represents null values.
 * <p>
 * Table metadata is deduced from the first row of each table. <b>Beware that DbUnit may think
 * a table miss some columns if the first row of that table has one or more null values.</b>
 * Because of that, this is highly recommended to use DTD. DbUnit will use the
 * columns declared in the DTD as table metadata. DbUnit only support external system URI.
 * The URI can be absolute or relative.
 *
 * @author Manuel Laflamme
 * @version $Revision$
 * @since Mar 12, 2002
 */
public class FlatXmlDataSet extends CachedDataSet
{

    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(FlatXmlDataSet.class);

    /**
     * Creates an FlatXmlDataSet object with the specifed InputSource.
     */
    public FlatXmlDataSet(InputSource source) throws IOException, DataSetException
    {
        super(new FlatXmlProducer(source));
    }

    /**
     * Creates an FlatXmlDataSet object with the specifed xml file.
     * Relative DOCTYPE uri are resolved from the xml file path.
     *
     * @param xmlFile the xml file
     */
    public FlatXmlDataSet(File xmlFile) throws IOException, DataSetException
    {
        this(xmlFile, true);
    }

    /**
     * Creates an FlatXmlDataSet object with the specifed xml file.
     * Relative DOCTYPE uri are resolved from the xml file path.
     *
     * @param xmlFile the xml file
     * @param dtdMetadata if <code>false</code> do not use DTD as metadata
     */
    public FlatXmlDataSet(File xmlFile, boolean dtdMetadata)
            throws IOException, DataSetException
    {
        this(xmlFile.toURL(), dtdMetadata);
    }

    /**
     * Creates an FlatXmlDataSet object with the specifed xml URL.
     * Relative DOCTYPE uri are resolved from the xml file path.
     *
     * @param xmlUrl the xml URL
     */
    public FlatXmlDataSet(URL xmlUrl) throws IOException, DataSetException
    {
        this(xmlUrl, true);
    }

    /**
     * Creates an FlatXmlDataSet object with the specifed xml URL.
     * Relative DOCTYPE uri are resolved from the xml file path.
     *
     * @param xmlUrl the xml URL
     * @param dtdMetadata if <code>false</code> do not use DTD as metadata
     */
    public FlatXmlDataSet(URL xmlUrl, boolean dtdMetadata)
            throws IOException, DataSetException
    {
        super(new FlatXmlProducer(
                new InputSource(xmlUrl.toString()), dtdMetadata));
    }
    
    /**
     * Creates an FlatXmlDataSet object with the specifed xml reader.
     * Relative DOCTYPE uri are resolved from the current working dicrectory.
     *
     * @param xmlReader the xml reader
     */
    public FlatXmlDataSet(Reader xmlReader) throws IOException, DataSetException
    {
        this(xmlReader, true);
    }

    /**
     * Creates an FlatXmlDataSet object with the specifed xml reader.
     * Relative DOCTYPE uri are resolved from the current working dicrectory.
     *
     * @param xmlReader the xml reader
     * @param dtdMetadata if <code>false</code> do not use DTD as metadata
     */
    public FlatXmlDataSet(Reader xmlReader, boolean dtdMetadata)
            throws IOException, DataSetException
    {
        super(new FlatXmlProducer(
                new InputSource(xmlReader), dtdMetadata));
    }

    /**
     * Creates an FlatXmlDataSet object with the specifed xml and dtd readers.
     *
     * @param xmlReader the xml reader
     * @param dtdReader the dtd reader
     */
    public FlatXmlDataSet(Reader xmlReader, Reader dtdReader)
            throws IOException, DataSetException
    {
        this(xmlReader, new FlatDtdDataSet(dtdReader));
    }

    /**
     * Creates an FlatXmlDataSet object with the specifed xml reader.
     *
     * @param xmlReader the xml reader
     * @param metaDataSet the dataset used as metadata source.
     */
    public FlatXmlDataSet(Reader xmlReader, IDataSet metaDataSet)
            throws IOException, DataSetException
    {
        super(new FlatXmlProducer(
                new InputSource(xmlReader), metaDataSet));
    }

    /**
     * Creates an FlatXmlDataSet object with the specifed xml input stream.
     * Relative DOCTYPE uri are resolved from the current working dicrectory.
     *
     * @param xmlStream the xml input stream
     */
    public FlatXmlDataSet(InputStream xmlStream) throws IOException, DataSetException
    {
        this(xmlStream, true);
    }

    /**
     * Creates an FlatXmlDataSet object with the specifed xml input stream.
     * Relative DOCTYPE uri are resolved from the current working dicrectory.
     *
     * @param xmlStream the xml input stream
     * @param dtdMetadata if <code>false</code> do not use DTD as metadata
     */
    public FlatXmlDataSet(InputStream xmlStream, boolean dtdMetadata)
            throws IOException, DataSetException
    {
        super(new FlatXmlProducer(
                new InputSource(xmlStream), dtdMetadata));
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
        super(new FlatXmlProducer(
                new InputSource(xmlStream), metaDataSet));
    }

    /**
     * Write the specified dataset to the specified output stream as xml.
     */
    public static void write(IDataSet dataSet, OutputStream out)
            throws IOException, DataSetException
    {
        logger.debug("write(dataSet=" + dataSet + ", out=" + out + ") - start");

        FlatXmlWriter datasetWriter = new FlatXmlWriter(out);
        datasetWriter.setIncludeEmptyTable(true);
        datasetWriter.write(dataSet);
    }

    /**
     * Write the specified dataset to the specified writer as xml.
     */
    public static void write(IDataSet dataSet, Writer writer)
            throws IOException, DataSetException
    {
        logger.debug("write(dataSet=" + dataSet + ", writer=" + writer + ") - start");

        write(dataSet, writer, null);
    }

    /**
     * Write the specified dataset to the specified writer as xml.
     */
    public static void write(IDataSet dataSet, Writer writer, String encoding)
            throws IOException, DataSetException
    {
        logger.debug("write(dataSet=" + dataSet + ", writer=" + writer + ", encoding=" + encoding + ") - start");

        FlatXmlWriter datasetWriter = new FlatXmlWriter(writer, encoding);
        datasetWriter.setIncludeEmptyTable(true);
        datasetWriter.write(dataSet);
    }

    /**
     * Write a DTD for the specified dataset to the specified output.
     * @deprecated use {@link FlatDtdDataSet#write}
     */
    public static void writeDtd(IDataSet dataSet, OutputStream out)
            throws IOException, DataSetException
    {
        logger.debug("writeDtd(dataSet=" + dataSet + ", out=" + out + ") - start");

        FlatDtdDataSet.write(dataSet, out);
    }
}











