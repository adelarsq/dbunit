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
 */

package org.dbunit.operation.mssqlserver;
import org.dbunit.operation.DatabaseOperation;

/**
 * This class disable the MS SQL Server automatic identifier generation for
 * the execution of inserts.
 * <p>
 * If you are using the Microsoft driver (i.e.
 * <code>com.microsoft.jdbc.sqlserver.SQLServerDriver</code>), you'll need to
 * use the <code>SelectMethod=cursor</code> parameter in the JDBC connection
 * string. Your databaseUrl would look something like the following:
 * <p>
 * <code>jdbc:microsoft:sqlserver://localhost:1433;DatabaseName=mydb;SelectMethod=cursor</code>
 * <p>
 * Thanks to Jeremy Stein who have submited multiple patches.
 *
 * @author Manuel Laflamme
 * @author Eric Pugh
 * @version $Revision$
 * @since Apr 9, 2002
 * @deprecated Replaced by {@link org.dbunit.ext.mssql.InsertIdentityOperation}. Be warned, this class will eventually be removed.
 */
public class InsertIdentityOperation extends org.dbunit.ext.mssql.InsertIdentityOperation
{
    /**
     * Creates a new InsertIdentityOperation object that decorates the
     * specified operation.
     */
    public InsertIdentityOperation(DatabaseOperation operation)
    {
        super(operation);
    }
}






