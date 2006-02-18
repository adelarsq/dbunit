/*
 *
 * The DbUnit Database Testing Framework
 * Copyright (C)2005, DbUnit.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

package org.dbunit.database;

import java.util.HashSet;
import java.util.Set;

import org.dbunit.AbstractHSQLTestCase;
import org.dbunit.dataset.Column;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.ITableMetaData;
import org.dbunit.dataset.RowOutOfBoundsException;
import org.dbunit.util.CollectionsHelper;

/**
 * @author Felipe Leme <dbunit@felipeal.net>
 * @version $Revision$
 * @since Sep 9, 2005
 */
public class PrimaryKeyFilteredTableWrapperTest extends AbstractHSQLTestCase {
  
  private ITable fTable; //fixture
  private IDataSet fDataSet; //fixture
  
  public PrimaryKeyFilteredTableWrapperTest( String name ) {
    super( name, "hypersonic_dataset.sql" );
  }
  
  protected void setUp() throws Exception {
    super.setUp();
    this.fDataSet = super.getConnection().createDataSet();
    this.fTable = this.fDataSet.getTable(E);
  }
  
  public void testConstructorNullTable() throws DataSetException {
    try { 
      PrimaryKeyFilteredTableWrapper table = new PrimaryKeyFilteredTableWrapper( null, new HashSet() );
      fail( "constructor accepted null argument and returned " + table );
    } catch( IllegalArgumentException e ) {
      assertNotNull( e.getMessage() );
    }
  }
  
  public void testConstructorNullSet() throws DataSetException {    
    try { 
      PrimaryKeyFilteredTableWrapper table = new PrimaryKeyFilteredTableWrapper( this.fTable, null );
      fail( "constructor accepted null argument and returned " + table );
    } catch( IllegalArgumentException e ) {
      assertNotNull( e.getMessage() );
    }
  }
  
  public void testDenyEverything() throws DataSetException {    
      PrimaryKeyFilteredTableWrapper table = new PrimaryKeyFilteredTableWrapper( this.fTable, new HashSet() );
      assertMetaInformationEquals( this.fTable, table );
      assertEquals( "table not empty", 0, table.getRowCount() );
      assertSecondTableIsEmpty( this.fTable, table );
  }
  
  public void testAllowEverything() throws DataSetException {    
    Set allowedPKs = getPKs( this.fTable );
    allowEveryThingTest( allowedPKs );
  }
  
  public void testAllowEverythingWithClonedSet() throws DataSetException {    
    Set allowedPKs = getPKs( this.fTable );
    Set newSet = new HashSet( allowedPKs );
    allowEveryThingTest( newSet );
  }
  
  public void testFilterLast() throws DataSetException {
   doFilter( new String[] { E1, E2, E3 } ); 
  }
  
  public void testFilterFirst() throws DataSetException {
    doFilter( new String[] { E2, E3, E4 } ); 
   }
  
  public void testFilterMiddle() throws DataSetException {
    doFilter( new String[] { E1, E4 } ); 
   }
  
  private void doFilter( String[] ids) throws DataSetException {
    Set allowedIds = CollectionsHelper.objectsToSet(ids);
    ITable table = new PrimaryKeyFilteredTableWrapper( this.fTable, allowedIds );
    assertEquals( "size of table does not match", ids.length, table.getRowCount() );
    String pkColumn = table.getTableMetaData().getPrimaryKeys()[0].getColumnName();
    int size = table.getRowCount();
    for ( int i=0; i<size; i++ ) {
      Object pk = table.getValue( i, pkColumn );
      assertEquals( "id didn't match at index " + i, ids[i], pk );
    }    
  }

  private void allowEveryThingTest( Set set ) throws DataSetException {    
    PrimaryKeyFilteredTableWrapper table = new PrimaryKeyFilteredTableWrapper( this.fTable, new HashSet(set) );
    assertTableSize( this.fTable, set.size() );
    assertMetaInformationEquals( this.fTable, table );
    assertTrue( "table is empty", table.getRowCount() > 0 );
    assertContentIsSame( this.fTable, table );
  }
  
  private void assertTableSize(ITable table, int i) {
    int size = table.getRowCount();
    assertEquals( "getRowCount() didn't match", i, size );    
  }

  private Set getPKs(ITable table) throws DataSetException {
    String pkColumn = table.getTableMetaData().getPrimaryKeys()[0].getColumnName();
    HashSet set = new HashSet();
    int size = table.getRowCount();
    for ( int i=0; i<size; i++ ) {
      Object pk = table.getValue( i, pkColumn );
      set.add( pk );
    }
    return set;
  }
  

  private void assertSecondTableIsEmpty(ITable t1, ITable t2) throws DataSetException {
    int size = t1.getRowCount();
    Column[] cols = t1.getTableMetaData().getColumns();
    for ( int i=0; i<size; i++ ) {
      for ( int j=0; j<cols.length; j++ ) {
        String col = cols[j].getColumnName();
        try {
          Object o = t2.getValue( j, col );
          fail( "there is an element at (" + i + ", " + col + ")" + o);
        } catch ( RowOutOfBoundsException e ) {
          assertNotNull( e.getMessage() );
        }
      }
    }    
  }
  
  private void assertContentIsSame(ITable t1, ITable t2) throws DataSetException {
    int size = t1.getRowCount();
    Column[] cols = t1.getTableMetaData().getColumns();
    for ( int i=0; i<size; i++ ) {
      for ( int j=0; j<cols.length; j++ ) {
        String col = cols[j].getColumnName();
        Object o1 = t1.getValue( j, col );
        Object o2 = t2.getValue( j, col );
        assertEquals( "element at (" + i + ", " + col + ") is not the same: ", o1, o2);
      }
    }    
  }
  

  private void assertMetaInformationEquals(ITable t1, ITable t2) {
    ITableMetaData metaData1 = t1.getTableMetaData();
    ITableMetaData metaData2 = t2.getTableMetaData();
    assertEquals( "metadata are not equal", metaData1, metaData2 );    
  }

  
}
