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
package org.dbunit.dataset.xml;

import org.dbunit.dataset.Column;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITableMetaData;

import java.io.PrintWriter;
import java.io.Writer;

/**
 * @author Manuel Laflamme
 * @since Jun 13, 2003
 * @version $Revision$
 */
public class FlatDtdWriter //implements IDataSetConsumer
{
    private Writer _writer;

    public FlatDtdWriter(Writer writer)
    {
        _writer = writer;
    }

    public void write(IDataSet dataSet) throws DataSetException
    {
        PrintWriter printOut = new PrintWriter(_writer);
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
}
