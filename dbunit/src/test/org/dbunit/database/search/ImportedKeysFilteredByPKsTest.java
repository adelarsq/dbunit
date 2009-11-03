package org.dbunit.database.search;

import java.sql.SQLException;

import org.dbunit.database.AbstractImportedKeysFilteredByPKsTestCase;
import org.dbunit.dataset.DataSetException;

import org.dbunit.util.search.SearchException;

public class ImportedKeysFilteredByPKsTest extends AbstractImportedKeysFilteredByPKsTestCase {

  public ImportedKeysFilteredByPKsTest(String testName) {
    super(testName, "hypersonic_dataset.sql");
  }
  protected int[] setupTablesSizeFixture() {
    int[] sizes = new int[] { 2, 8, 4, 2, 4, 2, 2, 2 };
    return sizes;
  }
     
  public void testAWithOne() throws DataSetException, SQLException, SearchException {
    addInput( A, new String[] { A1 } );
    addOutput( A, new String[] { A1 } );
    addOutput( D, new String[] { D1 } );
    doIt();      
  }

  public void testHWithOne() throws DataSetException, SQLException, SearchException {
    addInput( H, new String[] { H1 } );
    addOutput( H, new String[] { H1 } );
    doIt();      
  }

  public void testBWithOne() throws DataSetException, SQLException, SearchException {
    addInput( B, new String[] { B1 } );
    addOutput( B, new String[] { B1 } );
    addOutput( C, new String[] { C1 } );
    addOutput( E, new String[] { E1 } );
    addOutput( G, new String[] { G1 } );
    addOutput( A, new String[] { A1 } );
    addOutput( F, new String[] { F1 } );
    addOutput( D, new String[] { D1 } );    
    addOutput( H, new String[] { H1 } );
    doIt();      
  }

}
