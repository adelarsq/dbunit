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
 *
 */

package org.dbunit.ant;

import org.dbunit.DatabaseUnitException;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.ext.mssql.InsertIdentityOperation;
import org.dbunit.operation.DatabaseOperation;
import org.dbunit.operation.TransactionOperation;

import java.io.File;
import java.sql.SQLException;

/**
 * The <code>Operation</code> class is the step that defines which
 * operation will be performed in the execution of the <code>DbUnitTask</code>
 * task.
 *
 * @author Timothy Ruppert
 * @author Ben Cox
 * @version $Revision$
 * @since Jun 10, 2002
 * @see org.dbunit.ant.DbUnitTaskStep
 */
public class Operation extends AbstractStep
{
    private static final String DEFAULT_FORMAT = FORMAT_FLAT;

    protected String _type = "CLEAN_INSERT";
    private String _format;
    private File _src;
    private boolean _transaction = false;
    private DatabaseOperation _operation;
    private boolean _forwardOperation = true;

    public String getType()
    {
        return _type;
    }

    public File getSrc()
    {
        return _src;
    }

    public DatabaseOperation getDbOperation()
    {
        return _operation;
    }

    public String getFormat()
    {
        return _format != null ? _format : DEFAULT_FORMAT;
    }

    public boolean isTransaction()
    {
        return _transaction;
    }

    public void setType(String type)
    {
        if ("UPDATE".equals(type))
        {
            _operation = DatabaseOperation.UPDATE;
            _forwardOperation = true;
        }
        else if ("INSERT".equals(type))
        {
            _operation = DatabaseOperation.INSERT;
            _forwardOperation = true;
        }
        else if ("REFRESH".equals(type))
        {
            _operation = DatabaseOperation.REFRESH;
            _forwardOperation = true;
        }
        else if ("DELETE".equals(type))
        {
            _operation = DatabaseOperation.DELETE;
            _forwardOperation = false;
        }
        else if ("DELETE_ALL".equals(type))
        {
            _operation = DatabaseOperation.DELETE_ALL;
            _forwardOperation = false;
        }
        else if ("CLEAN_INSERT".equals(type))
        {
            _operation = DatabaseOperation.CLEAN_INSERT;
            _forwardOperation = false;
        }
        else if ("NONE".equals(type))
        {
            _operation = DatabaseOperation.NONE;
            _forwardOperation = true;
        }
        else if ("MSSQL_CLEAN_INSERT".equals(type))
        {
            _operation = InsertIdentityOperation.CLEAN_INSERT;
            _forwardOperation = false;
        }
        else if ("MSSQL_INSERT".equals(type))
        {
            _operation = InsertIdentityOperation.INSERT;
            _forwardOperation = true;
        }
        else if ("MSSQL_REFRESH".equals(type))
        {
            _operation = InsertIdentityOperation.REFRESH;
            _forwardOperation = true;
        }
        else
        {
            throw new IllegalArgumentException("Type must be one of: UPDATE, INSERT,"
                    + " REFRESH, DELETE, DELETE_ALL, CLEAN_INSERT, MSSQL_INSERT, "
                    + " or MSSQL_REFRESH but was: " + type);
        }
        _type = type;
    }

    public void setSrc(File src)
    {
        _src = src;
    }

    public void setFormat(String format)
    {
        if (format.equalsIgnoreCase(FORMAT_FLAT)
                || format.equalsIgnoreCase(FORMAT_XML)
                || format.equalsIgnoreCase(FORMAT_CSV)
        )
        {
            _format = format;
        }
        else
        {
            throw new IllegalArgumentException("Type must be either 'flat'(default), 'xml' or 'csv' but was: " + format);
        }
    }

    public void setTransaction(boolean transaction)
    {
        _transaction = transaction;
    }

    public void execute(IDatabaseConnection connection) throws DatabaseUnitException
    {
        if (_operation == null)
        {
            throw new DatabaseUnitException("Operation.execute(): setType(String) must be called before execute()!");
        }

        if (_operation == DatabaseOperation.NONE)
        {
            return;
        }

        try
        {
        	    DatabaseOperation operation = (_transaction ? new TransactionOperation(_operation) : _operation);
            IDataSet dataset = getSrcDataSet(getSrc(), getFormat(), _forwardOperation);
            operation.execute(connection, dataset);
        }
        catch (SQLException e)
        {
            throw new DatabaseUnitException(e);
        }
    }

    public String getLogMessage()
    {
        return "Executing operation: " + _type
                + "\n          on   file: " + ((_src == null) ? null : _src.getAbsolutePath())
                + "\n          with format: " + _format;
    }


    public String toString()
    {
        StringBuffer result = new StringBuffer();
        result.append("Operation: ");
        result.append(" type=" + _type);
        result.append(", format=" + _format);
        result.append(", src=" + _src == null ? null : _src.getAbsolutePath());
        result.append(", operation = " + _operation);

        return result.toString();
    }
}

