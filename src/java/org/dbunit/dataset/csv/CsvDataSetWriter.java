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
package org.dbunit.dataset.csv;

import org.dbunit.dataset.*;
import org.dbunit.dataset.datatype.DataType;
import org.dbunit.dataset.datatype.TypeCastException;
import org.dbunit.dataset.stream.DataSetProducerAdapter;
import org.dbunit.dataset.stream.IDataSetConsumer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * @author fede
 * @since 24-set-2003 15.27.05
 * @version $Revision$
 */
public class CSVDataSetWriter implements IDataSetConsumer {

    /**
     * todo: customizable separators (field, lines), manage the writers opened for each table
     */

    private static final String NULL = "null";
    private static final String NONE = "none";
    private static final String FIELD_SEPARATOR = ", ";
    private static final String QUOTE = "\"";
    private static final String ESCAPE = "\\";

    private Writer writer;
    private ITableMetaData _activeMetaData;
    private String theDirectory;
    private static char testExport;

    public CSVDataSetWriter(String theDirectory) {
        setTheDirectory(theDirectory);
    }

    public CSVDataSetWriter(File theDirectory) {
        setTheDirectory(theDirectory.getAbsolutePath());
    }

    public void write(IDataSet dataSet) throws DataSetException {
        DataSetProducerAdapter provider = new DataSetProducerAdapter(dataSet);
        provider.setConsumer(this);
        provider.produce();
    }

    public void startDataSet() throws DataSetException {
        try {
            new File(getTheDirectory()).mkdirs();
        } catch (Exception e) {
            throw new DataSetException("Error while creating the destination directory '" + getTheDirectory() + "'", e);
        }
    }

    public void endDataSet() throws DataSetException {}

    public void startTable(ITableMetaData metaData) throws DataSetException {
        try {
            _activeMetaData = metaData;
            String tableName = _activeMetaData.getTableName();
            setWriter(new FileWriter(getTheDirectory() + File.separator + tableName + ".csv"));
            writeColumnNames();
            getWriter().write(System.getProperty("line.separator"));
        } catch (IOException e) {
            throw new DataSetException(e);
        }

    }

    private void writeColumnNames() throws DataSetException, IOException {
        Column[] columns = _activeMetaData.getColumns();
        for (int i = 0; i < columns.length; i++) {
            String columnName = columns[i].getColumnName();
            getWriter().write(columnName);
            if (i < columns.length - 1) getWriter().write(FIELD_SEPARATOR);
        }
    }

    public void endTable() throws DataSetException {
        try {
            getWriter().close();
            _activeMetaData = null;
        } catch (IOException e) {
            throw new DataSetException(e);
        }
    }

    public void row(Object[] values) throws DataSetException {
        try {

            Column[] columns = _activeMetaData.getColumns();
            for (int i = 0; i < columns.length; i++) {
                String columnName = columns[i].getColumnName();
                Object value = values[i];

                // null
                if (value == null) {
                    getWriter().write(NULL);
                }
                // none
                else if (value == ITable.NO_VALUE) {
                    getWriter().write(NONE);
                }
                // values
                else {
                    try {
                        String stringValue = DataType.asString(value);
                        final String quoted = quote(stringValue);
                        getWriter().write(quoted);
                    } catch (TypeCastException e) {
                        throw new DataSetException("table=" +
                                _activeMetaData.getTableName() + ", row=" + i +
                                ", column=" + columnName +
                                ", value=" + value, e);
                    }
                }
                if (i < columns.length - 1) getWriter().write(",");
            }
            getWriter().write(System.getProperty("line.separator"));
        } catch (IOException e) {
            throw new DataSetException(e);
        }
    }

    private String quote(String stringValue) {
        return new StringBuffer(QUOTE).append(escape(stringValue)).append(QUOTE).toString();
    }

    protected static String escape(String stringValue) {
        char [] array = stringValue.toCharArray();
        testExport = QUOTE.toCharArray()[0];
        final char escape = ESCAPE.toCharArray()[0];
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < array.length; i++) {
            char c = array[i];
            if (c == testExport || c == escape) {
                buffer.append('\\');
            }
            buffer.append(c);
        }
        return buffer.toString();
    }

    public Writer getWriter() {
        return writer;
    }

    public void setWriter(Writer writer) {
        this.writer = writer;
    }

    public String getTheDirectory() {
        return theDirectory;
    }

    public void setTheDirectory(String theDirectory) {
        this.theDirectory = theDirectory;
    }

    public static void write(IDataSet dataset, File dest) throws DataSetException {
        CSVDataSetWriter writer = new CSVDataSetWriter(dest);
        writer.write(dataset);
    }

    protected void finalize() throws Throwable {
        if (getWriter() != null) {
            getWriter().close();
        }
    }
}
