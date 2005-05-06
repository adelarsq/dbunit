package org.dbunit.dataset.csv;

import java.io.File;

import org.dbunit.dataset.CachedDataSet;
import org.dbunit.dataset.DataSetException;

/**
 * This class constructs an IDataSet given a directory containing CSV
 * files. It handles translations of "null"(the string), into null. 
 *
 * @author Lenny Marks (lenny@aps.org)
 *
 */
public class CsvDataSet extends CachedDataSet {
	public static final String TABLE_ORDERING_FILE = "table-ordering.txt";
	
	private File dir;
	
    public CsvDataSet(File dir) throws DataSetException {
        super(new CsvProducer(dir));
        this.dir = dir;
    }
    
}
