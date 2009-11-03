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

import java.util.Set;

import org.apache.commons.collections.set.ListOrderedSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper for collections-related methods.
 * <br>
 * @author Felipe Leme (dbunit@felipeal.net)
 * @author gommma (gommma AT users.sourceforge.net)
 * @author Last changed by: $Author$
 * @version $Revision$ $Date$
 * @since Nov 5, 2005
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
     * Note the Iterator returned by this Set preserves the order of the array.
     * @param objects array of objects
     * @return Set with the elements of the array or null if entry is null
     */
    public static Set objectsToSet( Object[] objects ) {
        logger.debug("objectsToSet(objects={}) - start", objects);

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
     * @param set a Set 
     * @return array of Objects with the elements of the Set or null if set is null
     */
    public static Object[] setToObjects( Set set ) {
        logger.debug("setToObjects(set={}) - start", set);

        if ( set == null ) {
            return null;
        }
        else {
            return set.toArray();
        }
    }

    /**
     * Returns an array of Strings from a Set.
     * @param set a Set of Strings
     * @return array of Strings with the elements of the Set or null if set is null
     */
    public static String[] setToStrings( Set set ) {
        logger.debug("setToStrings(set={}) - start", set);

        if ( set == null ) {
            return null;
        }
        else {
            String[] strings = (String[]) set.toArray(new String[0]);
            return strings;
        }
    };

}
