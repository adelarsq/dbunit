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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.dbunit.Assertion;
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.SortedTable;
import org.dbunit.dataset.ITableMetaData;
import org.dbunit.dataset.filter.DefaultColumnFilter;

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

    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(Compare.class);

    private static final String DEFAULT_FORMAT = FORMAT_FLAT;

    private String _format;
    private File _src;
    private List _tables = new ArrayList();
    private boolean _sort = false;

    public File getSrc()
    {
        logger.debug("getSrc() - start");

        return _src;
    }

    public void setSrc(File src)
    {
        logger.debug("setSrc(src=" + src + ") - start");

        _src = src;
    }

    public void setSort(boolean sort)
    {
        logger.debug("setSort(sort=" + sort + ") - start");

        _sort = sort;
    }

    public String getFormat()
    {
        logger.debug("getFormat() - start");

        return _format != null ? _format : DEFAULT_FORMAT;
    }

    public void setFormat(String format)
    {
        logger.debug("setFormat(format=" + format + ") - start");

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
        logger.debug("getTables() - start");

        return _tables;
    }

    public void addTable(Table table)
    {
        logger.debug("addTable(table=" + table + ") - start");

        _tables.add(table);
    }

    public void addQuery(Query query)
    {
        logger.debug("addQuery(query=" + query + ") - start");

        _tables.add(query);
    }

    public void execute(IDatabaseConnection connection) throws DatabaseUnitException
    {
        logger.debug("execute(connection=" + connection + ") - start");

        IDataSet expectedDataset = getSrcDataSet(_src, getFormat(), false);
        IDataSet actualDataset = getDatabaseDataSet(connection, _tables, false);

        String[] tableNames = null;
        if (_tables.size() == 0)
        {
            // No tables specified, assume must compare all tables from
            // expected dataset
            tableNames = expectedDataset.getTableNames();
        }
        else
        {
            tableNames = actualDataset.getTableNames();
        }

        for (int i = 0; i < tableNames.length; i++)
        {
            String tableName = tableNames[i];
            ITable expectedTable = expectedDataset.getTable(tableName);
            ITableMetaData expectedMetaData = expectedTable.getTableMetaData();

            // Only compare columns present in expected table. Extra columns
            // are filtered out from actual database table.
            ITable actualTable = actualDataset.getTable(tableName);
            actualTable = DefaultColumnFilter.includedColumnsTable(
                    actualTable, expectedMetaData.getColumns());

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
        logger.debug("getLogMessage() - start");

        return "Executing compare: "
                + "\n          from file: " + ((_src == null) ? null : _src.getAbsolutePath())
                + "\n          with format: " + _format;
    }

    public String toString()
    {
        logger.debug("toString() - start");

        StringBuffer result = new StringBuffer();
        result.append("Compare: ");
        result.append(" src=" + _src.getAbsolutePath());
        result.append(", format= " + _format);
        result.append(", tables= " + _tables);

        return result.toString();
    }
}

