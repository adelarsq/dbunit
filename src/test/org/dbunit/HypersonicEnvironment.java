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

import java.io.*;
import java.sql.Connection;
import java.sql.Statement;

public class HypersonicEnvironment extends DatabaseEnvironment
{
    public HypersonicEnvironment(DatabaseProfile profile) throws Exception
    {
        super(profile);

        // Creates required tables into the hypersonic in-memory database
        BufferedReader sqlReader = new BufferedReader(new FileReader(
                new File("src/sql/hypersonic.sql")));
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

    public PrimaryKeySupport getPrimaryKeySupport() throws Exception
    {
        return PrimaryKeySupport.SINGLE;
    }

}
