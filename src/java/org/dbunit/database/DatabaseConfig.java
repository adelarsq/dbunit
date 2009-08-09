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
package org.dbunit.database;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import org.dbunit.database.statement.IStatementFactory;
import org.dbunit.database.statement.PreparedStatementFactory;
import org.dbunit.dataset.datatype.DefaultDataTypeFactory;
import org.dbunit.dataset.datatype.IDataTypeFactory;
import org.dbunit.dataset.filter.IColumnFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Configuration used by the {@link DatabaseConnection}.
 * 
 * @author manuel.laflamme
 * @author gommma (gommma AT users.sourceforge.net)
 * @author Last changed by: $Author$
 * @version $Revision$ $Date$
 * @since 2.0
 */
public class DatabaseConfig
{

    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConfig.class);

    public static final String PROPERTY_STATEMENT_FACTORY =
            "http://www.dbunit.org/properties/statementFactory";
    public static final String PROPERTY_RESULTSET_TABLE_FACTORY =
            "http://www.dbunit.org/properties/resultSetTableFactory";
    public static final String PROPERTY_DATATYPE_FACTORY =
            "http://www.dbunit.org/properties/datatypeFactory";
    public static final String PROPERTY_ESCAPE_PATTERN =
            "http://www.dbunit.org/properties/escapePattern";
    public static final String PROPERTY_TABLE_TYPE =
            "http://www.dbunit.org/properties/tableType";
    public static final String PROPERTY_PRIMARY_KEY_FILTER =
            "http://www.dbunit.org/properties/primaryKeyFilter";
    public static final String PROPERTY_BATCH_SIZE =
    		"http://www.dbunit.org/properties/batchSize";
	public static final String PROPERTY_FETCH_SIZE = 
			"http://www.dbunit.org/properties/fetchSize";
	public static final String PROPERTY_METADATA_HANDLER =
	        "http://www.dbunit.org/properties/metadataHandler";

    public static final String FEATURE_CASE_SENSITIVE_TABLE_NAMES =
        "http://www.dbunit.org/features/caseSensitiveTableNames";
    public static final String FEATURE_QUALIFIED_TABLE_NAMES =
        "http://www.dbunit.org/features/qualifiedTableNames";
    public static final String FEATURE_BATCHED_STATEMENTS =
        "http://www.dbunit.org/features/batchedStatements";
    public static final String FEATURE_DATATYPE_WARNING =
        "http://www.dbunit.org/features/datatypeWarning";
    public static final String FEATURE_SKIP_ORACLE_RECYCLEBIN_TABLES =
        "http://www.dbunit.org/features/skipOracleRecycleBinTables";

    /**
     * A list of all properties as {@link ConfigProperty} objects. 
     * The objects contain the allowed java type and whether or not a property is nullable.
     */
    public static final ConfigProperty[] ALL_PROPERTIES = new ConfigProperty[] {
        new ConfigProperty(PROPERTY_STATEMENT_FACTORY, IStatementFactory.class, false),
        new ConfigProperty(PROPERTY_RESULTSET_TABLE_FACTORY, IResultSetTableFactory.class, false),
        new ConfigProperty(PROPERTY_DATATYPE_FACTORY, IDataTypeFactory.class, false),
        new ConfigProperty(PROPERTY_ESCAPE_PATTERN, String.class, true),
        new ConfigProperty(PROPERTY_TABLE_TYPE, String[].class, false),
        new ConfigProperty(PROPERTY_PRIMARY_KEY_FILTER, IColumnFilter.class, true),
        new ConfigProperty(PROPERTY_BATCH_SIZE, Integer.class, false),
        new ConfigProperty(PROPERTY_FETCH_SIZE, Integer.class, false),
        new ConfigProperty(PROPERTY_METADATA_HANDLER, IMetadataHandler.class, false),
        new ConfigProperty(FEATURE_CASE_SENSITIVE_TABLE_NAMES, Object.class, false),
        new ConfigProperty(FEATURE_QUALIFIED_TABLE_NAMES, Object.class, false),
        new ConfigProperty(FEATURE_BATCHED_STATEMENTS, Object.class, false),
        new ConfigProperty(FEATURE_DATATYPE_WARNING, Object.class, false),
        new ConfigProperty(FEATURE_SKIP_ORACLE_RECYCLEBIN_TABLES, Object.class, false),
    };

    /**
     * A list of all features as strings
     */
    public static final String[] ALL_FEATURES = new String[] {
        FEATURE_CASE_SENSITIVE_TABLE_NAMES,
        FEATURE_QUALIFIED_TABLE_NAMES,
        FEATURE_BATCHED_STATEMENTS,
        FEATURE_DATATYPE_WARNING,
        FEATURE_SKIP_ORACLE_RECYCLEBIN_TABLES
    };
    
    private static final DefaultDataTypeFactory DEFAULT_DATA_TYPE_FACTORY =
            new DefaultDataTypeFactory();
    private static final PreparedStatementFactory PREPARED_STATEMENT_FACTORY =
            new PreparedStatementFactory();
    private static final CachedResultSetTableFactory RESULT_SET_TABLE_FACTORY =
            new CachedResultSetTableFactory();
    private static final String DEFAULT_ESCAPE_PATTERN = null;
    private static final String[] DEFAULT_TABLE_TYPE = {"TABLE"};
    private static final Integer DEFAULT_BATCH_SIZE = new Integer(100);
    private static final Integer DEFAULT_FETCH_SIZE = new Integer(100);



    private Map _propertyMap = new HashMap();
    
    private final Configurator configurator;

    public DatabaseConfig()
    {
        setFeature(FEATURE_BATCHED_STATEMENTS, false);
        setFeature(FEATURE_QUALIFIED_TABLE_NAMES, false);
        setFeature(FEATURE_CASE_SENSITIVE_TABLE_NAMES, false);
        setFeature(FEATURE_DATATYPE_WARNING, true);

        setProperty(PROPERTY_STATEMENT_FACTORY, PREPARED_STATEMENT_FACTORY);
        setProperty(PROPERTY_RESULTSET_TABLE_FACTORY, RESULT_SET_TABLE_FACTORY);
        setProperty(PROPERTY_DATATYPE_FACTORY, DEFAULT_DATA_TYPE_FACTORY);
        setProperty(PROPERTY_ESCAPE_PATTERN, DEFAULT_ESCAPE_PATTERN);
        setProperty(PROPERTY_TABLE_TYPE, DEFAULT_TABLE_TYPE);
        setProperty(PROPERTY_BATCH_SIZE, DEFAULT_BATCH_SIZE);
        setProperty(PROPERTY_FETCH_SIZE, DEFAULT_FETCH_SIZE);
        setProperty(PROPERTY_METADATA_HANDLER, new DefaultMetadataHandler());

        this.configurator = new Configurator(this);
    }

    /**
     * @return The configurator of this database config
     */
    protected Configurator getConfigurator() 
    {
        return configurator;
    }

    /**
     * Set the value of a feature flag.
     *
     * @param name the feature id
     * @param value the feature status
     */
    public void setFeature(String name, boolean value)
    {
        logger.trace("setFeature(name={}, value={}) - start", name, String.valueOf(value));

        setProperty(name, Boolean.valueOf(value));
    }

    /**
     * Look up the value of a feature flag.
     *
     * @param name the feature id
     * @return the feature status
     */
    public boolean getFeature(String name)
    {
        logger.trace("getFeature(name={}) - start", name);
        
        Object property = getProperty(name);
        if(property == null)
        {
            return false;
        }
        else if(property instanceof Boolean)
        {
            Boolean feature = (Boolean) property;
            return feature.booleanValue();
        }
        else
        {
            String propString = String.valueOf(property);
            Boolean feature = Boolean.valueOf(propString);
            return feature.booleanValue();
        }
    }

    /**
     * Set the value of a property.
     *
     * @param name the property id
     * @param value the property value
     */
    public void setProperty(String name, Object value)
    {
        logger.trace("setProperty(name={}, value={}) - start", name, value);
        
        // Validate if the type of the given object is correct
        checkObjectAllowed(name, value);
        
        // If we get here the type is allowed (no exception was thrown)
        _propertyMap.put(name, value);
    }

    /**
     * Look up the value of a property.
     *
     * @param name the property id
     * @return the property value
     */
    public Object getProperty(String name)
    {
        logger.trace("getProperty(name={}) - start", name);

        return _propertyMap.get(name);
    }

    /**
     * Checks whether the given value has the correct java type for the given property.
     * If the value is not allowed for the given property an {@link IllegalArgumentException} is thrown.
     * @param property The property to be set
     * @param value The value to which the property should be set
     */
    protected void checkObjectAllowed(String property, Object value)
    {
        logger.trace("checkObjectAllowed(property={}, value={}) - start", property, value);

        ConfigProperty prop = findByName(property);
        
        if(prop != null)
        {
            // First check for null
            if(value == null)
            {
                if(prop.isNullable())
                {
                    // All right. No class check is needed
                    return;
                }
                else
                {
                    throw new IllegalArgumentException("The property '" + property + "' is not nullable.");
                }
            }
            else
            {
                Class allowedPropType = prop.getPropertyType();
                if(!allowedPropType.isAssignableFrom(value.getClass()))
                {
                    throw new IllegalArgumentException("Cannot cast object of type '" + value.getClass() + 
                            "' to allowed type '" + allowedPropType + "'.");
                }
            }
        }
        else
        {
            logger.info("Unknown property '" + property + "'. Cannot validate the type of the object to be set." +
                    " Please notify a developer to update the list of properties.");
        }
    }
    
    /**
     * Searches the {@link ConfigProperty} object for the property with the given name
     * @param property The property for which the enumerated object should be resolved
     * @return The property object or <code>null</code> if it was not found.
     */
    public static final ConfigProperty findByName(String property) 
    {
        for (int i = 0; i < ALL_PROPERTIES.length; i++) {
            if(ALL_PROPERTIES[i].getProperty().equals(property))
            {
                return ALL_PROPERTIES[i];
            }
        }
        // property not found.
        return null;
    }
    
    public String toString()
    {
    	StringBuffer sb = new StringBuffer();
    	sb.append(getClass().getName()).append("[");
    	sb.append(", _propertyMap=").append(_propertyMap);
    	sb.append("]");
    	return sb.toString();
    }
    

    
    
    /**
     * @author gommma (gommma AT users.sourceforge.net)
     * @author Last changed by: $Author$
     * @version $Revision$ $Date$
     * @since 2.4.0
     */
    public static class ConfigProperty
    {
        private String property;
        private Class propertyType;
        private boolean nullable;
        
        public ConfigProperty(String property, Class propertyType, boolean nullable) {
            super();
            
            if (property == null) {
                throw new NullPointerException(
                        "The parameter 'property' must not be null");
            }
            if (propertyType == null) {
                throw new NullPointerException(
                        "The parameter 'propertyType' must not be null");
            }
            
            this.property = property;
            this.propertyType = propertyType;
            this.nullable = nullable;
        }
        
        public String getProperty() {
            return property;
        }

        public Class getPropertyType() {
            return propertyType;
        }

        public boolean isNullable() {
            return nullable;
        }

        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result
                    + ((property == null) ? 0 : property.hashCode());
            return result;
        }

        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            ConfigProperty other = (ConfigProperty) obj;
            if (property == null) {
                if (other.property != null)
                    return false;
            } else if (!property.equals(other.property))
                return false;
            return true;
        }

        public String toString()
        {
            StringBuffer sb = new StringBuffer();
            sb.append(getClass().getName()).append("[");
            sb.append("property=").append(property);
            sb.append(", propertyType=").append(propertyType);
            sb.append(", nullable=").append(nullable);
            sb.append("]");
            return sb.toString();
        }
    }
    
    
    
    /**
     * Sets parameters stored in the {@link DatabaseConfig} on specific java objects like {@link Statement}.
     * Is mainly there to avoid code duplication where {@link DatabaseConfig} parameters are used.
     * @author gommma (gommma AT users.sourceforge.net)
     * @author Last changed by: $Author$
     * @version $Revision$ $Date$
     * @since 2.4.4
     */
    protected static class Configurator
    {
        /**
         * Logger for this class
         */
        private static final Logger logger = LoggerFactory.getLogger(Configurator.class);

        private DatabaseConfig config;
        
        /**
         * @param config The configuration to be used by this configurator
         * @since 2.4.4
         */
        public Configurator(DatabaseConfig config)
        {
            if (config == null) {
                throw new NullPointerException(
                        "The parameter 'config' must not be null");
            }
            this.config = config;
        }
        /**
         * Configures the given statement so that it has the properties that are configured in this {@link DatabaseConfig}.
         * @param stmt The statement to be configured.
         * @throws SQLException
         * @since 2.4.4
         */
        void configureStatement(Statement stmt) throws SQLException 
        {
            logger.trace("configureStatement(stmt={}) - start", stmt);
            Integer fetchSize = (Integer) config.getProperty(DatabaseConfig.PROPERTY_FETCH_SIZE);
            stmt.setFetchSize(fetchSize.intValue());
            logger.debug("Statement fetch size set to {}",fetchSize);
        }
        
    }

}
