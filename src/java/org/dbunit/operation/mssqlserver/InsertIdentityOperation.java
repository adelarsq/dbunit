/*
 * InsertIdentityOperation.java   Apr 9, 2002
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
 */

package org.dbunit.operation.mssqlserver;

import java.sql.*;

import org.dbunit.DatabaseUnitException;
import org.dbunit.operation.*;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.*;

/**
 * This class disable the MS SQL Server automatic identifier generation for
 * the execution of inserts.
 * <p>
 * Thanks to <a href="mailto:epugh@upstate.com">Eric Pugh</a> for having
 * submitted the original patch and for the beta testing.
 *
 * @author Manuel Laflamme
 * @version $Revision$
 */
public class InsertIdentityOperation extends InsertOperation
{
    public static final DatabaseOperation INSERT =
            new InsertIdentityOperation();

    public static final DatabaseOperation CLEAN_INSERT =
            new CompositeOperation(DatabaseOperation.DELETE_ALL, INSERT);

    public static final DatabaseOperation REFRESH =
            new RefreshOperation((InsertOperation)INSERT,
                    (UpdateOperation)DatabaseOperation.UPDATE);

    ////////////////////////////////////////////////////////////////////////////
    // AbstractBatchOperation class

    public OperationData getOperationData(String schemaName,
            ITableMetaData metaData) throws DataSetException
    {
        OperationData data = super.getOperationData(schemaName, metaData);
        String tableName = DataSetUtils.getQualifiedName(
                schemaName, metaData.getTableName());

        // enable IDENTITY_INSERT
        StringBuffer sqlBuffer = new StringBuffer(256);
        sqlBuffer.append("SET IDENTITY_INSERT ");
        sqlBuffer.append(tableName);
        sqlBuffer.append(" ON ");

        // original insert statement
        sqlBuffer.append(data.getSql());

        // disable IDENTITY_INSERT
        sqlBuffer.append(" SET IDENTITY_INSERT ");
        sqlBuffer.append(tableName);
        sqlBuffer.append(" OFF");

        return new OperationData(sqlBuffer.toString(), data.getColumns());
    }

}


