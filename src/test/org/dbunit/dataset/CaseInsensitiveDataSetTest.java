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

import java.io.FileInputStream;

/**
 * @author Manuel Laflamme
 * @version 1.0
 */
public class CaseInsensitiveDataSetTest extends AbstractDataSetTest
{
    public CaseInsensitiveDataSetTest(String s)
    {
        super(s);
    }

    protected IDataSet createDataSet() throws Exception
    {
        return new CaseInsensitiveDataSet(new XmlDataSet(new FileInputStream(
                "src/xml/caseInsensitiveDataSetTest.xml")));
    }

    protected void assertEqualsTableName(String mesage, String expected,
            String actual)
    {
        if (!expected.equalsIgnoreCase(actual))
        {
            super.assertEqualsTableName(mesage, expected, actual);
        }
    }
}

