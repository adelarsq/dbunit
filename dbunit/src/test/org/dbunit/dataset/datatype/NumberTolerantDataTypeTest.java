package org.dbunit.dataset.datatype;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Types;

import org.dbunit.database.ExtendedMockSingleRowResultSet;
import org.dbunit.dataset.ITable;

/**
 * Unit test for the number tolerant data type which is quite similar to the NumberDataTypeTest.
 * @author gommma
 */
public class NumberTolerantDataTypeTest extends AbstractDataTypeTest
{

	private NumberTolerantDataType THIS_TYPE = new NumberTolerantDataType("NUMERIC", Types.NUMERIC,
	        new ToleratedDeltaMap.Precision(new BigDecimal("1E-5")) );
    private NumberTolerantDataType THIS_TYPE_PERCENTAGE = new NumberTolerantDataType("NUMERIC", Types.NUMERIC,
            new ToleratedDeltaMap.Precision(new BigDecimal("1.0"), true) );

	
    public NumberTolerantDataTypeTest(String name)
    {
        super(name);
    }
    
    public void testCreateWithNegativeDelta() throws Exception
    {
    	try 
    	{
    		new NumberTolerantDataType("NUMERIC", Types.NUMERIC, new ToleratedDeltaMap.Precision(new BigDecimal("-0.1")) );
    		fail("Should not be able to created datatype with negative delta");
    	}
    	catch(IllegalArgumentException expected)
    	{
    		String expectedMsg = "The given delta '-0.1' must be >= 0";
    		assertEquals(expectedMsg, expected.getMessage());
    	}
    }

    public void testCompareToWithDelta_DiffWithinToleratedDelta() throws Exception
    {
    	int result = THIS_TYPE.compare(new BigDecimal(0.12345678D), new BigDecimal(0.123456789D));
    	assertEquals(0, result);
    }

    public void testCompareToWithDelta_DiffOutsideOfToleratedDelta() throws Exception
    {
    	int result = THIS_TYPE.compare(new BigDecimal(0.1234), new BigDecimal(0.1235D));
    	assertEquals(-1, result);
    }

    public void testCompareToWithDeltaPercentage_DiffWithinToleratedDelta() throws Exception
    {
        int result = THIS_TYPE_PERCENTAGE.compare(new BigDecimal("1000.0"), new BigDecimal("1010.0"));
        assertEquals(0, result);
    }

    public void testCompareToWithDeltaPercentage_DiffOutsideOfToleratedDelta() throws Exception
    {
        int result = THIS_TYPE_PERCENTAGE.compare(new BigDecimal("1000.0"), new BigDecimal("1010.1"));
        assertEquals(-1, result);
    }

    /**
     *
     */
    public void testToString() throws Exception
    {
        assertEquals("name", "NUMERIC", THIS_TYPE.toString());
    }

    /**
     *
     */
    public void testGetTypeClass() throws Exception
    {
        assertEquals("class", java.math.BigDecimal.class, THIS_TYPE.getTypeClass());
    }

    /**
     *
     */
    public void testIsNumber() throws Exception
    {
        assertEquals("is number", true, THIS_TYPE.isNumber());
    }

    public void testIsDateTime() throws Exception
    {
        assertEquals("is date/time", false, THIS_TYPE.isDateTime());
    }

    public void testTypeCast() throws Exception
    {
        Object[] values = {
                null,
                new BigDecimal((double)1234),
                "1234",
                "12.34",
                Boolean.TRUE,
                Boolean.FALSE,
            };
        BigDecimal[] expected = {
            null,
            new BigDecimal((double)1234),
            new BigDecimal((double)1234),
            new BigDecimal("12.34"),
            new BigDecimal("1"),
            new BigDecimal("0"),
        };

        assertEquals("actual vs expected count", values.length, expected.length);

        for (int j = 0; j < values.length; j++)
        {
            assertEquals("typecast " + j, expected[j],
                    THIS_TYPE.typeCast(values[j]));
        }
    }

    public void testTypeCastNone() throws Exception
    {
        assertEquals("typecast", null, THIS_TYPE.typeCast(ITable.NO_VALUE));
    }

    public void testTypeCastInvalid() throws Exception
    {
        Object[] values = {
            new Object(),
            "bla",
        };

        for (int i = 0; i < values.length; i++)
        {
            try
            {
                THIS_TYPE.typeCast(values[i]);
                fail("Should throw TypeCastException - " + i);
            }
            catch (TypeCastException e)
            {
            }
        }
    }
    
    
    public void testCompareEquals() throws Exception
    {
        Object[] values1 = {
                null,
                new BigDecimal((double)1234),
                "1234",
                "12.34",
                Boolean.TRUE,
                Boolean.FALSE,
                new BigDecimal(123.4),
                "123",
            };
        Object[] values2 = {
            null,
            new BigDecimal((double)1234),
            new BigDecimal(1234),
            new BigDecimal("12.34"),
            new BigDecimal("1"),
            new BigDecimal("0"),
            new BigDecimal(123.4000),
            new BigDecimal("123.0"),
        };

        assertEquals("values count", values1.length, values2.length);

        for (int i = 0; i < values1.length; i++)
        {
            assertEquals("compare1 " + i, 0, THIS_TYPE.compare(values1[i], values2[i]));
            assertEquals("compare2 " + i, 0, THIS_TYPE.compare(values2[i], values1[i]));
        }
    }

    public void testCompareInvalid() throws Exception
    {
        Object[] values1 = {
            new Object(),
            "bla",
        };
        Object[] values2 = {
            null,
            null,
        };

        assertEquals("values count", values1.length, values2.length);

        for (int i = 0; i < values1.length; i++)
        {
            try
            {
                THIS_TYPE.compare(values1[i], values2[i]);
                fail("Should throw TypeCastException - " + i);
            }
            catch (TypeCastException e)
            {
            }

            try
            {
                THIS_TYPE.compare(values1[i], values2[i]);
                fail("Should throw TypeCastException - " + i);
            }
            catch (TypeCastException e)
            {
            }
        }
    }

    public void testCompareDifferent() throws Exception
    {
        Object[] less = {
                null,
                "-7500",
                new BigDecimal("-0.01"),
                new BigInteger("1234"),
            };

        Object[] greater = {
            "0",
            "5.555",
            new BigDecimal("0.01"),
            new BigDecimal("1234.5"),
        };

        assertEquals("values count", less.length, greater.length);

        for (int j = 0; j < less.length; j++)
        {
            assertTrue("less " + j, THIS_TYPE.compare(less[j], greater[j]) < 0);
            assertTrue("greater " + j, THIS_TYPE.compare(greater[j], less[j]) > 0);
        }
    }

    public void testSqlType() throws Exception
    {
    }

    public void testForObject() throws Exception
    {
    }

    public void testAsString() throws Exception
    {
        BigDecimal[] values = {
                new BigDecimal("1234"),
            };

        String[] expected = {
            "1234",
        };

        assertEquals("actual vs expected count", values.length, expected.length);

        for (int i = 0; i < values.length; i++)
        {
            assertEquals("asString " + i, expected[i], DataType.asString(values[i]));
        }
    }

    public void testGetSqlValue() throws Exception
    {
        BigDecimal[] expected = {
                null,
                new BigDecimal("12.34"),
            };

        ExtendedMockSingleRowResultSet resultSet = new ExtendedMockSingleRowResultSet();
        resultSet.addExpectedIndexedValues(expected);

        for (int i = 0; i < expected.length; i++)
        {
            Object expectedValue = expected[i];

            DataType dataType = THIS_TYPE;
            Object actualValue = dataType.getSqlValue(i + 1, resultSet);
            assertEquals("value", expectedValue, actualValue);
        }
    }

}
