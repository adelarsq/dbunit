/*
 * FlatDtdDataSet.java   Apr 4, 2002
 *
 * Copyright (c)2002 Manuel Laflamme. All Rights Reserved.
 *
 * This software is the proprietary information of Manuel Laflamme.
 * Use is subject to license terms.
 *
 */

package org.dbunit.dataset.xml;

import org.dbunit.dataset.*;
import org.dbunit.dataset.datatype.DataType;
import org.dbunit.database.AmbiguousTableNameException;

import java.io.*;
import java.util.*;

import com.wutka.dtd.*;

/**
 * @author Manuel Laflamme
 * @version $Revision$
 */
public class FlatDtdDataSet extends AbstractDataSet
{
    private static final List EMPTY_LIST = Arrays.asList(new Object[0]);
    private final List _tableNames = new ArrayList();
    private final Map _tableMap = new HashMap();

    /**
     * @deprecated Use Reader overload instead
     */
    public FlatDtdDataSet(InputStream in) throws IOException
    {
        this(new InputStreamReader(in));
    }

    public FlatDtdDataSet(Reader in) throws IOException
    {
        DTDParser dtdParser = new DTDParser(in);
        DTD dtd = dtdParser.parse(true);

        // table names
        DTDContainer contents = (DTDContainer)dtd.rootElement.getContent();
        DTDItem[] items = contents.getItems();

        for (int i = 0; i < items.length; i++)
        {
            DTDName tableName = (DTDName)items[i];
            _tableNames.add(tableName.getValue());
        }

        // table metadata
        Vector attrLists = dtd.getItemsByType(DTDAttlist.class);
        for (int i = 0; i < attrLists.size(); i++)
        {
            DTDAttlist attrList = (DTDAttlist)attrLists.elementAt(i);
            if (_tableNames.contains(attrList.getName()))
            {
                _tableMap.put(attrList.getName(), new DefaultTable(
                        createTableMetaData(attrList), EMPTY_LIST));
            }
        }
    }

    /**
     * Write the specified dataset to the specified output stream as DTD.
     */
    public static void write(IDataSet dataSet, OutputStream out)
            throws IOException, DataSetException
    {
        write(dataSet, new OutputStreamWriter(out));
    }

    /**
     * Write the specified dataset to the specified writer as DTD.
     */
    public static void write(IDataSet dataSet, Writer out)
            throws IOException, DataSetException
    {
        PrintWriter printOut = new PrintWriter(out);
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

        printOut.flush();
    }

    private ITableMetaData createTableMetaData(DTDAttlist attrList)
    {
        DTDAttribute[] attributes = attrList.getAttribute();
        Column[] columns = new Column[attributes.length];
        for (int i = 0; i < attributes.length; i++)
        {
            DTDAttribute attribute = attributes[i];
            Column.Nullable nullable = (attribute.getDecl() == DTDDecl.REQUIRED) ?
                    Column.NO_NULLS : Column.NULLABLE;
            columns[i] = new Column(attribute.getName(), DataType.UNKNOWN,
                    nullable);
        }

        return new DefaultTableMetaData(attrList.getName(), columns);
    }

    ////////////////////////////////////////////////////////////////////////////
    // IDataSet interface

    public String[] getTableNames() throws DataSetException
    {
        return (String[])_tableNames.toArray(new String[0]);
    }

//    public ITableMetaData getTableMetaData(String tableName)
//            throws DataSetException
//    {
//        return getTable(tableName).getTableMetaData();
//    }
//
//    public ITable getTable(String tableName) throws DataSetException
//    {
//        if (_tableNames.indexOf(tableName) != _tableNames.lastIndexOf(tableName))
//        {
//            throw new AmbiguousTableNameException(tableName);
//        }
//
//        ITable table = (ITable)_tableMap.get(tableName);
//        if (table == null)
//        {
//            throw new NoSuchTableException(tableName);
//        }
//
//        return table;
//    }

    public ITable[] getTables() throws DataSetException
    {
        String[] names = (String[])_tableNames.toArray(new String[0]);
        ITable[] tables = new ITable[names.length];
        for (int i = 0; i < names.length; i++)
        {
            String tableName = names[i];
            ITable table = (ITable)_tableMap.get(tableName);
            if (table == null)
            {
                throw new NoSuchTableException(tableName);
            }

            tables[i] = table;
        }

        return tables;
    }

}




