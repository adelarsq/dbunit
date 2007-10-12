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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.filter.SequenceTableFilter;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(DatabaseSequenceFilter.class);
  
    /** Cache for tablename/foreign key mappings. */
    private static Map _dependentMap;


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
     * @throws DataSetException
     * @throws SQLException If an exception is encountered in accessing the database.
     */
    static String[] sortTableNames(
        IDatabaseConnection connection,
        String[] tableNames)
        throws DataSetException, SQLException
            // not sure why this throws DataSetException ? - ENP
    {
        logger.debug("sortTableNames(connection=" + connection + ", tableNames=" + tableNames + ") - start");

        boolean reprocess = true;
        List tmpTableNames = Arrays.asList(tableNames);
        List sortedTableNames = null;
        DatabaseSequenceFilter._dependentMap = new HashMap();
        
        while (reprocess) {
            sortedTableNames = new LinkedList();
            
            // re-order 'tmpTableNames' into 'sortedTableNames'
            for (Iterator i = tmpTableNames.iterator(); i.hasNext();)
            {
                boolean foundDependentInSortedTableNames = false;
                String tmpTable = (String)i.next();
                Set tmpTableDependents = getDependentTableNames(connection, tmpTable);
                

                int sortedTableIndex = -1;
                for (Iterator k = sortedTableNames.iterator(); k.hasNext();)
                {
                    String sortedTable = (String)k.next();
                    if (tmpTableDependents.contains(sortedTable))
                    {
                        sortedTableIndex = sortedTableNames.indexOf(sortedTable);
                        foundDependentInSortedTableNames = true;
                        break; // end for loop; we know the index
                    }
                }

                
                // add 'tmpTable' to 'sortedTableNames'.
                // Insert it before its first dependent if there are any,
                // otherwise append it to the end of 'sortedTableNames'
                if (foundDependentInSortedTableNames) {
                    if (sortedTableIndex < 0) {
                        throw new IllegalStateException(
                            "sortedTableIndex should be 0 or greater, but is "
                                + sortedTableIndex);
                    }
                    sortedTableNames.add(sortedTableIndex, tmpTable);
                }
                else
                {
                    sortedTableNames.add(tmpTable);
                }
            }
            
            
            
            // don't stop processing until we have a perfect run (no re-ordering)
            if (tmpTableNames.equals(sortedTableNames))
            {
                reprocess = false;
            }
            else
            {

                tmpTableNames = null;
                tmpTableNames = (List)((LinkedList)sortedTableNames).clone();
            }
        }// end 'while (reprocess)'
        
        return (String[])sortedTableNames.toArray(new String[0]);
    }
    

    /**
     * Returns a Set containing the names of all tables which are dependent upon
     * <code>tableName</code>'s primary key as foreign keys.
     *
     * @param connection An IDatabaseConnection to a database that supports
     * referential integrity.
     * @param tableName The table whose primary key is to be used in determining
     * dependent foreign key tables.
     * @return The Set of dependent foreign key table names.
     * @throws SQLException If an exception is encountered in accessing the database.
     */
    private static Set getDependentTableNames(
        IDatabaseConnection connection,
        String tableName)
        throws SQLException
    {
        logger.debug("getDependentTableNames(connection=" + connection + ", tableName=" + tableName + ") - start");

        if (_dependentMap.containsKey(tableName))
        {
            return (Set)_dependentMap.get(tableName);
        }

        DatabaseMetaData metaData = connection.getConnection().getMetaData();
        String schema = connection.getSchema();

        ResultSet resultSet = metaData.getExportedKeys(null, schema, tableName);
        try
        {
            Set foreignTableSet = new HashSet();

            while (resultSet.next())
            {
                // TODO : add support for qualified table names
//                    String foreignSchemaName = resultSet.getString(6);
                String foreignTableName = resultSet.getString(7);

                foreignTableSet.add(foreignTableName);
            }

            _dependentMap.put(tableName, foreignTableSet);
            return foreignTableSet;
        }
        finally
        {
            resultSet.close();
        }
    }

}
