/*
 * Main.java   Mar 14, 2002
 *
 * Copyright 2002 Freeborders Canada Corp. All Rights Reserved.
 *
 * This software is the proprietary information of Karat Software Corp.
 * Use is subject to license terms.
 *
 */

package org.dbunit;

import java.io.FileOutputStream;
import java.util.Arrays;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.ITableMetaData;
import org.dbunit.dataset.FilteredDataSet;

/**
 * @author Manuel Laflamme
 * @version 1.0
 */
public class Main
{
    public static void main(String[] args) throws Exception
    {
        IDatabaseConnection connection =
                DatabaseEnvironment.getInstance().getConnection();

//        // initialize database connection here
//        IDatabaseConnection connection = ...

        // write DTD for database
        String[] tableNames = connection.createDataSet().getTableNames();
        Arrays.sort(tableNames);
        FlatXmlDataSet.writeDtd(new FilteredDataSet(tableNames,
                connection.createDataSet()),
                new FileOutputStream("test2.dtd"));
    }
}
