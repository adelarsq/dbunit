/*
 * FlatXmlDataSet.java   Mar 12, 2002
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
     * @param noneAsNull if <code>true</code> this specify that the absence of
     * value should be considered as a null value
     */
    public FlatXmlDataSet(InputStream in) throws DataSetException
    {
        super(createTables(in, true));
    }

    /**
     * Write the specified dataset to the specified output as xml.
     */
    public static void write(IDataSet dataSet, OutputStream out)
            throws IOException, DataSetException
    {
        createDocument(dataSet).write(out);
    }

    /**
     * Write a DTD for the specified dataset to the specified output.
     */
    public static void writeDtd(IDataSet dataSet, OutputStream out)
            throws IOException, DataSetException
    {
        PrintStream printOut = new PrintStream(out);
        String[] tableNames = dataSet.getTableNames();

        // dataset element
        printOut.println("<!ELEMENT dataset (");
        for (int i = 0; i < tableNames.length; i++)
        {
            printOut.print("    ");
            printOut.print(tableNames[i]);
            printOut.print("*");
            if (i + 1 < tableNames.length)
            {
                printOut.println(",");
            }
        }
        printOut.println(")>");
        printOut.println();

        // tables
        for (int i = 0; i < tableNames.length; i++)
        {
            // table element
            String tableName = tableNames[i];
            printOut.print("<!ELEMENT ");
            printOut.print(tableName);
            printOut.println(" EMPTY>");

            // column attributes
            printOut.print("<!ATTLIST ");
            printOut.println(tableName);
            Column[] columns = dataSet.getTableMetaData(tableName).getColumns();
            for (int j = 0; j < columns.length; j++)
            {
                Column column = columns[j];
                printOut.print("    ");
                printOut.print(column.getColumnName());
                if (column.getNullable() == Column.NULLABLE)
                {
                    printOut.println(" CDATA #IMPLIED");
                }
                else
                {
                    printOut.println(" CDATA #REQUIRED");
                }
            }
            printOut.println(">");
            printOut.println();
        }
    }

    private static ITable[] createTables(InputStream in, boolean noneAsNull) throws DataSetException
    {
        try
        {
            List tableList = new ArrayList();
            List rowList = new ArrayList();
            String lastTableName = null;
            Document document = new Document(in);

            Elements rowElems = document.getElement("dataset").getElements();
            while (rowElems.hasMoreElements())
            {
                Element rowElem = (Element)rowElems.nextElement();

                if (lastTableName != null &&
                        !lastTableName.equals(rowElem.getName()))
                {
                    Element[] elems = (Element[])rowList.toArray(new Element[0]);
                    rowList.clear();

                    tableList.add(new FlatXmlTable(elems, noneAsNull));
                }

                lastTableName = rowElem.getName();
                rowList.add(rowElem);
            }

            if (rowList.size() > 0)
            {
                Element[] elems = (Element[])rowList.toArray(new Element[0]);
                tableList.add(new FlatXmlTable(elems, noneAsNull));
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

        return document;
    }

}






