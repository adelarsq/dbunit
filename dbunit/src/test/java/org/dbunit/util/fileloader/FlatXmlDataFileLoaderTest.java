/*
 *
 * The DbUnit Database Testing Framework
 * Copyright (C)2002-2008, DbUnit.org
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

package org.dbunit.util.fileloader;

import junit.framework.TestCase;

import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;

/**
 * @author Jeff Jensen jeffjensen AT users.sourceforge.net
 * @author Last changed by: $Author$
 * @version $Revision$ $Date$
 * @since 2.4.8
 */
public class FlatXmlDataFileLoaderTest extends TestCase {
    FlatXmlDataFileLoader loader = null;

    /**
     * {@inheritDoc}
     */
    protected void setUp() throws Exception {
        loader = new FlatXmlDataFileLoader();
    }

    /**
     * Test can load the specified file.
     */
    public void testLoad() throws DataSetException {
        String filename = "/xml/flatXmlDataSetTest.xml";
        IDataSet ds = loader.load(filename);
        assertTrue("No tables found in dataset.", ds.getTableNames().length > 0);
        // DataSet loading tests verify data accuracy
    }
}
