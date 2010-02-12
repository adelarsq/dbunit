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
package org.dbunit.dataset;

import org.dbunit.dataset.Columns.ColumnDiff;
import org.dbunit.dataset.datatype.DataType;

import junit.framework.TestCase;

/**
 * @author gommma
 * @version $Revision$
 * @since 2.3.0
 */
public class ColumnsTest extends TestCase
{
    public void testGetColumn() throws Exception
    {
        Column[] columns = new Column[]{
            new Column("c0", DataType.UNKNOWN),
            new Column("c1", DataType.UNKNOWN),
            new Column("c2", DataType.UNKNOWN),
            new Column("c3", DataType.UNKNOWN),
            new Column("c4", DataType.UNKNOWN),
        };

        for (int i = 0; i < columns.length; i++)
        {
            assertEquals("find column same", columns[i],
                    Columns.getColumn("c" + i, columns));
        }
    }

    public void testGetColumnCaseInsensitive() throws Exception
    {
        Column[] columns = new Column[]{
            new Column("c0", DataType.UNKNOWN),
            new Column("C1", DataType.UNKNOWN),
            new Column("c2", DataType.UNKNOWN),
            new Column("C3", DataType.UNKNOWN),
            new Column("c4", DataType.UNKNOWN),
        };

        for (int i = 0; i < columns.length; i++)
        {
            assertEquals("find column same", columns[i],
                    Columns.getColumn("c" + i, columns));
        }
    }

    public void testGetColumnValidated() throws Exception
    {
        Column[] columns = new Column[]{
                new Column("c0", DataType.UNKNOWN),
                new Column("C1", DataType.UNKNOWN),
                new Column("c2", DataType.UNKNOWN),
        };
        for (int i = 0; i < columns.length; i++)
        {
            assertEquals("find column same", columns[i],
                    Columns.getColumnValidated("c" + i, columns, "TableABC"));
        }
    }
    
    public void testGetColumnValidatedColumnNotFound() throws Exception
    {
        Column[] columns = new Column[]{
                new Column("c0", DataType.UNKNOWN),
                new Column("C1", DataType.UNKNOWN),
                new Column("c2", DataType.UNKNOWN),
        };
        try 
        {
            Columns.getColumnValidated("A1", columns, "TableABC");
            fail("Should not be able to get a validated column that does not exist");
        }
        catch(NoSuchColumnException expected)
        {
            assertEquals("TableABC.A1", expected.getMessage());
        }
    }

    public void testGetColumnDiff_NoDifference() throws Exception
    {
        Column[] expectedColumns = new Column[]{
                new Column("c0", DataType.UNKNOWN),
                new Column("c1", DataType.UNKNOWN),
        };
        Column[] actualColumns = new Column[]{
                new Column("c0", DataType.UNKNOWN),
                new Column("c1", DataType.UNKNOWN),
        };
        ITableMetaData metaDataExpected = createMetaData(expectedColumns);
        ITableMetaData metaDataActual = createMetaData(actualColumns);
        
        // Create the difference
        ColumnDiff diff = new ColumnDiff(metaDataExpected, metaDataActual);
        assertEquals(false, diff.hasDifference());
        assertEquals("[]", diff.getExpectedAsString());
        assertEquals("[]", diff.getActualAsString());
        assertEquals("no difference found", diff.getMessage());
    }

    public void testGetColumnDiffDifferentOrder_NoDifference() throws Exception
    {
    	// order [c0, c1]
        Column[] expectedColumns = new Column[]{
                new Column("c0", DataType.UNKNOWN),
                new Column("c1", DataType.UNKNOWN),
        };
        // order [c1, c0]
        Column[] actualColumnsDifferentOrder = new Column[]{
                new Column("c1", DataType.UNKNOWN),
                new Column("c0", DataType.UNKNOWN),
        };
        ITableMetaData metaDataExpected = createMetaData(expectedColumns);
        ITableMetaData metaDataActual = createMetaData(actualColumnsDifferentOrder);
        
        // Create the difference
        ColumnDiff diff = new ColumnDiff(metaDataExpected, metaDataActual);
        assertEquals(false, diff.hasDifference());
        assertEquals("[]", diff.getExpectedAsString());
        assertEquals("[]", diff.getActualAsString());
        assertEquals("no difference found", diff.getMessage());
    }

    
    public void testGetColumnDiff_Difference() throws Exception
    {
        Column[] expectedColumns = new Column[]{
                new Column("c0", DataType.UNKNOWN),
                new Column("c2", DataType.UNKNOWN),
                new Column("c1", DataType.UNKNOWN),
        };
        Column[] actualColumns = new Column[]{
                new Column("d0", DataType.UNKNOWN),
                new Column("c2", DataType.UNKNOWN),
        };
        ITableMetaData metaDataExpected = createMetaData(expectedColumns);
        ITableMetaData metaDataActual = createMetaData(actualColumns);
        
        // Create the difference
        ColumnDiff diff = new ColumnDiff(metaDataExpected, metaDataActual);
        assertEquals(true, diff.hasDifference());
        assertEquals(2, diff.getExpected().length);
        assertEquals(1, diff.getActual().length);
        assertEquals(expectedColumns[0], diff.getExpected()[0]);
        assertEquals(expectedColumns[2], diff.getExpected()[1]);
        assertEquals(actualColumns[0], diff.getActual()[0]);
        assertEquals("[c0, c1]", diff.getExpectedAsString());
        assertEquals("[d0]", diff.getActualAsString());
        assertEquals("column count (table=MY_TABLE, expectedColCount=3, actualColCount=2)", diff.getMessage());
    }


    
    private ITableMetaData createMetaData(Column[] columns)
    {
    	DefaultTableMetaData tableMetaData = new DefaultTableMetaData("MY_TABLE", columns);
    	return tableMetaData;
    }

}
