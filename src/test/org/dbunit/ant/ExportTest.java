package org.dbunit.ant;

import junit.framework.TestCase;

/**
 * Created By:   fede
 * Date:         11-mar-2004 
 * Time:         10.54.02
 *
 * Last Checkin: $Author$
 * Date:         $Date$
 * Revision:     $Revision$
 */
public class ExportTest extends TestCase {
    Export export = new Export();

    public void testAcceptCsvFormat () {
        export.setFormat("csv");
        assertTrue(true);
    }

}
