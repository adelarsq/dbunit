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

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Manuel Laflamme
 * @version $Revision$
 */
public class AllTests extends TestSuite
{
    public static Test suite()
    {
        TestSuite suite = new TestSuite();
        suite.addTest(new TestSuite(BooleanDataTypeTest.class));
        suite.addTest(new TestSuite(BigIntegerDataTypeTest.class));
        suite.addTest(new TestSuite(BitDataTypeTest.class));
        suite.addTest(new TestSuite(BytesDataTypeTest.class));
        suite.addTest(new TestSuite(BlobDataTypeTest.class));
        suite.addTest(new TestSuite(BinaryStreamDataTypeTest.class));
        suite.addTest(new TestSuite(DateDataTypeTest.class));
        suite.addTest(new TestSuite(DefaultDataTypeFactoryTest.class));
        suite.addTest(new TestSuite(DoubleDataTypeTest.class));
        suite.addTest(new TestSuite(FloatDataTypeTest.class));
        suite.addTest(new TestSuite(IntegerDataTypeTest.class));
        suite.addTest(new TestSuite(LongDataTypeTest.class));
        suite.addTest(new TestSuite(NumberDataTypeTest.class));
        suite.addTest(new TestSuite(NumberTolerantDataTypeTest.class));
        suite.addTest(new TestSuite(StringDataTypeTest.class));
        suite.addTest(new TestSuite(StringIgnoreCaseDataTypeTest.class));
        suite.addTest(new TestSuite(TimeDataTypeTest.class));
        suite.addTest(new TestSuite(TimestampDataTypeTest.class));
        suite.addTest(new TestSuite(TypeCastExceptionTest.class));

        return suite;
    }
}




