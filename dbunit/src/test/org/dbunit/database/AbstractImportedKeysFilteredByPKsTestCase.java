package org.dbunit.database;

import java.sql.SQLException;

import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;

import org.dbunit.database.search.TablesDependencyHelper;
import org.dbunit.util.search.SearchException;

public abstract class AbstractImportedKeysFilteredByPKsTestCase extends
    AbstractSearchCallbackFilteredByPKsTestCase {

  public AbstractImportedKeysFilteredByPKsTestCase(String testName,
      String sqlFile) {
    super(testName, sqlFile);
  }

  protected IDataSet getDataset() throws SQLException, SearchException, DataSetException  {
    IDataSet dataset = TablesDependencyHelper.getDataset( getConnection(), getInput() );
    return dataset;
  }

}
