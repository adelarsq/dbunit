/*
 * PreparedBatchStatement.java   Mar 16, 2002
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

package org.dbunit.database.statement;

import java.sql.*;

import org.dbunit.dataset.datatype.DataType;
import org.dbunit.dataset.datatype.TypeCastException;

/**
 * @author Manuel Laflamme
 * @version 1.0
 */
public class SimplePreparedStatement extends AbstractPreparedBatchStatement
{
    private int _index;
    private int _result;

    SimplePreparedStatement(String sql, Connection connection)
            throws SQLException
    {
        super(sql, connection);
        _index = 0;
        _result = 0;
    }

    ////////////////////////////////////////////////////////////////////////////
    // IPreparedBatchStatement interface

    public void addValue(Object value, DataType dataType)
            throws TypeCastException, SQLException
    {
        _statement.setObject(_index + 1, dataType.typeCast(value), dataType.getSqlType());
    }

    public void addBatch() throws SQLException
    {
        _result += _statement.executeUpdate();
    }

    public int executeBatch() throws SQLException
    {
        int result = _result;
        clearBatch();
        return result;
    }

    public void clearBatch() throws SQLException
    {
        _statement.clearParameters();
        _index = 0;
        _result = 0;
    }

}

