/*
 * XmlDataSet.java   Feb 17, 2002
 *
 * DbUnit Database Testing Framework
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

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import org.dbunit.dataset.*;
import org.dbunit.dataset.datatype.DataType;
import org.dbunit.dataset.datatype.TypeCastException;
import electric.xml.*;

/**
 * Provides persistence support to read from and write to the dbunit xml format.
 * This format is specified by the dataset.dtd file.
 *
 * @author Manuel Laflamme
 * @version 1.0
 */
public class XmlDataSet extends AbstractDataSet
{
    private final ITable[] _tables;

    /**
     * Creates an XmlDataSet with the specified xml input stream.
     */
    public XmlDataSet(InputStream in) throws DataSetException
    {
        try
        {
            Document document = new Document(in);
            Elements tableElems = document.getElement("dataset").getElements("table");

            List tableList = new ArrayList();
            while (tableElems.hasMoreElements())
            {
                Element tableElem = (Element)tableElems.nextElement();
                ITable table = new XmlTable(tableElem);
                tableList.add(table);
            }

            _tables = (ITable[])tableList.toArray(new ITable[0]);
        }
        catch (ParseException e)
        {
            throw new DataSetException(e);
        }
    }

    private ITable[] getTables(Document document) throws DataSetException
    {
            Elements tableElems = document.getElement("dataset").getElements("table");

            List tableList = new ArrayList();
            while (tableElems.hasMoreElements())
            {
                Element tableElem = (Element)tableElems.nextElement();
                ITable table = new XmlTable(tableElem);
                tableList.add(table);
            }

            return (ITable[])tableList.toArray(new ITable[0]);
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

    private static Document buildDocument(IDataSet dataSet) throws DataSetException
    {
        Document document = new Document();
        String[] tableNames = dataSet.getTableNames();

        // dataset
        Element rootElem = document.addElement("dataset");
        for (int i = 0; i < tableNames.length; i++)
        {
            String tableName = tableNames[i];
            ITable table = dataSet.getTable(tableName);
            ITableMetaData metaData = table.getTableMetaData();

            // table
            Element tableElem = rootElem.addElement("table");
            tableElem.setAttribute("name", tableName);

            // columns
            Column[] columns = metaData.getColumns();
            for (int j = 0; j < columns.length; j++)
            {
                Column column = columns[j];
                tableElem.addElement("column").setText(column.getColumnName());
            }

            // rows
            for (int j = 0; j < table.getRowCount(); j++)
            {
                Element rowElem = tableElem.addElement("row");
                for (int k = 0; k < columns.length; k++)
                {
                    Column column = columns[k];
                    Object value = table.getValue(j, column.getColumnName());

                    // null
                    if (value == null)
                    {
                        rowElem.addElement("null");
                    }
                    // none
                    else if (value == ITable.NO_VALUE)
                    {
                        rowElem.addElement("none");
                    }
                    // values
                    else
                    {
                        try
                        {
                            String string = DataType.asString(value);

                            Text text = null;
                            if (string.startsWith(" ") || string.endsWith(""))
                            {
                                text = new CData(string);
                            }
                            else
                            {
                                text = new Text(string);
                            }

                            rowElem.addElement("value").setText(text);
                        }
                        catch (TypeCastException e)
                        {
                            throw new DataSetException("table=" +
                                    metaData.getTableName() + ", row=" + j +
                                    ", column=" + column.getColumnName() +
                                    ", value=" + value, e);
                        }
                    }
                }
            }
        }
        return document;
    }

    ////////////////////////////////////////////////////////////////////////////
    // AbstractDataSet class

    protected ITable[] getTables() throws DataSetException
    {
        return _tables;
    }

}





