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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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
    private String _startDelimiter;
    private String _endDelimiter;

    /**
     * Create a new ReplacementTable object that decorates the specified table.
     *
     * @param table the decorated table
     */
    public ReplacementTable(ITable table)
    {
        this(table, new HashMap(), new HashMap(), null, null);
    }

    public ReplacementTable(ITable table, Map objectMap, Map substringMap,
            String startDelimiter, String endDelimiter)
    {
        _table = table;
        _objectMap = objectMap;
        _substringMap = substringMap;
        _startDelimiter = startDelimiter;
        _endDelimiter = endDelimiter;
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

    /**
     * Sets substring delimiters.
     */
    public void setSubstringDelimiters(String startDelimiter, String endDelimiter)
    {
        if (startDelimiter == null || endDelimiter == null)
        {
            throw new NullPointerException();
        }

        _startDelimiter = startDelimiter;
        _endDelimiter = endDelimiter;
    }

    private String replaceSubstrings(String value)
    {
        StringBuffer buffer = null;

        for (Iterator it = _substringMap.entrySet().iterator(); it.hasNext();)
        {
            Map.Entry entry = (Map.Entry)it.next();
            String original = (String)entry.getKey();
            String replacement = (String)entry.getValue();

            int startIndex = 0;
            int lastEndIndex = 0;
            for(;;)
            {
                startIndex = value.indexOf(original, lastEndIndex);
                if (startIndex == -1)
                {
                    if (buffer != null)
                    {
                        buffer.append(value.substring(lastEndIndex));
                    }
                    break;
                }

                if (buffer == null)
                {
                    buffer = new StringBuffer();
                }
                buffer.append(value.substring(lastEndIndex, startIndex));
                buffer.append(replacement);
                lastEndIndex = startIndex + original.length();
            }
        }

        return buffer == null ? value : buffer.toString();
    }

    /**
     * This implementation is very inefficient and will be replaced soon by a
     * optimized version. At least the functionality exists for now! - Manuel
     */
    private String replaceDelimitedSubstrings(String value)
    {
        StringBuffer buffer = null;

        for (Iterator it = _substringMap.entrySet().iterator(); it.hasNext();)
        {
            Map.Entry entry = (Map.Entry)it.next();
            String original = _startDelimiter + entry.getKey() + _endDelimiter;
            String replacement = (String)entry.getValue();

            int startIndex = 0;
            int lastEndIndex = 0;
            for(;;)
            {
                startIndex = value.indexOf(original, lastEndIndex);
                if (startIndex == -1)
                {
                    if (buffer != null)
                    {
                        buffer.append(value.substring(lastEndIndex));
                    }
                    break;
                }

                if (buffer == null)
                {
                    buffer = new StringBuffer();
                }
                buffer.append(value.substring(lastEndIndex, startIndex));
                buffer.append(replacement);
                lastEndIndex = startIndex + original.length();
            }
        }

        return buffer == null ? value : buffer.toString();
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
        Object value = _table.getValue(row, column);

        // Object replacement
        if (_objectMap.containsKey(value))
        {
            return _objectMap.get(value);
        }

        // Stop here if substring replacement not applicable
        if (_substringMap.size() == 0 || !(value instanceof String))
        {
            return value;
        }

        // Substring replacement
        if (_startDelimiter != null && _endDelimiter != null)
        {
            return replaceDelimitedSubstrings((String)value);
        }
        return replaceSubstrings((String)value);
    }
}

