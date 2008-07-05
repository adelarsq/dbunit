package org.dbunit.dataset;

import org.dbunit.dataset.datatype.DataType;

import junit.framework.TestCase;

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

}
