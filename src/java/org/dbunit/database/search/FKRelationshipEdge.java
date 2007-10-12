package org.dbunit.database.search;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import org.dbunit.util.search.Edge;


public class FKRelationshipEdge extends Edge {

    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(FKRelationshipEdge.class);

  // TODO: support multiple columns
  private String fkColumn;
  private String pkColumn;
  
  public FKRelationshipEdge(String tableFrom, String tableTo, String fkColumn, String pkColumn) {
    super(tableFrom, tableTo);
    this.fkColumn = fkColumn;
    this.pkColumn = pkColumn;
  }

  public String getFKColumn() {
        logger.debug("getFKColumn() - start");

    return fkColumn;
  }
  
  public String getPKColumn() {
        logger.debug("getPKColumn() - start");

    return pkColumn;
  }
  
  public String toString() {
        logger.debug("toString() - start");

    return getFrom() + "(" + getFKColumn() + ")->" + getTo() + "(" + getPKColumn() + ")";
  }
  
  // TODO: hashcode and equals

}
