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
import java.sql.Connection;
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
        BufferedReader sqlReader = new BufferedReader(
                new FileReader(new File("src/sql/hypersonic.sql")));
        StringBuffer sqlBuffer = new StringBuffer();
        while (sqlReader.ready())
        {
            String line = sqlReader.readLine();
            if (!line.startsWith("-"))
            {
                sqlBuffer.append(line);
            }
        }

        Connection connection = getConnection().getConnection();
        Statement statement = connection.createStatement();
        try
        {
            String sql = sqlBuffer.toString();
            statement.execute(sql);
        }
        finally
        {
            statement.close();
        }
    }

    public void closeConnection() throws Exception
    {
        DatabaseOperation.DELETE_ALL.execute(getConnection(), getInitDataSet());
    }
}






