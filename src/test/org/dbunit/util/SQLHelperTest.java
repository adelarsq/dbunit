/*
 *
 * The DbUnit Database Testing Framework
 * Copyright (C)2005, DbUnit.org
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

import java.sql.Connection;
import java.sql.SQLException;

import org.dbunit.AbstractHSQLTestCase;

/**
 * @author Felipe Leme <dbunit@felipeal.net>
 * @version $Revision$
 * @since Nov 5, 2005
 */
public class SQLHelperTest extends AbstractHSQLTestCase {
  
  public SQLHelperTest( String name ) {
    super( name, "hypersonic_dataset.sql" );
  }  
  
  public void testGetPrimaryKeyColumn() throws SQLException {
    String[] tables = { "A", "B", "C", "D", "E", "F", "G", "H" };
    Connection conn = getConnection().getConnection();
    assertNotNull( "didn't get a connection", conn );
    for (int i = 0; i < tables.length; i++) {
      String table = tables[i];
      String expectedPK = "PK" + table;
      String actualPK = SQLHelper.getPrimaryKeyColumn( conn, table );
      assertNotNull( actualPK );
      assertEquals( "primary key column for table " + table + " does not match", expectedPK, actualPK );
    }
  }
  
}
