/*
 * DefaultDataSet.java   Feb 18, 2002
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

package org.dbunit.dataset;


/**
 * @author Manuel Laflamme
 * @version $Revision$
 */
public class DefaultDataSet extends AbstractDataSet
{
    private final ITable[] _tables;

    public DefaultDataSet(ITable table)
    {
        _tables = new ITable[]{table};
    }

    public DefaultDataSet(ITable[] tables)
    {
        _tables = tables;
    }

    ////////////////////////////////////////////////////////////////////////////
    // AbstractDataSet class

    protected ITable[] getTables() throws DataSetException
    {
        return _tables;
    }
}





