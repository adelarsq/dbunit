/*
 * FlatXmlDataSet.java   Mar 12, 2002
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

import electric.xml.*;
import org.dbunit.dataset.*;
import org.dbunit.dataset.datatype.DataType;
import org.dbunit.dataset.datatype.TypeCastException;

/**
 * @author Manuel Laflamme
 * @since 1.2
 * @version 1.0
 */
public class FlatXmlDataSet extends DefaultDataSet
{
    /**
     * Creates an FlatXmlDataSet object with specifed xml input stream.
     *
     * @param in the xml contents
     */
    public FlatXmlDataSet(InputStream in) throws IOException, DataSetException
    {
        super(createTables(in));
    }

    /**
     * Write the specified dataset to the specified output as xml.
     */
    public static void write(IDataSet dataSet, OutputStream out)
            throws IOException, DataSetException
    {
        Document document = new Document();
        String[] tableNames = dataSet.getTableNames();

        // dataset
        Element rootElem = document.addElement("dataset");

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

        // write xml document
        document.write(out);
    }

    /**
     * Write a DTD for the specified dataset to the specified output.
     * @deprecated use {@link FlatXmlDocType#write}
     */
    public static void writeDtd(IDataSet dataSet, OutputStream out)
            throws IOException, DataSetException
    {
        FlatXmlDocType.write(dataSet, out);
    }

    private static ITable[] createTables(InputStream in)
            throws IOException, DataSetException
    {
        try
        {
            List tableList = new ArrayList();
            List rowList = new ArrayList();
            String lastTableName = null;

            Document document = new Document(in);

//            // Load dtd if defined
//            FlatXmlDocType dtdDataSet = null;
//            DocType docType = document.getDocType();
//            if (docType != null && docType.getExternalId() != null)
//            {
//                dtdDataSet = new FlatXmlDocType(
//                        new FileInputStream(docType.getExternalId()));
//            }

            Elements rowElems = document.getElement("dataset").getElements();
            while (rowElems.hasMoreElements())
            {
                Element rowElem = (Element)rowElems.nextElement();

                if (lastTableName != null &&
                        !lastTableName.equals(rowElem.getName()))
                {
                    Element[] elems = (Element[])rowList.toArray(new Element[0]);
                    rowList.clear();

                    FlatXmlTable table = new FlatXmlTable(elems);
                    tableList.add(table);
                }

                lastTableName = rowElem.getName();
                rowList.add(rowElem);
            }

            if (rowList.size() > 0)
            {
                Element[] elems = (Element[])rowList.toArray(new Element[0]);
                tableList.add(new FlatXmlTable(elems));
            }

            return (ITable[])tableList.toArray(new ITable[0]);
        }
        catch (ParseException e)
        {
            throw new DataSetException(e);
        }
    }

}







