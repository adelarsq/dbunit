/*
 * XmlDataSet.java   Feb 17, 2002
 *
 * The dbUnit database testing framework.
 * Copyright (C) 2002   Manuel Laflamme
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
 * @author Manuel Laflamme
 * @version 1.0
 */
public class XmlDataSet extends DefaultDataSet
{
    public XmlDataSet(InputStream in) throws DataSetException
    {
        super(createTables(in));
    }

    private static ITable[] createTables(InputStream in) throws DataSetException
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

            return (ITable[])tableList.toArray(new ITable[0]);
        }
        catch (ParseException e)
        {
            throw new DataSetException(e);
        }
    }

    public static void write(IDataSet dataSet, OutputStream out) throws IOException, DataSetException
    {
        createDocument(dataSet).write(out);
    }

    private static Document createDocument(IDataSet dataSet) throws DataSetException
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
                            String stringValue = (String)DataType.STRING.typeCast(value);
                            rowElem.addElement("value").setText(stringValue);
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

}
