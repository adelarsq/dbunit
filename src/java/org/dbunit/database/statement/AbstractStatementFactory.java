/*
 * Created by IntelliJ IDEA.
 * User: mlaflamm
 * Date: Apr 10, 2002
 * Time: 9:28:37 AM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */

package org.dbunit.database.statement;

import org.dbunit.database.IDatabaseConnection;

import java.sql.SQLException;

public abstract class AbstractStatementFactory implements IStatementFactory
{
    static final String SUPPORT_BATCH_STATEMENT = "dbunit.database.supportBatchStatement";

    /**
     * Returns <code>true</code> if target database supports batch statement.
     */
    protected boolean supportBatchStatement(IDatabaseConnection connection)
            throws SQLException
    {
        if (System.getProperty(SUPPORT_BATCH_STATEMENT, "true").equals("true"))
        {
            return connection.getConnection().getMetaData().supportsBatchUpdates();
        }

        return false;
    }
}

