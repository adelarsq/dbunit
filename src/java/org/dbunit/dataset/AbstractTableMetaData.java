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

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.datatype.IDataTypeFactory;
import org.dbunit.dataset.filter.IColumnFilter;
import org.dbunit.ext.db2.Db2DataTypeFactory;
import org.dbunit.ext.h2.H2DataTypeFactory;
import org.dbunit.ext.hsqldb.HsqldbDataTypeFactory;
import org.dbunit.ext.mssql.MsSqlDataTypeFactory;
import org.dbunit.ext.mysql.MySqlDataTypeFactory;
import org.dbunit.ext.oracle.Oracle10DataTypeFactory;
import org.dbunit.ext.oracle.OracleDataTypeFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Manuel Laflamme
 * @version $Revision$
 * @since Mar 8, 2002
 */
public abstract class AbstractTableMetaData implements ITableMetaData
{

	private Map _columnsToIndexes;
	
    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(AbstractTableMetaData.class);

    private static final Column[] EMPTY_COLUMNS = new Column[0];
    
    private DataTypeFactoryValidator dataTypeFactoryValidator = new DataTypeFactoryValidator();

    /**
     * Default constructor
     */
    public AbstractTableMetaData()
    {
    }
    
    /**
     * @param columns
     * @param keyNames
     * @return The primary key columns
     */
    protected static Column[] getPrimaryKeys(Column[] columns, String[] keyNames)
    {
        logger.debug("getPrimaryKeys(columns={}, keyNames={}) - start", columns, keyNames);

        if (keyNames == null || keyNames.length == 0)
        {
            return EMPTY_COLUMNS;
        }

        List keyList = new ArrayList();
        for (int i = 0; i < keyNames.length; i++)
        {
            Column primaryKey = Columns.getColumn(keyNames[i], columns);
            if (primaryKey != null)
            {
                keyList.add(primaryKey);
            }
        }

        return (Column[])keyList.toArray(new Column[0]);
    }

    protected static Column[] getPrimaryKeys(String tableName, Column[] columns,
            IColumnFilter columnFilter)
    {
    	if (logger.isDebugEnabled())
    	{
    		logger.debug("getPrimaryKeys(tableName={}, columns={}, columnFilter={}) - start",
    				new Object[]{ tableName, columns, columnFilter });
    	}

        List keyList = new ArrayList();
        for (int i = 0; i < columns.length; i++)
        {
            Column column = columns[i];
            if (columnFilter.accept(tableName, column))
            {
                keyList.add(column);
            }
        }

        return (Column[])keyList.toArray(new Column[0]);
    }

	/**
	 * Provides the index of the column with the given name within this table.
	 * Uses method {@link ITableMetaData#getColumns()} to retrieve all available columns.
	 * @throws DataSetException 
	 * @see org.dbunit.dataset.ITableMetaData#getColumnIndex(java.lang.String)
	 */
	public int getColumnIndex(String columnName) throws DataSetException 
	{
        logger.debug("getColumnIndex(columnName={}) - start", columnName);

        if(this._columnsToIndexes == null) 
		{
			// lazily create the map
			this._columnsToIndexes = createColumnIndexesMap(this.getColumns());
		}
		
        String columnNameUpperCase = columnName.toUpperCase();
		Integer colIndex = (Integer) this._columnsToIndexes.get(columnNameUpperCase);
		if(colIndex != null) 
		{
			return colIndex.intValue();
		}
		else 
		{
			throw new NoSuchColumnException(this.getTableName(), columnNameUpperCase,
					" (Non-uppercase input column: "+columnName+") in ColumnNameToIndexes cache map. " +
					"Note that the map's column names are NOT case sensitive.");
		}
	}

	/**
	 * @param columns The columns to be put into the hash table
	 * @return A map having the key value pair [columnName, columnIndexInInputArray]
	 */
	private Map createColumnIndexesMap(Column[] columns) 
	{
		Map colsToIndexes = new HashMap(columns.length);
		for (int i = 0; i < columns.length; i++) 
		{
			colsToIndexes.put(columns[i].getColumnName().toUpperCase(), new Integer(i));
		}
		return colsToIndexes;
	}

	/**
	 * Validates and returns the datatype factory of the given connection
	 * @param connection The connection providing the {@link IDataTypeFactory}
	 * @return The datatype factory of the given connection
	 * @throws SQLException
	 */
	public IDataTypeFactory getDataTypeFactory(IDatabaseConnection connection) 
	throws SQLException 
	{
		DatabaseConfig config = connection.getConfig();
        IDataTypeFactory dataTypeFactory = (IDataTypeFactory)config.getProperty(
                DatabaseConfig.PROPERTY_DATATYPE_FACTORY);
        
    	// Validate, e.g. oracle metaData + oracleDataTypeFactory ==> OK
    	Connection jdbcConnection = connection.getConnection();
    	DatabaseMetaData metaData = jdbcConnection.getMetaData();
    	String validationResult = dataTypeFactoryValidator.validate(metaData, dataTypeFactory);
    	if(validationResult != null)
    	{
    		// Inform the user that we think he could get trouble with the current configuration
    		logger.warn("Potential problem found: " + validationResult);
    	}
        
        return dataTypeFactory;
	}
    
	
	
	/**
	 * Utility to validate a given {@link IDataTypeFactory} against a given physical database system.
	 * For details refer to {@link #validate(DatabaseMetaData, IDataTypeFactory)}.
	 * 
	 * @author gommma
	 * @version $Revision$
	 * @since 2.3.0
	 */
	public static class DataTypeFactoryValidator
	{
	    /**
	     * Logger for this class
	     */
	    private static final Logger logger = LoggerFactory.getLogger(AbstractTableMetaData.class);

		/**
		 * Map that holds: {@link IDataTypeFactory} class ==> {@link Collection} [String validDbProductNames]
		 */
		private Map dataTypeFactoryToDbProductMap = new HashMap();
		
		public DataTypeFactoryValidator()
		{
//			addValidCombinationInternal(DefaultDataTypeFactory.class, "hsql");
			addValidCombinationInternal(Db2DataTypeFactory.class, "db2");
			addValidCombinationInternal(H2DataTypeFactory.class, "h2");
			addValidCombinationInternal(HsqldbDataTypeFactory.class, "hsql");
			addValidCombinationInternal(MsSqlDataTypeFactory.class, "mssql");
			addValidCombinationInternal(MySqlDataTypeFactory.class, "mysql");
			addValidCombinationInternal(OracleDataTypeFactory.class, "oracle");
			addValidCombinationInternal(Oracle10DataTypeFactory.class, "oracle");
		}
		
		/**
		 * @param iDataTypeFactoryImpl The class of the {@link IDataTypeFactory} to be validated
		 * @param databaseProductName The database product name considered to be 
		 * valid for the given {@link IDataTypeFactory}
		 */
		public void addValidCombination(Class iDataTypeFactoryImpl, String databaseProductName)
		{
			addValidCombinationInternal(iDataTypeFactoryImpl, databaseProductName);
			
		}
		
		private void addValidCombinationInternal(Class iDataTypeFactoryImpl, String databaseProductName)
		{
			logger.debug("addValidCombinationInternal(iDataTypeFactoryImpl={}, databaseProductName={}) - start", 
					iDataTypeFactoryImpl, databaseProductName);

			this.addValidCombinationInternal(iDataTypeFactoryImpl, new String[]{databaseProductName});
		}
		
		private void addValidCombinationInternal(Class iDataTypeFactoryImpl, String[] databaseProductNameList)
		{
			logger.debug("addValidCombinationInternal(iDataTypeFactoryImpl={}, databaseProductNameList={}) - start", 
					iDataTypeFactoryImpl, databaseProductNameList);

			Set dbProductSet = (Set)this.dataTypeFactoryToDbProductMap.get(iDataTypeFactoryImpl);
			if(dbProductSet == null)
			{
				dbProductSet = new HashSet();
				this.dataTypeFactoryToDbProductMap.put(iDataTypeFactoryImpl, dbProductSet);
			}
			
			for (int i = 0; i < databaseProductNameList.length; i++) 
			{
				if(databaseProductNameList[i] != null && databaseProductNameList[i].trim().length()>0)
				{
					dbProductSet.add(databaseProductNameList[i].toLowerCase());
				}
			}
		}

		/**
		 * Validates if the database system is supported by the given {@link IDataTypeFactory}.
		 * @param databaseMetaData The database metadata of the current database
		 * @param dataTypeFactory The {@link IDataTypeFactory} to be validated with the given database metadata
		 * @return <code>null</code> if the validation was successful. Otherwise a validation message
		 * is returned with details about why the validation failed.
		 * @throws SQLException 
		 */
		public String validate(DatabaseMetaData databaseMetaData, IDataTypeFactory dataTypeFactory) 
		throws SQLException
		{
			Class dataTypeFactoryClass = dataTypeFactory.getClass();
			String databaseProductName = databaseMetaData.getDatabaseProductName();

			Collection validDbProductCollection = (Collection)this.dataTypeFactoryToDbProductMap.get(dataTypeFactoryClass);
			if(validDbProductCollection != null)
			{
				String lowerCaseDbProductName = databaseProductName.toLowerCase();
				for (Iterator iterator = validDbProductCollection.iterator(); iterator.hasNext();) {
					String validDbProduct = (String) iterator.next();
					if(lowerCaseDbProductName.indexOf(validDbProduct) > -1) {
						logger.debug("The current database '" + databaseProductName + "' " +
								"fits to the configured data type factory '" + dataTypeFactory + "'. Validation successful.");
						return null;
					}
				}
			}
			
			// If we get here, the validation failed
			String validationMessage = "The configured data type factory '" + dataTypeFactoryClass + 
					"' might cause problems with the current database '" + databaseProductName + 
					"' (e.g. some datatypes may not be supported properly). " +
					"In rare cases you might see this message because the list of supported database " +
					"products is incomplete (list=" + validDbProductCollection + "). " +
					"If so please request a java-class update via the forums.";
//			String validationMessage = "The current database '" + databaseProductName + "' " +
//					"is not supported by the data type factory '" + dataTypeFactoryClass + "'. " +
//							"In some cases this can happen when the list of supported database " +
//							"products is incomplete (list=" + validDbProductCollection + "). " +
//									"If so please request a java-class update via the forums.";
			return validationMessage;
		}
	}
}
