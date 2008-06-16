package org.dbunit.dataset;


/**
 * Provides arbitrary values for one single database/ITable row.
 * 
 * @author gommmma
 * @since 2.3.0
 */
public interface IRowValueProvider {

	/**
	 * Returns the column value for the column with the given name of the currently processed row
     * @param columnName The db column name for which the value should be provided (current row's value)
     * @return The value of the given column in the current row
     * @throws DataSetException
     */
    public Object getColumnValue(String columnName) throws DataSetException;
}
