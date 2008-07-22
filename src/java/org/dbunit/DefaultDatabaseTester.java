package org.dbunit;

import org.dbunit.database.IDatabaseConnection;

/**
 * Default implementation of AbstractDatabaseTester, which does not know how
 * to get a connection by itself.
 *
 * @author Felipe Leme <dbunit@felipeal.net>
 * @version $Revision$
 * @since 2.2
 */

public class DefaultDatabaseTester extends AbstractDatabaseTester {

  final IDatabaseConnection connection;

  /**
   * Creates a new DefaultDatabaseTester with the supplied connection.
   */
  public DefaultDatabaseTester( final IDatabaseConnection connection ) {
    this.connection = connection;
  }

  public IDatabaseConnection getConnection() throws Exception {
    return this.connection;
  }

}
