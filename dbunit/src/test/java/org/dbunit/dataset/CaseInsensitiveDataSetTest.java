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

package org.dbunit.dataset;

import java.io.FileReader;

import org.dbunit.dataset.xml.XmlDataSet;
import org.dbunit.testutil.TestUtils;

/**
 * @author Manuel Laflamme
 * @author Last changed by: $Author$
 * @version $Revision$ $Date$
 * @since 1.0 (Mar 27, 2002)
 */
public class CaseInsensitiveDataSetTest extends AbstractDataSetTest
{
    public CaseInsensitiveDataSetTest(String s)
    {
        super(s);
    }

    protected IDataSet createDataSet() throws Exception
    {
        return new CaseInsensitiveDataSet(new XmlDataSet(TestUtils.getFileReader(
                "xml/caseInsensitiveDataSetTest.xml")));
    }

    protected IDataSet createDuplicateDataSet() throws Exception
    {
        throw new UnsupportedOperationException();
    }

    protected IDataSet createMultipleCaseDuplicateDataSet() throws Exception 
    {
        throw new UnsupportedOperationException();
    }

    protected void assertEqualsTableName(String message, String expected,
            String actual)
    {
        assertEqualsIgnoreCase(message, expected, actual);
    }
    
    public void testCreateDuplicateDataSet() throws Exception 
    {
        // No op. This dataSet is only a wrapper for another dataSet which is why duplicates cannot occur.
    }

    public void testCreateMultipleCaseDuplicateDataSet() throws Exception 
    {
        // No op. This dataSet is only a wrapper for another dataSet which is why duplicates cannot occur.
    }

}


