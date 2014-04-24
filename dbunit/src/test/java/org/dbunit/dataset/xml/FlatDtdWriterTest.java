package org.dbunit.dataset.xml;

import junit.framework.TestCase;
import org.dbunit.dataset.Column;
import org.dbunit.dataset.DefaultDataSet;
import org.dbunit.dataset.DefaultTable;
import org.dbunit.dataset.datatype.DataType;

import java.io.StringWriter;

/**
 * <p> Copyright (c) 2003 OZ.COM.  All Rights Reserved. </p>
 * 
 * @author manuel.laflamme
 * @since Jan 26, 2004
 */
public class FlatDtdWriterTest extends TestCase
{
    public void testWriteSequenceModel() throws Exception
    {
        String expectedOutput =
                "<!ELEMENT dataset (\n" +
                "    TABLE1*,\n" +
                "    TABLE2*)>\n" +
                "\n" +
                "<!ELEMENT TABLE1 EMPTY>\n" +
                "<!ATTLIST TABLE1\n" +
                "    COL0 CDATA #IMPLIED\n" +
                "    COL1 CDATA #IMPLIED\n" +
                "    COL2 CDATA #REQUIRED\n" +
                "    COL3 CDATA #IMPLIED\n" + // Has default value
                ">\n" +
                "\n" +
                "<!ELEMENT TABLE2 EMPTY>\n" +
                "<!ATTLIST TABLE2\n" +
                "    COL0 CDATA #IMPLIED\n" +
                "    COL1 CDATA #IMPLIED\n" +
                "    COL2 CDATA #REQUIRED\n" +
                "    COL3 CDATA #IMPLIED\n" + // Has default value
                ">\n" +
                "\n";

        Column[] columns = new Column[]{
            new Column("COL0", DataType.UNKNOWN, Column.NULLABLE),
            new Column("COL1", DataType.UNKNOWN, Column.NULLABLE_UNKNOWN),
            new Column("COL2", DataType.UNKNOWN, Column.NO_NULLS),
            new Column("COL3", DataType.UNKNOWN, DataType.UNKNOWN.toString(), Column.NO_NULLS, "default"),
        };

        DefaultTable table1 = new DefaultTable("TABLE1", columns);
        DefaultTable table2 = new DefaultTable("TABLE2", columns);

        StringWriter stringWriter = new StringWriter();
        FlatDtdWriter dtdWriter = new FlatDtdWriter(stringWriter);
        dtdWriter.write(new DefaultDataSet(table1, table2));

        String actualOutput = stringWriter.toString();
        assertEquals("output", expectedOutput, actualOutput);
    }

    public void testWriteChoiceModel() throws Exception
    {
        String expectedOutput =
                "<!ELEMENT dataset (\n" +
                "   (TABLE1|\n" +
                "    TABLE2)*)>\n" +
                "\n" +
                "<!ELEMENT TABLE1 EMPTY>\n" +
                "<!ATTLIST TABLE1\n" +
                "    COL0 CDATA #IMPLIED\n" +
                "    COL1 CDATA #IMPLIED\n" +
                "    COL2 CDATA #REQUIRED\n" +
                ">\n" +
                "\n" +
                "<!ELEMENT TABLE2 EMPTY>\n" +
                "<!ATTLIST TABLE2\n" +
                "    COL0 CDATA #IMPLIED\n" +
                "    COL1 CDATA #IMPLIED\n" +
                "    COL2 CDATA #REQUIRED\n" +
                ">\n" +
                "\n";

        Column[] columns = new Column[]{
            new Column("COL0", DataType.UNKNOWN, Column.NULLABLE),
            new Column("COL1", DataType.UNKNOWN, Column.NULLABLE_UNKNOWN),
            new Column("COL2", DataType.UNKNOWN, Column.NO_NULLS),
        };

        DefaultTable table1 = new DefaultTable("TABLE1", columns);
        DefaultTable table2 = new DefaultTable("TABLE2", columns);

        StringWriter stringWriter = new StringWriter();
        FlatDtdWriter dtdWriter = new FlatDtdWriter(stringWriter);
        dtdWriter.setContentModel(FlatDtdWriter.CHOICE);
        dtdWriter.write(new DefaultDataSet(table1, table2));

        String actualOutput = stringWriter.toString();
        assertEquals("output", expectedOutput, actualOutput);
    }

    
    public void testWriteChoiceModel_NoInputColumns() throws Exception
    {
        String expectedOutput =
                "<!ELEMENT dataset (\n" +
                "   (TABLE1|\n" +
                "    TABLE2)*)>\n" +
                "\n" +
                "<!ELEMENT TABLE1 EMPTY>\n" +
                "<!ATTLIST TABLE1\n" +
                ">\n" +
                "\n" +
                "<!ELEMENT TABLE2 EMPTY>\n" +
                "<!ATTLIST TABLE2\n" +
                ">\n" +
                "\n";

        Column[] columns = new Column[0];

        DefaultTable table1 = new DefaultTable("TABLE1", columns);
        DefaultTable table2 = new DefaultTable("TABLE2", columns);

        StringWriter stringWriter = new StringWriter();
        FlatDtdWriter dtdWriter = new FlatDtdWriter(stringWriter);
        dtdWriter.setContentModel(FlatDtdWriter.CHOICE);
        dtdWriter.write(new DefaultDataSet(table1, table2));

        String actualOutput = stringWriter.toString();
        assertEquals("output", expectedOutput, actualOutput);
    }

}
