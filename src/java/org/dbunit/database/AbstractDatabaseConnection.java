/*
 * AbstractDatabaseConnection.java   Mar 6, 2002
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

package org.dbunit.database;

import org.dbunit.DatabaseUnitRuntimeException;
import org.dbunit.database.statement.IStatementFactory;
import org.dbunit.dataset.*;
import org.dbunit.dataset.datatype.IDataTypeFactory;
import org.dbunit.dataset.datatype.DefaultDataTypeFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;

/**
 * @author Manuel Laflamme
 * @version $Revision$
 */
public abstract class AbstractDatabaseConnection implements IDatabaseConnection
{
    static final String STATEMENT_FACTORY = "dbunit.statement.factory";
    static final String DEFAULT_FACTORY =
            "org.dbunit.database.statement.PreparedStatementFactory";

    private final IStatementFactory _statementFactory;
    private final IDataTypeFactory _dataTypeFactory = new DefaultDataTypeFactory();
    private IDataSet _dataSet = null;

    public AbstractDatabaseConnection()
    {
        String className = System.getProperty(STATEMENT_FACTORY, DEFAULT_FACTORY);
        try
        {
            Constructor constructor = Class.forName(className).getConstructor(new Class[0]);
            _statementFactory = (IStatementFactory)constructor.newInstance(new Object[0]);
        }
        catch (NoSuchMethodException e)
        {
            throw new DatabaseUnitRuntimeException(e);
        }
        catch (ClassNotFoundException e)
        {
            throw new DatabaseUnitRuntimeException(e);
        }
        catch (InstantiationException e)
        {
            throw new DatabaseUnitRuntimeException(e);
        }
        catch (IllegalAccessException e)
        {
            throw new DatabaseUnitRuntimeException(e);
        }
        catch (IllegalArgumentException e)
        {
            throw new DatabaseUnitRuntimeException(e);
        }
        catch (InvocationTargetException e)
        {
            throw new DatabaseUnitRuntimeException(e);
        }
    }

    public AbstractDatabaseConnection(IStatementFactory factory)
    {
        _statementFactory = factory;
    }

    protected IDataTypeFactory getDataTypeFactory()
    {
        return _dataTypeFactory;
    }

    ////////////////////////////////////////////////////////////////////////////
    // IDatabaseConnection interface

    public IDataSet createDataSet() throws SQLException
    {
        if (_dataSet == null)
        {
            _dataSet = new DatabaseDataSet(this, getDataTypeFactory());
        }

        return _dataSet;
    }

    public IDataSet createDataSet(String[] tableNames) throws SQLException
    {
        return new FilteredDataSet(tableNames, createDataSet());
    }

    public ITable createQueryTable(String resultName, String sql)
            throws DataSetException, SQLException
    {
        Statement statement = getConnection().createStatement();
        try
        {
            ResultSet resultSet = statement.executeQuery(sql);

            try
            {
                ITableMetaData metaData = AbstractResultSetTable.createTableMetaData(
                        resultName, resultSet, getDataTypeFactory());
                return new CachedResultSetTable(metaData, resultSet);
            }
            finally
            {
                resultSet.close();
            }
        }
        finally
        {
            statement.close();
        }
    }

    public int getRowCount(String tableName) throws SQLException
    {
        return getRowCount(tableName, null);
    }

    public int getRowCount(String tableName, String whereClause) throws SQLException
    {
        StringBuffer sqlBuffer = new StringBuffer(128);
        sqlBuffer.append("select count(*) from ");
        sqlBuffer.append(tableName);
        if (whereClause != null)
        {
            sqlBuffer.append(" ");
            sqlBuffer.append(whereClause);
        }

        Statement statement = getConnection().createStatement();
        try
        {
            ResultSet resultSet = statement.executeQuery(sqlBuffer.toString());
            try
            {
                resultSet.next();
                return resultSet.getInt(1);
            }
            finally
            {
                resultSet.close();
            }
        }
        finally
        {
            statement.close();
        }
    }

    public IStatementFactory getStatementFactory()
    {
        return _statementFactory;
    }

}









