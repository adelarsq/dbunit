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
package org.dbunit.dataset;

import java.math.BigDecimal;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Decorator that replace configured values from the decorated table
 * with replacement values.
 *
 * @author Manuel Laflamme
 * @since Mar 17, 2003
 * @version $Revision$
 */
public class ReplacementTable implements ITable
{
    private final ITable _table;
    private final Map _objectMap;
    private final Map _substringMap;

    /**
     * Create a new ReplacementTable object that decorates the specified table.
     *
     * @param table the decorated table
     */
    public ReplacementTable(ITable table)
    {
        this(table, new HashMap(), new HashMap());
    }

    ReplacementTable(ITable table, Map objectMap, Map substringMap)
    {
        _table = table;
        _objectMap = objectMap;
        _substringMap = substringMap;
    }

    /**
     * Add a new Object replacement mapping.
     *
     * @param originalObject the object to replace
     * @param replacementObject the replacement object
     */
    public void addReplacementObject(Object originalObject, Object replacementObject)
    {
        _objectMap.put(originalObject, replacementObject);
    }

    /**
     * Add a new substring replacement mapping.
     *
     * @param originalSubstring the substring to replace
     * @param replacementSubstring the replacement substring
     */
    public void addReplacementSubstring(String originalSubstring,
            String replacementSubstring)
    {
        if (originalSubstring == null || replacementSubstring == null)
        {
            throw new NullPointerException();
        }

        _substringMap.put(originalSubstring, replacementSubstring);
    }

    ////////////////////////////////////////////////////////////////////////
    // ITable interface

    public ITableMetaData getTableMetaData()
    {
        return _table.getTableMetaData();
    }

    public int getRowCount()
    {
        return _table.getRowCount();
    }

    public Object getValue(int row, String column) throws DataSetException
    {
        Object objectValue = _table.getValue(row, column);

        // Object replacement
        if (_objectMap.containsKey(objectValue))
        {
            return _objectMap.get(objectValue);
        }

        // Stop here if substring replacement not applicable
        if (_substringMap.size() == 0 || !(objectValue instanceof String))
        {
            return objectValue;
        }

        // Substring replacement
        String stringValue = (String)objectValue;
        for (Iterator it = _substringMap.entrySet().iterator(); it.hasNext();)
        {
            Map.Entry entry = (Map.Entry)it.next();
            String original = (String)entry.getKey();
            String replacement = (String)entry.getValue();

            int startIndex = 0;
            int lastEndIndex = 0;
            StringBuffer buffer = null;
            for(;;)
            {
                startIndex = stringValue.indexOf(original, lastEndIndex);
                if (startIndex == -1)
                {
                    if (buffer != null)
                    {
                        buffer.append(stringValue.substring(lastEndIndex));
                    }
                    break;
                }

                if (buffer == null)
                {
                    buffer = new StringBuffer();
                }
                buffer.append(stringValue.substring(lastEndIndex, startIndex));
                buffer.append(replacement);
                lastEndIndex = startIndex + original.length();
            }

            if (buffer != null)
            {
                return buffer.toString();
            }
        }

        return objectValue;
    }
}

