/*
 * Main.java   Mar 14, 2002
 *
 * The DbUnit Database Testing Framework
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
import org.dbunit.dataset.xml.XmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.IDataSet;
import org.dbunit.operation.DatabaseOperation;

import java.io.FileOutputStream;
import java.io.FileInputStream;

/**
 * @author Manuel Laflamme
 * @version $Revision$
 */
public class Main
{
    public static void main(String[] args) throws Exception
    {
        System.setProperty("dbunit.name.escapePattern", "\"?\"");
        IDatabaseConnection connection =
                DatabaseEnvironment.getInstance().getConnection();
        IDataSet dataSet = new XmlDataSet(new FileInputStream("dataSetTest.xml"));
        DatabaseOperation.CLEAN_INSERT.execute(connection, dataSet);

//        String[] tableNames = connection.createDataSet().getTableNames();
//        Arrays.sort(tableNames);
//        FlatXmlDataSet.writeDtd(new FilteredDataSet(tableNames,
//                connection.createDataSet()),
//                new FileOutputStream("test.dtd"));
//
//
        FlatXmlDataSet.write(connection.createDataSet(),
                new FileOutputStream("test.xml"));
//        FlatXmlDataSet.write(new FlatXmlDataSet(
//                new FileInputStream("P:/dbunit-cvs/dbunit/src/xml/flatXmlDataSetDuplicateTest.xml")),
//                new FileOutputStream("flattest.xml"));


//        ////////////////////////////////
//        Document document = new Document(new File("src/xml/flatXmlDataSetTest.xml"));
//        DocType docType = document.getDocType();
//        System.out.println(docType);
//
//        // display children of DocType
//        for (Children decls = docType.getChildren(); decls.hasMoreElements();)
//        {
//            Child decl = decls.next();
//            String type = decl.getClass().getName();
//            System.out.println("decl = " + decl + ", class: " + type);
//        }

//        IDataSet dataSet = new FlatXmlDataSet(
//                new FileInputStream("flatXmlDataSetTest.xml"));
//        FlatDtdDataSet.write(new FlatXmlDataSet(
//                new FileInputStream("src/xml/flatXmlDataSetTest.xml")),
//                new FileOutputStream("src/dtd/flatXmlDataSetTest.dtd"));
    }

}

















