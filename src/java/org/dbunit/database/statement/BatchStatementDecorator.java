/*
 * BatchStatementDecorator.java   Mar 16, 2002
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

import java.sql.SQLException;
import java.util.*;

import org.dbunit.dataset.DataSetUtils;
import org.dbunit.dataset.datatype.DataType;
import org.dbunit.dataset.datatype.TypeCastException;

/**
 * @author Manuel Laflamme
 * @version 1.0
 */
public class BatchStatementDecorator implements IPreparedBatchStatement
{
    private final IBatchStatement _statement;
    private final String[] _sqlTemplate;
    private StringBuffer _sqlBuffer;
    private int _index;

    BatchStatementDecorator(String sql, IBatchStatement statement)
    {
        List list = new ArrayList();
        StringTokenizer tokenizer = new StringTokenizer(sql, "?");
        while (tokenizer.hasMoreTokens())
        {
            list.add(tokenizer.nextToken());
        }

        if (sql.endsWith("?"))
        {
            list.add("");
        }

        _sqlTemplate = (String[])list.toArray(new String[0]);
        _statement = statement;

        // reset sql buffer
        _index = 0;
        _sqlBuffer = new StringBuffer(_sqlTemplate[_index++]);
    }

    ////////////////////////////////////////////////////////////////////////////
    // IPreparedBatchStatement interface

    public void addValue(Object value, DataType dataType)
            throws TypeCastException, SQLException
    {
        _sqlBuffer.append(DataSetUtils.getSqlValueString(value, dataType));
        _sqlBuffer.append(_sqlTemplate[_index++]);
    }

    public void addBatch() throws SQLException
    {
        _statement.addBatch(_sqlBuffer.toString());

        // reset sql buffer
        _index = 0;
        _sqlBuffer = new StringBuffer(_sqlTemplate[_index++]);
    }

    public int executeBatch() throws SQLException
    {
        return _statement.executeBatch();
    }

    public void clearBatch() throws SQLException
    {
        _statement.clearBatch();

        // reset sql buffer
        _index = 0;
        _sqlBuffer = new StringBuffer(_sqlTemplate[_index++]);
    }

    public void close() throws SQLException
    {
        _statement.close();
    }
}
