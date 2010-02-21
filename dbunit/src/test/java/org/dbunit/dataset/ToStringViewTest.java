package org.dbunit.dataset;

import junit.framework.TestCase;

import org.dbunit.database.CachedResultSetTable;
import org.dbunit.database.ForwardOnlyResultSetTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class only logs out the toString() results for review, does not test
 * anything. Currently only ITables that subclass AbstractTable.
 * 
 * @author Jeff Jensen jeffjensen AT users.sourceforge.net
 * @author Last changed by: $Author$
 * @version $Revision$ $Date$
 * @since 2.4.8
 */
public class ToStringViewTest extends TestCase {
    private final Logger LOG = LoggerFactory.getLogger(ToStringViewTest.class);

    public void testForwardOnlyResultSetTable() {
        // TODO existing test is an IT
        ForwardOnlyResultSetTable table = null;
        LOG.info("ForwardOnlyResultSetTable.toString()={}", table);
    }

    public void testScrollableResultSetTable() throws Exception {
        // TODO existing test is an IT
        // ScrollableResultSetTableTest test =
        // new ScrollableResultSetTableTest("the string");
        ITable table = null; // test.createTable();
        LOG.info("ScrollableResultSetTable.toString()={}", table);
    }

    public void testCompositeTable() throws Exception {
        CompositeTableTest test = new CompositeTableTest("the string");
        ITable table = test.createTable();
        LOG.info("CompositeTable.toString()={}", table);
    }

    public void testDefaultTable() throws Exception {
        DefaultTableTest test = new DefaultTableTest("the string");
        ITable table = test.createTable();
        LOG.info("DefaultTable.toString()={}", table);
    }

    public void testCachedTable() {
        // TODO no existing test to use
        CachedTable table = null;
        LOG.info("CachedTable.toString()={}", table);
    }

    public void testCachedResultSetTable() {
        // TODO existing test is an IT
        CachedResultSetTable table = null;
        LOG.info("CachedResultSetTable.toString()={}", table);
    }

    public void testSortedTable() throws Exception {
        SortedTableTest test = new SortedTableTest("the string");
        ITable table = test.createTable();
        LOG.info("SortedTable.toString()={}", table);
    }

    public void testStreamingTable() {
        // StreamingTable is not a public class
        // StreamingTable table = null;
        // LOG.info("StreamingTable.toString()={}", table);
    }

    public void testXlsTable() {
        // XlsTable is not a public class
        // XlsTable table = null;
        // LOG.info("XlsTable.toString()={}", table);
    }
}
