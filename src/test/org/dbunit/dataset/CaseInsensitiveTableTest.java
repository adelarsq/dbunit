/*
 * CaseInsensitiveTableTest.java   Mar 27, 2002
 *
 * Copyright (c)2002 Manuel Laflamme. All Rights Reserved.
 *
 * This software is the proprietary information of Manuel Laflamme.
 * Use is subject to license terms.
 *
 */

package org.dbunit.dataset;

import org.dbunit.dataset.xml.XmlDataSet;
import org.dbunit.dataset.xml.XmlTableTest;

import java.io.*;

/**
 * @author Manuel Laflamme
 * @version $Revision$
 */
public class CaseInsensitiveTableTest extends XmlTableTest
{
    public CaseInsensitiveTableTest(String s)
    {
        super(s);
    }

    protected ITable createTable() throws Exception
    {
        return new CaseInsensitiveTable(createDataSet().getTable("TEST_TABLE"));
    }

    protected IDataSet createDataSet() throws Exception
    {
        Reader in = new FileReader(
                new File("src/xml/caseInsensitiveTableTest.xml"));
        return new XmlDataSet(in);
    }

    public void testTableMetaData() throws Exception
    {
        Column[] columns = createTable().getTableMetaData().getColumns();
        assertEquals("column count", COLUMN_COUNT, columns.length);
        for (int i = 0; i < columns.length; i++)
        {
            String expected = "COLUMN" + i;
            String actual = columns[i].getColumnName();
            if (!actual.equalsIgnoreCase(expected))
            {
                assertEquals("column name", expected, actual);
            }
        }
    }
}

