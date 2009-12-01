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
package org.dbunit.dataset.datatype;

import java.sql.Blob;
import java.sql.Types;

import junit.framework.TestCase;
import junitx.framework.ArrayAssert;

import org.dbunit.database.statement.MockPreparedStatement;

/**
 * @author gommma
 * @version $Revision$
 * @since 2.3.0
 */
public class BlobDataTypeTest extends TestCase 
{
	private DataType TYPE = DataType.BLOB;

	public BlobDataTypeTest(String name) {
		super(name);
	}

	public void testGetSqlType()
	{
		assertEquals(Types.BLOB, TYPE.getSqlType());
	}

	public void testSetSqlValue() throws Exception
	{
		// Create a hsqldb specific blob
		byte[] byteArray = new byte[]{1, 2, 3, 4, 5, 6};
		Blob blob = new TestBlob(byteArray);
		MockPreparedStatement statement = new MockPreparedStatement();
		TYPE.setSqlValue(blob, 1, statement);
		assertEquals(1, statement.getLastSetObjectParamIndex());
		assertEquals(Types.BLOB, statement.getLastSetObjectTargetSqlType());
		assertEquals(byte[].class, statement.getLastSetObjectParamValue().getClass());
		ArrayAssert.assertEquals(byteArray, (byte[])statement.getLastSetObjectParamValue());
	}

	
	public void testAsString() throws Exception {
        assertEquals("name", "BLOB", TYPE.toString());
	}

	public void testGetTypeClass() throws Exception {
		assertEquals("class", byte[].class, TYPE.getTypeClass());
	}


}
