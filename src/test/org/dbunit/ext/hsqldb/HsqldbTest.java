package org.dbunit.ext.hsqldb;

import org.dbunit.AbstractDatabaseTest;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.dataset.datatype.IDataTypeFactory;

public class HsqldbTest extends AbstractDatabaseTest {

  public HsqldbTest(String s) {
    super(s);
  }
  
  public void testRightFactory() throws Exception {
    final String connClass = _connection.getConnection().getClass().getName();
    if ( connClass.startsWith("org.hsqldb")) {
      final DatabaseConfig config = _connection.getConfig();
      final IDataTypeFactory factory = (IDataTypeFactory) 
        config.getProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY);
      assertTrue( "factory is not instance of HsqldbDataTypeFactory: "+ 
          factory.getClass().getName(), factory instanceof HsqldbDataTypeFactory );
    } else {
       System.err.println( "WARNING: connection of class " + connClass + "on HSQLDB test" );
    }
  }

}
