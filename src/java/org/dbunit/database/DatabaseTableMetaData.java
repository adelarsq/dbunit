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

package org.dbunit.database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.dbunit.dataset.AbstractTableMetaData;
import org.dbunit.dataset.Column;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.ITableMetaData;
import org.dbunit.dataset.NoSuchTableException;
import org.dbunit.dataset.datatype.DataType;
import org.dbunit.dataset.datatype.IDataTypeFactory;
import org.dbunit.dataset.filter.IColumnFilter;
import org.dbunit.util.QualifiedTableName;
import org.dbunit.util.SQLHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Manuel Laflamme
 * @version $Revision$
 * @since Mar 8, 2002
 */
public class DatabaseTableMetaData extends AbstractTableMetaData
{

    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(DatabaseTableMetaData.class);

    /**
     * Table name, potentially qualified
     */
    private final String _tableName;
    private final QualifiedTableName _qualifiedTableNameSupport;
    private final IDatabaseConnection _connection;
    private Column[] _columns;
    private Column[] _primaryKeys;

    
    DatabaseTableMetaData(String tableName, IDatabaseConnection connection) throws DataSetException
    {
    	this(tableName, connection, true);
    }
    
    /**
     * Creates a new database table metadata
     * @param tableName The name of the table - can be fully qualified
     * @param connection The database connection
     * @param validate Whether or not to validate the given input data. It is not recommended to
     * set the validation to <code>false</code> because it is then possible to create an instance
     * of this object for a db table that does not exist.
     * @throws DataSetException
     */
    DatabaseTableMetaData(String tableName, IDatabaseConnection connection, boolean validate) throws DataSetException
    {
    	if (tableName == null) {
			throw new NullPointerException("The parameter 'tableName' must not be null");
		}
    	if (connection == null) {
			throw new NullPointerException("The parameter 'connection' must not be null");
		}
    	
        _tableName = tableName;
        _connection = connection;
        // qualified names support
        this._qualifiedTableNameSupport = new QualifiedTableName(_tableName, _connection.getSchema());

        if(validate) 
        {
	        String schemaName = _qualifiedTableNameSupport.getSchema();
	        String plainTableName = _qualifiedTableNameSupport.getTable();
	        logger.debug("Validating if table '" + plainTableName + "' exists in schema '" + schemaName + "' ...");
	        try {
		        DatabaseMetaData databaseMetaData = connection.getConnection().getMetaData();
		        if(!SQLHelper.tableExists(databaseMetaData, schemaName, plainTableName)) {
		        	throw new NoSuchTableException("Did not find table '" + plainTableName + "' in schema '" + schemaName + "'");
		        }
	        }
	        catch (SQLException e)
	        {
	            throw new DataSetException("Exception while validation existence of table '" + plainTableName + "'", e);
	        }
        }
        else
        {
	        logger.debug("Validation switched off. Will not check if table exists.");
        }
    }

    /**
     * @param tableName
     * @param resultSet
     * @param dataTypeFactory
     * @return The table metadata created for the given parameters
     * @throws DataSetException
     * @throws SQLException
     * @deprecated since 2.3.0. use {@link ResultSetTableMetaData#ResultSetTableMetaData(String, ResultSet, IDataTypeFactory)}
     */
    public static ITableMetaData createMetaData(String tableName,
            ResultSet resultSet, IDataTypeFactory dataTypeFactory)
            throws DataSetException, SQLException
    {
    	if (logger.isDebugEnabled())
    	{
    		logger.debug("createMetaData(tableName={}, resultSet={}, dataTypeFactory={}) - start",
    				new Object[]{ tableName, resultSet, dataTypeFactory });
    	}

    	return new ResultSetTableMetaData(tableName, resultSet, dataTypeFactory);
    }


    
    /**
     * @param tableName
     * @param resultSet
     * @param connection
     * @return The table metadata created for the given parameters
     * @throws SQLException
     * @throws DataSetException
     * @deprecated since 2.3.0. use {@link ResultSetTableMetaData#ResultSetTableMetaData(String, ResultSet, IDatabaseConnection)}
     */
    public static ITableMetaData createMetaData(String tableName,
            ResultSet resultSet, IDatabaseConnection connection)
            throws SQLException, DataSetException
    {
    	if (logger.isDebugEnabled())
    	{
    		logger.debug("createMetaData(tableName={}, resultSet={}, connection={}) - start",
    				new Object[] { tableName, resultSet, connection });
    	}
    	return new ResultSetTableMetaData(tableName,resultSet,connection);
    }

    private String[] getPrimaryKeyNames() throws SQLException
    {
        logger.debug("getPrimaryKeyNames() - start");

    	String schemaName = _qualifiedTableNameSupport.getSchema();
    	String tableName = _qualifiedTableNameSupport.getTable();

        Connection connection = _connection.getConnection();
        DatabaseMetaData databaseMetaData = connection.getMetaData();
        ResultSet resultSet = databaseMetaData.getPrimaryKeys(
                null, schemaName, tableName);

        List list = new ArrayList();
        try
        {
            while (resultSet.next())
            {
                String name = resultSet.getString(4);
                int sequence = resultSet.getInt(5);
                list.add(new PrimaryKeyData(name, sequence));
            }
        }
        finally
        {
            resultSet.close();
        }

        Collections.sort(list);
        String[] keys = new String[list.size()];
        for (int i = 0; i < keys.length; i++)
        {
            PrimaryKeyData data = (PrimaryKeyData)list.get(i);
            keys[i] = data.getName();
        }

        return keys;
    }

    private class PrimaryKeyData implements Comparable
    {
        private final String _name;
        private final int _index;

        public PrimaryKeyData(String name, int index)
        {
            _name = name;
            _index = index;
        }

        public String getName()
        {
            logger.debug("getName() - start");

            return _name;
        }

        public int getIndex()
        {
            return _index;
        }

        ////////////////////////////////////////////////////////////////////////
        // Comparable interface

        public int compareTo(Object o)
        {
            PrimaryKeyData data = (PrimaryKeyData)o;
            return getIndex() - data.getIndex();
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    // ITableMetaData interface

    public String getTableName()
    {
        return _tableName;
    }

    public Column[] getColumns() throws DataSetException
    {
        logger.debug("getColumns() - start");

        if (_columns == null)
        {
            try
            {
                // qualified names support
            	String schemaName = _qualifiedTableNameSupport.getSchema();
            	String tableName = _qualifiedTableNameSupport.getTable();
            	
                Connection jdbcConnection = _connection.getConnection();
                DatabaseMetaData databaseMetaData = jdbcConnection.getMetaData();
                
                ResultSet resultSet = databaseMetaData.getColumns(
                        null, schemaName, tableName, "%");

                try
                {
                	DatabaseConfig config = _connection.getConfig();
                    IDataTypeFactory dataTypeFactory = super.getDataTypeFactory(_connection);
                    boolean datatypeWarning = config.getFeature(
                            DatabaseConfig.FEATURE_DATATYPE_WARNING);

                    List columnList = new ArrayList();
                    while (resultSet.next())
                    {
                        String columnName = resultSet.getString(4);
                        int sqlType = resultSet.getInt(5);
                        String sqlTypeName = resultSet.getString(6);
//                        int columnSize = resultSet.getInt(7);
                        int nullable = resultSet.getInt(11);

                        // Convert SQL type to DataType
                        DataType dataType =
                                dataTypeFactory.createDataType(sqlType, sqlTypeName, tableName, columnName);
                        if (dataType != DataType.UNKNOWN)
                        {
                            Column column = new Column(columnName, dataType,
                                    sqlTypeName, Column.nullableValue(nullable));
                            columnList.add(column);
                        }
                        else if (datatypeWarning)
                        {
                            logger.warn(
                                    tableName + "." + columnName +
                                    " data type (" + sqlType + ", '" + sqlTypeName +
                                    "') not recognized and will be ignored. See FAQ for more information.");
                        }
                    }

                    if (columnList.size() == 0)
                    {
                    	logger.warn("No columns found for table '"+ tableName +"' that are supported by dbunit. " +
                    			"Will return an empty column list");
                    }

                    _columns = (Column[])columnList.toArray(new Column[0]);
                }
                finally
                {
                    resultSet.close();
                }
            }
            catch (SQLException e)
            {
                throw new DataSetException(e);
            }
        }
        return _columns;
    }

    public Column[] getPrimaryKeys() throws DataSetException
    {
        logger.debug("getPrimaryKeys() - start");

        if (_primaryKeys == null)
        {
            try
            {
                DatabaseConfig config = _connection.getConfig();
                IColumnFilter primaryKeysFilter = (IColumnFilter)config.getProperty(
                        DatabaseConfig.PROPERTY_PRIMARY_KEY_FILTER);
                if (primaryKeysFilter != null)
                {
                    _primaryKeys = getPrimaryKeys(getTableName(), getColumns(),
                            primaryKeysFilter);
                }
                else
                {
                    _primaryKeys = getPrimaryKeys(getColumns(),
                            getPrimaryKeyNames());
                }
            }
            catch (SQLException e)
            {
                throw new DataSetException(e);
            }
        }
        return _primaryKeys;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Object class
    public String toString()
    {
        try
        {
            String tableName = getTableName();
            String columns = Arrays.asList(getColumns()).toString();
            String primaryKeys = Arrays.asList(getPrimaryKeys()).toString();
            return "table=" + tableName + ", cols=" + columns + ", pk=" + primaryKeys + "";
        }
        catch (DataSetException e)
        {
            return super.toString();
        }
    }
}
