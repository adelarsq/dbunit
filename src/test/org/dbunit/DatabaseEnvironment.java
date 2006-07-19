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

package org.dbunit;

import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.XmlDataSet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

/**
 * @author Manuel Laflamme
 * @version $Revision$
 * @since Feb 18, 2002
 */
public class DatabaseEnvironment
{
    private static DatabaseEnvironment INSTANCE = null;

    private DatabaseProfile _profile = null;
    private IDatabaseConnection _connection = null;
    private IDataSet _dataSet = null;
    private IDatabaseTester _databaseTester = null;

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
            else if (profileName.equals("oracle"))
            {
                INSTANCE = new OracleEnvironment(profile);
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
        File file = new File("src/xml/dataSetTest.xml");
        _dataSet = new XmlDataSet(new FileReader(file));
        _databaseTester = new JdbcDatabaseTester( _profile.getDriverClass(),
            _profile.getConnectionUrl(), _profile.getUser(), _profile.getPassword() );
        _databaseTester.setSchema( _profile.getSchema() );
    }

    public IDatabaseConnection getConnection() throws Exception
    {
        if (_connection == null)
        {
            String name = _profile.getDriverClass();
            Class.forName(name);
            Connection connection = DriverManager.getConnection(
                    _profile.getConnectionUrl(), _profile.getUser(),
                    _profile.getPassword());
            _connection = new DatabaseConnection(connection,
                    _profile.getSchema());
        }
        return _connection;
    }
    
    public IDatabaseTester getDatabaseTester()
    {
        return _databaseTester;
    }

    public void closeConnection() throws Exception
    {
        if (_connection != null)
        {
            _connection.close();
            _connection = null;
        }
    }

    public IDataSet getInitDataSet() throws Exception
    {
        return _dataSet;
    }

    public DatabaseProfile getProfile() throws Exception
    {
        return _profile;
    }

    public boolean support(TestFeature feature)
    {
        String[] unsupportedFeatures = _profile.getUnsupportedFeatures();
        for (int i = 0; i < unsupportedFeatures.length; i++)
        {
            String unsupportedFeature = unsupportedFeatures[i];
            if (feature.toString().equals(unsupportedFeature))
            {
                return false;
            }
        }

        return true;
    }
}






