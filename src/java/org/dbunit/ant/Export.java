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
import org.dbunit.dataset.xml.XmlDataSet;

import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * The <code>Export</code> class is the step that facilitates exporting
 * the contents of the database to a file.  The database can be exported
 * as a full dataset or partially if specific table names are identified.
 *
 * @author Timothy Ruppert && Ben Cox
 * @version $Revision$
 * @see org.dbunit.ant.DbUnitTaskStep
 */
public class Export implements DbUnitTaskStep
{

    private boolean flat = true;
    private File dest;
    private List tables = new ArrayList();

    public Export()
    {
    }

    public File getDest()
    {
        return dest;
    }

    public List getTables()
    {
        return tables;
    }

    public boolean getFlat()
    {
        return flat;
    }

    public void setDest(File dest)
    {
        this.dest = dest;
    }

    public void addTable(Table table)
    {
        tables.add(table);
    }

    public void setFlat(boolean flat)
    {
        this.flat = flat;
    }

    public void execute(Connection conn) throws DatabaseUnitException
    {
        IDataSet dataset;
        IDatabaseConnection connection = new DatabaseConnection(conn);
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
            if (flat)
            {
                FlatXmlDataSet.write(dataset, new FileOutputStream(dest));
            }
            else
            {
                XmlDataSet.write(dataset, new FileOutputStream(dest));
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
                + "\n          to   file: " + dest.getAbsolutePath();
    }


    public String toString()
    {
        StringBuffer result = new StringBuffer();
        result.append("Export: ");
        result.append(" dest=" + dest.getAbsolutePath());
        result.append(", flat=" + flat);
        result.append(", tables= " + tables);

        return result.toString();
    }
}


