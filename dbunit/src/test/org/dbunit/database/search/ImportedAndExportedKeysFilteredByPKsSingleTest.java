package org.dbunit.database.search;

import java.sql.SQLException;

import org.dbunit.database.AbstractImportedAndExportedKeysFilteredByPKsTestCase;
import org.dbunit.dataset.DataSetException;

import org.dbunit.util.search.SearchException;

public class ImportedAndExportedKeysFilteredByPKsSingleTest extends AbstractImportedAndExportedKeysFilteredByPKsTestCase {

  public ImportedAndExportedKeysFilteredByPKsSingleTest(String testName) {
   this(testName, "hypersonic_simple_dataset.sql");
  }
  public ImportedAndExportedKeysFilteredByPKsSingleTest(String testName, String sqlFile) {
    super( testName, sqlFile );
  }
  protected int[] setupTablesSizeFixture() {
    int[] sizes = new int[] { 1, 1, 2 };
    return sizes;
  }
  
  public void testCWithOne() throws DataSetException, SQLException, SearchException {
    addInput( C, new String[] { C1 } );
    addOutput( C, new String[] { C1 } );
    addOutput( B, new String[] { B1 } );
    addOutput( A, new String[] { A1 } );
    doIt();      
  }
  
  public void testCWithTwo() throws DataSetException, SQLException, SearchException {
    addInput( C, new String[] { C1, C2 } );
    addOutput( C, new String[] { C1, C2 } );
    addOutput( B, new String[] { B1 } );
    addOutput( A, new String[] { A1 } );
    doIt();      
  }
  
  public void testCWithTwoInvertedInput() throws DataSetException, SQLException, SearchException {
    addInput( C, new String[] { C2, C1 } );
    addOutput( C, new String[] { C1, C2 } );
    addOutput( B, new String[] { B1 } );
    addOutput( A, new String[] { A1 } );
    doIt();      
  }
  
  public void testCWithTwoInvertedOutput() throws DataSetException, SQLException, SearchException {
    addInput( C, new String[] { C1, C2 } );
    addOutput( C, new String[] { C2, C1 } );
    addOutput( B, new String[] { B1 } );
    addOutput( A, new String[] { A1 } );
    doIt();      
  }
  
  public void testCWithRepeated() throws DataSetException, SQLException, SearchException {
    addInput( C, new String[] { C1, C2, C2, C1, C1, C1, C2, C2 } );
    addOutput( C, new String[] { C2, C1 } );
    addOutput( B, new String[] { B1 } );
    addOutput( A, new String[] { A1 } );
    doIt();      
  }
  
  public void testB() throws DataSetException, SQLException, SearchException {
    addInput( B, new String[] { B1 } );
    addOutput( C, new String[] { C1, C2 } );
    addOutput( B, new String[] { B1 } );
    addOutput( A, new String[] { A1 } );
    doIt();      
  }

  public void testA() throws DataSetException, SQLException, SearchException {
    addInput( A, new String[] { A1 } );
    addOutput( C, new String[] { C1, C2 } );
    addOutput( B, new String[] { B1 } );
    addOutput( A, new String[] { A1 } );
    doIt();      
  }
}
