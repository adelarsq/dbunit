package org.dbunit.dataset.csv;

import java.io.*;
import java.io.File;
import java.io.FileWriter;
import java.util.*;
import java.util.ArrayList;
import java.util.Comparator;

import org.dbunit.dataset.*;
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
    
    /**
     * Return an IDataSet ordered using table-orderings.txt in this
     * CsvDataSets directory.
     * 
     */
    public IDataSet getOrdered() throws DataSetException, IOException {
		File tableOrderingFile = new File(dir, TABLE_ORDERING_FILE);
		String[] tableNames = getTableNames(); 
		Arrays.sort(tableNames, new TableComparator(tableOrderingFile));
		return new FilteredDataSet(tableNames, this); 
    }
    
    public void row(Object[] values) throws DataSetException {
        Object[] newValues = new Object[values.length];
        for(int i = 0; i < values.length; i++) {
            newValues[i] = values[i].equals(CsvDataSetWriter.NULL) ? null : values[i];
        }
        super.row(newValues);
    }
    
    /**
     * Write out the <i>dataSet</i> to CSV files in <i>dir</i>. Also
     * generate table-orderings.txt from the ordering in the <i>dataSet</i>.
     *
     */
    public static void write(IDataSet dataSet, File dir) throws DataSetException, IOException {
		CsvDataSetWriter.write(dataSet, dir);
		generateTableOrderingFile(dataSet, dir);
    }
    
	private static void generateTableOrderingFile(IDataSet dataSet, File dest) throws IOException, DataSetException {
		File outFile = new File(dest, TABLE_ORDERING_FILE);
		
		PrintWriter writer = new PrintWriter(new FileWriter(outFile));
		String[] tableNames = dataSet.getTableNames();
		for(int i = 0; i < tableNames.length; i++) {
			writer.print(tableNames[i] + "\n");
		}
		writer.close();	

	}
    
	private static class TableComparator implements Comparator {
		
		private List orderedNames = new ArrayList();
		
		public TableComparator(File tableOrderingFile) throws IOException {
			BufferedReader reader = 
			new BufferedReader(new FileReader(tableOrderingFile));
			String line = null;
			while((line = reader.readLine()) != null) {
				orderedNames.add(line.trim());
			}
			reader.close();
		}
		
		public int compare(Object o1, Object o2) {
			Integer o1Int = new Integer(orderedNames.indexOf(o1));
			Integer o2Int = new Integer(orderedNames.indexOf(o2));
			return o1Int.compareTo(o2Int);
			
		}
		
	}
}
