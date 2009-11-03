package org.dbunit.database;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.dbunit.AbstractHSQLTestCase;
import org.dbunit.database.PrimaryKeyFilter.PkTableMap;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.ITableIterator;
import org.dbunit.util.CollectionsHelper;
import org.dbunit.util.search.SearchException;

public abstract class AbstractSearchCallbackFilteredByPKsTestCase extends AbstractHSQLTestCase {

  private static final char FIRST_TABLE = 'A';

  private PkTableMap fInput = new PkTableMap();
  private PkTableMap fOutput = new PkTableMap();  
  
  public AbstractSearchCallbackFilteredByPKsTestCase(String testName, String sqlFile) {
    super(testName, sqlFile);
  }
  
  protected abstract int[] setupTablesSizeFixture();
  
  protected IDataSet setupTablesDataSetFixture() throws SQLException {
    IDatabaseConnection connection = getConnection();
    IDataSet allDataSet = connection.createDataSet();
    return allDataSet;
  }
    
  protected void addInput(String tableName, String[] ids) {
//    Set idsSet = CollectionsHelper.objectsToSet( ids );
    SortedSet idsSet = new TreeSet(Arrays.asList(ids));
    this.fInput.put( tableName, idsSet );
  }
  protected void addOutput(String tableName, String[] ids) {
//    List idsList = Arrays.asList( ids );
//      Set idsSet = CollectionsHelper.objectsToSet( ids );
      SortedSet idsSet = new TreeSet(Arrays.asList(ids));
    this.fOutput.put( tableName, idsSet );
  }
  
  protected abstract IDataSet getDataset() throws SQLException, SearchException, DataSetException; 

  protected void doIt() throws SQLException, DataSetException, SearchException  {
    IDataSet dataset = getDataset();
    assertNotNull( dataset );
    
    // first, check if only the correct tables had been generated
    String[] outputTables = dataset.getTableNames();
    assertTablesSize( outputTables );
    assertTablesName( outputTables );
    assertRows( dataset );   
  }

  protected void assertTablesSize(String[] actualTables) {
    int expectedSize = this.fOutput.size();
    int actualSize = actualTables.length;
    if ( expectedSize != actualSize ) {
      super.logger.error( "Expected tables: " + dump(this.fOutput.getTableNames()) );
      super.logger.error( "Actual tables: " + dump(actualTables) );
      fail( "I number of returned tables did not match: " + actualSize + " instead of " + expectedSize );
    }    
  }
  protected void assertTablesName(String[] outputTables) {
    Set expectedTables = CollectionsHelper.objectsToSet(this.fOutput.getTableNames());
    Set notExpectedTables = new HashSet();
    boolean ok = true;
    // first check if expected tables are lacking or nonExpected tables were found
    for (int i = 0; i < outputTables.length; i++) {
      String table = outputTables[i];
      if ( expectedTables.contains(table) ) {
        expectedTables.remove(table);
      } else {
        notExpectedTables.add(table);
      }
    }
    if ( ! notExpectedTables.isEmpty() ) {
      ok = false;
      super.logger.error( "Returned tables not waited: " + dump(notExpectedTables) );
    }
    if ( ! expectedTables.isEmpty() ) {
      ok = false;
      super.logger.error( "Waited tables not returned: " + dump(expectedTables) );
    }
    if ( ! ok ) {
      fail( "Returned tables do not match the expectation; check error output" );
    }
  }
  
  protected void assertRows(IDataSet dataset) throws DataSetException {
    ITableIterator iterator = dataset.iterator();
    while (iterator.next()) {
      ITable table = iterator.getTable();
      String tableName = table.getTableMetaData().getTableName();
      String idField = "PK" + tableName;
      Set expectedIds = this.fOutput.get( tableName );
      Set actualIds = new HashSet();
      int rowCount = table.getRowCount();
      for( int row=0; row<rowCount; row++ ) {
        String id = (String) table.getValue( row, idField );
        actualIds.add( id );
        if ( super.logger.isDebugEnabled() ) {
          super.logger.debug( "T:" + tableName + " row: " + row + " id: " + id );      
        }
      }
//      Collections.sort( expectedIds );
//      Collections.sort( actualIds );
      assertEquals( "ids of table " + tableName + " do not match", expectedIds, actualIds );
    }
  }
  
  public void testSetupTables() throws SQLException, DataSetException {
    int[] sizes = setupTablesSizeFixture(); 
    IDataSet allDataSet = setupTablesDataSetFixture();
    assertNotNull( allDataSet );
    for (short i = 0; i < sizes.length; i++) {
      char table = (char) (FIRST_TABLE + i);
      if ( super.logger.isDebugEnabled() ) {
        super.logger.debug( "Getting table " + table );
      }
      ITable itable = allDataSet.getTable( ""+table );
      assertNotNull( "did not find table " + table, itable );
      assertEquals( "size did not match for table " + table, sizes[i], itable.getRowCount());
    }
  }
  
  protected PkTableMap getInput() {
    return this.fInput;
  }

  protected PkTableMap getOutput() {
    return this.fOutput;
  }
  

}
