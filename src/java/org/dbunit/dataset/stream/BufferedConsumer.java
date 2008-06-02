package org.dbunit.dataset.stream;

import java.util.ArrayList;
import java.util.Iterator;

import org.dbunit.dataset.Column;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.ITableMetaData;

public class BufferedConsumer implements IDataSetConsumer {

	private IDataSetConsumer _wrappedConsumer;
	
	private ITableMetaData _activeMetaData;
	/**
	 * List buffer where every list entry is a Object[] for one single row
	 */
	private ArrayList _allRows = new ArrayList();

	
	
	public BufferedConsumer(IDataSetConsumer wrappedConsumer) 
	{
		if (wrappedConsumer == null) {
			throw new NullPointerException(
					"The parameter '_wrappedConsumer' must not be null");
		}
		this._wrappedConsumer = wrappedConsumer;
	}
	
	public void startDataSet() throws DataSetException 
	{
		this._wrappedConsumer.startDataSet();
	}

	public void endDataSet() throws DataSetException 
	{
		this._wrappedConsumer.endDataSet();
	}

	public void row(Object[] values) throws DataSetException 
	{
		// Just collect/buffer the row
		this._allRows.add(values);
	}

	public void startTable(ITableMetaData metaData) throws DataSetException 
	{
		// Do nothing here - we will buffer all data in the "row" method in order to write
		// them in the "endTable" method
		this._activeMetaData = metaData;
	}

	public void endTable() throws DataSetException 
	{
		if(this._activeMetaData == null) {
			throw new IllegalStateException("The field _activeMetaData must not be null at this stage");
		}

		Column[] columns = this._activeMetaData.getColumns();
		int finalColumnCount = columns.length;

		int rowCount = this._allRows.size();
		// Fill up columns that were potentially missing in this row
		for (int i=0; i < rowCount; i++) {
			// Note that this only works when new columns are always added at the end to the _activeMetaData
			Object[] rowValues = (Object[]) this._allRows.get(i);
			// If this row has less columns than final metaData, fill it up with "null"s so that it matches the length
			if(rowValues.length < finalColumnCount) {
				Object[] newRowValues = new Object[finalColumnCount];
				// Put in original values and leave all missing columns on "null"
				System.arraycopy(rowValues, 0, newRowValues, 0, rowValues.length);
				this._allRows.set(i, newRowValues);
			}
		}
		

		// Start the table with the final metadata
		this._wrappedConsumer.startTable(this._activeMetaData);
		
		// flush the buffer using all gathered/buffered information
		for (Iterator iterator = _allRows.iterator(); iterator.hasNext();) {
			Object[] rowValues = (Object[]) iterator.next();
			this._wrappedConsumer.row(rowValues);
		}
		
		this._wrappedConsumer.endTable();
	}

}
