/*
 * AllTests.java   Mar 17, 2002
 *
 * Copyright 2001 Karat Software Corp. All Rights Reserved.
 *
 * This software is the proprietary information of Karat Software Corp.
 * Use is subject to license terms.
 *
 */

package org.dbunit.dataset.datatype;

import org.dbunit.dataset.*;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Manuel Laflamme
 * @version 1.0
 */
public class AllTests
{
    public static Test suite()
    {
        TestSuite suite = new TestSuite();
        suite.addTest(new TestSuite(BooleanDataTypeTest.class));
        suite.addTest(new TestSuite(DateDataTypeTest.class));
        suite.addTest(new TestSuite(LongDataTypeTest.class));
        suite.addTest(new TestSuite(FloatDataTypeTest.class));
        suite.addTest(new TestSuite(IntegerDataTypeTest.class));
        suite.addTest(new TestSuite(DoubleDataTypeTest.class));
        suite.addTest(new TestSuite(NumberDataTypeTest.class));
        suite.addTest(new TestSuite(StringDataTypeTest.class));
        suite.addTest(new TestSuite(TimeDataTypeTest.class));
        suite.addTest(new TestSuite(TimestampDataTypeTest.class));

        return suite;
    }
}

