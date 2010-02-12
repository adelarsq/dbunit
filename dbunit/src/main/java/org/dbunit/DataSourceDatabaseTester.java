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
package org.dbunit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;

import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;

/**
 * DatabaseTester that uses a {@link DataSource} to create connections.
 *
 * @author Andres Almiray (aalmiray@users.sourceforge.net)
 * @author Felipe Leme (dbunit@felipeal.net)
 * @author Last changed by: $Author$
 * @version $Revision$ $Date$
 * @since 2.2.0
 */
public class DataSourceDatabaseTester extends AbstractDatabaseTester
{

	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(DataSourceDatabaseTester.class);

	private DataSource dataSource;

	/**
	 * Creates a new DataSourceDatabaseTester with the specified DataSource.
	 *
	 * @param dataSource the DataSource to pull connections from
	 */
	public DataSourceDatabaseTester( DataSource dataSource )
	{
		super();

        if (dataSource == null) {
            throw new NullPointerException(
                    "The parameter 'dataSource' must not be null");
        }
		this.dataSource = dataSource;
	}

	/**
     * Creates a new DataSourceDatabaseTester with the specified DataSource and schema name.
     * @param dataSource the DataSource to pull connections from
	 * @param schema The schema name to be used for new dbunit connections
	 * @since 2.4.5
	 */
	public DataSourceDatabaseTester(DataSource dataSource, String schema) 
	{
        super(schema);
        
        if (dataSource == null) {
            throw new NullPointerException(
                    "The parameter 'dataSource' must not be null");
        }
        this.dataSource = dataSource;
    }

    public IDatabaseConnection getConnection() throws Exception
	{
		logger.debug("getConnection() - start");

		assertTrue( "DataSource is not set", dataSource!=null );
		return new DatabaseConnection( dataSource.getConnection(), getSchema() );
	}
}
