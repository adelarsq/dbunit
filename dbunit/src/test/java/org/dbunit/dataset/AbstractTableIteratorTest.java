/*
 *
 * The DbUnit Database Testing Framework
 * Copyright (C)2002-2004, DbUnit.org
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

/**
 * @author Manuel Laflamme
 * @since Apr 6, 2003
 * @version $Revision$
 */
public abstract class AbstractTableIteratorTest extends AbstractTest
{
    public AbstractTableIteratorTest(String s)
    {
        super(s);
    }

    protected abstract ITableIterator getIterator() throws Exception;

    protected abstract ITableIterator getEmptyIterator() throws Exception;

    public void testNext() throws Exception
    {
        int count = 0;
        String[] names = getExpectedNames();
        ITableIterator iterator = getIterator();
        while(iterator.next())
        {
            count++;
        }

        assertEquals("count", names.length, count);
    }

    public void testNextAndEmpty() throws Exception
    {
        int count = 0;
        ITableIterator iterator = getEmptyIterator();
        while(iterator.next())
        {
            count++;
        }

        assertEquals("count", 0, count);
    }

    public void testGetTableMetaData() throws Exception
    {
        int i = 0;
        String[] names = getExpectedNames();
        ITableIterator iterator = getIterator();
        while(iterator.next())
        {
            assertEquals("name " + i, names[i],
                    iterator.getTableMetaData().getTableName());
            i++;
        }

        assertEquals("count", names.length, i);
    }

    public void testGetTableMetaDataBeforeNext() throws Exception
    {
        ITableIterator iterator = getIterator();
        try
        {
            iterator.getTableMetaData();
            fail("Should have throw a ???Exception");
        }
        catch (IndexOutOfBoundsException e)
        {

        }

        int i = 0;
        String[] names = getExpectedNames();
        while(iterator.next())
        {
            assertEquals("name " + i, names[i],
                    iterator.getTableMetaData().getTableName());
            i++;
        }

        assertEquals("count", names.length, i);
    }

    public void testGetTableMetaDataAfterLastNext() throws Exception
    {
        int count = 0;
        String[] names = getExpectedNames();
        ITableIterator iterator = getIterator();
        while(iterator.next())
        {
            count++;
        }

        assertEquals("count", names.length, count);

        try
        {
            iterator.getTableMetaData();
            fail("Should have throw a ???Exception");
        }
        catch (IndexOutOfBoundsException e)
        {
        }
    }

    public void testGetTable() throws Exception
    {
        int i = 0;
        String[] names = getExpectedNames();
        ITableIterator iterator = getIterator();
        while(iterator.next())
        {
            assertEquals("name " + i, names[i],
                    iterator.getTable().getTableMetaData().getTableName());
            i++;
        }

        assertEquals("count", names.length, i);
    }
}
