/*
 * XmlRowTable.java   Mar 12, 2002
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

import java.util.ArrayList;
import java.util.List;

import org.dbunit.dataset.*;
import org.dbunit.dataset.datatype.DataType;
import electric.xml.*;

/**
 * @author Manuel Laflamme
 * @version 1.0
 */
public class XmlRowTable extends AbstractTable
{
    private final ITableMetaData _metaData;
    private final Element[] _rows;
    private final boolean _noneAsNull;

    /**
     * Creates a new XmlRowTable object with specified rows.
     *
     * @param rows the table rows
     * @param noneAsNull if <code>true</code> the {@link getValue} method
     * return <code>null</code> if no value exist.
     */
    public XmlRowTable(Element[] rows, boolean noneAsNull)
    {
        _noneAsNull = noneAsNull;

        // metadata
        Element firstRow = rows[0];
        String tableName = firstRow.getName();

        List columnList = new ArrayList();
        Attributes columnAttributes = firstRow.getAttributes();
        while (columnAttributes.hasMoreElements())
        {
            Attribute columnAttr = (Attribute)columnAttributes.nextElement();
            Column column = new Column(columnAttr.getName(), DataType.OBJECT);
            columnList.add(column);
        }
        Column[] columns = (Column[])columnList.toArray(new Column[0]);
        _metaData = new DefaultTableMetaData(tableName, columns);

        // rows
        if (columns.length == 0)
        {
            rows = new Element[0];
        }
        _rows = rows;

    }

    ////////////////////////////////////////////////////////////////////////////
    // ITable interface

    public ITableMetaData getTableMetaData()
    {
        return _metaData;
    }

    public int getRowCount()
    {
        return _rows.length;
    }

    public Object getValue(int row, String column) throws DataSetException
    {
        assertValidRowIndex(row);
        assertValidColumn(column);

        Object value = _rows[row].getAttributeValue(column);
        if (value == null && !_noneAsNull)
        {
            value = NO_VALUE;
        }
        return value;
    }

}
