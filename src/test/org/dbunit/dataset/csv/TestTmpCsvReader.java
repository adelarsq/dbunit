/*
 *
 * The DbUnit Database Testing Framework
 * Copyright (C)2002-2004, DbUnit.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

package org.dbunit.dataset.csv;

import junit.framework.TestCase;

import java.sql.SQLException;

public class TestTmpCSVReader extends TestCase {

    TmpCSVReader reader;

    void verify(String [] expected, String[] got) {

        assertEquals(expected.length, got.length);

        for (int i = 0; i < got.length; i++) {
            assertEquals (expected[i], got[i]);
        }

    }

    public void testUnquoted () throws SQLException {
        String [] expected = new String[] {"dario ", "mario " };
        verify(expected, reader.parseCsvLine(" dario , mario "));
    }

    public void testEmpty () throws SQLException {
        String [] expected = new String[] {"", "" };
        verify(expected, reader.parseCsvLine(" , "));
    }

    public void testQuoted () throws SQLException {
        String [] expected = new String[] {"dario", "mar, io" };
        verify(expected, reader.parseCsvLine("\"dario\", \"mar, io\""));
    }

    public void testUnquotedAndEmpty () throws SQLException {
        String [] expected = new String[] {"dario il lunario ", "mar\"io" };
        verify(expected, reader.parseCsvLine(" dario il lunario , mar\"\"io"));
    }

    public void testMixedAndEmpty () throws SQLException {
        String [] expected = new String[] {"dario il \"lunario\" ", "calendario, \"lunare", "mar\"io", "" };
        verify(expected, reader.parseCsvLine(" dario il \"\"lunario\"\" , \"calendario, \"\"lunare\", mar\"\"io,  "));
    }

    public void testUnquotedWithNewline () throws SQLException {
        String [] expected = new String[] {"dario il \n lunario", "calendario, \"lun\nare", "mar\"io", "" };
        verify(expected, reader.parseCsvLine(" dario il \n lunario, \"calendario, \"\"lun\nare\", mar\"\"io,  "));
    }

    protected void setUp() throws Exception {
        reader = new TmpCSVReader("src/csv/ordini.csv");
    }

}
