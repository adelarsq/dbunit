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

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.tools.ant.Task;
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.CachedResultSetTableFactory;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.ForwardOnlyResultSetTableFactory;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.database.IResultSetTableFactory;
import org.dbunit.database.QueryDataSet;
import org.dbunit.dataset.CachedDataSet;
import org.dbunit.dataset.CompositeDataSet;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.csv.CsvProducer;
import org.dbunit.dataset.excel.XlsDataSet;
import org.dbunit.dataset.stream.IDataSetProducer;
import org.dbunit.dataset.stream.StreamingDataSet;
import org.dbunit.dataset.xml.FlatDtdProducer;
import org.dbunit.dataset.xml.FlatXmlProducer;
import org.dbunit.dataset.xml.XmlProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;

/**
 * @author Manuel Laflamme
 * @since Apr 3, 2004
 * @version $Revision$
 */
public abstract class AbstractStep implements DbUnitTaskStep
{

    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(AbstractStep.class);

    public static final String FORMAT_FLAT = "flat";
    public static final String FORMAT_XML = "xml";
    public static final String FORMAT_DTD = "dtd";
    public static final String FORMAT_CSV = "csv";
    public static final String FORMAT_XLS = "xls";

	// Needed a path to Project for logging and references.
	private Task parentTask;
	
    protected IDataSet getDatabaseDataSet(IDatabaseConnection connection,
            List tables, boolean forwardonly) throws DatabaseUnitException
    {
    	if (logger.isDebugEnabled())
    	{
            logger.debug("getDatabaseDataSet(connection={}, tables={}, forwardonly={}) - start",
            		new Object[] { connection, tables, new Boolean(forwardonly) });
    	}

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
            config.setProperty(DatabaseConfig.PROPERTY_RESULTSET_TABLE_FACTORY, factory);

            // Retrieve the complete database if no tables or queries specified.
            if (tables.size() == 0)
            {
            	logger.debug("Retrieving the whole database because now tables/queries have been specified");
                return connection.createDataSet();
            }

            List queryDataSets = createQueryDataSet(tables, connection);
			IDataSet[] dataSetsArray = (IDataSet[])queryDataSets.toArray( new IDataSet[queryDataSets.size()] );
            return new CompositeDataSet(dataSetsArray);
        }
        catch (SQLException e)
        {
            throw new DatabaseUnitException(e);
        }
    }

   
	private List createQueryDataSet(List tables, IDatabaseConnection connection) 
	throws DataSetException, SQLException 
	{
		logger.debug("createQueryDataSet(tables={}, connection={})", tables, connection);
		
		List queryDataSets = new ArrayList();
		
        QueryDataSet queryDataSet = new QueryDataSet(connection);
        
        for (Iterator it = tables.iterator(); it.hasNext();)
        {
            Object item = it.next();
            
            if(item instanceof QuerySet) {
				if(queryDataSet.getTableNames().length > 0) 
            		queryDataSets.add(queryDataSet);
				QueryDataSet newQueryDataSet = getQueryDataSetForQuerySet(connection, (QuerySet)item);
				queryDataSets.add(newQueryDataSet);
				queryDataSet = new QueryDataSet(connection);
            }
            else if (item instanceof Query)
            {
                Query queryItem = (Query)item;
                queryDataSet.addTable(queryItem.getName(), queryItem.getSql());
            }
            else if (item instanceof Table)
            {
                Table tableItem = (Table)item;
                queryDataSet.addTable(tableItem.getName());
            }
            else
            {
            	throw new IllegalArgumentException("Unsupported element type " + item.getClass().getName() + ".");
            }
        }
        
        if(queryDataSet.getTableNames().length > 0) 
        	queryDataSets.add(queryDataSet);
        
        return queryDataSets;
	}


	protected IDataSet getSrcDataSet(File src, String format,
            boolean forwardonly) throws DatabaseUnitException
    {
		if (logger.isDebugEnabled())
		{
			logger.debug("getSrcDataSet(src={}, format={}, forwardonly={}) - start",
					new Object[]{ src, format, new Boolean(forwardonly) });
		}

        try
        {
            IDataSetProducer producer = null;
            if (format.equalsIgnoreCase(FORMAT_XML))
            {
                producer = new XmlProducer(getInputSource(src));
            }
            else if (format.equalsIgnoreCase(FORMAT_CSV))
            {
                producer = new CsvProducer(src);
            }
            else if (format.equalsIgnoreCase(FORMAT_FLAT))
            {
                producer = new FlatXmlProducer(getInputSource(src));
            }
            else if (format.equalsIgnoreCase(FORMAT_DTD))
            {
                producer = new FlatDtdProducer(getInputSource(src));
            }
            else if (format.equalsIgnoreCase(FORMAT_XLS))
            {
                return new CachedDataSet(new XlsDataSet(src));
            }
            else
            {
            	throw new IllegalArgumentException("Type must be either 'flat'(default), 'xml', 'csv', 'xls' or 'dtd' but was: " + format);
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
    
	/**
	 * Creates and returns an {@link InputSource}
	 * @param file The file for which an {@link InputSource} should be created
	 * @return The input source for the given file
	 * @throws MalformedURLException
	 */
	public static InputSource getInputSource(File file) throws MalformedURLException
	{
		URI uri = file.toURI();
		InputSource inputSource = new InputSource(uri.toURL().toString());
		return inputSource;
	}
	
	private QueryDataSet getQueryDataSetForQuerySet
		(IDatabaseConnection connection, QuerySet querySet) throws SQLException {
        logger.debug("getQueryDataSetForQuerySet(connection={}, querySet={}) - start", connection, querySet);
		
		//incorporate queries from referenced queryset
		String refid = querySet.getRefid();
		if(refid != null) {
			QuerySet referenced = (QuerySet)
				getParentTask().getProject().getReference(refid);
			querySet.copyQueriesFrom(referenced);
		}
		
		QueryDataSet partialDataSet = new QueryDataSet(connection);
		
		Iterator queriesIter = querySet.getQueries().iterator();
		while(queriesIter.hasNext()) {
			Query query = (Query)queriesIter.next();
			partialDataSet.addTable(query.getName(), query.getSql());
		}
		
		return partialDataSet;
		
	}

	
	public Task getParentTask() {
		return parentTask;
	}

	public void setParentTask(Task task) {
        logger.debug("setParentTask(task={}) - start", task);
		parentTask = task;
	}
	
	public void log(String msg, int level) {
		if(logger.isDebugEnabled())
			logger.debug("log(msg={}, level={}) - start", msg, new Integer(level));

		if(parentTask != null)
			parentTask.log(msg, level);
	}

}
