/*
 * Export.java    Jun 10, 2002
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
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatDtdDataSet;
import org.dbunit.dataset.xml.XmlDataSet;

import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * The <code>Export</code> class is the step that facilitates exporting
 * the contents of the database and/or it's corresponding dtd to a file.  
 * The export can be performed on a full dataset or a partial one if 
 * specific table names are identified.
 *
 * @author Timothy Ruppert && Ben Cox
 * @version $Revision$
 * @see org.dbunit.ant.DbUnitTaskStep
 */
public class Export implements DbUnitTaskStep
{

    private File dest;
    private String format = "flat";
    private List tables = new ArrayList();

    public Export()
    {
    }

    private String getAbsolutePath(File filename) 
    { 
        return filename != null ? filename.getAbsolutePath() : "null";
    }  

    public File getDest()
    {
        return dest;
    }

    public String getFormat()
    {
        return format;
    }

    public List getTables()
    {
        return tables;
    }

    public void setDest(File dest)
    {
        this.dest = dest;
    }

    public void setFormat(String format)
    {
	if (format.equalsIgnoreCase("flat")
	    || format.equalsIgnoreCase("xml")
	    || format.equalsIgnoreCase("dtd"))
	{
	    this.format = format;
	}
	else 
	{
	    throw new IllegalArgumentException("Type must be one of: 'flat'(default), 'xml', or 'dtd' but was: " + format);
	}
    }

    public void addTable(Table table)
    {
        tables.add(table);
    }

    public void execute(IDatabaseConnection connection) throws DatabaseUnitException
    {
        IDataSet dataset;
        try
        {
            if (tables.size() == 0)
            {
                dataset = connection.createDataSet();
            }
            else
            {
                dataset = connection.createDataSet(getTableArray());
            }
	    if (dest == null) 
            {
	      throw new DatabaseUnitException ("'dest' is a required attribute of the <export> step.");
	    }
	    else  
	    {
	        if (format.equalsIgnoreCase("flat"))
		{
                    FlatXmlDataSet.write(dataset, new FileOutputStream(dest));
                }
	        else if (format.equalsIgnoreCase("xml"))
                {
                    XmlDataSet.write(dataset, new FileOutputStream(dest));
                }
		else if (format.equalsIgnoreCase("dtd"))
		{
		    FlatDtdDataSet.write(dataset, new FileOutputStream(dest));
		}
	    }

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

    private String[] getTableArray()
    {
        String[] result = new String[tables.size()];
        for (int i = 0; i < tables.size(); i++)
        {
            Table table = (Table)tables.get(i);
            result[i] = table.getName();
        }
        return result;
    }

    public String getLogMessage()
    {
        return "Executing export: "
             + "\n      in format: " + format 
	     + " to datafile: " + getAbsolutePath(dest);
    }


    public String toString()
    {
        StringBuffer result = new StringBuffer();
        result.append("Export: ");
        result.append(" dest=" + getAbsolutePath(dest));
        result.append(", format= " + tables);
        result.append(", tables= " + tables);

        return result.toString();
    }
}


