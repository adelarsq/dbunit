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

package org.dbunit.assertion;

import java.sql.SQLException;

import org.dbunit.DatabaseUnitException;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.Column;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;

/**
 * Provides static methods for the most common DbUnit assertion needs.
 * 
 * Although the methods are static, they rely on a Assertion instance to do the
 * work. So, if you need to customize the behavior, you can create your own
 * sub-class, override the desired methods, then call setInstance() with the new
 * object.
 * 
 * @author Felipe Leme <dbunit@felipeal.net>
 * @version $Revision: 864 $
 * @since Nov 07, 2008
 */
public interface DbUnitAssert {

  /**
   * Compare one table present in two datasets ignoring specified columns.
   * 
   * @param expectedDataset
   *          First dataset.
   * @param actualDataset
   *          Second dataset.
   * @param tableName
   *          Table name of the table to be compared.
   * @param ignoreCols
   *          Columns to be ignored in comparison.
   * @throws org.dbunit.DatabaseUnitException
   *           If an error occurs.
   */
  void assertEqualsIgnoreCols(final IDataSet expectedDataset,
      final IDataSet actualDataset, final String tableName,
      final String[] ignoreCols) throws DatabaseUnitException;

  /**
   * Compare the given tables ignoring specified columns.
   * 
   * @param expectedTable
   *          First table.
   * @param actualTable
   *          Second table.
   * @param ignoreCols
   *          Columns to be ignored in comparison.
   * @throws org.dbunit.DatabaseUnitException
   *           If an error occurs.
   */
  void assertEqualsIgnoreCols(final ITable expectedTable,
      final ITable actualTable, final String[] ignoreCols)
      throws DatabaseUnitException;

  /**
   * Compare a table from a dataset with a table generated from an sql query.
   * 
   * @param expectedDataset
   *          Dataset to retrieve the first table from.
   * @param connection
   *          Connection to use for the SQL statement.
   * @param sqlQuery
   *          SQL query that will build the data in returned second table rows.
   * @param tableName
   *          Table name of the table to compare
   * @param ignoreCols
   *          Columns to be ignored in comparison.
   * @throws DatabaseUnitException
   *           If an error occurs while performing the comparison.
   * @throws java.sql.SQLException
   *           If an SQL error occurs.
   */
  void assertEqualsByQuery(final IDataSet expectedDataset,
      final IDatabaseConnection connection, final String sqlQuery,
      final String tableName, final String[] ignoreCols)
      throws DatabaseUnitException, SQLException;

  /**
   * Compare a table with a table generated from an sql query.
   * 
   * @param expectedTable
   *          Table containing all expected results.
   * @param connection
   *          Connection to use for the SQL statement.
   * @param tableName
   *          The name of the table to query from the database
   * @param sqlQuery
   *          SQL query that will build the data in returned second table rows.
   * @param ignoreCols
   *          Columns to be ignored in comparison.
   * @throws DatabaseUnitException
   *           If an error occurs while performing the comparison.
   * @throws java.sql.SQLException
   *           If an SQL error occurs.
   */
  void assertEqualsByQuery(final ITable expectedTable,
      final IDatabaseConnection connection, final String tableName,
      final String sqlQuery, final String[] ignoreCols)
      throws DatabaseUnitException, SQLException;

  /**
   * Asserts that the two specified dataset are equals. This method ignore the
   * tables order.
   */
  void assertEquals(IDataSet expectedDataSet, IDataSet actualDataSet)
      throws DatabaseUnitException;

  /**
   * Asserts that the two specified dataset are equals. This method ignore the
   * tables order.
   */
  void assertEquals(IDataSet expectedDataSet, IDataSet actualDataSet,
      FailureHandler failureHandler) throws DatabaseUnitException;

  /**
   * Asserts that the two specified tables are equals. This method ignores the
   * table names, the columns order, the columns data type and which columns are
   * composing the primary keys.
   * 
   * @param expectedTable
   *          Table containing all expected results.
   * @param actualTable
   *          Table containing all actual results.
   * @throws DatabaseUnitException
   */
  void assertEquals(ITable expectedTable, ITable actualTable)
      throws DatabaseUnitException;

  /**
   * Asserts that the two specified tables are equals. This method ignores the
   * table names, the columns order, the columns data type and which columns are
   * composing the primary keys. <br />
   * Example: <code><pre>
   * ITable actualTable = ...;
   * ITable expectedTable = ...;
   * ITableMetaData metaData = actualTable.getTableMetaData();
   * Column[] additionalInfoCols = Columns.getColumns(new String[] {"MY_PK_COLUMN"}, metaData.getColumns());
   * Assertion.assertEquals(expectedTable, actualTable, additionalInfoCols);
   * </pre></code>
   * 
   * @param expectedTable
   *          Table containing all expected results.
   * @param actualTable
   *          Table containing all actual results.
   * @param additionalColumnInfo
   *          The columns to be printed out if the assert fails because of a
   *          data mismatch. Provides some additional column values that may be
   *          useful to quickly identify the columns for which the mismatch
   *          occurred (for example a primary key column). Can be
   *          <code>null</code>
   * @throws DatabaseUnitException
   */
  public void assertEquals(ITable expectedTable, ITable actualTable,
      Column[] additionalColumnInfo) throws DatabaseUnitException;

  /**
   * Asserts that the two specified tables are equals. This method ignores the
   * table names, the columns order, the columns data type and which columns are
   * composing the primary keys. <br />
   * Example: <code><pre>
   * ITable actualTable = ...;
   * ITable expectedTable = ...;
   * ITableMetaData metaData = actualTable.getTableMetaData();
   * Column[] additionalInfoCols = Columns.getColumns(new String[] {"MY_PK_COLUMN"}, metaData.getColumns());
   * Assertion.assertEquals(expectedTable, actualTable, additionalInfoCols);
   * </pre></code>
   * 
   * @param expectedTable
   *          Table containing all expected results.
   * @param actualTable
   *          Table containing all actual results.
   * @param failureHandler
   *          The failure handler used if the assert fails because of a data
   *          mismatch. Provides some additional information that may be useful
   *          to quickly identify the rows for which the mismatch occurred (for
   *          example by printing an additional primary key column). Can be
   *          <code>null</code>
   * @throws DatabaseUnitException
   * @since 2.4
   */
  void assertEquals(ITable expectedTable, ITable actualTable,
      FailureHandler failureHandler) throws DatabaseUnitException;

}
