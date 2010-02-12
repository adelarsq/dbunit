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

package org.dbunit.dataset;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;

import junit.framework.TestCase;

import org.dbunit.HypersonicEnvironment;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.database.QueryDataSet;
import org.dbunit.dataset.Column;
import org.dbunit.dataset.CompositeDataSet;
import org.dbunit.dataset.DefaultDataSet;
import org.dbunit.dataset.DefaultTable;
import org.dbunit.dataset.datatype.DataType;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.testutil.TestUtils;


/**
 * Test Case for issue #1721870
 * @author Sebastien Le Callonnec
 * @version $Revision$
 * @since Mar 11, 2008
 */
public class CompositeDataSetIterationTest extends TestCase {

	private Connection jdbcConnection;
	private final String sqlFile = "hypersonic_simple_dataset.sql"; 
	private IDatabaseConnection connection;

	protected void setUp() throws Exception {
		super.setUp();
		this.jdbcConnection = HypersonicEnvironment.createJdbcConnection("mem:tempdb");
		HypersonicEnvironment.executeDdlFile(TestUtils.getFile("sql/" + sqlFile), jdbcConnection);
		this.connection = new DatabaseConnection(jdbcConnection);
	}
	
	  protected void tearDown() throws Exception {
		super.tearDown();

		HypersonicEnvironment.shutdown(this.jdbcConnection);
		this.jdbcConnection.close();
	}

	public void testMe() throws Exception {
		
		// 1. QueryDataSet
		QueryDataSet queryDataSet = new QueryDataSet(connection);
		queryDataSet.addTable("B", "select * from B");
		queryDataSet.addTable("C", "select * from C");

		// 2. Hard-coded data set
		DefaultDataSet plainDataSet = new DefaultDataSet();

		Column id   = new Column("id",   DataType.DOUBLE);
		Column name = new Column("name", DataType.VARCHAR);

		Column[] cols = { id, name };

		DefaultTable aTable = new DefaultTable("D", cols);
		Object[] row1 = { new Long(1), "D1" };
		Object[] row2 = { new Long(2), "D2" };

		aTable.addRow(row1);
		aTable.addRow(row2);

		plainDataSet.addTable(aTable);

		// 3. Composite
		CompositeDataSet compositeDataSet = new CompositeDataSet(queryDataSet, plainDataSet);

		// 4. Write
		try {
			FlatXmlDataSet.write(compositeDataSet, new FileOutputStream("target/full.xml"));
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
}
