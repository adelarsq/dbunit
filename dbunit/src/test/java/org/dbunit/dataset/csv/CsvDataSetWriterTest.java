package org.dbunit.dataset.csv;

import junit.framework.TestCase;
import org.dbunit.Assertion;
import org.dbunit.dataset.CachedDataSet;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.testutil.TestUtils;

import java.io.File;

/**
 * Created By:   fede
 * Date:         10-mar-2004 
 * Time:         17.21.34
 *
 * Last Checkin: $Author$
 * Date:         $Date$
 * Revision:     $Revision$
 */
public class CsvDataSetWriterTest extends TestCase {
    private static final String DEST = "target/csv/orders-out";
    private static final String SOURCE = TestUtils.getFileName("csv/orders");

    public void testProduceAndWriteBackToDisk () throws Exception {
        produceToFolder (SOURCE, DEST);
        IDataSet expected = produceToMemory (SOURCE);
        IDataSet actual = produceToMemory(DEST);
        Assertion.assertEquals(expected, actual);
    }

    private IDataSet produceToMemory(String source) throws DataSetException {
        CsvProducer producer = new CsvProducer(source);
        CachedDataSet cached = new CachedDataSet();
        producer.setConsumer(cached);
        producer.produce();
        return cached;
    }

    private void produceToFolder(String source, String dest) throws DataSetException {
        CsvProducer producer = new CsvProducer(source);
        new File(dest).delete();
        CsvDataSetWriter writer = new CsvDataSetWriter(dest);
        producer.setConsumer(writer);
        producer.produce();
    }

    public void testEscapeQuote () {
        assertEquals("\\\"foo\\\"", CsvDataSetWriter.escape("\"foo\""));
    }

    public void testEscapeEscape () {
        assertEquals("\\\\foo\\\\", CsvDataSetWriter.escape("\\foo\\"));
    }

}
