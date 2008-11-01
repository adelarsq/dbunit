/*
 *
 * The DbUnit Database Testing Framework
 * Copyright (C)2002-2008, DbUnit.org
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
package org.dbunit.ant;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.taskdefs.Property;
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConfig.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The database configuration for the ant task.
 * 
 * @author gommma (gommma AT users.sourceforge.net)
 * @author Last changed by: $Author$
 * @version $Revision$ $Date$
 * @since 2.4.0
 */
public class DbConfig extends ProjectComponent
{
    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(AbstractStep.class);

    private Set properties = new HashSet();
    private Set features = new HashSet();
    
    public DbConfig()
    {
    }

    public void addProperty(Property property)
    {
        logger.debug("addProperty(property={}) - start)", property);
        
        this.properties.add(property);
    }

    public void addFeature(Feature feature)
    {
        logger.debug("addFeature(feature={}) - start)", feature);
        
        this.features.add(feature);
    }

    /**
     * Copies the parameters set in this configuration via ant into the given
     * {@link DatabaseConfig} that is used by the dbunit connection.
     * @param config The configuration object to be initialized/updated
     * @throws DatabaseUnitException 
     */
    public void copyTo(DatabaseConfig config) throws DatabaseUnitException 
    {
        // Features
        for (Iterator iterator = this.features.iterator(); iterator.hasNext();) {
            Feature feature = (Feature) iterator.next();
            String fullFeatureName = findFeatureByShortName(feature.getName());
            config.setFeature(fullFeatureName, feature.isValue());
        }
        
        // Properties
        for (Iterator iterator = this.properties.iterator(); iterator.hasNext();) {
            Property prop = (Property) iterator.next();
            
            String propName = prop.getName();
            String propValue = prop.getValue();

            ConfigProperty dbunitProp = findByShortName(propName);
            if(dbunitProp == null)
            {
                logger.info("Could not set property '" + prop + "'");
            }
            else
            {
                String fullPropName = dbunitProp.getProperty();
                config.setProperty(fullPropName, createObjectFromString(dbunitProp, propValue));
            }
        }
    }

    private Object createObjectFromString(ConfigProperty dbunitProp, String propValue) 
    throws DatabaseUnitException 
    {
        if (dbunitProp == null) {
            throw new NullPointerException(
                    "The parameter 'dbunitProp' must not be null");
        }
        if (propValue == null) {
            // Null must not be casted
            return null;
        }
        
        Class targetClass = dbunitProp.getPropertyType();
        if(targetClass == String.class)
        {
            return propValue;
        }
        else if(targetClass == String[].class)
        {
            String[] result = propValue.split(",");
            for (int i = 0; i < result.length; i++) {
                result[i] = result[i].trim();
            }
            return result;
        }
        else if(targetClass == Integer.class)
        {
            return new Integer(propValue);
        }
        else
        {
            // Try via reflection
            return createInstance(propValue);
        }
    }

    private Object createInstance(String className) throws DatabaseUnitException 
    {
        // Setup data type factory
        try
        {
            Object o = Class.forName(className).newInstance();
            return o;
        }
        catch (ClassNotFoundException e)
        {
            throw new DatabaseUnitException(
                    "Class Not Found: '" + className + "' could not be loaded", e);
        }
        catch (IllegalAccessException e)
        {
            throw new DatabaseUnitException(
                    "Illegal Access: '" + className + "' could not be loaded", e);
        }
        catch (InstantiationException e)
        {
            throw new DatabaseUnitException(
                    "Instantiation Exception: '" + className + "' could not be loaded", e);
        }
    }

    private ConfigProperty findByShortName(String propShortName) 
    {
        for (int i = 0; i < DatabaseConfig.ALL_PROPERTIES.length; i++) {
            String fullProperty = DatabaseConfig.ALL_PROPERTIES[i].getProperty();
            if(fullProperty.endsWith(propShortName))
            {
                return DatabaseConfig.ALL_PROPERTIES[i];
            }
        }
        // Property not found
        logger.info("The property ending with '" + propShortName + "' was not found. " +
        		"Please notify a dbunit developer to add the property to the " + DatabaseConfig.class);
        return null;
    }
    
    private String findFeatureByShortName(String featureShortName)
    {
        for (int i = 0; i < DatabaseConfig.ALL_FEATURES.length; i++) {
            if(DatabaseConfig.ALL_FEATURES[i].endsWith(featureShortName))
            {
                return DatabaseConfig.ALL_FEATURES[i];
            }
        }
        // Property not found
        logger.info("The feature ending with '" + featureShortName + "' was not found. " +
                "Please notify a dbunit developer to add the feature to the " + DatabaseConfig.class);
        return null;

    }
    
    
    /**
     * @author gommma (gommma AT users.sourceforge.net)
     * @author Last changed by: $Author$
     * @version $Revision$ $Date$
     * @since 2.4.0
     */
    public static class Feature
    {
        private String name;
        private boolean value;
        
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public boolean isValue() {
            return value;
        }
        public void setValue(boolean value) {
            this.value = value;
        }
    }
}
