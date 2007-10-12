package org.dbunit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.dbunit.database.IDatabaseConnection;

/**
 * Base testCase for database testing.<br>
 * Subclasses may override {@link newDatabaseTester()} to plug-in a different implementation
 * of IDatabaseTester.<br> Default implementation uses a {@link PropertiesBasedJdbcDatabaseTester}.
 *
 * @author Felipe Leme <dbunit@felipeal.net>
 */
public abstract class DBTestCase extends DatabaseTestCase {

    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(DBTestCase.class);

  public DBTestCase() {
    super();
  }

  public DBTestCase(String name) {
    super(name);
  }

  protected IDatabaseConnection getConnection() throws Exception {
        logger.debug("getConnection() - start");

    final IDatabaseTester databaseTester = getDatabaseTester();
    assertNotNull( "DatabaseTester is not set", databaseTester );
    return databaseTester.getConnection();
 }

  /**
   * Creates a new IDatabaseTester.
   * Default implementation returns a {@link PropertiesBasedJdbcDatabaseTester}.
   */
  protected IDatabaseTester newDatabaseTester() throws Exception {
        logger.debug("newDatabaseTester() - start");

    return new PropertiesBasedJdbcDatabaseTester();
  }

}
