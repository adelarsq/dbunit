/*
 * PreparedStatementFactory.java   Mar 20, 2002
 *
 * Copyright 2001 Karat Software Corp. All Rights Reserved.
 *
 * This software is the proprietary information of Karat Software Corp.
 * Use is subject to license terms.
 *
 */

package org.dbunit.database.statement;

import org.dbunit.database.IDatabaseConnection;

import java.sql.SQLException;
import java.sql.Connection;

/**
 * @author Manuel Laflamme
 * @version 1.0
 */
public class PreparedStatementFactory implements IStatementFactory
{
    public IBatchStatement createBatchStatement(IDatabaseConnection connection)
            throws SQLException
    {
        Connection jdbcConnection = connection.getConnection();
        if (jdbcConnection.getMetaData().supportsBatchUpdates())
        {
            return new BatchStatement(jdbcConnection);
        }
        else
        {
            return new SimpleStatement(jdbcConnection);
        }
    }

    public IPreparedBatchStatement createPreparedStatement(String sql,
            IDatabaseConnection connection) throws SQLException
    {
        Connection jdbcConnection = connection.getConnection();
        if (jdbcConnection.getMetaData().supportsBatchUpdates())
        {
            return new PreparedBatchStatement(sql, jdbcConnection);
        }
        else
        {
            return new SimplePreparedStatement(sql, jdbcConnection);
        }
    }
}
