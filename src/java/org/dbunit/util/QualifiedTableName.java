/*
 *
 * The DbUnit Database Testing Framework
 * Copyright (C)2004-2008, DbUnit.org
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

import org.dbunit.database.DatabaseConfig;
import org.dbunit.dataset.DataSetUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility to parse a fully qualified table name into its components <i>schema</i> and <i>table</i>.
 * @author gommma
 * @version $Revision: $
 * @since 2.3.0
 */
public class QualifiedTableName
{
    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(QualifiedTableName.class);

	private String schema;
	private String table;
	
	/**
	 * Creates an object parsing the given tableName.
	 * @param tableName The table name, either qualified or unqualified. If it is qualified (like "MYSCHEMA.MYTABLE")
	 * this schema name has precedence before the given <code>defaultSchema</code> parameter.
	 * @param defaultSchema The schema that is used when the given tableName is not fully qualified
	 * (i.e. it is not like "MYSCHEMA.MYTABLE"). Can be null
	 */
	public QualifiedTableName(String tableName, String defaultSchema)
	{
		if(tableName==null){
			throw new NullPointerException("The parameter 'tableName' must not be null");
		}
    	parseFullTableName(tableName, defaultSchema);
	}
	

	/**
	 * Parses the given full table name into a schema name and a table name if available. If
	 * no schema is set the value of the {@link #getSchema()} is null.
	 * Sets the corresponding members of this class if found.
	 * @param fullTableName potentially fully qualified table name
	 * @param defaultSchema The schema that is used when the given tableName is not fully qualified
	 * (i.e. it is not like "MYSCHEMA.MYTABLE"). Can be null
	 */
	private void parseFullTableName(String fullTableName, String defaultSchema)
	{
		if(fullTableName==null){
			throw new NullPointerException("The parameter 'fullTableName' must not be null");
		}
        // check if a schema is in front
		int firstDotIndex = fullTableName.indexOf(".");
        if (firstDotIndex != -1) {
            // set schema
        	this.schema = fullTableName.substring(0, firstDotIndex);
            // set table name without schema
        	this.table = fullTableName.substring(firstDotIndex + 1);
        }
        else 
        {
        	// No schema name found in table
        	this.table = fullTableName;
        	// If the schema has not been found in the given table name 
        	// (that means there is no "MYSCHEMA.MYTABLE" but only a "MYTABLE")
        	// then set the schema to the given default schema
    		this.schema = defaultSchema;
        }
	}

	/**
	 * @return The schema name which can be null if no schema has been given in the constructor
	 */
	public String getSchema() {
		return schema;
	}

	/**
	 * @return The name of the plain, unqualified table
	 */
	public String getTable() {
		return table;
	}

	/**
	 * @return The qualified table name with the prepended schema if a schema is available
	 */
	public String getQualifiedName() {
		return getQualifiedName(this.schema, this.table);
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(getClass().getName()).append("[");
		sb.append("schema=").append(schema);
		sb.append(", table=").append(table);
		sb.append("]");
		return sb.toString();
	}
	
	
	
	
	/**
	 * Returns the qualified name according to {@link QualifiedTableName#getQualifiedName(String, String)}
	 * if the feature {@link DatabaseConfig#FEATURE_QUALIFIED_TABLE_NAMES} is set. Otherwise the given
	 * name is returned unqualified (i.e. without prepending the prefix). 
     * @param prefix the prefix that qualifies the name and is prepended if the name is not qualified yet
     * @param name the name The name to be qualified if it is not qualified already
	 * @param config The configuration used to check if the feature {@link DatabaseConfig#FEATURE_QUALIFIED_TABLE_NAMES}
	 * is set or not.
	 * @return The qualified name as defined in {@link QualifiedTableName#getQualifiedName(String, String)} if needed
	 */
	public static String getQualifiedName(String prefix, String name,
			DatabaseConfig config) 
	{
		if(logger.isDebugEnabled())
			logger.debug("getQualifiedName(prefix={}, name={}, config={}) - start", 
					new Object[] {prefix, name, config} );

        boolean feature = config.getFeature(DatabaseConfig.FEATURE_QUALIFIED_TABLE_NAMES);
        if (feature)
        {
        	logger.debug("Qualified table names feature is enabled. Returning qualified table name");
            return getQualifiedName(prefix, name);
        }
        else 
        {
        	logger.debug("Qualified table names feature is disabled. Returning plain table name");
        	return name;
        }
	}

    /**
     * Returns the specified name qualified with the specified prefix. The name
     * is not modified if the prefix is <code>null</code> or if the name is
     * already qualified.
     * <p>
     * Example: <br>
     * <code>getQualifiedName(null, "NAME")</code> returns
     * <code>"NAME"</code>. <code>getQualifiedName("PREFIX", "NAME")</code>
     * returns <code>"PREFIX.NAME"</code> and
     * <code>getQualifiedName("PREFIX2", "PREFIX1.NAME")</code>
     * returns <code>"PREFIX1.NAME"</code>.
     *
     * @param prefix the prefix that qualifies the name and is prepended if the name is not qualified yet
     * @param name the name The name to be qualified if it is not qualified already
     * @return the qualified name
     */
    public static String getQualifiedName(String prefix, String name)
    {
        logger.debug("getQualifiedName(prefix={}, name={}) - start", prefix, name);

        return getQualifiedName(prefix, name, (String)null);
    }

    /**
     * @param prefix the prefix that qualifies the name and is prepended if the name is not qualified yet
     * @param name the name The name to be qualified if it is not qualified already
     * @param escapePattern The escape pattern to be applied on the prefix and the name. Can be null.
     * @return The qualified name
     */
    public static String getQualifiedName(String prefix, String name,
            String escapePattern)
    {
        if(logger.isDebugEnabled())
            logger.debug("getQualifiedName(prefix={}, name={}, escapePattern={}) - start", 
                    new String[] {prefix, name, escapePattern});

        if (escapePattern != null)
        {
            prefix = DataSetUtils.getEscapedName(prefix, escapePattern);
            name = DataSetUtils.getEscapedName(name, escapePattern);
        }

        if (prefix == null || prefix.equals("") || name.indexOf(".") >= 0)
        {
            return name;
        }

        return prefix + "." + name;
    }
	
}