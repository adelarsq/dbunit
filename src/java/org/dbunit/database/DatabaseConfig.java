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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.dbunit.database.statement.PreparedStatementFactory;
import org.dbunit.dataset.datatype.DefaultDataTypeFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author manuel.laflamme
 * @since Jul 17, 2003
 * @version $Revision$
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

    public static final String FEATURE_QUALIFIED_TABLE_NAMES =
            "http://www.dbunit.org/features/qualifiedTableNames";
    public static final String FEATURE_BATCHED_STATEMENTS =
            "http://www.dbunit.org/features/batchedStatements";
    public static final String FEATURE_DATATYPE_WARNING =
            "http://www.dbunit.org/features/datatypeWarning";
    public static final String FEATURE_SKIP_ORACLE_RECYCLEBIN_TABLES =
            "http://www.dbunit.org/features/skipOracleRecycleBinTables";

    private static final DefaultDataTypeFactory DEFAULT_DATA_TYPE_FACTORY =
            new DefaultDataTypeFactory();
    private static final PreparedStatementFactory PREPARED_STATEMENT_FACTORY =
            new PreparedStatementFactory();
    private static final CachedResultSetTableFactory RESULT_SET_TABLE_FACTORY =
            new CachedResultSetTableFactory();
    private static final String DEFAULT_ESCAPE_PATTERN = null;
    private static final String[] DEFAULT_TABLE_TYPE = {"TABLE"};

    private Set _featuresSet = new HashSet();
    private Map _propertyMap = new HashMap();

    public DatabaseConfig()
    {
        setFeature(FEATURE_BATCHED_STATEMENTS, false);
        setFeature(FEATURE_QUALIFIED_TABLE_NAMES, false);
        setFeature(FEATURE_DATATYPE_WARNING, true);

        setProperty(PROPERTY_STATEMENT_FACTORY, PREPARED_STATEMENT_FACTORY);
        setProperty(PROPERTY_RESULTSET_TABLE_FACTORY, RESULT_SET_TABLE_FACTORY);
        setProperty(PROPERTY_DATATYPE_FACTORY, DEFAULT_DATA_TYPE_FACTORY);
        setProperty(PROPERTY_ESCAPE_PATTERN, DEFAULT_ESCAPE_PATTERN);
        setProperty(PROPERTY_TABLE_TYPE, DEFAULT_TABLE_TYPE);
    }

    /**
     * Set the value of a feature flag.
     *
     * @param name the feature id
     * @param value the feature status
     */
    public void setFeature(String name, boolean value)
    {
        logger.debug("setFeature(name=" + name + ", value=" + value + ") - start");

        if (value)
        {
            _featuresSet.add(name);
        }
        else
        {
            _featuresSet.remove(name);
        }
    }

    /**
     * Look up the value of a feature flag.
     *
     * @param name the feature id
     * @return the feature status
     */
    public boolean getFeature(String name)
    {
        logger.debug("getFeature(name=" + name + ") - start");

        return _featuresSet.contains(name);
    }

    /**
     * Set the value of a property.
     *
     * @param name the property id
     * @param value the property value
     */
    public void setProperty(String name, Object value)
    {
        logger.debug("setProperty(name=" + name + ", value=" + value + ") - start");

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
        logger.debug("getProperty(name=" + name + ") - start");

       return _propertyMap.get(name);
    }


}
