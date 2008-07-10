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

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Copy from org/dbunit/dataset/datatype/NumberDataType.java with
 * extended version of the compare method in order to respect precision tolerance.
 * This is comparable to the junit method <code>assert(double val1, double val2, double toleratedDelta)</code>.
 * 
 * @author gommma
 * @since 2.3.0
 * @version $Revision $
 */
public final class NumberTolerantDataType extends NumberDataType
{

    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(NumberTolerantDataType.class);

    private static final BigDecimal ZERO = new BigDecimal(0.0);
    
    /**
     * The allowed/tolerated difference 
     */
    private double delta;

    /**
     * Creates a new number tolerant datatype
     * @param name
     * @param sqlType
     * @param delta The tolerated delta to be used for the comparison
     */
    NumberTolerantDataType(String name, int sqlType, double delta)
    {
        super(name, sqlType);
        
        if(delta<0.0) {
        	throw new IllegalArgumentException("The given delta '"+delta+"' must be >= 0");
        }
        this.delta=delta;
    }

    public double getDelta() 
    {
		return delta;
	}
    
    /**
     * The only method overwritten from the base implementation to compare numbers allowing a tolerance
     * @see org.dbunit.dataset.datatype.AbstractDataType#compare(java.lang.Object, java.lang.Object)
     */
    public int compare(Object o1, Object o2) throws TypeCastException
    {
        logger.debug("compare(o1={}, o2={}) - start", o1, o2);

        try
        {
            Comparable value1 = (Comparable)typeCast(o1);
            Comparable value2 = (Comparable)typeCast(o2);

            if (value1 == null && value2 == null)
            {
                return 0;
            }

            if (value1 == null && value2 != null)
            {
                return -1;
            }

            if (value1 != null && value2 == null)
            {
                return 1;
            }

            // Start of special handling
            if(value1 instanceof BigDecimal && value2 instanceof BigDecimal){
                BigDecimal bdValue1 = (BigDecimal)value1;
                BigDecimal bdValue2 = (BigDecimal)value2;
                BigDecimal diff = bdValue1.subtract(bdValue2);
                // Exact match
                if(diff.compareTo(ZERO)==0) 
                {
                	return 0;
                }
                else if(Math.abs(diff.doubleValue()) <= delta) 
                {
                    // within tolerance delta, so accept
                	logger.debug("Values val1={}, val2={} differ but are within tolerated delta {}",
                			new Object[] {bdValue1, bdValue2, new Double(delta) } );
                    return 0;
                } else {
                	// TODO it would be beautiful to report a precise description about difference and tolerated delta values in the assertion
                	// Therefore think about introducing a method "DataType.getCompareInfo()"
                    return diff.signum();
                }
                
            }
            else {
                return value1.compareTo(value2);
            }
            
        }
        catch (ClassCastException e)
        {
            throw new TypeCastException(e);
        }
    }

    
    
    
}
