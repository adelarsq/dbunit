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

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.dbunit.Assertion;
import org.dbunit.dataset.datatype.DataType;
import org.dbunit.dataset.datatype.TypeCastException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class contains various methods for manipulating datasets.
 *
 * @author Manuel Laflamme
 * @version $Revision$
 * @since Feb 19, 2002
 */
public class DataSetUtils
{

    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(DataSetUtils.class);

    private DataSetUtils()
    {
    }

    /**
     * Asserts that the two specified dataset are equals. This method ignore
     * the tables order.
     *
     * @deprecated Use Assertion.assertEquals
     */
    public static void assertEquals(IDataSet expectedDataSet,
            IDataSet actualDataSet) throws Exception
    {
        logger.debug("assertEquals(expectedDataSet={}, actualDataSet={}) - start", expectedDataSet, actualDataSet);

        Assertion.assertEquals(expectedDataSet, actualDataSet);
    }


    /**
     * Asserts that the two specified tables are equals. This method ignore the
     * table names, the columns order, the columns data type and the primary
     * keys.
     *
     * @deprecated Use Assertion.assertEquals
     */
    public static void assertEquals(ITable expectedTable, ITable actualTable)
            throws Exception
    {
        logger.debug("assertEquals(expectedTable={}, actualTable={}) - start", expectedTable, actualTable);

        Assertion.assertEquals(expectedTable, actualTable);
    }


    /**
     * Returns the specified name qualified with the specified prefix. The name
     * is not modified if the prefix is <code>null</code> or if the name is
     * already qualified.
     * <p>
     * Example: <br>
     * <code>getQualifiedName(null, "NAME")</code> returns
     * <code>"NAME"</code>. <code>getQualifiedName("PREFIX", "NAME")</code>
     * returns <code>"PREFIX.NAME"</code> and
     * <code>getQualifiedName("PREFIX2", "PREFIX1.NAME")</code>
     * returns <code>"PREFIX1.NAME"</code>.
     *
     * @param prefix the prefix
     * @param name the name
     * @return the qualified name
     */
    public static String getQualifiedName(String prefix, String name)
    {
        logger.debug("getQualifiedName(prefix={}, name={}) - start", prefix, name);

        return getQualifiedName(prefix, name, null);
    }

    public static String getQualifiedName(String prefix, String name,
            String escapePattern)
    {
        logger.debug("getQualifiedName(prefix={}, name={}, escapePattern={}) - start", new String[] {prefix, name, escapePattern});

        if (escapePattern != null)
        {
            prefix = getEscapedName(prefix, escapePattern);
            name = getEscapedName(name, escapePattern);
        }

        if (prefix == null || prefix.equals("") || name.indexOf(".") >= 0)
        {
            return name;
        }

        return prefix + "." + name;
    }

    public static String getEscapedName(String name, String escapePattern)
    {
        logger.debug("getEscapedName(name={}, escapePattern={}) - start", name, escapePattern);

        if (name == null || escapePattern == null)
        {
            return name;
        }
    
        int split = name.indexOf(".");
        if (split > 1)
        {
        	return getEscapedName(name.substring(0, split), escapePattern) + "." + getEscapedName(name.substring(split + 1), escapePattern);
        }

        int index = escapePattern.indexOf("?");
        if (index >=0 )
        {
            String prefix = escapePattern.substring(0, index);
            String suffix = escapePattern.substring(index + 1);

            return prefix + name + suffix;
        }
        return name;
    }

    /**
     * Returns the specified value as a string to be use in an SQL Statement.
     * For example the string <code>myValue</code> is returned as
     * <code>'myValue'</code>.
     *
     * @param value the value
     * @param dataType the value data type
     * @return the SQL string value
     */
    public static String getSqlValueString(Object value, DataType dataType)
            throws TypeCastException
    {
        logger.debug("getSqlValueString(value={}, dataType={}) - start", value, dataType);

        if (value == null || value == ITable.NO_VALUE)
        {
            return "NULL";
        }

        String stringValue = DataType.asString(value);
        if (dataType == DataType.DATE)
        {
            return "{d '" + stringValue + "'}";
        }

        if (dataType == DataType.TIME)
        {
            return "{t '" + stringValue + "'}";
        }

        if (dataType == DataType.TIMESTAMP)
        {
            return "{ts '" + stringValue + "'}";
        }

        if (!dataType.isNumber())
        {
            // no single quotes
            if (stringValue.indexOf("'") < 0)
            {
                return stringValue = "'" + stringValue + "'";
            }

            // escaping single quotes
            StringBuffer buffer = new StringBuffer(stringValue.length() * 2);
            StringTokenizer tokenizer = new StringTokenizer(stringValue, "'", true);

            buffer.append("'");
            while (tokenizer.hasMoreTokens())
            {
                String token = tokenizer.nextToken();
                buffer.append(token);
                if (token.equals("'"))
                {
                    buffer.append("'");
                }
            }
            buffer.append("'");
            return buffer.toString();

        }

        return stringValue;
    }

    /**
     * Search and returns the specified column from the specified column array.
     *
     * @param columnName the name of the column to search.
     * @param columns the array of columns from which the column must be searched.
     * @return the column or <code>null</code> if the column is not found
     * @deprecated since 2.3.0 - prefer usage of {@link Columns#getColumn(String, Column[])}
     */
    public static Column getColumn(String columnName, Column[] columns)
    {
        logger.debug("getColumn(columnName={}, columns={}) - start", columnName, columns);
        return Columns.getColumn(columnName, columns);
    }

    /**
     * Search and returns the specified tables from the specified dataSet.
     *
     * @param names the names of the tables to search.
     * @param dataSet the dataset from which the tables must be searched.
     * @return the tables or an empty array if no tables are found.
     */
    public static ITable[] getTables(String[] names, IDataSet dataSet)
            throws DataSetException
    {
        logger.debug("getTables(names={}, dataSet={}) - start", names, dataSet);

        ITable[] tables = new ITable[names.length];
        for (int i = 0; i < names.length; i++)
        {
            String name = names[i];
            tables[i] = dataSet.getTable(name);
        }

        return tables;
    }

    /**
     * Returns the tables from the specified dataset.
     */
    public static ITable[] getTables(IDataSet dataSet) throws DataSetException
    {
        logger.debug("getTables(dataSet={}) - start", dataSet);

        return getTables(dataSet.iterator());
    }

    /**
     * Returns the tables from the specified iterator.
     */
    public static ITable[] getTables(ITableIterator iterator) throws DataSetException
    {
        logger.debug("getTables(iterator={}) - start", iterator);

        List tableList = new ArrayList();
        while(iterator.next())
        {
            tableList.add(iterator.getTable());
        }
        return (ITable[])tableList.toArray(new ITable[0]);
    }

    /**
     * Returns the table names from the specified dataset in reverse order.
     */
    public static String[] getReverseTableNames(IDataSet dataSet)
            throws DataSetException
    {
        logger.debug("getReverseTableNames(dataSet={}) - start", dataSet);
        return reverseStringArray(dataSet.getTableNames());
    }

    /**
     * reverses a String array.
     * @param array
     * @return String[] - reversed array.
     */
    public static String[] reverseStringArray(String[] array)
	{
		logger.debug("reverseStringArray(array={}) - start", array);
        String[] newArray = new String[array.length];
        for (int i = 0; i < array.length; i++)
        {
            newArray[array.length - 1 - i] = array[i];
        }
        return newArray;
	}

}
