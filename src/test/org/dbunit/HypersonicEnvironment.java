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

import org.dbunit.operation.DatabaseOperation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author Manuel Laflamme
 * @version $Revision$
 * @since Feb 18, 2002
 */
public class HypersonicEnvironment extends DatabaseEnvironment
{
  public HypersonicEnvironment(DatabaseProfile profile) throws Exception
  {
      super(profile);

      // Creates required tables into the hypersonic in-memory database
      File ddlFile = new File("src/sql/hypersonic.sql");
      Connection connection = getConnection().getConnection();

      executeDdlFile(ddlFile, connection);

  }

  public static void executeDdlFile(File ddlFile, Connection connection) throws Exception
  {
      BufferedReader sqlReader = new BufferedReader(new FileReader(ddlFile));
      StringBuffer sqlBuffer = new StringBuffer();
      while (sqlReader.ready())
      {
          String line = sqlReader.readLine();
          if (!line.startsWith("-"))
          {
              sqlBuffer.append(line);
          }
      }

      String sql = sqlBuffer.toString();
      executeSql( connection, sql );
  }
  
  public static void executeSql( Connection connection, String sql ) throws SQLException {
      Statement statement = connection.createStatement();
      try
      {
          statement.execute(sql);
      }
      finally
      {
          statement.close();
      }
  }

  public static Connection createJdbcConnection(String databaseName) throws Exception
  {
      Class.forName("org.hsqldb.jdbcDriver");
      Connection connection = DriverManager.getConnection(
              "jdbc:hsqldb:" + databaseName, "sa", "");
      return connection;
  }

  public void closeConnection() throws Exception
  {
      DatabaseOperation.DELETE_ALL.execute(getConnection(), getInitDataSet());
  }

  public static void shutdown(Connection connection) throws SQLException {
    executeSql( connection, "SHUTDOWN IMMEDIATELY" );      
  }

  public static void deleteFiles(final String filename) {
    File[] files = new File(".").listFiles(new FilenameFilter()
        {
            public boolean accept(File dir, String name)
            {
                if (name.indexOf(filename) != -1)
                {
                    return true;
                }
                return false;
            }
        });

for (int i = 0; i < files.length; i++)
{
    File file = files[i];
    file.delete();
}
  }
  
}



