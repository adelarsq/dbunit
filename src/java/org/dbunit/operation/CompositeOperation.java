/*
 * CompositeOperation.java   Feb 18, 2002
 *
 * DbUnit Database Testing Framework
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

package org.dbunit.operation;

import java.sql.SQLException;

import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;

/**
 * This class is a composite that combines multiple database operation in a
 * single one.
 *
 * @author Manuel Laflamme
 * @version 1.0
 */
public class CompositeOperation extends DatabaseOperation
{
    private final DatabaseOperation[] _actions;

    /**
     * Creates a new composite operation combining the two specified operations.
     */
    public CompositeOperation(DatabaseOperation action1, DatabaseOperation action2)
    {
        _actions = new DatabaseOperation[]{action1, action2};
    }

    /**
     * Creates a new composite operation combining the specified operations.
     */
    public CompositeOperation(DatabaseOperation[] actions)
    {
        _actions = actions;
    }

    ////////////////////////////////////////////////////////////////////////////
    // DatabaseOperation class

    public void execute(IDatabaseConnection connection, IDataSet dataSet)
            throws DatabaseUnitException, SQLException
    {
        for (int i = 0; i < _actions.length; i++)
        {
            DatabaseOperation action = _actions[i];
            action.execute(connection, dataSet);
        }
    }
}



