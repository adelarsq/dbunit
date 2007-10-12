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

package org.dbunit.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//TODO: should not have dependency on sub-package!
import org.dbunit.database.search.ForeignKeyRelationshipEdge;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.ITableIterator;
import org.dbunit.dataset.ITableMetaData;
import org.dbunit.dataset.filter.AbstractTableFilter;
import org.dbunit.util.SQLHelper;

/**
 * Filter a table given a map of the allowed rows based on primary key values.<br>
 * It uses a depth-first algorithm (although not recursive - it might be refactored
 * in the future) to define which rows are allowed, as well which rows are necessary
 * (and hence allowed) because of dependencies with the allowed rows.<br>
 * <strong>NOTE:</strong> multi-column primary keys are not supported at the moment.
 * TODO: test cases
 * @author Felipe Leme <dbunit@felipeal.net>
 * @version $Revision$
 * @since Sep 9, 2005
 */
public class PrimaryKeyFilter extends AbstractTableFilter {

  private final IDatabaseConnection connection;

  private final Map allowedPKsPerTable;
  private final Map allowedPKsInput;
  private final Map pksToScanPerTable;
  
  private final boolean reverseScan;
  
  protected final Logger logger = LoggerFactory.getLogger(getClass());
  
  // cache de primary keys
  private final Map pkColumnPerTable = new HashMap();
  
  private final Map fkEdgesPerTable = new HashMap();
  private final Map fkReverseEdgesPerTable = new HashMap();
  
  // name of the tables, in reverse order of depedency
  private final List tableNames = new ArrayList();

  /**
   * Default constructor, it takes as input a map with desired rows in a final
   * dataset; the filter will ensure that the rows necessary by these initial rows
   * are also allowed (and so on...).
   * @param connection database connection
   * @param allowedPKs map of allowed rows, based on the primary keys (key is the name
   * of a table; value is a Set with allowed primary keys for that table)
   * @param reverseDependency flag indicating if the rows that depend on a row should
   * also be allowed by the filter
   */
  public PrimaryKeyFilter(IDatabaseConnection connection, Map allowedPKs, boolean reverseDependency) {
    this.connection = connection;    
    this.allowedPKsPerTable = new HashMap();    
    this.allowedPKsInput = allowedPKs;
    this.reverseScan = reverseDependency;

    // we need a deep copy here
//    this.idsToScanPerTable = new HashMap(allowedIds);   
    this.pksToScanPerTable = new HashMap(allowedPKs.size()); 
    Iterator iterator = allowedPKs.entrySet().iterator();
    while ( iterator.hasNext() ) {
      Map.Entry entry = (Map.Entry) iterator.next();
      Object table = entry.getKey();
      Set inputSet = (Set) entry.getValue();
      Set newSet = new HashSet( inputSet );
      this.pksToScanPerTable.put( table, newSet );
    }
    
  }

  public void nodeAdded(Object node) {
        logger.debug("nodeAdded(node=" + node + ") - start");

    this.tableNames.add( node );
    if ( this.logger.isDebugEnabled() ) {
      this.logger.debug("nodeAdded: " + node );
    }
  }

  public void edgeAdded(ForeignKeyRelationshipEdge edge) {
    if ( this.logger.isDebugEnabled() ) {
      this.logger.debug("edgeAdded: " + edge );
    }
    // first add it to the "direct edges"
    String from = (String) edge.getFrom();
    Set edges = (Set) this.fkEdgesPerTable.get(from);
    if ( edges == null ) {
      edges = new HashSet();
      this.fkEdgesPerTable.put( from, edges );
    }
    if ( ! edges.contains(edge) ) {
      edges.add(edge);
    }
    
    // then add it to the "reverse edges"
    String to = (String) edge.getTo();
    edges = (Set) this.fkReverseEdgesPerTable.get(to);
    if ( edges == null ) {
      edges = new HashSet();
      this.fkReverseEdgesPerTable.put( to, edges );
    }
    if ( ! edges.contains(edge) ) {
      edges.add(edge);
    }
    
    // finally, update the PKs cache
    Object pkTo = this.pkColumnPerTable.get( to );
    if ( pkTo == null ) {
      Object pk = edge.getPKColumn();
      this.pkColumnPerTable.put( to, pk );
    }
    
  }

  /**
   * @see AbstractTableFilter
   */
  public boolean isValidName(String tableName) throws DataSetException {
        logger.debug("isValidName(tableName=" + tableName + ") - start");

    //    boolean isValid = this.allowedIds.containsKey(tableName);
    //    return isValid;
    return true;
  }

  public ITableIterator iterator(IDataSet dataSet, boolean reversed)
      throws DataSetException {
    if ( this.logger.isDebugEnabled() ) {
      this.logger.debug("Filter.iterator()" );
    }
    try {
      searchPKs(dataSet);
    } catch (SQLException e) {
            logger.error("iterator()", e);

      throw new DataSetException( e );
    }
    return new FilterIterator(reversed ? dataSet.reverseIterator() : dataSet
        .iterator());
  }

  private void searchPKs(IDataSet dataSet) throws DataSetException, SQLException {
        logger.debug("searchPKs(dataSet=" + dataSet + ") - start");
    
    int counter = 0;
    while ( ! this.pksToScanPerTable.isEmpty() ) {
      counter ++;
      if ( this.logger.isDebugEnabled() ) {
        this.logger.debug( "RUN # " + counter );
      }
      
      for ( int i=this.tableNames.size()-1; i>=0; i-- ) {
        String tableName = (String) this.tableNames.get(i);
        // TODO: support multi-column PKs
        String pkColumn = dataSet.getTable(tableName).getTableMetaData().getPrimaryKeys()[0].getColumnName();
        Set tmpSet = (Set) this.pksToScanPerTable.get( tableName );
        if ( tmpSet != null && ! tmpSet.isEmpty() ) {
          Set pksToScan = new HashSet( tmpSet );
          if ( this.logger.isDebugEnabled() ) {
            this.logger.debug(  "before search: "+ tableName + "=>" + pksToScan );
          }
          scanPKs( tableName, pkColumn, pksToScan );
          scanReversePKs( tableName, pksToScan );
          allowPKs( tableName, pksToScan );
          removePKsToScan( tableName, pksToScan );
        } // if
      } // for 
      removeScannedTables();
    } // while
    if ( this.logger.isDebugEnabled() ) {
      this.logger.debug( "Finished searchIds()" );
    }
  } 

  private void removeScannedTables() {
        logger.debug("removeScannedTables() - start");

    Iterator iterator = this.pksToScanPerTable.entrySet().iterator();
    List tablesToRemove = new ArrayList();
    while ( iterator.hasNext() ) {
      Map.Entry entry = (Map.Entry) iterator.next();
      String table = (String) entry.getKey();
      Set pksToScan = (Set) entry.getValue();
      boolean removeIt = pksToScan.isEmpty();
      if ( ! this.tableNames.contains(table) ) {
        if ( this.logger.isWarnEnabled() ) {
          this.logger.warn("Discarding ids " + pksToScan + " of table " + table +
          "as this table has not been passed as input" );
        }
        removeIt = true;
      }
      if ( removeIt ) {
        tablesToRemove.add( table );
      }
    }
    iterator = tablesToRemove.iterator();
    while ( iterator.hasNext() ) {
      this.pksToScanPerTable.remove( iterator.next() );
    }
  }

  private void allowPKs(String table, Set newAllowedPKs) {
        logger.debug("allowPKs(table=" + table + ", newAllowedPKs=" + newAllowedPKs + ") - start");

    // first, obtain the current allowed ids for that table
    Set currentAllowedIds = (Set) this.allowedPKsPerTable.get( table );
    if ( currentAllowedIds == null ) {
      currentAllowedIds = new HashSet();
      this.allowedPKsPerTable.put( table, currentAllowedIds );
    }
    // then, add the new ids, but checking if it should be allowed to add them
    Set forcedAllowedPKs = (Set) this.allowedPKsInput.get( table );
    if ( forcedAllowedPKs == null || forcedAllowedPKs.isEmpty() ) {
      currentAllowedIds.addAll( newAllowedPKs );
    } else {
      Iterator iterator = newAllowedPKs.iterator();
      while ( iterator.hasNext() ) {
        Object id = iterator.next();
        if ( forcedAllowedPKs.contains(id) ) {
          currentAllowedIds.add(id);
        } else {
          if ( this.logger.isDebugEnabled() ) {
            this.logger.debug( "Discarding id " + id + " of table " + table + 
              " as it was not included in the input!" );
          }
        }
      }
    }
  }
  
  private void scanPKs( String table, String pkColumn, Set allowedIds ) throws SQLException {
        logger.debug("scanPKs(table=" + table + ", pkColumn=" + pkColumn + ", allowedIds=" + allowedIds + ") - start");

    Set fkEdges = (Set) this.fkEdgesPerTable.get( table );
    if ( fkEdges == null || fkEdges.isEmpty() ) {
      return;
    }
    // we need a temporary list as there is no warranty about the set order...
    List fkTables = new ArrayList( fkEdges.size() );
    Iterator iterator = fkEdges.iterator();
    StringBuffer colsBuffer = new StringBuffer();
    while ( iterator.hasNext() ) {
      ForeignKeyRelationshipEdge edge = (ForeignKeyRelationshipEdge) iterator.next();
      fkTables.add( edge.getTo() );
      colsBuffer.append( edge.getFKColumn() );
      if ( iterator.hasNext() ) {
        colsBuffer.append( ", " );
      }
    }
    // NOTE: make sure the query below is compatible standard SQL
    String sql = "SELECT " + colsBuffer + " FROM " + table + 
    " WHERE " + pkColumn + " = ? ";
    if ( this.logger.isDebugEnabled() ) {
      this.logger.debug( "SQL: " + sql );
    }
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    try {
      pstmt = this.connection.getConnection().prepareStatement( sql );
      iterator = allowedIds.iterator();
      while( iterator.hasNext() ) {
        Object pk = iterator.next(); // id being scanned
        if ( this.logger.isDebugEnabled() ) {
          this.logger.debug("Executing sql for ? = " + pk );
        }
        pstmt.setObject( 1, pk );
        rs = pstmt.executeQuery();
        while( rs.next() ) {
          for ( int i=0; i<fkTables.size(); i++ ) {
            String newTable = (String) fkTables.get(i);
            Object fk = rs.getObject(i+1);
            if ( fk != null ) {
              if ( this.logger.isDebugEnabled() ) {
                this.logger.debug("New ID: " + newTable + "->" + fk  );
              }
              addPKToScan( newTable, fk );
            } else {
              this.logger.warn( "Found null FK for relationship  " + 
                  table + "=>" + newTable );
            }
          }
        }
      }
    } catch (SQLException e) {
            logger.error("scanPKs()", e);

      SQLHelper.close( rs, pstmt );
    }        
  }
  
  private void scanReversePKs(String table, Set pksToScan) throws SQLException {
        logger.debug("scanReversePKs(table=" + table + ", pksToScan=" + pksToScan + ") - start");

    if ( ! this.reverseScan ) {
      return; 
    }
    Set fkReverseEdges = (Set) this.fkReverseEdgesPerTable.get( table );
    if ( fkReverseEdges == null || fkReverseEdges.isEmpty() ) {
      return;
    }
    Iterator iterator = fkReverseEdges.iterator();
    while ( iterator.hasNext() ) {
      ForeignKeyRelationshipEdge edge = (ForeignKeyRelationshipEdge) iterator.next();
      addReverseEdge( edge, pksToScan );
    }
  }

  private void addReverseEdge(ForeignKeyRelationshipEdge edge, Set idsToScan) throws SQLException {
        logger.debug("addReverseEdge(edge=" + edge + ", idsToScan=" + idsToScan + ") - start");

    String fkTable = (String) edge.getFrom();
    String fkColumn = edge.getFKColumn();
    String pkColumn = getPKColumn( fkTable );
    // NOTE: make sure the query below is compatible standard SQL
    String sql = "SELECT " + pkColumn + " FROM " + fkTable + " WHERE " + fkColumn + " = ? ";
    
    PreparedStatement pstmt = null;
    try {
      if ( this.logger.isDebugEnabled() ) {
        this.logger.debug( "Preparing SQL query '" + sql + "'" );
      }
      pstmt = this.connection.getConnection().prepareStatement( sql );
    } catch (SQLException e) {
            logger.error("addReverseEdge()", e);

      SQLHelper.close( pstmt );
    }        
    ResultSet rs = null;
    Iterator iterator = idsToScan.iterator();
    try {
      while ( iterator.hasNext() ) {
        Object pk = iterator.next();
        if ( this.logger.isDebugEnabled() ) {
          this.logger.debug( "executing query '" + sql + "' for ? = " + pk );
        }
        pstmt.setObject( 1, pk );
        rs = pstmt.executeQuery();
        while( rs.next() ) {
          Object fk = rs.getObject(1);
          addPKToScan( fkTable, fk );
        }
      } 
    } finally {
      SQLHelper.close( rs, pstmt );
    }
  }

  // TODO: support PKs with multiple values
  private String getPKColumn( String table ) throws SQLException {
        logger.debug("getPKColumn(table=" + table + ") - start");

    String pkColumn = (String) this.pkColumnPerTable.get( table );
    if ( pkColumn == null ) {
      pkColumn = SQLHelper.getPrimaryKeyColumn( this.connection.getConnection(), table );
      this.pkColumnPerTable.put( table, pkColumn );
    }
    return pkColumn;
  }
  
  private void removePKsToScan(String table, Set ids) {
        logger.debug("removePKsToScan(table=" + table + ", ids=" + ids + ") - start");

    Set pksToScan = (Set) this.pksToScanPerTable.get(table);
    if ( pksToScan != null ) {
      if ( pksToScan == ids ) {   
        throw new RuntimeException( "INTERNAL ERROR on removeIdsToScan() for table " + table );
      } else {
        pksToScan.removeAll( ids );
      }
    }    
  }

  private void addPKToScan(String table, Object pk) {
        logger.debug("addPKToScan(table=" + table + ", pk=" + pk + ") - start");

    // first, check if it wasn't added yet
    Set scannedIds = (Set) this.allowedPKsPerTable.get( table );
    if ( scannedIds != null && scannedIds.contains(pk)) {
      if ( this.logger.isDebugEnabled() ) {
        this.logger.debug( "Discarding already scanned id=" + pk + " for table " + table );
      }
      return;
    }
    
    Set pksToScan = (Set) this.pksToScanPerTable.get(table);
    if ( pksToScan == null ) {
      pksToScan = new HashSet();
      this.pksToScanPerTable.put( table, pksToScan );
    }
    pksToScan.add( pk );
  }

  private class FilterIterator implements ITableIterator {

        /**
         * Logger for this class
         */
        private final Logger logger = LoggerFactory.getLogger(FilterIterator.class);

    private final ITableIterator _iterator;

    public FilterIterator(ITableIterator iterator) {
      
      _iterator = iterator;
    }

    ////////////////////////////////////////////////////////////////////////////
    // ITableIterator interface

    public boolean next() throws DataSetException {
      if ( logger.isDebugEnabled() ) {
        logger.debug("Iterator.next()" );
      }      
      while (_iterator.next()) {
        if (accept(_iterator.getTableMetaData().getTableName())) {
          return true;
        }
      }
      return false;
    }

    public ITableMetaData getTableMetaData() throws DataSetException {
      if ( logger.isDebugEnabled() ) {
        logger.debug("Iterator.getTableMetaData()" );
      }      
      return _iterator.getTableMetaData();
    }

    public ITable getTable() throws DataSetException {
      if ( logger.isDebugEnabled() ) {
        logger.debug("Iterator.getTable()" );
      }
      ITable table = _iterator.getTable();
      String tableName = table.getTableMetaData().getTableName();
      Set allowedPKs = (Set) allowedPKsPerTable.get( tableName );
      if ( allowedPKs != null ) {
        return new PrimaryKeyFilteredTableWrapper(table, allowedPKs);
      }
      return table;
    }
  }

}
