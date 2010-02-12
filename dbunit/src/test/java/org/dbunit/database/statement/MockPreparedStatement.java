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
package org.dbunit.database.statement;

import java.sql.SQLException;


/**
 * @author gommma
 * @version $Revision$
 * @since 2.3.0
 */
public class MockPreparedStatement extends
		com.mockobjects.sql.MockPreparedStatement 
{
	// TODO Create a real mock that records all values in the future (when needed)
	private int lastSetObjectParamIndex;
	private Object lastSetObjectParamValue;
	private int lastSetObjectTargetSqlType;
	
	public void setObject(int parameterIndex, Object value, int targetSqlType) 
      	throws SQLException 
  	{
			this.lastSetObjectParamIndex=parameterIndex;
			this.lastSetObjectParamValue=value;
			this.lastSetObjectTargetSqlType=targetSqlType;
	}

	public int getLastSetObjectParamIndex() {
		return lastSetObjectParamIndex;
	}

	public Object getLastSetObjectParamValue() {
		return lastSetObjectParamValue;
	}

	public int getLastSetObjectTargetSqlType() {
		return lastSetObjectTargetSqlType;
	}


}
