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

import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.CachedDataSet;
import org.dbunit.dataset.csv.CSVProducer;
import org.dbunit.dataset.xml.XmlProducer;
import org.dbunit.dataset.xml.FlatXmlProducer;
import org.dbunit.dataset.xml.FlatDtdProducer;
import org.dbunit.dataset.stream.IDataSetProducer;
import org.dbunit.dataset.stream.StreamingDataSet;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.database.IResultSetTableFactory;
import org.dbunit.database.ForwardOnlyResultSetTableFactory;
import org.dbunit.database.CachedResultSetTableFactory;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.QueryDataSet;
import org.dbunit.DatabaseUnitException;

import org.xml.sax.InputSource;

import java.util.List;
import java.util.Iterator;
import java.sql.SQLException;
import java.io.File;
import java.io.IOException;

/**
 * @author Manuel Laflamme
 * @since Apr 3, 2004
 * @version $Revision$
 */
public abstract class AbstractStep implements DbUnitTaskStep
{
    public static final String FORMAT_FLAT = "flat";
    public static final String FORMAT_XML = "xml";
    public static final String FORMAT_DTD = "dtd";
    public static final String FORMAT_CSV = "csv";

    protected IDataSet getDatabaseDataSet(IDatabaseConnection connection,
            List tables, boolean forwardonly) throws DatabaseUnitException
    {
        try
        {
            // Setup the ResultSet table factory
            IResultSetTableFactory factory = null;
            if (forwardonly)
            {
                factory = new ForwardOnlyResultSetTableFactory();
            }
            else
            {
                factory = new CachedResultSetTableFactory();
            }
            DatabaseConfig config = connection.getConfig();
            config.setProperty(DatabaseConfig.PROPERTY_RESULTSET_TABLE_FACTORY,
                    factory);

            // Retrieve the complete database if no tables or queries specified.
            if (tables.size() == 0)
            {
                return connection.createDataSet();
            }

            QueryDataSet queryDataset = new QueryDataSet(connection);
            for (Iterator it = tables.iterator(); it.hasNext();)
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

            return queryDataset;
        }
        catch (SQLException e)
        {
            throw new DatabaseUnitException(e);
        }
    }

    protected IDataSet getSrcDataSet(File src, String format,
            boolean forwardonly) throws DatabaseUnitException
    {
        try
        {
            IDataSetProducer producer = null;
            if (format.equalsIgnoreCase(FORMAT_XML))
            {
                producer = new XmlProducer(new InputSource(src.toURL().toString()));
            }
            else if (format.equalsIgnoreCase(FORMAT_CSV))
            {
                producer = new CSVProducer(src);
            }
            else if (format.equalsIgnoreCase(FORMAT_FLAT))
            {
                producer = new FlatXmlProducer(new InputSource(src.toURL().toString()));
            }
            else if (format.equalsIgnoreCase(FORMAT_DTD))
            {
                producer = new FlatDtdProducer(new InputSource(src.toURL().toString()));
            }
            else
            {
                throw new IllegalArgumentException("Type must be either 'flat'(default), 'xml', 'csv' or 'dtd' but was: " + format);
            }

            if (forwardonly)
            {
                return new StreamingDataSet(producer);
            }
            return new CachedDataSet(producer);
        }
        catch (IOException e)
        {
            throw new DatabaseUnitException(e);
        }
    }
}
