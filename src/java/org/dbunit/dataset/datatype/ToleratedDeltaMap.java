package org.dbunit.dataset.datatype;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Container that manages a map of {@link ToleratedDelta} objects to be used
 * for numeric comparisons with an allowed deviation of two values
 *  
 * @author gommma
 * @since 2.3.0
 */
public class ToleratedDeltaMap
{
    /**
     * List of {@link ToleratedDelta} objects 
     */ 
    private Map _toleratedDeltas;
	/**
	 * The logger
	 */
    private Logger logger = LoggerFactory.getLogger(ToleratedDeltaMap.class);
    
    
    /**
     * Lookup a tolerated delta object by tableName and ColumnName.
     * @param tableName
     * @param columnName
     * @return The object from the map or <code>null</code> if no such object was found
     */
    public ToleratedDelta findToleratedDelta(String tableName, String columnName) 
    {
        Map toleratedDeltas = getToleratedDeltasNullSafe();
        String mapKey = ToleratedDeltaMap.buildMapKey(tableName, columnName);
        ToleratedDelta deltaObj = (ToleratedDelta)toleratedDeltas.get(mapKey);
        return deltaObj;
	}

	private final Map getToleratedDeltasNullSafe() 
	{
		Map res = getToleratedDeltas();
        if(res==null) 
        {
            return Collections.EMPTY_MAP;
        }
        return res;
    }
    
    public Map getToleratedDeltas() 
    {
		return _toleratedDeltas;
	}

	/**
	 * Adds a new object to the map of tolerated deltas
	 * @param delta The object to be added to the map
	 */
	public void addToleratedDelta(ToleratedDelta delta) 
	{
		if (delta == null) 
		{
			throw new NullPointerException("The parameter 'delta' must not be null");
		}

		if(this._toleratedDeltas==null) 
		{
			this._toleratedDeltas=new HashMap();
		}
		String key = ToleratedDeltaMap.buildMapKey(delta);
		// Put the new object into the map
    	ToleratedDelta removed = (ToleratedDelta)_toleratedDeltas.put(key, delta);
    	//Give a hint to the user when an already existing object has been overwritten/replaced
    	if(removed!=null) 
    	{
    		logger.debug("Replaced old tolerated delta object from map with key {}. Old replaced object={}", key, removed);
    	}
	}

	/**
	 * Utility method to create a map key from the input parameters
	 * @param tableName
	 * @param columnName
	 * @return The key for the tolerated delta object map, consisting of the tableName and the columnName
	 */
	static String buildMapKey(String tableName, String columnName) 
	{
		return tableName+ "." + columnName;
	}

	/**
	 * Utility method to create a map key from the input parameters
	 * @param delta
	 * @return The key for the tolerated delta object map, consisting of the tableName and the columnName
	 */
	static String buildMapKey(ToleratedDelta delta) 
	{
		return buildMapKey(delta.getTableName(),delta.getColumnName());
	}
	
	
    /**
     * Simple bean that holds the tolerance for floating point comparisons for a specific
     * database column.
     */
    public static class ToleratedDelta 
    {
    	private String tableName;
    	private String columnName;
    	private double toleratedDelta;
    	
		/**
		 * @param tableName The name of the table
		 * @param columnName The name of the column for which the tolerated delta should be applied
		 * @param toleratedDelta The tolerated delta. For example 1E-5 means that the comparison must
		 * match the first 5 decimal digits. All subsequent decimals are ignored.
		 */
		public ToleratedDelta(String tableName, String columnName, double toleratedDelta) 
		{
			super();
			this.tableName = tableName;
			this.columnName = columnName;
			this.toleratedDelta = toleratedDelta;
		}

		public String getTableName() {
			return tableName;
		}

		public String getColumnName() {
			return columnName;
		}

		public double getToleratedDelta() {
			return toleratedDelta;
		}
    	
    	public boolean matches(String tableName, String columnName) {
    		if(this.tableName.equals(tableName) && this.columnName.equals(columnName)) {
    			return true;
    		}else {
    			return false;
    		}
    	}
    	
		public String toString() {
    		StringBuffer sb = new StringBuffer();
    		sb.append("tableName=").append(tableName);
    		sb.append(", columnName=").append(columnName);
    		sb.append(", toleratedDelta=").append(toleratedDelta);
    		return sb.toString();
    	}
    }


}