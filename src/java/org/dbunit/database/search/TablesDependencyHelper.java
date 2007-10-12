/*
 *
 * The DbUnit Database Testing Framework
 * Copyright (C)2005, DbUnit.org
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
package org.dbunit.database.search;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.FilteredDataSet;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.filter.ITableFilter;

import org.dbunit.util.CollectionsHelper;
import org.dbunit.util.search.DepthFirstSearch;
import org.dbunit.util.search.SearchException;


/**
 * Helper for the graph-search based classes used to calculate dependency
 * among tables.  
 * 
 * @author Felipe Leme <dbunit@felipeal.net>
 * @version $Revision$
 * @since Aug 26, 2005
 */
public class TablesDependencyHelper {

    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(TablesDependencyHelper.class);
  
  // this is a "static" class
  private TablesDependencyHelper() {
  }
  
  /**
   * Get the name of all tables that depend on a root table (i.e, all tables whose PK
   * is a FK for the root table).
   * @param connection database conncetion
   * @param rootTable root table described above
   * @return name of all tables that depend on the root table (including the root table), 
   * in the right order for insertions
   * @throws SearchException if an exception occurred while calculating the order
   */
  public static String[] getDependentTables( IDatabaseConnection connection, String rootTable ) throws SearchException {
        logger.debug("getDependentTables(connection=" + connection + ", rootTable=" + rootTable + ") - start");

    return getDependentTables( connection, new String[] { rootTable } );    
  }
  
  /**
   * Get the name of all tables that depend on the root tables (i.e, all tables whose PK
   * is a FK for one of root tables).
   * @param connection database conncetion
   * @param rootTables array of root tables described above
   * @return name of all tables that depend on the root tables (including the root tables), 
   * in the right order for insertions
   * @throws SearchException if an exception occurred while calculating the order
   */
  public static String[] getDependentTables( IDatabaseConnection connection, String[] rootTables ) throws SearchException {
        logger.debug("getDependentTables(connection=" + connection + ", rootTables=" + rootTables + ") - start");

    ImportedKeysSearchCallback callback = new ImportedKeysSearchCallback(connection);
    DepthFirstSearch search = new DepthFirstSearch();
    Set tables = search.search( rootTables, callback );
    return (String[]) CollectionsHelper.setToStrings( tables );
  }
  
  /**
   * Get the name of all tables that depend on a root table ( i.e, all tables whose PK
   * is a FK for the root table) and also the tables the root table depends on 
   * (i.e., all tables which have a FK for the root table's PK). 
   * @param connection database conncetion
   * @param rootTable root table described above
   * @return name of all tables that depend on the root table (including the root table), 
   * in the right order for insertions
   * @throws SearchException if an exception occurred while calculating the order
   */
  public static String[] getAllDependentTables( IDatabaseConnection connection, String rootTable ) throws SearchException {
        logger.debug("getAllDependentTables(connection=" + connection + ", rootTable=" + rootTable + ") - start");

    return getAllDependentTables( connection, new String[] { rootTable } );    
  }
  
  /**
   * Get the name of all tables that depend on the root tables ( i.e, all tables whose PK
   * is a FK for any of the root tables) and also the tables the root tables depends on 
   * (i.e., all tables which have a FK for any of the root table's PK). 
   * @param connection database conncetion
   * @param rootTables root tables described above
   * @return name of all tables that depend on the root tables (including the root tables), 
   * in the right order for insertions
   * @throws SearchException if an exception occurred while calculating the order
   */
  public static String[] getAllDependentTables( IDatabaseConnection connection, String[] rootTables ) throws SearchException {
        logger.debug("getAllDependentTables(connection=" + connection + ", rootTables=" + rootTables + ") - start");

    ImportedAndExportedKeysSearchCallback callback = new ImportedAndExportedKeysSearchCallback(connection);
    DepthFirstSearch search = new DepthFirstSearch();
    Set tables = search.search( rootTables, callback );
    return (String[]) CollectionsHelper.setToStrings( tables );
  }

  // TODO: javadoc (and unit tests) from down here...

  public static IDataSet getDataset( IDatabaseConnection connection, String rootTable, Set allowedIds ) throws SearchException, SQLException {
        logger.debug("getDataset(connection=" + connection + ", rootTable=" + rootTable + ", allowedIds=" + allowedIds
                + ") - start");

    HashMap map = new HashMap(1);
    map.put( rootTable, allowedIds );
    return getDataset( connection, map );
  }
  
  public static IDataSet getDataset( IDatabaseConnection connection, Map rootTables ) throws SearchException, SQLException {
        logger.debug("getDataset(connection=" + connection + ", rootTables=" + rootTables + ") - start");

    ImportedKeysSearchCallbackFilteredByPKs callback = new ImportedKeysSearchCallbackFilteredByPKs(connection, rootTables);
    ITableFilter filter = callback.getFilter();
    DepthFirstSearch search = new DepthFirstSearch();
    String[] tableNames = CollectionsHelper.setToStrings( rootTables.keySet() ); 
    Set tmpTables = search.search( tableNames, callback );
    String[] dependentTables  = CollectionsHelper.setToStrings( tmpTables );
    IDataSet tmpDataset = connection.createDataSet( dependentTables );
    FilteredDataSet dataset = new FilteredDataSet(filter, tmpDataset);
    return dataset;
  }

  public static IDataSet getAllDataset( IDatabaseConnection connection, String rootTable, Set allowedPKs ) throws SearchException, SQLException {
        logger.debug("getAllDataset(connection=" + connection + ", rootTable=" + rootTable + ", allowedPKs="
                + allowedPKs + ") - start");

    HashMap map = new HashMap(1);
    map.put( rootTable, allowedPKs );
    return getAllDataset( connection, map );
  }
  
  public static IDataSet getAllDataset( IDatabaseConnection connection, Map rootTables ) throws SearchException, SQLException {
        logger.debug("getAllDataset(connection=" + connection + ", rootTables=" + rootTables + ") - start");

    ImportedAndExportedKeysSearchCallbackFilteredByPKs callback = new ImportedAndExportedKeysSearchCallbackFilteredByPKs(connection, rootTables);    
    ITableFilter filter = callback.getFilter();
    DepthFirstSearch search = new DepthFirstSearch();
    String[] tableNames = CollectionsHelper.setToStrings( rootTables.keySet() ); 
    Set tmpTables = search.search( tableNames, callback );
    String[] dependentTables  = CollectionsHelper.setToStrings( tmpTables );
    IDataSet tmpDataset = connection.createDataSet( dependentTables );
    FilteredDataSet dataset = new FilteredDataSet(filter, tmpDataset);
    return dataset;
  }
  
  
}
