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

package org.dbunit.dataset.csv;

import org.dbunit.dataset.Column;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.DefaultTableMetaData;
import org.dbunit.dataset.ITableMetaData;
import org.dbunit.dataset.csv.handlers.PipelineException;
import org.dbunit.dataset.datatype.DataType;
import org.dbunit.dataset.stream.DefaultConsumer;
import org.dbunit.dataset.stream.IDataSetConsumer;
import org.dbunit.dataset.stream.IDataSetProducer;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.List;

/**
 * @author Federico Spinazzi
 * @since Sep 17, 2003
 * @version $Revision$
 */

public class CsvProducer implements IDataSetProducer {

    private static final IDataSetConsumer EMPTY_CONSUMER = new DefaultConsumer();
    private IDataSetConsumer _consumer = EMPTY_CONSUMER;
    private String _theDirectory;

    public CsvProducer(String theDirectory) {
        _theDirectory = theDirectory;
    }

    public CsvProducer(File theDirectory) {
        _theDirectory = theDirectory.getAbsolutePath();
    }

    public void setConsumer(IDataSetConsumer consumer) throws DataSetException {
        _consumer = consumer;
    }

    public void produce() throws DataSetException {

        File dir = new File(_theDirectory);

        if (!dir.isDirectory()) {
            throw new DataSetException("'" + _theDirectory + "' should be a directory");
        }

        // @todo: move in a class by itself, somewhere
        FilenameFilter filter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".csv") && !dir.isFile();
            }
        };

        _consumer.startDataSet();

        File[] children = dir.listFiles(filter);
        for (int i = 0; i < children.length; i++) {
            try {
                produceFromFile(children[i]);
            } catch (CsvParserException e) {
                throw new DataSetException(e);
            }
        }

        _consumer.endDataSet();

    }

    private void produceFromFile(File theDataFile) throws DataSetException, CsvParserException {
        try {
            CsvParser parser = new CsvParserImpl();
            List readData = parser.parse(theDataFile);
            List readColumns = ((List) readData.get(0));
            Column[] columns = new Column[readColumns.size()];

            for (int i = 0; i < readColumns.size(); i++) {
                columns[i] = new Column((String) readColumns.get(i), DataType.UNKNOWN);
            }

            String tableName = theDataFile.getName().substring(0, theDataFile.getName().indexOf(".csv"));
            ITableMetaData metaData = new DefaultTableMetaData(tableName, columns);
            _consumer.startTable(metaData);
            for (int i = 1 ; i < readData.size(); i++) {
                List rowList = (List)readData.get(i);
                _consumer.row(rowList.toArray());
            }
            _consumer.endTable();
        } catch (PipelineException e) {
            throw new DataSetException(e);
        } catch (IllegalInputCharacterException e) {
            throw new DataSetException(e);
        } catch (IOException e) {
            throw new DataSetException(e);
        }
    }

}
