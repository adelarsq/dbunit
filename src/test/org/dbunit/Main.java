/*
 * Main.java   Mar 14, 2002
 *
 * DbUnit Database Testing Framework
 * Copyright (C)2002, Manuel Laflamme
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

package org.dbunit;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.datatype.DataType;
import org.dbunit.dataset.FilteredDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;

import Base64;
import electric.xml.*;
import junit.framework.Assert;

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

//        System.out.println(connection.createDataSet().getTableMetaData("EMPTY_MULTITYPE_TABLE"));
        String[] tableNames = connection.createDataSet().getTableNames();
        Arrays.sort(tableNames);
        FlatXmlDataSet.writeDtd(new FilteredDataSet(tableNames,
                connection.createDataSet()),
                new FileOutputStream("test2.dtd"));
//        FlatXmlDataSet.write(new FilteredDataSet(tableNames,
//                connection.createDataSet()),
//                new FileOutputStream("test.xml"));

//        IDataSet dataSet = new FlatXmlDataSet(new FileInputStream("test.xml"));
//
//        DatabaseOperation.CLEAN_INSERT.execute(connection, dataSet);
//        DatabaseOperation.DELETE.execute(connection, dataSet);
//        DatabaseOperation.INSERT.execute(connection, dataSet);
//        DatabaseOperation.UPDATE.execute(connection, dataSet);
//        DatabaseOperation.REFRESH.execute(connection, dataSet);
//        DatabaseOperation.DELETE_ALL.execute(connection, dataSet);
//        DatabaseOperation.REFRESH.execute(connection, dataSet);


//        FlatXmlDataSet.write(new XmlDataSet(
//                new FileInputStream("src/xml/refreshOperationTest.xml")),
//                new FileOutputStream("src/xml/refreshOperationTestSetup.xml"));
    }

}








