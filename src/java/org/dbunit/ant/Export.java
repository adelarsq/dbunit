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
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.QueryDataSet;
import org.dbunit.dataset.CompositeDataSet;
import org.dbunit.dataset.xml.*;

import java.io.*;
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
    private List queries = new ArrayList();

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

    public List getQueries()
    {
        return queries;
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

    public void addQuery(Query query)
    {
        queries.add(query);
    }

    public void execute(IDatabaseConnection connection) throws DatabaseUnitException
    {
        IDataSet dataset = null;
        try
        {

            if (dest == null)
            {
                throw new DatabaseUnitException("'dest' is a required attribute of the <export> step.");
            }
            // retrieve the dataset if no tables or queries specifedid.
            if (tables.size() == 0 && queries.size()==0)
            {
                dataset = connection.createDataSet();
            }
            else
            {
                if (tables.size() > 0) {
                    dataset = connection.createDataSet(getTableArray());
                }
                if (queries.size() > 0) {
                    QueryDataSet queryDataSet = new QueryDataSet(connection);
                    for (int i = 0; i < queries.size(); i++){
                        Query query = (Query)queries.get(i);
                        if (query.getSql() == null){
                            throw new DatabaseUnitException("'sql' is a required attribute of the <query> step.");
                        }
                        queryDataSet.addTable(query.getName(),query.getSql());

                    }
                    //crummy merge!
                    if(dataset != null) {
                        dataset = new CompositeDataSet(queryDataSet,dataset);
                    }
                    else {
                        dataset = queryDataSet;
                    }
                }

            }
            // save the dataset
            OutputStream out = new FileOutputStream(dest);
            try
            {
                if (format.equalsIgnoreCase("flat"))
                {
                    FlatXmlDataSet.write(dataset, out);
                }
                else if (format.equalsIgnoreCase("xml"))
                {
                    XmlDataSet.write(dataset, out);
                }
                else if (format.equalsIgnoreCase("dtd"))
                {
                    FlatDtdDataSet.write(dataset, out);
                }
            }
            finally
            {
                out.close();
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

//    private String[] convertListToStringArray(List list){
//        String []strArray = new String[list.size()];
//        for (int i = 0; i < list.size(); i++)
//        {
//            Table table = (Table)list.get(i);
//            strArray[i] = table.getName();
//        }
//        return strArray;
//    }
//
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
        result.append(", format= " + format);
        result.append(", tables= " + tables);
        result.append(", queries= " + queries);

        return result.toString();
    }
}


