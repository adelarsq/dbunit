/*
 *
 * The DbUnit Database Testing Framework
 * Copyright (C)2002-2008, DbUnit.org
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
package org.dbunit.util;

import java.math.BigDecimal;

import org.dbunit.dataset.Column;
import org.dbunit.dataset.DefaultTable;
import org.dbunit.dataset.datatype.DataType;

import junit.framework.TestCase;

/**
 * Unit test for the {@link TableFormatter}
 * @author gommma (gommma AT users.sourceforge.net)
 * @author Last changed by: $Author$
 * @version $Revision$ $Date$
 * @since 2.4.1
 */
public class TableFormatterTest extends TestCase 
{

    public void testFormatSimpleTable() throws Exception
    {
        Column[] cols = new Column[]{
             new Column("COL1", DataType.VARCHAR),   
             new Column("COL2", DataType.NUMERIC)   
        };
        DefaultTable table = new DefaultTable("MY_TABLE", cols);
        table.addRow(new Object[]{
           "my string value", new BigDecimal("39284.1")
        });
        table.addRow(new Object[]{
            "my string value2", new BigDecimal("2")     
         });
        
        TableFormatter formatter = new TableFormatter();
        String actual = formatter.format(table);
        
        String expected = 
            "****** table: MY_TABLE ** row count: 2 ******\n"+
            "COL1                |COL2                |\n"+
            "====================|====================|\n"+
            "my string value     |39284.1             |\n"+
            "my string value2    |2                   |\n";
        assertEquals(expected, actual);
//        System.out.println(actual);
    }
}
