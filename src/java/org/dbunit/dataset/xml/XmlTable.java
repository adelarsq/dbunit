/*
 * XmlTable.java   Feb 17, 2002
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

import electric.xml.Element;
import electric.xml.Elements;
import org.dbunit.dataset.*;
import org.dbunit.dataset.datatype.DataType;

/**
 * @author Manuel Laflamme
 * @version 1.0
 */
public class XmlTable extends AbstractTable
{
    private final ITableMetaData _metaData;
    private final Element[] _rows;

    public XmlTable(Element tableElem) throws DataSetException
    {
        // metadata
        String tableName = tableElem.getAttributeValue("name");

        List columnList = new ArrayList();
        Elements columnElems = tableElem.getElements("column");
        while (columnElems.hasMoreElements())
        {
            Element columnElem = (Element)columnElems.nextElement();
            Column column = new Column(columnElem.getTextString(),
                    DataType.OBJECT);
            columnList.add(column);
        }
        Column[] columns = (Column[])columnList.toArray(new Column[0]);
        _metaData = new DefaultTableMetaData(tableName, columns);

        // rows
        Elements rowElems = tableElem.getElements("row");
        List rowList = new ArrayList();
        while (rowElems.hasMoreElements())
        {
            Element rowElem = (Element)rowElems.nextElement();
            rowList.add(rowElem);
        }
        _rows = (Element[])rowList.toArray(new Element[0]);
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

        Element rowElem = _rows[row];
        try
        {
            Element valueElem = rowElem.getElementAt(getColumnIndex(column) + 1);
            if (valueElem.getName().equals("value"))
            {
                return valueElem.getTextString();
            }
            else if (valueElem.getName().equals("null"))
            {
                return null;
            }
            else if (valueElem.getName().equals("none"))
            {
                return NO_VALUE;
            }

            throw new DataSetException("Unknown element type: <" +
                    valueElem.getName() + ">");
        }
        catch (IndexOutOfBoundsException e)
        {
            return NO_VALUE;
        }
    }
}
