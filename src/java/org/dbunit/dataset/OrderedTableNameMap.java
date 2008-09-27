/*
 *
 * The DbUnit Database Testing Framework
 * Copyright (C)2002-2004, DbUnit.org
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
package org.dbunit.dataset;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dbunit.database.AmbiguousTableNameException;

/**
 * Associates a table name with an arbitrary object. Moreover the
 * order of the added table names is maintained and the ordered table
 * names can be retrieved via {@link #getTableNames()}.
 * <p>
 * The map ensures that one table name can only be added once.
 * </p>
 * 
 * TODO In the future it might be discussed if a ListOrderedMap (apache-commons-collections) can/should be used.
 * TODO If case-sensitive tables will be re-introduced (as it was before 1.5) remove all the "toUpperCase()"s.
 * 
 * @author gommma
 * @author Last changed by: $Author$
 * @version $Revision$
 * @since 2.4.0
 */
public class OrderedTableNameMap
{
	
	/**
	 * The map for fast access to the existing table names and for
	 * associating an arbitrary object with a table name
	 */
	private Map _tableMap = new HashMap();
	/**
	 * Chronologically ordered list of table names - keeps the order
	 * in which the table names have been added
	 */
	private List _tableNames = new ArrayList();
	
	/**
	 * Creates a new map which does strictly force that one table can only occur once.
	 */
	public OrderedTableNameMap()
	{
	}

	/**
	 * @param tableName The table name for which the associated object is retrieved
	 * @return The object that has been associated with the given table name
	 */
	public Object get(String tableName) 
	{
		return this._tableMap.get(tableName.toUpperCase());//TODO remove ToUpperCase when table names are case sensitive
	}


	/**
	 * Provides the ordered table names having the same order in which the table
	 * names have been added via {@link #add(String, Object)}.
	 * @return The list of table names ordered in the sequence as
	 * they have been added to this map
	 */
	public String[] getTableNames() 
	{
		return (String[])this._tableNames.toArray(new String[0]);
	}

	/**
	 * Checks if this map contains the given table name
	 * @param tableName
	 * @return Returns <code>true</code> if the map of tables contains the given table name
	 */
	public boolean containsTable(String tableName) 
	{
		return _tableMap.containsKey(tableName.toUpperCase());//TODO remove ToUpperCase when table names are case sensitive
	}

	/**
	 * Adds the given table name to the map of table names, associating 
	 * it with the given object.
	 * @param tableName The table name to be added
	 * @param object Object to be associated with the given table name. Can be null
	 * @throws AmbiguousTableNameException If the given table name already exists
	 */
	public void add(String tableName, Object object) throws AmbiguousTableNameException 
	{
        // prevent table name conflict
        if (this.containsTable(tableName))
        {
            throw new AmbiguousTableNameException(tableName.toUpperCase());//TODO remove ToUpperCase when table names are case sensitive
        }
        else {
            this._tableMap.put(tableName.toUpperCase(), object);//TODO remove ToUpperCase when table names are case sensitive
            this._tableNames.add(tableName);
        }
	}
	
    /**
     * @return The values of this map ordered in the sequence they have been added
     */
    public Collection orderedValues() {
        List orderedValues = new ArrayList(this._tableNames.size());
        for (Iterator iterator = _tableNames.iterator(); iterator.hasNext();) {
            String tableName = (String) iterator.next();
            Object object = this.get(tableName);
            orderedValues.add(object);
        }
        return orderedValues;
    }
    
	/**
	 * Updates the value associated with the given table name. Must be invoked if
	 * the table name has already been added before.
	 * @param tableName The table name for which the association should be updated
	 * @param object The new object to be associated with the given table name
	 */
	public void update(String tableName, Object object) 
	{
        // prevent table name conflict
        if (!this.containsTable(tableName))
        {
        	throw new IllegalArgumentException("The table name '" + tableName + "' does not exist in the map");
        }
        this._tableMap.put(tableName.toUpperCase(), object);//TODO remove ToUpperCase when table names are case sensitive
	}

	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		sb.append(getClass().getName()).append("[");
		sb.append("_tableNames=").append(_tableNames);
		sb.append(", _tableMap=").append(_tableMap);
		sb.append("]");
		return sb.toString();
	}

}