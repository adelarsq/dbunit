/*
 * PreparedBatchStatement.java   Mar 16, 2002
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

import org.dbunit.dataset.datatype.DataType;
import org.dbunit.dataset.datatype.TypeCastException;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author Manuel Laflamme
 * @version $Revision$
 */
public class SimplePreparedStatement extends AbstractPreparedBatchStatement
{
    private int _index;
    private int _result;

    public SimplePreparedStatement(String sql, Connection connection)
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
        // Special NULL handling
        if (value == null)
        {
            _statement.setNull(++_index, dataType.getSqlType());
            return;
        }

        // Special BLOB handling
        if (dataType == DataType.CLOB)
        {
            _statement.setObject(++_index, dataType.typeCast(value),
                    DataType.LONGVARCHAR.getSqlType());
            return;
        }

        // Special CLOB handling
        if (dataType == DataType.BLOB)
        {
            _statement.setObject(++_index, dataType.typeCast(value),
                    DataType.LONGVARBINARY.getSqlType());
            return;
        }

        _statement.setObject(++_index, dataType.typeCast(value), dataType.getSqlType());
    }

    public void addBatch() throws SQLException
    {
        _result += _statement.executeUpdate();
        _index = 0;
//        _statement.clearParameters();
    }

    public int executeBatch() throws SQLException
    {
        int result = _result;
        clearBatch();
        return result;
    }

    public void clearBatch() throws SQLException
    {
//        _statement.clearParameters();
        _index = 0;
        _result = 0;
    }

}






