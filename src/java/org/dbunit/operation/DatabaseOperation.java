/*
 * DatabaseOperation.java   Feb 18, 2002
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

package org.dbunit.operation;

import java.sql.SQLException;

import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.IDataSet;

/**
 * @author Manuel Laflamme
 * @version 1.0
 */
public abstract class DatabaseOperation
{
    public static final DatabaseOperation NONE = new DummyAction();
    public static final DatabaseOperation UPDATE = new UpdateOperation();
    public static final DatabaseOperation INSERT = new InsertOperation();
    public static final DatabaseOperation REFRESH = new RefreshOperation();
    public static final DatabaseOperation DELETE = new DeleteOperation();
    public static final DatabaseOperation DELETE_ALL = new DeleteAllOperation();
    public static final DatabaseOperation CLEAN_INSERT = new CompositeOperation(
            DELETE_ALL, INSERT);

    public abstract void execute(DatabaseConnection connection,
            IDataSet dataSet) throws DatabaseUnitException, SQLException;

    private static class DummyAction extends DatabaseOperation
    {
        public void execute(DatabaseConnection connection, IDataSet dataSet)
        {
        }
    }
}
