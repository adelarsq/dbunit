package org.dbunit.database.search;


import org.dbunit.util.search.Edge;


public class FKRelationshipEdge extends Edge {

  // TODO: support multiple columns
  private String fkColumn;
  private String pkColumn;
  
  public FKRelationshipEdge(String tableFrom, String tableTo, String fkColumn, String pkColumn) {
    super(tableFrom, tableTo);
    this.fkColumn = fkColumn;
    this.pkColumn = pkColumn;
  }

  public String getFKColumn() {
    return fkColumn;
  }
  
  public String getPKColumn() {
    return pkColumn;
  }
  
  public String toString() {
    return getFrom() + "(" + getFKColumn() + ")->" + getTo() + "(" + getPKColumn() + ")";
  }
  
  // TODO: hashcode and equals

}
