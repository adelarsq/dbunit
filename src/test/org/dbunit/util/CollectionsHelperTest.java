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
package org.dbunit.util;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.collections.set.ListOrderedSet;

import junit.framework.TestCase;

/**
 * @author Felipe Leme <dbunit@felipeal.net>
 * @version $Revision$
 * @since Nov 5, 2005
 * 
 */
public class CollectionsHelperTest extends TestCase {
  
  public static final String A = "A";
  public static final String B = "B";
  public static final String C = "C";
  
  public CollectionsHelperTest( String testName ) {
    super( testName );
  }
  
  public void testObjectsToSetNullEntry() {
    Set output = CollectionsHelper.objectsToSet( null );
    assertNull( "set should be null", output );
  }
  
  public void testObjectsToSetEmptyEntry() {
    Set output = CollectionsHelper.objectsToSet( new Object[0] );
    assertNotNull( "set should not be null", output );
    assertEquals( "set should be empty", 0, output.size() );
  }

  public void testObjectsToSetSingleInput() {
    Object[] input = { A };
    Set output = CollectionsHelper.objectsToSet( input );
    assertNotNull( "set should not be null", output );
    Iterator i = output.iterator();
    assertTrue( "iterator is empty", i.hasNext() );
    assertEquals( "element 0 match", A, i.next() );
    assertFalse( "iterator is not empty", i.hasNext() );
  }
  
  public void testObjectsToSetSequence() {
    Object[] input = { A, C, B };
    Set output = CollectionsHelper.objectsToSet( input );
    assertNotNull( "set should not be null", output );
    Iterator i = output.iterator();
    assertTrue( "iterator is empty", i.hasNext() );
    assertEquals( "element 0 match", A, i.next() );
    assertEquals( "element 1 match", C, i.next() );
    assertEquals( "element 2 match", B, i.next() );
    assertFalse( "iterator is not empty", i.hasNext() );
  }
  
  public void testSetToObjectsNullEntry() {
    Object[] output = CollectionsHelper.setToObjects( null );
    assertNull( "array should be null", output );
  }
  
  public void testSetToObjectsEmptyEntry() {
    Set input = new HashSet();
    Object[] output = CollectionsHelper.setToObjects( input );    
    assertNotNull( "array should not be null", output );
    assertEquals( "array should be empty", 0, output.length );
  }

  public void testSetToObjectsSingle() {
    Set input = new HashSet();
    input.add( A );
    Object[] output = CollectionsHelper.setToObjects( input );    
    assertNotNull( "array should not be null", output );
    assertEquals( "array size does not match", 1, output.length );
    assertEquals( "element 0 does not match", A, output[0] );
  }

  public void testSetToObjectsOrderedSet() {
    Set input = new TreeSet();
    input.add( A );
    input.add( C );
    input.add( B );
    Object[] output = CollectionsHelper.setToObjects( input );    
    assertNotNull( "array should not be null", output );
    assertEquals( "array size does not match", 3, output.length );
    assertEquals( "element 0 does not match", A, output[0] );
    assertEquals( "element 1 does not match", B, output[1] );
    assertEquals( "element 2 does not match", C, output[2] );
  }

  public void testSetToObjectsSequencialSet() {
    Set input = new ListOrderedSet();
    input.add( A );
    input.add( C );
    input.add( B );
    Object[] output = CollectionsHelper.setToObjects( input );    
    assertNotNull( "array should not be null", output );
    assertEquals( "array size does not match", 3, output.length );
    assertEquals( "element 0 does not match", A, output[0] );
    assertEquals( "element 1 does not match", C, output[1] );
    assertEquals( "element 2 does not match", B, output[2] );
  }
  
  public void testSetToStringsNullEntry() {
    Object[] output = CollectionsHelper.setToStrings( null );
    assertNull( "array should be null", output );
  }
  
  public void testSetToStringsEmptyEntry() {
    Set input = new HashSet();
    Object[] output = CollectionsHelper.setToStrings( input );    
    assertNotNull( "array should not be null", output );
    assertEquals( "array should be empty", 0, output.length );
  }

  public void testSetToStringsSingle() {
    Set input = new HashSet();
    input.add( A );
    String[] output = CollectionsHelper.setToStrings( input );    
    assertNotNull( "array should not be null", output );
    assertEquals( "array size does not match", 1, output.length );
    assertEquals( "element 0 does not match", A, output[0] );
  }

  public void testSetToStringsOrderedSet() {
    Set input = new TreeSet();
    input.add( A );
    input.add( C );
    input.add( B );
    String[] output = CollectionsHelper.setToStrings( input );    
    assertNotNull( "array should not be null", output );
    assertEquals( "array size does not match", 3, output.length );
    assertEquals( "element 0 does not match", A, output[0] );
    assertEquals( "element 1 does not match", B, output[1] );
    assertEquals( "element 2 does not match", C, output[2] );
  }

  public void testSetToStringsSequencialSet() {
    Set input = new ListOrderedSet();
    input.add( A );
    input.add( C );
    input.add( B );
    String[] output = CollectionsHelper.setToStrings( input );    
    assertNotNull( "array should not be null", output );
    assertEquals( "array size does not match", 3, output.length );
    assertEquals( "element 0 does not match", A, output[0] );
    assertEquals( "element 1 does not match", C, output[1] );
    assertEquals( "element 2 does not match", B, output[2] );
  }


}
