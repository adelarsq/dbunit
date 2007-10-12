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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

import org.apache.tools.ant.Task;
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.*;
import org.dbunit.dataset.*;
import org.dbunit.dataset.csv.CsvProducer;
import org.dbunit.dataset.stream.IDataSetProducer;
import org.dbunit.dataset.stream.StreamingDataSet;
import org.dbunit.dataset.xml.*;
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

	// Needed a path to Project for logging and references.
	private Task parentTask;
	
    protected IDataSet getDatabaseDataSet(IDatabaseConnection connection,
            List tables, boolean forwardonly) throws DatabaseUnitException
    {
        logger.debug("getDatabaseDataSet(connection=" + connection + ", tables=" + tables + ", forwardonly="
                + forwardonly + ") - start");

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

			List queryDataSets = new ArrayList();
			
            QueryDataSet queryDataSet = new QueryDataSet(connection);
            
            for (Iterator it = tables.iterator(); it.hasNext();)
            {
                Object item = it.next();
                if(item instanceof QuerySet) {
					if(queryDataSet.getTableNames().length > 0) 
                		queryDataSets.add(queryDataSet);
					queryDataSets.add
						(getQueryDataSetForQuerySet(connection, (QuerySet)item));
					queryDataSet = new QueryDataSet(connection);
                }
                else if (item instanceof Query)
                {
                    Query queryItem = (Query)item;
                    queryDataSet.addTable(queryItem.getName(), queryItem.getSql());
                }
                else
                {
                    Table tableItem = (Table)item;
                    queryDataSet.addTable(tableItem.getName());
                }
            }
            
            if(queryDataSet.getTableNames().length > 0) 
            	queryDataSets.add(queryDataSet);

			IDataSet[] dataSetsArray = new IDataSet[queryDataSets.size()];
            return new CompositeDataSet((IDataSet[])queryDataSets.toArray(dataSetsArray));
        }
        catch (SQLException e)
        {
            logger.error("getDatabaseDataSet()", e);

            throw new DatabaseUnitException(e);
        }
    }

   
	protected IDataSet getSrcDataSet(File src, String format,
            boolean forwardonly) throws DatabaseUnitException
    {
        logger.debug("getSrcDataSet(src=" + src + ", format=" + format + ", forwardonly=" + forwardonly + ") - start");

        try
        {
            IDataSetProducer producer = null;
            if (format.equalsIgnoreCase(FORMAT_XML))
            {
                producer = new XmlProducer(new InputSource(src.toURL().toString()));
            }
            else if (format.equalsIgnoreCase(FORMAT_CSV))
            {
                producer = new CsvProducer(src);
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
            logger.error("getSrcDataSet()", e);

            throw new DatabaseUnitException(e);
        }
    }
    
	private QueryDataSet getQueryDataSetForQuerySet
		(IDatabaseConnection connection, QuerySet querySet) throws SQLException {
        logger.debug("getQueryDataSetForQuerySet(connection=" + connection + ", querySet=" + querySet + ") - start");
		
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
        logger.debug("getParentTask() - start");

		return parentTask;
	}

	public void setParentTask(Task task) {
        logger.debug("setParentTask(task=" + task + ") - start");

		parentTask = task;
	}
	
	public void log(String msg, int level) {
        logger.debug("log(msg=" + msg + ", level=" + level + ") - start");

		if(parentTask != null)
			parentTask.log(msg, level);
	}

}
