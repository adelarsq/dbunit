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
package org.dbunit.database;

import org.dbunit.DatabaseUnitRuntimeException;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.DataSetUtils;
import org.dbunit.dataset.filter.SequenceTableFilter;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * This filter orders tables using dependency information provided by
 * {@link java.sql.DatabaseMetaData#getExportedKeys}.
 *
 * @author Manuel Laflamme
 * @author Erik Price
 * @since Mar 23, 2003
 * @version $Revision$
 */
public class DatabaseSequenceFilter extends SequenceTableFilter
{
    /**
     * Create a DatabaseSequenceFilter that only exposes specified table names.
     */
    public DatabaseSequenceFilter(IDatabaseConnection connection,
            String[] tableNames) throws DataSetException, SQLException
    {
        super(sortTableNames(connection, tableNames));
    }

    /**
     * Create a DatabaseSequenceFilter that exposes all the database tables.
     */
    public DatabaseSequenceFilter(IDatabaseConnection connection)
            throws DataSetException, SQLException
    {
        this(connection, connection.createDataSet().getTableNames());
    }

    /**
     * Re-orders a string array of table names, placing dependent ("parent")
     * tables after their dependencies ("children").
     *
     * @param tableNames A string array of table names to be ordered.
     * @return The re-ordered array of table names.
     * @throws SQLException if a database access error occurs
     * @throws CyclicTablesDependencyException if encounter a cyclic dependency
     */
    private static String[] sortTableNames(IDatabaseConnection connection,
            String[] tableNames) throws DataSetException, SQLException
    {
        try
        {
            TableSequenceComparator tableSequenceComparator =
                    new TableSequenceComparator(connection);
            String[] lastNames = tableNames;
            String[] cloneNames = tableNames;
            do
            {
                lastNames = cloneNames;
                cloneNames = (String[])lastNames.clone();
                Arrays.sort(cloneNames,
                        tableSequenceComparator);
            } while(!Arrays.equals(lastNames, cloneNames));

            return lastNames;
        }
        catch (DatabaseUnitRuntimeException e)
        {
            if (e.getException() instanceof CyclicTablesDependencyException)
            {
                throw (CyclicTablesDependencyException)e.getException();
            }
            if (e.getException() instanceof SQLException)
            {
                throw (SQLException)e.getException();
            }

            throw e;
        }
    }

    private static class TableSequenceComparator implements Comparator
    {
        IDatabaseConnection _connection;
        Map _dependentMap = new HashMap();

        public TableSequenceComparator(IDatabaseConnection connection)
        {
            _connection = connection;
        }

        public int compare(Object o1, Object o2)
        {
            String tableName1 = (String)o1;
            String tableName2 = (String)o2;

            try
            {
                if (getDependentTableNames(tableName1).contains(tableName2))
                {
                    if (getDependentTableNames(tableName2).contains(tableName1))
                    {
                        throw new CyclicTablesDependencyException(tableName1, tableName2);
                    }
                    return -1;
                }

                if (getDependentTableNames(tableName2).contains(tableName1))
                {
                    return 1;
                }
            }
            catch (SQLException e)
            {
                throw new DatabaseUnitRuntimeException(e);
            }
            catch (CyclicTablesDependencyException e)
            {
                throw new DatabaseUnitRuntimeException(e);
            }

            return tableName1.compareTo(tableName2);
        }

        /**
         * Returns a Set containing the names of all tables which are dependent upon
         * <code>tableName</code>'s primary key as foreign keys.
         *
         * @param tableName The table whose primary key is to be used in determining
         * dependent foreign key tables.
         * @return The Set of dependent foreign key table names.
         * @throws SQLException if a database access error occurs
         */
        private Set getDependentTableNames(String tableName)
                throws SQLException
        {
            if (_dependentMap.containsKey(tableName))
            {
                return (Set)_dependentMap.get(tableName);
            }

            boolean qualifiedNames = _connection.getConfig().getFeature(
                    DatabaseConfig.FEATURE_QUALIFIED_TABLE_NAMES);

            String originalTableName = tableName;
            String schemaName = _connection.getSchema();
            int index = tableName.indexOf(".");
            if (index >= 0)
            {
                schemaName = tableName.substring(0, index);
                tableName = tableName.substring(index + 1);
            }
            DatabaseMetaData metaData = _connection.getConnection().getMetaData();
            ResultSet resultSet = metaData.getExportedKeys(null, schemaName, tableName);

            try
            {
                Set foreignTableSet = new HashSet();

                while (resultSet.next())
                {
                    String foreignSchemaName = resultSet.getString(6);
                    String foreignTableName = resultSet.getString(7);
                    if (qualifiedNames)
                    {
                        foreignTableName = DataSetUtils.getQualifiedName(
                                foreignSchemaName, foreignTableName);
                    }

                    foreignTableSet.add(foreignTableName);
                }

                _dependentMap.put(originalTableName, foreignTableSet);
                return foreignTableSet;
            }
            finally
            {
                resultSet.close();
            }
        }

    }

}
