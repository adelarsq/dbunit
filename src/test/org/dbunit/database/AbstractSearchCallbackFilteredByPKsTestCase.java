package org.dbunit.database;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junitx.framework.ListAssert;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.ITableIterator;

import org.dbunit.AbstractHSQLTestCase;
import org.dbunit.util.CollectionsHelper;
import org.dbunit.util.search.SearchException;

public abstract class AbstractSearchCallbackFilteredByPKsTestCase extends AbstractHSQLTestCase {

  private static final char FIRST_TABLE = 'A';

  private Map fInput = new HashMap();
  private Map fOutput = new HashMap();  
  
  public AbstractSearchCallbackFilteredByPKsTestCase(String testName, String sqlFile) {
    super(testName, sqlFile);
  }
  
  protected abstract int[] setupTablesSizeFixture();
  
  protected IDataSet setupTablesDataSetFixture() throws SQLException {
    IDatabaseConnection connection = getConnection();
    IDataSet allDataSet = connection.createDataSet();
    return allDataSet;
  }
    
  protected  void addInput(String tableName, String[] ids) {
    Set idsSet = CollectionsHelper.objectsToSet( ids );
    this.fInput.put( tableName, idsSet );
  }
  protected  void addOutput(String tableName, String[] ids) {
    List idsList = Arrays.asList( ids );
    this.fOutput.put( tableName, idsList );
  }
  
  protected abstract IDataSet getDataset() throws SQLException, SearchException; 

  protected void doIt() throws SQLException, DataSetException, SearchException  {
    IDataSet dataset = getDataset();
    assertNotNull( dataset );
    
    // primeiro, checa se somente as tabelas corretas foram geradas
    String[] outputTables = dataset.getTableNames();
    assertTablesSize( outputTables );
    assertTablesName( outputTables );
    assertRows( dataset );   
  }

  protected void assertTablesSize(String[] actualTables) {
    int expectedSize = this.fOutput.size();
    int actualSize = actualTables.length;
    if ( expectedSize != actualSize ) {
      super.logger.error( "Tabelas esperadas: " + dump(this.fOutput.keySet()) );
      super.logger.error( "Tabelas retornadas: " + dump(actualTables) );
      fail( "numero de tabelas retornadas nao bateu: " + actualSize + " em vez de " + expectedSize );
    }    
  }
  protected void assertTablesName(String[] outputTables) {
    Set expectedTables = new HashSet(this.fOutput.keySet());
    Set notExpectedTables = new HashSet();
    boolean ok = true;
    // primeiro ve quais estao faltando
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
      super.logger.error( "Tabelas retornadas nao esperadas: " + dump(notExpectedTables) );
    }
    if ( ! expectedTables.isEmpty() ) {
      ok = false;
      super.logger.error( "Tabelas esperadas nao retornadas: " + dump(expectedTables) );
    }
    if ( ! ok ) {
      fail( "Tabelas retornadas nao batem com a expectativa; check o error output" );
    }
  }
  
  protected void assertRows(IDataSet dataset) throws DataSetException {
    ITableIterator iterator = dataset.iterator();
    while (iterator.next()) {
      ITable table = iterator.getTable();
      String tableName = table.getTableMetaData().getTableName();
      String idField = "PK" + tableName;
      List expectedIds = (List) this.fOutput.get( tableName );
      List actualIds = new ArrayList();
      int rowCount = table.getRowCount();
      for( int row=0; row<rowCount; row++ ) {
        String id = (String) table.getValue( row, idField );
        actualIds.add( id );
        if ( super.logger.isDebugEnabled() ) {
          super.logger.debug( "T:" + tableName + " row: " + row + " id: " + id );      
        }
      }
      Collections.sort( expectedIds );
      Collections.sort( actualIds );
      ListAssert.assertEquals( "ids para tabela " + tableName + " nao batem", expectedIds, actualIds );
    }
  }
  
  public void testSetupTables() throws SQLException, DataSetException {
    int[] sizes = setupTablesSizeFixture(); 
    IDataSet allDataSet = setupTablesDataSetFixture();
    assertNotNull( allDataSet );
    for (short i = 0; i < sizes.length; i++) {
      char table = (char) (FIRST_TABLE + i);
      if ( super.logger.isDebugEnabled() ) {
        super.logger.debug( "Obtendo tabela " + table );
      }
      ITable itable = allDataSet.getTable( ""+table );
      assertNotNull( "nao encontrou tabela " + table, itable );
      assertEquals( "tamanho nao bateu para tabela " + table, sizes[i], itable.getRowCount());
    }
  }
  
  protected Map getInput() {
    return this.fInput;
  }

  protected Map getOutput() {
    return this.fOutput;
  }
  

}
