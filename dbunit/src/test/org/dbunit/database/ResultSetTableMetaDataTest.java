package org.dbunit.database;

import java.io.File;
import java.sql.Connection;

import org.dbunit.AbstractDatabaseTest;
import org.dbunit.HypersonicEnvironment;
import org.dbunit.dataset.Column;
import org.dbunit.dataset.Columns;
import org.dbunit.dataset.IDataSet;

/**
 * @author gommma (gommma AT users.sourceforge.net)
 * @author Last changed by: $Author$
 * @version $Revision$ $Date$
 * @since 2.4.0
 */
public class ResultSetTableMetaDataTest extends AbstractDatabaseTest
{
    
    public ResultSetTableMetaDataTest(String s)
    {
        super(s);
    }

    protected IDataSet createDataSet() throws Exception
    {
        return _connection.createDataSet();
    }

    /**
     * Tests the pattern-like column retrieval from the database. DbUnit
     * should not interpret any table names as regex patterns. 
     * @throws Exception
     */
    public void testGetColumnsForTablesMatchingSamePattern() throws Exception
    {
        Connection jdbcConnection = HypersonicEnvironment.createJdbcConnection("tempdb");
        HypersonicEnvironment.executeDdlFile(new File("src/sql/hypersonic_dataset_pattern_test.sql"),
                jdbcConnection);
        IDatabaseConnection connection = new DatabaseConnection(jdbcConnection);

        try {
            String tableName = "PATTERN_LIKE_TABLE_X_";
            String[] columnNames = {"VARCHAR_COL_XUNDERSCORE"};
    
            String sql = "select * from " + tableName;
            ForwardOnlyResultSetTable resultSetTable = new ForwardOnlyResultSetTable(tableName, sql, connection);
            ResultSetTableMetaData metaData = (ResultSetTableMetaData) resultSetTable.getTableMetaData();
            
            Column[] columns = metaData.getColumns();
    
            assertEquals("column count", columnNames.length, columns.length);
    
            for (int i = 0; i < columnNames.length; i++)
            {
                Column column = Columns.getColumn(columnNames[i], columns);
                assertEquals(columnNames[i], columnNames[i], column.getColumnName());
            }
        }
        finally {
            HypersonicEnvironment.shutdown(jdbcConnection);
            jdbcConnection.close();
            HypersonicEnvironment.deleteFiles("tempdb");
        }
    }

}
