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
package org.dbunit.ext.oracle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.dbunit.dataset.datatype.ClobDataType;
import org.dbunit.dataset.datatype.TypeCastException;

import java.io.IOException;
import java.io.Writer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Manuel Laflamme
 * @author Last changed by: $Author$
 * @version $Revision$ $Date$
 * @since Jan 12, 2004
 */
public class OracleClobDataType extends ClobDataType
{

    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(OracleClobDataType.class);

    public Object getSqlValue(int column, ResultSet resultSet)
            throws SQLException, TypeCastException
    {
    	if(logger.isDebugEnabled())
    		logger.debug("getSqlValue(column={}, resultSet={}) - start", new Integer(column), resultSet);

        return typeCast(resultSet.getClob(column));
    }

    public void setSqlValue(Object value, int column, PreparedStatement statement)
            throws SQLException, TypeCastException
    {
    	if(logger.isDebugEnabled())
    		logger.debug("setSqlValue(value={}, column={}, statement={}) - start",
    				new Object[]{value, new Integer(column), statement} );

        statement.setObject(column, getClob(value, statement.getConnection()));
    }

    protected Object getClob(Object value, Connection connection) throws TypeCastException
    {
        logger.debug("getClob(value={}, connection={}) - start", value, connection);

        oracle.sql.CLOB tempClob = null;
        try
        {
            tempClob = oracle.sql.CLOB.createTemporary(connection, true, oracle.sql.CLOB.DURATION_SESSION);
            tempClob.open(oracle.sql.CLOB.MODE_READWRITE);
            Writer tempClobWriter = tempClob.getCharacterOutputStream();

            // Write the data into the temporary CLOB
            tempClobWriter.write((String)typeCast(value));

            // Flush and close the stream
            tempClobWriter.flush();
            tempClobWriter.close();

            // Close the temporary CLOB
            tempClob.close();
        }
        catch (IOException e)
        {
            // JH_TODO: shouldn't freeTemporary be called in finally {} ?
            // It wasn't done like that in the original reflection-styled DbUnit code.
            freeTemporaryClob(tempClob);
            throw new TypeCastException(value, this, e);
        }
        catch (SQLException e)
        {
            freeTemporaryClob(tempClob);
            throw new TypeCastException(value, this, e);
        }

        return tempClob;
    }


    protected void freeTemporaryClob(oracle.sql.CLOB tempClob) throws TypeCastException
    {
        logger.debug("freeTemporaryClob(tempClob={}) - start", tempClob);

        if (tempClob == null)
        {
            return;
        }

        try
        {
            tempClob.freeTemporary();
        }
        catch (SQLException e)
        {
            throw new TypeCastException("error freeing Oracle CLOB", e);
        }
    }

}
