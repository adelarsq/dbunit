/*
 * Operation.java    Mar 24, 2002
 *
 * The DbUnit Database Testing Framework
 * Copyright (C)2002, Timothy Ruppert && Ben Cox
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
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.XmlDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.dbunit.operation.mssqlserver.InsertIdentityOperation;

import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * The <code>Operation</code> class is the step that defines which
 * operation will be performed in the execution of the <code>DbUnitTask</code>
 * task.
 *
 * @author Timothy Ruppert && Ben Cox
 * @version $Revision$
 * @see org.dbunit.ant.DbUnitTaskStep
 */
public class Operation implements DbUnitTaskStep
{
  
    protected String type;
    private final String DEFAULT_FORMAT = "flat";
    private String format;
    private File src;
    private DatabaseOperation dbOperation;

    public Operation()
    {
        this.type = "CLEAN_INSERT";
    }

    public String getType()
    {
        return type;
    }

    public File getSrc()
    {
        return src;
    }

    public DatabaseOperation getDbOperation()
    {
        return dbOperation;
    }

    public String getFormat()
    {
        return format != null ? format : DEFAULT_FORMAT;
    }

  /**
   * This returns the actual value of the <code>format</code> field, 
   * which makes it possible to determine whether the setFormat() method was ever called
   * despite the fact that the <code>getFormat()</code> method returns a default.
   * 
   * @return a <code>String</code>, the actual value of the <code>format</code> field.
   *         If <code>setFormat()</code> has not been called, this method will return null.
   */
    String getRawFormat()
    {
        return format;
    }

    public void setType(String type)
    {
        if ("UPDATE".equals(type))
        {
            dbOperation = DatabaseOperation.UPDATE;
        }
        else if ("INSERT".equals(type))
        {
            dbOperation = DatabaseOperation.INSERT;
        }
        else if ("REFRESH".equals(type))
        {
            dbOperation = DatabaseOperation.REFRESH;
        }
        else if ("DELETE".equals(type))
        {
            dbOperation = DatabaseOperation.DELETE;
        }
        else if ("DELETE_ALL".equals(type))
        {
            dbOperation = DatabaseOperation.DELETE_ALL;
        }
        else if ("CLEAN_INSERT".equals(type))
        {
            dbOperation = DatabaseOperation.CLEAN_INSERT;
        }
        else if ("MSSQL_CLEAN_INSERT".equals(type)) 
	{
	    dbOperation = InsertIdentityOperation.CLEAN_INSERT;
	}
	else if ("MSSQL_INSERT".equals(type)) 
	{
	    dbOperation = InsertIdentityOperation.INSERT;
	} 
	else if ("MSSQL_REFRESH".equals(type)) 
	{
	    dbOperation = InsertIdentityOperation.REFRESH;
	} 
	else 
	{
	    throw new IllegalArgumentException("Type must be one of: UPDATE, INSERT,"
                    + " REFRESH, DELETE, DELETE_ALL, CLEAN_INSERT, MSSQL_INSERT, "
		    + " or MSSQL_REFRESH but was: " + type);
        }
        this.type = type;
    }

    public void setSrc(File src)
    {
        this.src = src;
    }

    public void setFormat(String format)
    {
	if (format.equalsIgnoreCase("flat")
	    || format.equalsIgnoreCase("xml"))
	{
            this.format = format;
	}
	else
	{
   	    throw new IllegalArgumentException("Type must be either 'flat'(default) or 'xml' but was: " + format);
	}
    }

    public void execute(IDatabaseConnection connection) throws DatabaseUnitException
    {
        if (dbOperation != null)
        {
            try
            {
                IDataSet dataset;
		if (format == null) 
		{
		    format = DEFAULT_FORMAT;
		}
                if (format.equalsIgnoreCase("xml"))
                {
                    dataset = new XmlDataSet(new FileInputStream(src));
                }
                else 
                {
                    dataset = new FlatXmlDataSet(new FileInputStream(src));
                }
                dbOperation.execute(connection, dataset);
            }
            catch (IOException e)
            {
                throw new DatabaseUnitException(e);
            }
            catch (SQLException e)
            {
                throw new DatabaseUnitException(e);
            }
        }
        else
        {
            throw new DatabaseUnitException("Operation.execute(): setType(String) must be called before execute()!");
        }
    }

    public String getLogMessage()
    {
        return "Executing operation: " + type
                + "\n          on   file: " + src.getAbsolutePath()
	        + "\n          with format: " + format;
    }


    public String toString()
    {
        StringBuffer result = new StringBuffer();
        result.append("Operation: ");
        result.append(" type=" + type);
        result.append(", format=" + format);
        result.append(", src=" + src.getAbsolutePath());
        result.append(", dbOperation = " + dbOperation);

        return result.toString();
    }
}

