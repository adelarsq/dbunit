/*
 * CompoundStatement.java   Feb 20, 2002
 *
 * The DbUnit Database Testing Framework
 * Copyright (C)2002, Manuel Laflamme
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

package org.dbunit.database.statement;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Manuel Laflamme
 * @version $Revision$
 */
public class SimpleStatement extends AbstractBatchStatement
{
    private final List _list = new ArrayList();

    SimpleStatement(Connection connection) throws SQLException
    {
        super(connection);
    }

    public void addBatch(String sql) throws SQLException
    {
        _list.add(sql);
    }

    public int executeBatch() throws SQLException
    {
        int result = 0;
        for (int i = 0; i < _list.size(); i++)
        {
            String sql = (String)_list.get(i);
            boolean r = _statement.execute(sql);
            if(!r)
            {
                result += _statement.getUpdateCount();
            }
        }
        return result;
    }

    public void clearBatch() throws SQLException
    {
        _list.clear();
    }
}





