/*
 * HypersonicEnvironment.java   Feb 18, 2002
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

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.XmlDataSet;

public class DatabaseEnvironment
{
    private static DatabaseEnvironment INSTANCE = null;

    private DatabaseProfile _profile = null;
    private DatabaseConnection _connection = null;
    private IDataSet _dataSet = null;

    public static DatabaseEnvironment getInstance() throws Exception
    {
        if (INSTANCE == null)
        {
            Properties properties = new Properties();
            properties.load(new FileInputStream(new File("profile.properties")));
            DatabaseProfile profile = new DatabaseProfile(properties);

            String profileName = profile.getActiveProfile();
            if (profileName == null || profileName.equals("hypersonic"))
            {
                INSTANCE = new HypersonicEnvironment(profile);
            }
            else
            {
                INSTANCE = new DatabaseEnvironment(profile);
            }
        }

        return INSTANCE;
    }

    public DatabaseEnvironment(DatabaseProfile profile) throws Exception
    {
        _profile = profile;

        String name = profile.getDriverClass();
        Class driverClass = Class.forName(name);
        Connection connection = DriverManager.getConnection(
                _profile.getConnectionUrl(), _profile.getUser(),
                _profile.getPassword());
        _connection = new DatabaseConnection(connection,
                _profile.getSchema());

        File file = new File("src/xml/dataSetTest.xml");
        _dataSet = new XmlDataSet(new FileInputStream(file));
    }

    public DatabaseConnection getConnection() throws Exception
    {
        return _connection;
    }

//    public String getSchema() throws Exception
//    {
//        return _profile.getSchema();
//    }

    public IDataSet getInitDataSet() throws Exception
    {
        return _dataSet;
    }

    public PrimaryKeySupport getPrimaryKeySupport() throws Exception
    {
        return _profile.getPrimaryKeySupport();
    }

}
