/*
 *
 * The DbUnit Database Testing Framework
 * Copyright (C)2002-2008, DbUnit.org
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
package org.dbunit.database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.dbunit.dataset.AbstractTableMetaData;
import org.dbunit.dataset.Column;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.DefaultTableMetaData;
import org.dbunit.dataset.datatype.IDataTypeFactory;
import org.dbunit.util.SQLHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link ResultSet} based {@link org.dbunit.dataset.ITableMetaData} implementation
 * @author gommma
 * @version $Revision$
 * @since 2.3.0
 */
public class ResultSetTableMetaData extends AbstractTableMetaData 
{
    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(DatabaseTableMetaData.class);

    /**
     * The actual table metadata
     */
    private DefaultTableMetaData wrappedTableMetaData;
	

	/**
	 * @param tableName The name of the database table
	 * @param resultSet The JDBC result set that is used to retrieve the columns
	 * @param connection The connection which is needed to retrieve some configuration values
	 * @throws DataSetException
	 * @throws SQLException
	 */
	public ResultSetTableMetaData(String tableName,
            ResultSet resultSet, IDatabaseConnection connection) throws DataSetException, SQLException {
		super();
		this.wrappedTableMetaData = createMetaData(tableName, resultSet, connection);
		
	}

	/**
	 * @param tableName The name of the database table
	 * @param resultSet The JDBC result set that is used to retrieve the columns
	 * @param dataTypeFactory
	 * @throws DataSetException
	 * @throws SQLException
	 */
	public ResultSetTableMetaData(String tableName,
            ResultSet resultSet, IDataTypeFactory dataTypeFactory) throws DataSetException, SQLException {
		super();
		this.wrappedTableMetaData = createMetaData(tableName, resultSet, dataTypeFactory);
	}

    private DefaultTableMetaData createMetaData(String tableName,
            ResultSet resultSet, IDatabaseConnection connection)
            throws SQLException, DataSetException
    {
    	if (logger.isDebugEnabled())
    		logger.debug("createMetaData(tableName={}, resultSet={}, connection={}) - start",
    				new Object[] { tableName, resultSet, connection });

        IDataTypeFactory typeFactory = super.getDataTypeFactory(connection);
        return createMetaData(tableName, resultSet, typeFactory);
    }

    private DefaultTableMetaData createMetaData(String tableName,
            ResultSet resultSet, IDataTypeFactory dataTypeFactory)
            throws DataSetException, SQLException
    {
    	if (logger.isDebugEnabled())
    		logger.debug("createMetaData(tableName={}, resultSet={}, dataTypeFactory={}) - start",
    				new Object[]{ tableName, resultSet, dataTypeFactory });
    	
    	Connection connection = resultSet.getStatement().getConnection();
    	DatabaseMetaData databaseMetaData = connection.getMetaData();
    	
    	// Create the columns from the result set
        ResultSetMetaData metaData = resultSet.getMetaData();
        
        Column[] columns = new Column[metaData.getColumnCount()];
        for (int i = 0; i < columns.length; i++)
        {
            int rsIndex = i+1;
            
            // use DatabaseMetaData to retrieve the actual column definition
            String catalogNameMeta = metaData.getCatalogName(rsIndex);
            String schemaNameMeta = metaData.getSchemaName(rsIndex);
            String tableNameMeta = metaData.getTableName(rsIndex);
            String columnNameMeta = metaData.getColumnName(rsIndex);
            
            ResultSet columnsResultSet = databaseMetaData.getColumns(
                    catalogNameMeta,
                    schemaNameMeta,
                    tableNameMeta,
                    columnNameMeta
            );

            // Scroll resultset forward - must have one result which exactly matches the required parameters
            scrollTo(columnsResultSet, catalogNameMeta, schemaNameMeta, tableNameMeta, columnNameMeta);
            
            columns[i] = SQLHelper.createColumn(columnsResultSet, dataTypeFactory, true);

        }

        return new DefaultTableMetaData(tableName, columns);
    }

    
	private void scrollTo(ResultSet columnsResultSet, String catalog,
            String schema, String table, String column) 
	throws SQLException 
	{
	    while(columnsResultSet.next())
	    {
	        boolean match = SQLHelper.matches(columnsResultSet, catalog, schema, table, column);
	        if(match)
	        {
	            // All right. Return immediately because the resultSet is positioned on the correct row
	            return;
	        }
	    }

	    // If we get here the column could not be found
	    String msg = 
                "Did not find column '" + column + 
                "' for <schema.table> '" + schema + "." + table + 
                "' in catalog '" + catalog + "' because names do not exactly match.";

        throw new IllegalStateException(msg);
    }

    public Column[] getColumns() throws DataSetException {
		return this.wrappedTableMetaData.getColumns();
	}

	public Column[] getPrimaryKeys() throws DataSetException {
		return this.wrappedTableMetaData.getPrimaryKeys();
	}

	public String getTableName() {
		return this.wrappedTableMetaData.getTableName();
	}

	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		sb.append(getClass().getName()).append("[");
		sb.append("wrappedTableMetaData=").append(this.wrappedTableMetaData);
		sb.append("]");
		return sb.toString();
	}
}
