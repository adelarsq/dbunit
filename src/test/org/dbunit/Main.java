/*
 * Main.java   Mar 14, 2002
 *
 * The dbUnit database testing framework.
 * Copyright (C) 2002   Manuel Laflamme
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

import java.io.File;

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
        String s1 = Base64.encodeBytes(new byte[]{0, 1, 2, 3, 4, 5, 6,7 , 8, 9});
        String s2 = Base64.encodeBytes(new byte[80]);
        String s3 = "   ";
//        System.out.println(s1);
//        System.out.println(s2);

        Document document = new Document();
        Element rootElem = document.addElement("root");
        rootElem.addElement("oneline").addText(new CData(s1));
        rootElem.addElement("manylines").addText(new CData(s2));
        rootElem.addElement("spaces").addText(s3);
        rootElem.addElement("spaces").addText(new CData(s3));
        document.write(new File("test.xml"));

        document = new Document(new File("test.xml"));
        System.out.println(document);

        Element elem = document.getElement("root").getElement("manylines");
        System.out.println(elem.getText());
        System.out.println(elem.getText().getString());
//        Text text2 = new Text(new CData());
//        text2.setRaw(true);
//        String s3 = text.getBytes();
//
//        System.out.println(s3);
//        Assert.assertEquals(s2, s3);

//        IDatabaseConnection connection =
//                DatabaseEnvironment.getInstance().getConnection();

//        System.out.println(connection.createDataSet().getTableMetaData("EMPTY_MULTITYPE_TABLE"));
//        String[] tableNames = connection.createDataSet().getTableNames();
//        Arrays.sort(tableNames);
//        FlatXmlDataSet.writeDtd(new FilteredDataSet(tableNames,
//                connection.createDataSet()),
//                new FileOutputStream("test2.dtd"));
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







