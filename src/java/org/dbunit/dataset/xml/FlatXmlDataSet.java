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

import org.dbunit.dataset.CachedDataSet;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

/**
 * @author Manuel Laflamme
 * @version $Revision$
 */
public class FlatXmlDataSet extends CachedDataSet
{
    private static final String DEFAULT_ENCODING = "UTF8";

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
        super(new FlatXmlProducer(
                new InputSource(xmlFile.toURL().toString()),
                dtdMetadata));
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
        super(new FlatXmlProducer(
                new InputSource(xmlReader),
                new DtdEntityResolver(dtdReader)));
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
        super(new FlatXmlProducer(
                new InputSource(xmlStream),
                new DtdEntityResolver(dtdStream)));
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
        OutputStreamWriter writer = new OutputStreamWriter(out, DEFAULT_ENCODING);
        write(dataSet, writer);
    }

    /**
     * Write the specified dataset to the specified writer as xml.
     */
    public static void write(IDataSet dataSet, Writer writer)
            throws IOException, DataSetException
    {
        write(dataSet, writer, null);
    }

    /**
     * Write the specified dataset to the specified writer as xml.
     */
    public static void write(IDataSet dataSet, Writer writer, String encoding)
            throws IOException, DataSetException
    {
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
        FlatDtdDataSet.write(dataSet, out);
    }

    private static class DtdEntityResolver implements EntityResolver
    {
        InputSource _dtdSource;

        public DtdEntityResolver(InputSource dtdSource)
        {
            _dtdSource = dtdSource;
        }

        public DtdEntityResolver(Reader dtdReader)
        {
            this(new InputSource(dtdReader));
        }

        public DtdEntityResolver(InputStream dtdStream)
        {
            this(new InputSource(dtdStream));
        }

        public InputSource resolveEntity(String publicId, String systemId)
                throws SAXException, IOException
        {
            return _dtdSource;
        }

    }
}











