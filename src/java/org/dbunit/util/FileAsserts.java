package org.dbunit.util;

/*
 * From  "Dale E Martin" <dmartin@c..>
 * Date  Thursday, March 14, 2002 2:42 pm
 * To  junit@yahoogroups.com
 * Subject  [junit] file assert, a starting point
 *
 * OK, this isn't rocket science or anything but it's working for me.  What it
 * is is a couple of assert methods that compare files, so your expected
 * results can be located in a file.
 *
 * I did not use the diff library as the docs are in French, which I have not
 * studied for about 15 years and am not ready to pick back up ;-)
 *
 * I declare this code to be open to the public domain and anyone may do
 * anything they like with it.  Before inclusion in JUnit I'm sure you'll need
 * to tweak it some, but it's working for my needs and maybe it will help
 * someone else.
 *
 * Later,
 *       Dale
 *
 */

import junit.framework.Assert;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FileAsserts
{

    private static String processOneLine(int lineNumber,
            BufferedReader expectedData,
            BufferedReader actualData)
            throws IOException
    {

        String problem = null;
        String expectedLine = expectedData.readLine();
        if (!actualData.ready())
        {
            problem = "at line " + lineNumber + ", expected:\n" +
                    expectedLine + "\n" +
                    "but actual file was not ready for reading at this line.";
        }
        else
        {
            String actualLine = actualData.readLine();
            if (!expectedLine.equals(actualLine))
            {
                // Uh oh, they did not match.
                problem = "at line " + lineNumber + " there was a mismatch.  Expected:\n";
                int maxLen = expectedLine.length();
                if (expectedLine.length() > actualLine.length())
                {
                    maxLen = actualLine.length();
                }
                int startOffset = 0;
                for (int i = 0; i < maxLen; i++)
                {
                    if (expectedLine.charAt(i) != actualLine.charAt(i))
                    {
                        startOffset = i;
                        break;
                    }
                }
                problem += expectedLine.substring(startOffset) + "\n" +
                        "actual was:\n" +
                        actualLine.substring(startOffset) + "\n";
            }
        }
        return problem;
    }

    public static void assertEquals(BufferedReader expected,
            BufferedReader actual) throws Exception
    {
        Assert.assertNotNull(expected);
        Assert.assertNotNull(actual);

        String problem = null;
        try
        {
            int lineCounter = 0;
            while (expected.ready() && problem == null)
            {
                problem = processOneLine(lineCounter, expected, actual);
                lineCounter++;
            }
        }
        finally
        {
            expected.close();
            actual.close();
        }

        if (problem != null)
        {
            Assert.fail(problem);
        }
    }

    public static void assertEquals(InputStream expected, File actual)
            throws Exception
    {
        Assert.assertNotNull(expected);
        Assert.assertNotNull(actual);

        Assert.assertTrue(actual.canRead());

        
        BufferedReader expectedData = new BufferedReader(new InputStreamReader(expected));

        BufferedReader actualData =
                new BufferedReader(new InputStreamReader(new FileInputStream(actual)));
        assertEquals(expectedData, actualData);
    }

    public static void assertEquals(File expected, File actual) throws Exception
    {
        Assert.assertNotNull(expected);
        Assert.assertNotNull(actual);

        Assert.assertTrue(expected.canRead());
        Assert.assertTrue(actual.canRead());

        BufferedReader expectedData =
                new BufferedReader(new InputStreamReader(new FileInputStream(expected)));
        BufferedReader actualData =
                new BufferedReader(new InputStreamReader(new FileInputStream(actual)));
        assertEquals(expectedData, actualData);
    }
}



