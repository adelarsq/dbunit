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
import org.dbunit.database.DatabaseConfig;

import java.sql.SQLException;

public abstract class AbstractStatementFactory implements IStatementFactory
{
    /**
     * Returns <code>true</code> if target database supports batch statement.
     */
    protected boolean supportBatchStatement(IDatabaseConnection connection)
            throws SQLException
    {
        if (connection.getConfig().getFeature(DatabaseConfig.FEATURE_BATCHED_STATEMENTS))
        {
            return connection.getConnection().getMetaData().supportsBatchUpdates();
        }

        return false;
    }
}

