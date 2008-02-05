package org.dbunit;

import java.io.File;
import java.sql.Connection;
import java.util.Set;

import junit.framework.TestCase;

import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;

import org.dbunit.util.CollectionsHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractHSQLTestCase extends TestCase {
  
  public static final String A = "A";
  public static final String B = "B";
  public static final String C = "C";
  public static final String D = "D";
  public static final String E = "E";
  public static final String F = "F";
  public static final String G = "G";
  public static final String H = "H";
  
  public static final String A1 = "A1";
  public static final String B1 = "B1";
  public static final String C1 = "C1";
  public static final String D1 = "D1";
  public static final String E1 = "E1";
  public static final String F1 = "F1";
  public static final String G1 = "G1";
  public static final String H1 = "H1";

  public static final String A2 = "A2";
  public static final String B2 = "B2";
  public static final String C2 = "C2";
  public static final String D2 = "D2";
  public static final String E2 = "E2";
  public static final String F2 = "F2";
  public static final String G2 = "G2";
  public static final String H2 = "H2";
  
  public static final String C3 = "C3";
  public static final String E3 = "E3";
  
  public static final String C4 = "C4";
  public static final String E4 = "E4";
  
  public static final String B3 = "B3";
  public static final String B4 = "B4";
  public static final String B5 = "B5";
  public static final String B6 = "B6";
  public static final String B7 = "B7";
  public static final String B8 = "B8";  
  
  private Connection jdbcConnection;
  private final String sqlFile;
  private IDatabaseConnection connection;
  
  protected final Logger logger = LoggerFactory.getLogger(getClass());

  public AbstractHSQLTestCase(String testName, String sqlFile) {
    super(testName);
    this.sqlFile = sqlFile;
  }

  protected void setUp() throws Exception {
    super.setUp();

    this.jdbcConnection = HypersonicEnvironment.createJdbcConnection("mem:tempdb");
    HypersonicEnvironment.executeDdlFile(new File(
        "src/sql/" + sqlFile), jdbcConnection);
    this.connection = new DatabaseConnection(jdbcConnection);
  }

  protected void tearDown() throws Exception {
    super.tearDown();

    HypersonicEnvironment.shutdown(this.jdbcConnection);
    this.jdbcConnection.close();
  }

  protected IDatabaseConnection getConnection() {
    return this.connection;
  }

  protected static String dump(String[] parent) {
    StringBuffer buffer = new StringBuffer("[ " + parent[0]);
    for (int i = 1; i < parent.length; i++) {
      buffer.append(", " + parent[i]);
    }
    buffer.append(" ]");
    return buffer.toString();
  }

  public static String dump(Set set) {
    String[] strings= CollectionsHelper.setToStrings(set);
    return dump(strings);
  }

}
