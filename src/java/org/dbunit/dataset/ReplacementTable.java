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

    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(ReplacementTable.class);

    private final ITable _table;
    private final Map _objectMap;
    private final Map _substringMap;
    private String _startDelim;
    private String _endDelim;

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
        _startDelim = startDelimiter;
        _endDelim = endDelimiter;
    }

    /**
     * Add a new Object replacement mapping.
     *
     * @param originalObject the object to replace
     * @param replacementObject the replacement object
     */
    public void addReplacementObject(Object originalObject, Object replacementObject)
    {
        logger.debug("addReplacementObject(originalObject=" + originalObject + ", replacementObject="
                + replacementObject + ") - start");

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
        logger.debug("addReplacementSubstring(originalSubstring=" + originalSubstring + ", replacementSubstring="
                + replacementSubstring + ") - start");

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
        logger.debug("setSubstringDelimiters(startDelimiter=" + startDelimiter + ", endDelimiter=" + endDelimiter
                + ") - start");

        if (startDelimiter == null || endDelimiter == null)
        {
            throw new NullPointerException();
        }

        _startDelim = startDelimiter;
        _endDelim = endDelimiter;
    }

    private String replaceSubstrings(String value)
    {
        logger.debug("replaceSubstrings(value=" + value + ") - start");

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

    private String replaceDelimitedSubstrings(String value)
    {
        logger.debug("replaceDelimitedSubstrings(value=" + value + ") - start");

        StringBuffer buffer = null;

        int startIndex = 0;
        int endIndex = 0;
        int lastEndIndex = 0;
        for(;;)
        {
            startIndex = value.indexOf(_startDelim, lastEndIndex);
            if (startIndex != -1)
            {
                endIndex = value.indexOf(_endDelim, startIndex + _startDelim.length());
                if (endIndex != -1)
                {
                    if (buffer == null)
                    {
                        buffer = new StringBuffer();
                    }

                    String substring = value.substring(
                            startIndex + _startDelim.length(), endIndex);
                    if (_substringMap.containsKey(substring))
                    {
                        buffer.append(value.substring(lastEndIndex, startIndex));
                        buffer.append(_substringMap.get(substring));
                    }
                    else
                    {
                        buffer.append(value.substring(
                                lastEndIndex, endIndex + _endDelim.length()));
                    }

                    lastEndIndex = endIndex + _endDelim.length();
                }
            }

            // No more delimited substring
            if (startIndex == -1 || endIndex == -1)
            {
                if (buffer != null)
                {
                    buffer.append(value.substring(lastEndIndex));
                }
                break;
            }
        }

        return buffer == null ? value : buffer.toString();
    }

    ////////////////////////////////////////////////////////////////////////
    // ITable interface

    public ITableMetaData getTableMetaData()
    {
        logger.debug("getTableMetaData() - start");

        return _table.getTableMetaData();
    }

    public int getRowCount()
    {
        logger.debug("getRowCount() - start");

        return _table.getRowCount();
    }

    public Object getValue(int row, String column) throws DataSetException
    {
        logger.debug("getValue(row=" + row + ", column=" + column + ") - start");

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
        if (_startDelim != null && _endDelim != null)
        {
            return replaceDelimitedSubstrings((String)value);
        }
        return replaceSubstrings((String)value);
    }
}

