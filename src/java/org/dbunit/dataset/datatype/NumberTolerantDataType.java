package org.dbunit.dataset.datatype;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.dbunit.dataset.ITable;
import org.dbunit.dataset.datatype.AbstractDataType;
import org.dbunit.dataset.datatype.NumberDataType;
import org.dbunit.dataset.datatype.TypeCastException;
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
public final class NumberTolerantDataType extends AbstractDataType
{

    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(NumberDataType.class);

    private static final BigDecimal ZERO = new BigDecimal(0.0);
    
    private static final Number TRUE = new BigDecimal((double)1);
    private static final Number FALSE = new BigDecimal((double)0);
    
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
        super(name, sqlType, BigDecimal.class, true);
        
        if(delta<0.0) {
        	throw new IllegalArgumentException("The given delta '"+delta+"' must be >= 0");
        }
        this.delta=delta;
    }

    public double getDelta() 
    {
		return delta;
	}

    
//FROM NumberDataType
    ////////////////////////////////////////////////////////////////////////////
    // DataType class


	public Object typeCast(Object value) throws TypeCastException
    {
        logger.debug("typeCast(value={}) - start", value);

        if (value == null || value == ITable.NO_VALUE)
        {
            return null;
        }

        if (value instanceof BigDecimal)
        {
            return value;
        }

        if (value instanceof Boolean)
        {
            return ((Boolean)value).booleanValue() ? TRUE : FALSE;
        }

        String stringValue = value.toString();
        try
        {
            return new BigDecimal(stringValue);
        }
        catch (java.lang.NumberFormatException e)
        {
//            logger.error("typeCast() error for value " + stringValue, e);
            throw new TypeCastException(value, this, e);
        }
    }

    public Object getSqlValue(int column, ResultSet resultSet)
            throws SQLException, TypeCastException
    {
    	if(logger.isDebugEnabled())
    		logger.debug("getSqlValue(column={}, resultSet={}) - start", new Integer(column), resultSet);

        BigDecimal value = resultSet.getBigDecimal(column);
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
    		logger.debug("setSqlValue(value={}, column={}, , statement={}) - start",
        		new Object[]{value, new Integer(column), statement});

        statement.setBigDecimal(column, (BigDecimal)typeCast(value));
    }
// END FROM NumberDataType
    


    // Only method overwritten from the base implementation
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
                    return diff.signum();
                }
                
            }
            else {
                return value1.compareTo(value2);
            }
            
        }
        catch (ClassCastException e)
        {
//            logger.error("compare()", e);
            throw new TypeCastException(e);
        }
    }

    
    
    
}
