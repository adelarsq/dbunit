package org.dbunit;

import org.dbunit.database.IDatabaseConnection;

/**
 * TODO: document it, and mention it uses PropertiesBasedJdbcDatabaseTester
 * @author Felipe Leme <dbunit@felipeal.net>
 */
public abstract class DBTestCase extends DatabaseTestCase {

  public DBTestCase() {
    super();
  }
  
  public DBTestCase(String name) {
    super(name);
  }

  protected IDatabaseConnection getConnection() throws Exception {
    final IDatabaseTester databaseTester = getDatabaseTester();
    assertNotNull( "DatabaseTester is not set", databaseTester );
    return databaseTester.getConnection();
 }
  
  protected IDatabaseTester newDatabaseTester() throws Exception {
    return new PropertiesBasedJdbcDatabaseTester();
  }
    
}
