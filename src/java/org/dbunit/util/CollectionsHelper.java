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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Set;

import org.apache.commons.collections.set.ListOrderedSet;

/**
 * Helper for collections-related methods.
 * <br>
 * @author Felipe Leme <dbunit@felipeal.net>
 * @version $Revision$
 * @since Nov 5, 2005
 * 
 */

public class CollectionsHelper {

    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(CollectionsHelper.class);
  
  // class is "static"
  private CollectionsHelper() {}

  /**
   * Returns a Set from an array of objects.
   * Note the Iterator returned by this Set mantains the order of the array.
   * @param objects array of objects
   * @return Set with the elements of the array or null if entry is null
   */
  public static Set objectsToSet( Object[] objects ) {
        logger.debug("objectsToSet(objects=" + objects + ") - start");

    if ( objects == null ) {
      return null;
    }
    Set set = new ListOrderedSet();
    for (int i = 0; i < objects.length; i++) {
      set.add(objects[i]);
    }
    return set;
  }

  /**
   * Returns an array of Objects from a Set.
   * @param a Set 
   * @return array of Objects with the elements of the Set or null if set is null
   */
  public static Object[] setToObjects( Set set ) {
        logger.debug("setToObjects(set=" + set + ") - start");

    if ( set == null ) {
      return null;
    }
    Object[] objects = new Object[ set.size() ];
    int i=0;
    for (Iterator iter = set.iterator(); iter.hasNext(); i++) {
      objects[i] = iter.next();      
    }
    return objects;
  }

  /**
   * Returns an array of Strings from a Set.
   * @param a Set of Strings
   * @return array of Strings with the elements of the Set or null if set is null
   */
  public static String[] setToStrings( Set set ) {
        logger.debug("setToStrings(set=" + set + ") - start");

    if ( set == null ) {
      return null;
    }
    String[] strings = new String[ set.size() ];
    int i=0;
    for (Iterator iter = set.iterator(); iter.hasNext(); i++) {
      strings[i] = (String) iter.next();      
    }
    return strings;
  };

}
