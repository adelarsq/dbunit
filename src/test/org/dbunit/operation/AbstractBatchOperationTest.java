/*
 * AbstractBatchOperationTest.java   May 7, 2002
 *
 * Copyright 2002 Freeborders Canada Corp. All Rights Reserved.
 *
 * This software is the proprietary information of Karat Software Corp.
 * Use is subject to license terms.
 *
 */

package org.dbunit.operation;

import java.io.InputStream;
import java.io.FileInputStream;

import org.dbunit.AbstractDatabaseTest;
import org.dbunit.dataset.*;
import org.dbunit.dataset.xml.XmlDataSet;

/**
 * @author Manuel Laflamme
 * @version 1.0
 */
public class AbstractBatchOperationTest extends AbstractDatabaseTest
{
    public AbstractBatchOperationTest(String s)
    {
        super(s);
    }

    public void testGetOperationMetaData() throws Exception
    {
        InputStream in = new FileInputStream("src/xml/missingColumnTest.xml");
        IDataSet xmlDataSet = new XmlDataSet(in);

        ITable[] xmlTables = DataSetUtils.getTables(xmlDataSet);
        for (int i = 0; i < xmlTables.length; i++)
        {
            ITable xmlTable = xmlTables[i];

        }
    }

}
