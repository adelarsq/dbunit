/*
 * FlatXmlTable.java   Mar 12, 2002
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

import java.util.ArrayList;
import java.util.List;

import org.dbunit.dataset.*;
import org.dbunit.dataset.datatype.DataType;
import electric.xml.*;

/**
 * @author Manuel Laflamme
 * @version $Revision$
 */
public class FlatXmlTable extends AbstractTable
{
    private final ITableMetaData _metaData;
    private final Element[] _rows;

    /**
     * Creates a new FlatXmlTable object with specified rows and metadata.
     * @param rows the table rows
     * @param metaData the table metadata
     */
    FlatXmlTable(Element[] rows, ITableMetaData metaData)
    {
        // metadata
        _metaData = metaData;
        _rows = rows;
    }

    static ITableMetaData createMetaData(Element sampleRow)
    {
        String tableName = sampleRow.getName();

        List columnList = new ArrayList();
        Attributes columnAttributes = sampleRow.getAttributes();
        while (columnAttributes.hasMoreElements())
        {
            Attribute columnAttr = (Attribute)columnAttributes.nextElement();
            Column column = new Column(columnAttr.getName(), DataType.UNKNOWN);
            columnList.add(column);
        }

        Column[] columns = (Column[])columnList.toArray(new Column[0]);
        return new DefaultTableMetaData(tableName, columns);
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

        return _rows[row].getAttributeValue(column);
    }

}






