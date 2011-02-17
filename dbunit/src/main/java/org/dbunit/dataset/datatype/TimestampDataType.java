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

package org.dbunit.dataset.datatype;

import java.math.BigInteger;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dbunit.dataset.ITable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Manuel Laflamme
 * @author Last changed by: $Author$
 * @version $Revision$ $Date$
 * @since 1.0 (Feb 19, 2002)
 */
public class TimestampDataType extends AbstractDataType
{
    private static final BigInteger ONE_BILLION = new BigInteger ("1000000000");
    private static final Pattern TIMEZONE_REGEX = Pattern.compile("(.*)(?:\\W([+-][0-2][0-9][0-5][0-9]))");
 
	/**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(TimestampDataType.class);

    TimestampDataType()
    {
        super("TIMESTAMP", Types.TIMESTAMP, Timestamp.class, false);
    }

    ////////////////////////////////////////////////////////////////////////////
    // DataType class

    public Object typeCast(Object value) throws TypeCastException
    {
        logger.debug("typeCast(value={}) - start", value);

        if (value == null || value == ITable.NO_VALUE)
        {
            return null;
        }

        if (value instanceof java.sql.Timestamp)
        {
            return value;
        }

        if (value instanceof java.util.Date)
        {
            java.util.Date date = (java.util.Date)value;
            return new java.sql.Timestamp(date.getTime());
        }

        if (value instanceof Long)
        {
            Long date = (Long)value;
            return new java.sql.Timestamp(date.longValue());
        }

        if (value instanceof String)
        {
        	String stringValue = value.toString();
           	String zoneValue = null;
        	
        	Matcher tzMatcher = TIMEZONE_REGEX.matcher(stringValue);
        	if (tzMatcher.matches() && tzMatcher.group(2) != null) 
        	{
        		stringValue = tzMatcher.group(1);
        		zoneValue = tzMatcher.group(2);
        	}

        	Timestamp ts = null;
        	if (stringValue.length() == 10)
            {
                try
                {
                    long time = java.sql.Date.valueOf(stringValue).getTime();
                    ts = new java.sql.Timestamp(time);
                }
                catch (IllegalArgumentException e)
                {
                    // Was not a java.sql.Date, let Timestamp handle this value
                }
            }
        	if (ts == null) 
        	{
	            try
	            {
	                ts = java.sql.Timestamp.valueOf(stringValue);
	            }
	            catch (IllegalArgumentException e)
	            {
	                throw new TypeCastException(value, this, e);
	            }
            }
        	
        	// Apply zone if any
        	if (zoneValue != null)
        	{
        		BigInteger time = BigInteger.valueOf(ts.getTime() / 1000 * 1000).multiply(ONE_BILLION).add(BigInteger.valueOf(ts.getNanos()));
    			int hours = Integer.parseInt(zoneValue.substring(1, 3));
    			int minutes = Integer.parseInt(zoneValue.substring(3, 5));
    			BigInteger offsetAsSeconds = BigInteger.valueOf((hours * 3600) + (minutes * 60));
    			BigInteger offsetAsNanos = offsetAsSeconds.multiply(BigInteger.valueOf(1000)).multiply(ONE_BILLION);
        		if (zoneValue.charAt(0) == '+') {
        			time = time.subtract(offsetAsNanos);
        		} else {
           			time = time.add(offsetAsNanos);
        		}
    			BigInteger[] components = time.divideAndRemainder(ONE_BILLION);
        		ts = new Timestamp(components[0].longValue());
        		ts.setNanos(components[1].intValue());
        	}
        	
        	return ts;
        }

        throw new TypeCastException(value, this);
    }

    public boolean isDateTime()
    {
        logger.debug("isDateTime() - start");

        return true;
    }

    public Object getSqlValue(int column, ResultSet resultSet)
            throws SQLException, TypeCastException
    {
    	if(logger.isDebugEnabled())
    		logger.debug("getSqlValue(column={}, resultSet={}) - start", new Integer(column), resultSet);

        Timestamp value = resultSet.getTimestamp(column);
        if (value == null || resultSet.wasNull())
        {
            return null;
        }
        return value;
    }

    public void setSqlValue(Object value, int column, PreparedStatement statement)
            throws SQLException, TypeCastException
    {
    	if(logger.isDebugEnabled())
    		logger.debug("setSqlValue(value={}, column={}, statement={}) - start",
        		new Object[]{value, new Integer(column), statement} );

        statement.setTimestamp(column, (java.sql.Timestamp)typeCast(value));
    }
}





