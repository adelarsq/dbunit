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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.operation.DatabaseOperation;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.XmlDataSet;

/**
 * The <code>Operation</code> class is the step that defines which
 * operation will be performed in the execution of the <code>DbUnitTask</code>
 * task.
 *
 * @author Timothy Ruppert && Ben Cox
 * @version $Revision$
 * @see org.dbunit.ant.DbUnitTaskStep
 */
public class Operation implements DbUnitTaskStep {

    protected String type;
    private boolean flat = true;
    private File src;
    private DatabaseOperation dbOperation;

    public Operation () 
    {
        this.type="CLEAN_INSERT";
    }

    public String getType () 
    {
        return type; 
    }

    public File getSrc () 
    {
        return src; 
    }

    public DatabaseOperation getDbOperation () 
    { 
        return dbOperation; 
    }

    public boolean getFlat ()
    {
        return flat;
    }

    public void setType (String type) 
    {
        this.type = type;
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
	else 
	{
	    throw new IllegalArgumentException("Type must be one of: UPDATE, INSERT,"
					 + " REFRESH, DELETE, DELETE_ALL, or CLEAN_INSERT,"
					 + " but was: " + type);
	}
    }

    public void setSrc (File src) 
    {
        this.src = src;
    }

    public void setFlat (boolean flat) 
    {
        this.flat = flat;
    }

    public void execute(Connection conn) throws DatabaseUnitException 
    {
        if (dbOperation != null) 
	{
	    try 
	    {
	        DatabaseConnection dbConn = new DatabaseConnection(conn);
		IDataSet dataset; 
		if (flat) 
		{
  		    dataset = new FlatXmlDataSet(new FileInputStream(src));
		}
		else
		{
		    dataset = new XmlDataSet(new FileInputStream(src));
		}
		dbOperation.execute(dbConn, dataset);
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
	    throw new DatabaseUnitException ("Operation.execute(): setType(String) must be called before execute()!");
	} 
    }
  
    public String getLogMessage () 
    {
        return "Executing operation: " + type 
	       + "\n          on   file: " + src.getAbsolutePath();
    }	


    public String toString() 
    {
        StringBuffer result = new StringBuffer();
	result.append("Operation: ");
	result.append(" type=" + type);
	result.append(", flat=" + flat);
	result.append(", src=" + src.getAbsolutePath());
	result.append(", dbOperation = " + dbOperation);

	return result.toString();
    }
}
