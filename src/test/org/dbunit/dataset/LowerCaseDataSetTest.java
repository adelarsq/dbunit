/*
 * CaseInsensitiveDataSetTest.java   Feb 14, 2003
 *
 * Copyright (c)2002 Manuel Laflamme. All Rights Reserved.
 *
 * This software is the proprietary information of Manuel Laflamme.
 * Use is subject to license terms.
 *
 */

package org.dbunit.dataset;

import java.io.FileReader;

import org.dbunit.dataset.xml.FlatXmlDataSet;

/**
 * @author Manuel Laflamme
 * @version $Revision$
 */
public class LowerCaseDataSetTest extends AbstractDataSetTest
{
    public LowerCaseDataSetTest(String s)
    {
        super(s);
    }

    protected IDataSet createDataSet() throws Exception
    {
        return new LowerCaseDataSet(new FlatXmlDataSet(new FileReader(
                "src/xml/flatXmlDataSetTest.xml")));
    }

    protected IDataSet createDuplicateDataSet() throws Exception
    {
        return new LowerCaseDataSet(new FlatXmlDataSet(new FileReader(
                "src/xml/flatXmlDataSetDuplicateTest.xml")));
    }

    protected String[] getExpectedNames() throws Exception
    {
        return getExpectedLowerNames();
    }

    protected String[] getExpectedDuplicateNames()
    {
        String[] names = super.getExpectedDuplicateNames();
        for (int i = 0; i < names.length; i++)
        {
            names[i] = names[i].toLowerCase();
        }

        return names;
    }


}


