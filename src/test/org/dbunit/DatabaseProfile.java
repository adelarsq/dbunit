/*
 * DatabaseProfile.java   Feb 20, 2002
 *
 * The dbUnit database testing framework.
 * Copyright (C) 2002   Manuel Laflamme
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

package org.dbunit;

import java.util.Properties;

/**
 * @author Manuel Laflamme
 * @version 1.0
 */
public class DatabaseProfile
{
    private static final String PROFILE_PREFIX = "dbunit.profile";

    private static final String DRIVER_CLASS = "driverClass";
    private static final String CONNECTION_URL = "connectionUrl";
    private static final String SCHEMA = "schema";
    private static final String USER = "user";
    private static final String PASSWORD = "password";
    private static final String PRIMARY_KEY_SUPPORT = "primaryKey";

    private final Properties _properties;

    public DatabaseProfile(Properties properties)
    {
        _properties = properties;
    }

    private String getPropertyKey(String name)
    {
        return PROFILE_PREFIX + "." + getActiveProfile() + "." + name;
    }

    public String getActiveProfile()
    {
        return _properties.getProperty(PROFILE_PREFIX);
    }

    public String getDriverClass()
    {
        return _properties.getProperty(getPropertyKey(DRIVER_CLASS));
    }

    public String getConnectionUrl()
    {
        return _properties.getProperty(getPropertyKey(CONNECTION_URL));
    }

    public String getSchema()
    {
        return _properties.getProperty(getPropertyKey(SCHEMA), null);
    }

    public String getUser()
    {
        return _properties.getProperty(getPropertyKey(USER));
    }

    public String getPassword()
    {
        return _properties.getProperty(getPropertyKey(PASSWORD));
    }

    public PrimaryKeySupport getPrimaryKeySupport()
    {
        String value = _properties.getProperty(
                getPropertyKey(PRIMARY_KEY_SUPPORT));
        return PrimaryKeySupport.forName(value);
    }
}
