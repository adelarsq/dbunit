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

package org.dbunit.ant;

import org.dbunit.Assertion;
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.CompositeTable;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.SortedTable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * The <code>Compare</code> class is the step that compare the content of the
 * database against the specified dataset.
 *
 * @author Manuel Laflamme
 * @version $Revision$
 * @since Apr 3, 2004
 * @see DbUnitTaskStep
 */
public class Compare extends AbstractStep
{
    private static final String DEFAULT_FORMAT = FORMAT_FLAT;

    private String _format;
    private File _src;
    private List _tables = new ArrayList();
    private boolean _sort = false;

    public File getSrc()
    {
        return _src;
    }

    public void setSrc(File src)
    {
        _src = src;
    }

    public void setSort(boolean sort)
    {
        _sort = sort;
    }

    public String getFormat()
    {
        return _format != null ? _format : DEFAULT_FORMAT;
    }

    public void setFormat(String format)
    {
        if (format.equalsIgnoreCase(FORMAT_FLAT)
                || format.equalsIgnoreCase(FORMAT_XML)
                || format.equalsIgnoreCase(FORMAT_CSV)
        )
        {
            _format = format;
        }
        else
        {
            throw new IllegalArgumentException("Type must be either 'flat'(default) csv or 'xml' but was: " + format);
        }
    }

    public List getTables()
    {
        return _tables;
    }

    public void addTable(Table table)
    {
        _tables.add(table);
    }

    public void addQuery(Query query)
    {
        _tables.add(query);
    }

    public void execute(IDatabaseConnection connection) throws DatabaseUnitException
    {
        IDataSet expectedDataset = getSrcDataSet(_src, getFormat(), false);
        IDataSet actualDataset = getDatabaseDataSet(connection, _tables, false);

        String[] tableNames = null;
        if (_tables.size() > 0)
        {
            tableNames = actualDataset.getTableNames();
        }
        else
        {
            tableNames = expectedDataset.getTableNames();
        }

        for (int i = 0; i < tableNames.length; i++)
        {
            String tableName = tableNames[i];
            ITable expectedTable = expectedDataset.getTable(tableName);
            ITable actualTable = actualDataset.getTable(tableName);
            actualTable = new CompositeTable(
                    expectedTable.getTableMetaData(), actualTable);

            if (_sort)
            {
                expectedTable = new SortedTable(expectedTable);
                actualTable = new SortedTable(actualTable);
            }
            Assertion.assertEquals(expectedTable, actualTable);
        }
    }

    public String getLogMessage()
    {
        return "Executing compare: "
                + "\n          from file: " + ((_src == null) ? null : _src.getAbsolutePath())
                + "\n          with format: " + _format;
    }

    public String toString()
    {
        StringBuffer result = new StringBuffer();
        result.append("Compare: ");
        result.append(" src=" + _src.getAbsolutePath());
        result.append(", format= " + _format);
        result.append(", tables= " + _tables);

        return result.toString();
    }
}
