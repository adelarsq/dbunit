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
import org.dbunit.database.QueryDataSet;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatDtdDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.XmlDataSet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * The <code>Export</code> class is the step that facilitates exporting
 * the contents of the database and/or it's corresponding dtd to a file.
 * The export can be performed on a full dataset or a partial one if
 * specific table names are identified.
 *
 * @author Timothy Ruppert 
 * @author Ben Cox
 * @version $Revision$
 * @since Jun 10, 2002
 * @see DbUnitTaskStep
 */
public class Export implements DbUnitTaskStep
{
    private static final String FORMAT_FLAT = "flat";
    private static final String FORMAT_XML = "xml";
    private static final String FORMAT_DTD = "dtd";

    private File _dest;
    private String _format = FORMAT_FLAT;
    private List _tables = new ArrayList();

    public Export()
    {
    }

    private String getAbsolutePath(File filename)
    {
        return filename != null ? filename.getAbsolutePath() : "null";
    }

    public File getDest()
    {
        return _dest;
    }

    public String getFormat()
    {
        return _format;
    }

    public List getTables()
    {
        return _tables;
    }

    public void setDest(File dest)
    {
        _dest = dest;
    }

    public void setFormat(String format)
    {
        if (format.equalsIgnoreCase(FORMAT_FLAT)
                || format.equalsIgnoreCase(FORMAT_XML)
                || format.equalsIgnoreCase(FORMAT_DTD))
        {
            _format = format;
        }
        else
        {
            throw new IllegalArgumentException("Type must be one of: 'flat'(default), 'xml', or 'dtd' but was: " + format);
        }
    }

    public void addTable(Table table)
    {
        _tables.add(table);
    }

    public void addQuery(Query query)
    {
        _tables.add(query);
    }

    public void execute(IDatabaseConnection connection) throws DatabaseUnitException
    {
        try
        {
            IDataSet dataset = null;

            if (_dest == null)
            {
                throw new DatabaseUnitException("'_dest' is a required attribute of the <export> step.");
            }

            // Retrieve the complete database if no tables or queries specified.
            if (_tables.size() == 0)
            {
                dataset = connection.createDataSet();
            }
            else
            {
                QueryDataSet queryDataset = new QueryDataSet(connection);
                for (Iterator it = _tables.iterator(); it.hasNext();)
                {
                    Object item = it.next();
                    if (item instanceof Query)
                    {
                        Query queryItem = (Query)item;
                        queryDataset.addTable(queryItem.getName(), queryItem.getSql());
                    }
                    else
                    {
                        Table tableItem = (Table)item;
                        queryDataset.addTable(tableItem.getName());
                    }
                }

                dataset = queryDataset;
            }

            // Write the dataset
            OutputStream out = new FileOutputStream(_dest);
            try
            {
                if (_format.equalsIgnoreCase(FORMAT_FLAT))
                {
                    FlatXmlDataSet.write(dataset, out);
                }
                else if (_format.equalsIgnoreCase(FORMAT_XML))
                {
                    XmlDataSet.write(dataset, out);
                }
                else if (_format.equalsIgnoreCase(FORMAT_DTD))
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

    public String getLogMessage()
    {
        return "Executing export: "
                + "\n      in _format: " + _format
                + " to datafile: " + getAbsolutePath(_dest);
    }


    public String toString()
    {
        StringBuffer result = new StringBuffer();
        result.append("Export: ");
        result.append(" _dest=" + getAbsolutePath(_dest));
        result.append(", _format= " + _format);
        result.append(", _tables= " + _tables);

        return result.toString();
    }
}


