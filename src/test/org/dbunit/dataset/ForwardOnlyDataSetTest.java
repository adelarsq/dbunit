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
 * @author Last changed by: $Author$
 * @version $Revision$ $Date$
 * @since 1.x (Apr 11, 2003)
 */
public class ForwardOnlyDataSetTest extends DefaultDataSetTest
{
    public ForwardOnlyDataSetTest(String s)
    {
        super(s);
    }

    protected IDataSet createDataSet() throws Exception
    {
        return new ForwardOnlyDataSet(super.createDataSet());
    }

    protected IDataSet createDuplicateDataSet() throws Exception
    {
        throw new UnsupportedOperationException();
    }

    protected IDataSet createMultipleCaseDuplicateDataSet() throws Exception 
    {
        throw new UnsupportedOperationException();
    }

    public void testGetTableNames() throws Exception
    {
        try
        {
            createDataSet().getTableNames();
            fail("Should have throw UnsupportedOperationException");
        }
        catch (UnsupportedOperationException e)
        {

        }
    }

    public void testGetTable() throws Exception
    {
        String[] tableNames = getExpectedNames();
        try
        {
            createDataSet().getTable(tableNames[0]);
            fail("Should have throw UnsupportedOperationException");
        }
        catch (UnsupportedOperationException e)
        {

        }
    }

    public void testGetTableMetaData() throws Exception
    {
        String[] tableNames = getExpectedNames();
        try
        {
            createDataSet().getTableMetaData(tableNames[0]);
            fail("Should have throw UnsupportedOperationException");
        }
        catch (UnsupportedOperationException e)
        {

        }
    }

    public void testReverseIterator() throws Exception
    {
        try
        {
            createDataSet().reverseIterator();
            fail("Should have throw UnsupportedOperationException");
        }
        catch (UnsupportedOperationException e)
        {

        }
    }

    public void testGetTableNamesDefensiveCopy() throws Exception
    {
        // Cannot test! Unsupported feature.
    }

    public void testGetUnknownTable() throws Exception
    {
        // Cannot test! Unsupported feature.
    }

    public void testGetUnknownTableMetaData() throws Exception
    {
        // Cannot test! Unsupported feature.
    }

    public void testGetTablesDefensiveCopy() throws Exception
    {
        // Cannot test! Unsupported feature.
    }

    public void testGetCaseInsensitiveTable() throws Exception
    {
        // Cannot test! Unsupported feature.
    }

    public void testGetCaseInsensitiveTableMetaData() throws Exception
    {
        // Cannot test! Unsupported feature.
    }

    public void testCreateDuplicateDataSet() throws Exception 
    {
        // No op. This dataSet is only a wrapper for another dataSet which is why duplicates cannot occur.
    }

    public void testCreateMultipleCaseDuplicateDataSet() throws Exception 
    {
        // No op. This dataSet is only a wrapper for another dataSet which is why duplicates cannot occur.
    }

    
}
