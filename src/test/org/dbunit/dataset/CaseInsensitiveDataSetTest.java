/*
 * CaseInsensitiveDataSetTest.java   Mar 27, 2002
 *
 * Copyright (c)2002 Manuel Laflamme. All Rights Reserved.
 *
 * This software is the proprietary information of Manuel Laflamme.
 * Use is subject to license terms.
 *
 */

package org.dbunit.dataset;

import org.dbunit.dataset.xml.XmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

import java.io.FileInputStream;
import java.io.FileReader;

/**
 * @author Manuel Laflamme
 * @version $Revision$
 */
public class CaseInsensitiveDataSetTest extends AbstractDataSetTest
{
    public CaseInsensitiveDataSetTest(String s)
    {
        super(s);
    }

    protected IDataSet createDataSet() throws Exception
    {
        return new CaseInsensitiveDataSet(new XmlDataSet(new FileReader(
                "src/xml/caseInsensitiveDataSetTest.xml")));
    }

    protected IDataSet createDuplicateDataSet() throws Exception
    {
        return new CaseInsensitiveDataSet(new FlatXmlDataSet(new FileReader(
                "src/xml/caseInsensitiveDataSetDuplicateTest.xml")));
    }

    protected void assertEqualsTableName(String message, String expected,
            String actual)
    {
        assertEqualsIgnoreCase(message, expected, actual);
    }
}


