/*
 * XmlDataSet.java   Feb 17, 2002
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

import org.xml.sax.InputSource;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

/**
 * Provides persistence support to read from and write to the dbunit xml format.
 * This format is specified by the dataset.dtd file.
 *
 * @author Manuel Laflamme
 * @version $Revision$
 */
public class XmlDataSet extends CachedDataSet
{
    private static final String DEFAULT_ENCODING = "UTF8";

    /**
     * Creates an XmlDataSet with the specified xml reader.
     */
    public XmlDataSet(Reader reader) throws DataSetException
    {
        super(new XmlProducer(new InputSource(reader)));
    }

    /**
     * Creates an XmlDataSet with the specified xml input stream.
     */
    public XmlDataSet(InputStream in) throws DataSetException
    {
        super(new XmlProducer(new InputSource(in)));
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
        XmlDataSetWriter datasetWriter = new XmlDataSetWriter(writer, encoding);
        datasetWriter.write(dataSet);
    }
}