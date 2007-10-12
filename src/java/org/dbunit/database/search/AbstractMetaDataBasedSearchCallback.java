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

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.SortedSet;
import java.util.TreeSet;

import org.dbunit.database.IDatabaseConnection;

import org.dbunit.util.search.AbstractNodesFilterSearchCallback;
import org.dbunit.util.search.IEdge;
import org.dbunit.util.search.SearchException;

/**
 * Super-class for the ISearchCallback that implements the
 * <code>getEdges()</code> method using the database meta-data.
 * 
 * @author Felipe Leme <dbunit@felipeal.net>
 * @version $Revision$
 * @since Aug 25, 2005
 */
public abstract class AbstractMetaDataBasedSearchCallback extends AbstractNodesFilterSearchCallback {

  /**
   * Logger for this class
   */
  private static final Logger logger = LoggerFactory.getLogger(AbstractMetaDataBasedSearchCallback.class);

  private final IDatabaseConnection connection;

  /**
   * Defautl constructor.
   * @param connection connection where the edges will be calculated from
   */
  public AbstractMetaDataBasedSearchCallback(IDatabaseConnection connection) {
    this.connection = connection;
  }

  /**
   * Get the connection where the edges will be calculated from.
   * @return the connection where the edges will be calculated from
   */
  public IDatabaseConnection getConnection() {
        logger.debug("getConnection() - start");

    return connection;
  }

  protected static final int IMPORT = 0;
  protected static final int EXPORT = 1;
  
  /** 
   * indexes of the column names on the MetaData result sets.
   */
  protected static final int[] TABLENAME_INDEXES = { 3, 7 };  
  protected static final int[] PK_INDEXES = { 4, 4 };
  protected static final int[] FK_INDEXES = { 8, 8 };


  /**
   * Get the nodes using the direct foreign key dependency, i.e, if table A has
   * a FK for a table B, then getNodesFromImportedKeys(A) will return B.
   * @param node table name 
   * @return tables with direct FK dependency from node
   * @throws SearchException
   */
  protected SortedSet getNodesFromImportedKeys(Object node)
      throws SearchException {
        logger.debug("getNodesFromImportedKeys(node=" + node + ") - start");

    return getNodes(IMPORT, node);
  }

  /**
   * Get the nodes using the reverse foreign key dependency, i.e, if table C has
   * a FK for a table A, then getNodesFromExportedKeys(A) will return C.<br>
   * 
   * <strong>NOTE:</strong> this method should be used only as an auxiliary
   * method for sub-classes that also use <code>getNodesFromImportedKeys()</code>
   * or something similiar, otherwise the generated sequence of tables might not
   * work when inserted in the database (as some tables might be missing).
   * <br>
   * @param node table name 
   * @return tables with reverse FK dependency from node
   * @throws SearchException
   */
  protected SortedSet getNodesFromExportedKeys(Object node)
      throws SearchException {
        logger.debug("getNodesFromExportedKeys(node=" + node + ") - start");

    return getNodes(EXPORT, node);
  }

  /**
   * Get the nodes using the both direct and reverse foreign key dependency, i.e, 
   * if table C has a FK for a table A and table A has a FK for a table B, then 
   * getNodesFromImportAndExportedKeys(A) will return B and C.
   * @param node table name 
   * @return tables with reverse and direct FK dependency from node
   * @throws SearchException
   */
  protected SortedSet getNodesFromImportAndExportKeys(Object node)
      throws SearchException {
        logger.debug("getNodesFromImportAndExportKeys(node=" + node + ") - start");

    SortedSet importedNodes = getNodesFromImportedKeys( node );
    SortedSet exportedNodes = getNodesFromExportedKeys( node );
    importedNodes.addAll( exportedNodes );
    return importedNodes;
  }

  private SortedSet getNodes(int type, Object node) throws SearchException {
        logger.debug("getNodes(type=" + type + ", node=" + node + ") - start");

    try {
      Connection conn = this.connection.getConnection();
      String schema = this.connection.getSchema();
      DatabaseMetaData metaData = conn.getMetaData();
      SortedSet edges = new TreeSet();
      getNodes(type, node, conn, schema, metaData, edges);
      return edges;
    } catch (SQLException e) {
            logger.error("getNodes()", e);

      throw new SearchException(e);
    }
  }

  private void getNodes(int type, Object node, Connection conn,
      String schema, DatabaseMetaData metaData, SortedSet edges)
      throws SearchException {
        logger.debug("getNodes(type=" + type + ", node=" + node + ", conn=" + conn + ", schema=" + schema
                + ", metaData=" + metaData + ", edges=" + edges + ") - start");

    if ( logger.isDebugEnabled() ) {
      logger.debug("Getting edges for node " + node);
    }
    try {
      if (!(node instanceof String)) {
        throw new IllegalArgumentException("node should be a String, not a "
            + node.getClass().getName());
      }
      String tableName = (String) node;
      ResultSet rs = null;
      switch (type) {
      case IMPORT:
        rs = metaData.getImportedKeys(null, schema, tableName);
        break;
      case EXPORT:
        rs = metaData.getExportedKeys(null, schema, tableName);
        break;
      }
      while (rs.next()) {
        int index = TABLENAME_INDEXES[type];
        String dependentTableName = rs.getString(index);
        String pkColumn = rs.getString( PK_INDEXES[type] );
        String fkColumn = rs.getString( FK_INDEXES[type] );
        IEdge edge = newEdge(rs, type, tableName, dependentTableName, fkColumn, pkColumn );
        if ( logger.isDebugEnabled() ) {
          logger.debug("Adding edge " + edge);
        }
        edges.add(edge);
      }
    } catch (SQLException e) {
            logger.error("getNodes()", e);

      throw new SearchException(e);
    }

  }
  
  /**
   * Creates an edge representing a foreign key relationship between 2 tables.<br>
   * @param rs database meta-data result set
   * @param type type of relationship (IMPORT or EXPORT)
   * @param from name of the table representing the 'from' node
   * @param to name of the table representing the 'to' node
   * @param fkColumn name of the foreign key column
   * @param pkColumn name of the primary key column
   * @return edge representing the relationship between the 2 tables, according to 
   * the type
   * @throws SearchException not thrown in this method (but might on sub-classes)
   */
  protected static ForeignKeyRelationshipEdge createFKEdge(ResultSet rs, int type, 
      String from, String to, String fkColumn, String pkColumn)
  throws SearchException {
        logger.debug("createFKEdge(rs=" + rs + ", type=" + type + ", from=" + from + ", to=" + to + ", fkColumn="
                + fkColumn + ", pkColumn=" + pkColumn + ") - start");

    return type == IMPORT ? 
        new ForeignKeyRelationshipEdge( from, to, fkColumn, pkColumn ) :
          new ForeignKeyRelationshipEdge( to, from, fkColumn, pkColumn );
  }
  

  /**
   * This method can be overwritten by the sub-classes if they need to decorate
   * the edge (for instance, providing an Edge that contains the primary and 
   * foreign keys used).
   * @param rs database meta-data result set
   * @param type type of relationship (IMPORT or EXPORT)
   * @param from name of the table representing the 'from' node
   * @param to name of the table representing the 'to' node
   * @param fkColumn name of the foreign key column
   * @param pkColumn name of the primary key column
   * @return edge representing the relationship between the 2 tables, according to 
   * the type
   * @throws SearchException not thrown in this method (but might on sub-classes)
   */
  protected IEdge newEdge(ResultSet rs, int type, String from, String to, String fkColumn, String pkColumn)
      throws SearchException {
        logger.debug("newEdge(rs=" + rs + ", type=" + type + ", from=" + from + ", to=" + to + ", fkColumn=" + fkColumn
                + ", pkColumn=" + pkColumn + ") - start");

    return createFKEdge( rs, type, from, to, fkColumn, pkColumn );
  }
            

}
