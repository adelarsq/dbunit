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

package org.dbunit.dataset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Manuel Laflamme
 * @version $Revision$
 * @since Feb 18, 2002
 */
public class DefaultDataSet extends AbstractDataSet
{

    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(DefaultDataSet.class);

    private final List _tableList = new ArrayList();

    public DefaultDataSet()
    {
    }

    public DefaultDataSet(ITable table)
    {
        addTable(table);
    }

    public DefaultDataSet(ITable[] tables)
    {
        for (int i = 0; i < tables.length; i++)
        {
            addTable(tables[i]);
        }
    }

    public DefaultDataSet(ITable table1, ITable table2)
    {
        addTable(table1);
        addTable(table2);
    }

    /**
     * Add a new table in this dataset.
     */
    public void addTable(ITable table)
    {
        logger.debug("addTable(table=" + table + ") - start");

        _tableList.add(table);
    }

    ////////////////////////////////////////////////////////////////////////////
    // AbstractDataSet class

    protected ITableIterator createIterator(boolean reversed)
            throws DataSetException
    {
        logger.debug("createIterator(reversed=" + reversed + ") - start");

        ITable[] tables = (ITable[])_tableList.toArray(new ITable[0]);
        return new DefaultTableIterator(tables, reversed);
    }
}






