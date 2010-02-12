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
package org.dbunit.dataset.stream;

import java.io.FileReader;

import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.ForwardOnlyDataSetTest;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetTest;
import org.dbunit.dataset.xml.FlatXmlProducer;
import org.xml.sax.InputSource;

/**
 * @author Manuel Laflamme
 * @author Last changed by: $Author$
 * @version $Revision$ $Date$
 * @since 1.x (Apr 18, 2003)
 */
public class StreamingDataSetTest extends ForwardOnlyDataSetTest
{
    public StreamingDataSetTest(String s)
    {
        super(s);
    }

    protected IDataSet createDataSet() throws Exception
    {
        IDataSetProducer source = new FlatXmlProducer(
                new InputSource(new FileReader(FlatXmlDataSetTest.DATASET_FILE)));
        return new StreamingDataSet(source);
    }

    protected IDataSet createDuplicateDataSet() throws Exception
    {
        return new StreamingDataSet(
                new DataSetProducerAdapter(super.createDuplicateDataSet()));
    }
    
    public void testReturnsOnException() throws Exception
    {
    	RuntimeException exceptionToThrow = new IllegalArgumentException("For this test case we throw something that we normally would never do");
    	ExceptionThrowingProducer source = new ExceptionThrowingProducer(exceptionToThrow);
    	StreamingDataSet streamingDataSet = new StreamingDataSet(source);
    	try {
    		streamingDataSet.createIterator(false);
    	}
    	catch(DataSetException expected) {
    		Throwable cause = expected.getCause();
    		assertEquals(IllegalArgumentException.class, cause.getClass());
    		assertEquals(exceptionToThrow, cause);
    	}
    }
    
    private static class ExceptionThrowingProducer implements IDataSetProducer
    {
    	private RuntimeException exceptionToThrow;
    	
		public ExceptionThrowingProducer(RuntimeException exceptionToThrow) {
			super();
			this.exceptionToThrow = exceptionToThrow;
		}

		public void produce() throws DataSetException {
			throw exceptionToThrow;
		}

		public void setConsumer(IDataSetConsumer consumer)
				throws DataSetException {
			// Ignore for this test
		}
    	
    }
}
