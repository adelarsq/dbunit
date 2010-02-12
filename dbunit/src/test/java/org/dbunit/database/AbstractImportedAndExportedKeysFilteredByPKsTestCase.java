package org.dbunit.database;

import java.sql.SQLException;

import org.dbunit.database.search.TablesDependencyHelper;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.util.search.SearchException;


public abstract class AbstractImportedAndExportedKeysFilteredByPKsTestCase extends
AbstractSearchCallbackFilteredByPKsTestCase 
{

    public AbstractImportedAndExportedKeysFilteredByPKsTestCase(String testName,
            String sqlFile) 
    {
        super(testName, sqlFile);
    }

    protected IDataSet getDataset() throws SQLException, SearchException, DataSetException
    {
        IDataSet dataset = TablesDependencyHelper.getAllDataset( getConnection(), getInput() );
        return dataset;
    }

}
