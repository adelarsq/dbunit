/*
 * XmlRowDataSet.java   Mar 12, 2002
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
 * @since 1.2
 * @version 1.0
 */
public class XmlRowDataSet extends DefaultDataSet
{
    /**
     * Creates an XmlRowDataSet object with specifed xml input stream.
     *
     * @param in the xml contents
     * @param noneAsNull if <code>true</code> this specify that the absence of
     * value should be considered as a null value
     */
    public XmlRowDataSet(InputStream in, boolean noneAsNull) throws DataSetException
    {
        super(createTables(in, noneAsNull));
    }

    /**
     * Write the specified dataset to the specified output as xml.
     */
    public static void write(IDataSet dataSet, OutputStream out)
            throws IOException, DataSetException
    {
        createDocument(dataSet).write(out);
    }

    private static ITable[] createTables(InputStream in, boolean noneAsNull) throws DataSetException
    {
        try
        {
            List tableList = new ArrayList();
            List rowList = new ArrayList();
            String lastTableName = null;
            Document document = new Document(in);

            Elements rowElems = document.getElement("dataset2").getElements();
            while (rowElems.hasMoreElements())
            {
                Element rowElem = (Element)rowElems.nextElement();

                if (lastTableName != null &&
                        !lastTableName.equals(rowElem.getName()))
                {
                    Element[] elems = (Element[])rowList.toArray(new Element[0]);
                    rowList.clear();

                    tableList.add(new XmlRowTable(elems, noneAsNull));
                }

                lastTableName = rowElem.getName();
                rowList.add(rowElem);
            }

            if (rowList.size() > 0)
            {
                Element[] elems = (Element[])rowList.toArray(new Element[0]);
                tableList.add(new XmlRowTable(elems, noneAsNull));
            }

            return (ITable[])tableList.toArray(new ITable[0]);
        }
        catch (ParseException e)
        {
            throw new DataSetException(e);
        }
    }

    private static Document createDocument(IDataSet dataSet) throws DataSetException
    {
        Document document = new Document();
        String[] tableNames = dataSet.getTableNames();

        // dataset
        Element rootElem = document.addElement("dataset2");

        // tables
        for (int i = 0; i < tableNames.length; i++)
        {
            String tableName = tableNames[i];
            ITable table = dataSet.getTable(tableName);
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
                            String stringValue = (String)DataType.STRING.typeCast(value);
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

}
