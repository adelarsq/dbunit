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

package org.dbunit.dataset.common.handlers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

public class HandlersTest extends TestCase {

    Pipeline pipeline;

    public void testEmptyFields() throws IllegalInputCharacterException, PipelineException {
        pipeline.putFront(SeparatorHandler.ENDPIECE());
        pipeline.putFront(WhitespacesHandler.IGNORE());
        pipeline.putFront(TransparentHandler.IGNORE());

        String words = ",, ,";

        feed (pipeline, words);

        assertEquals(4, pipeline.getProducts().size());

        for (int i = 0; i < pipeline.getProducts().size(); i++) {
            assertEquals("", pipeline.getProducts().get(i).toString());
        }
    }

    public void testUnquotedFieldsParser() throws IllegalInputCharacterException, PipelineException {

        pipeline.putFront(SeparatorHandler.ENDPIECE());
        pipeline.putFront(IsAlnumHandler.QUOTE());
        pipeline.putFront(WhitespacesHandler.IGNORE());
        pipeline.putFront(TransparentHandler.IGNORE());

        String words = "Today: Hello , World!";

        feed (pipeline, words);

        assertEquals(2, pipeline.getProducts().size());
        assertEquals("Today: Hello ", pipeline.getProducts().get(0));
        assertEquals("World!", pipeline.getProducts().get(1));
    }

    public void testQuotedFieldWithEscapedCharacterAssembler () throws PipelineException, IllegalInputCharacterException {
        pipeline.putFront(SeparatorHandler.ENDPIECE());
        pipeline.putFront(IsAlnumHandler.ACCEPT());
        pipeline.putFront(WhitespacesHandler.IGNORE());
        pipeline.putFront(QuoteHandler.QUOTE());

        String words = " \"Hello, \\\"World!\" ";

        feed (pipeline, words);

        assertEquals(1, pipeline.getProducts().size());
        assertEquals("Hello, \"World!", pipeline.getProducts().get(0).toString());
    }

    public void testUnquotedFieldWithEscapedCharacterAssembler () throws PipelineException, IllegalInputCharacterException {
        pipeline.putFront(SeparatorHandler.ENDPIECE());
        pipeline.putFront(EscapeHandler.ACCEPT());
        pipeline.putFront(IsAlnumHandler.QUOTE());
        pipeline.putFront(WhitespacesHandler.IGNORE());
        pipeline.putFront(TransparentHandler.IGNORE());

        String words = "Hello \\\"World!";

        feed (pipeline, words);

        assertEquals(1, pipeline.getProducts().size());
        assertEquals("Hello \\\"World!", pipeline.getProducts().get(0).toString());
    }

    public void testEscapedFieldAssembler () throws PipelineException, IllegalInputCharacterException {
        String words = "\"He\"llo, \"World, !\", \\\"St. James O\"Connor";

        pipeline.putFront(SeparatorHandler.ENDPIECE());
        pipeline.putFront(EscapeHandler.ACCEPT());
        pipeline.putFront(IsAlnumHandler.QUOTE());
        pipeline.putFront(QuoteHandler.QUOTE());
        pipeline.putFront(EscapeHandler.ESCAPE());
        pipeline.putFront(WhitespacesHandler.IGNORE());
        pipeline.putFront(TransparentHandler.IGNORE());

        feed (pipeline, words);

        assertEquals(3, pipeline.getProducts().size());
        assertEquals("Hello", pipeline.getProducts().get(0));
        assertEquals("World, !", pipeline.getProducts().get(1));
        assertEquals("\"St. James O\"Connor", pipeline.getProducts().get(2));
    }

    private void dump(List products) {
        Iterator it = products.iterator();
        int i = 0;
        while (it.hasNext()) {
            System.out.println(i++ + ": " + it.next());
        }
    }

    private void feed(Pipeline pipeline, String words) throws PipelineException, IllegalInputCharacterException {
        for (int i = 0; i < words.length(); i++) {
            pipeline.handle(words.toCharArray()[i]);
        }
        pipeline.thePieceIsDone();
    }

    public void testQuotedFieldAssembler() throws IllegalInputCharacterException, PipelineException {
        pipeline.putFront(SeparatorHandler.ENDPIECE());
        pipeline.putFront(IsAlnumHandler.ACCEPT());
        pipeline.putFront(WhitespacesHandler.IGNORE());
        pipeline.putFront(QuoteHandler.QUOTE());

        String words = " \"Hello, World!\" ";

        feed (pipeline, words);

        assertEquals(1, pipeline.getProducts().size());
        assertEquals("Hello, World!", pipeline.getProducts().get(0).toString());
    }

    public void testQuotedFieldsParser() throws IllegalInputCharacterException, PipelineException {
        pipeline.putFront(SeparatorHandler.ENDPIECE());
        pipeline.putFront(IsAlnumHandler.QUOTE());
        pipeline.putFront(WhitespacesHandler.IGNORE());
        pipeline.putFront(QuoteHandler.QUOTE());
        pipeline.putFront(TransparentHandler.IGNORE());

        String words = "\"Hello\", \"oh my\", \"ehm. oh yeah. World!\", \" craa azy \"";

        feed (pipeline, words);

        assertEquals(4, pipeline.getProducts().size());

        List expected = new ArrayList();
        expected.add("Hello");
        expected.add("oh my");
        expected.add("ehm. oh yeah. World!");
        expected.add(" craa azy ");

        List got = new ArrayList();

        for (int i = 0; i < pipeline.getProducts().size(); i++) {
            got.add(pipeline.getProducts().get(i).toString());
        }

        assertEquals(expected, got);

        assertEquals("Hello", pipeline.getProducts().get(0).toString());
        assertEquals("oh my", pipeline.getProducts().get(1).toString());
        assertEquals("ehm. oh yeah. World!", pipeline.getProducts().get(2).toString());
        assertEquals(" craa azy ", pipeline.getProducts().get(3).toString());

    }

    private void acceptHelper(String toAccept, Handler component) throws IllegalInputCharacterException, PipelineException {
        for (int i = 0; i < toAccept.length(); i++) {
            char c = toAccept.charAt(i);
            assertTrue(c + " should be accepted", component.canHandle(c));
            // Handle
            component.handle(c);
        }

    }

    /**
     * Test the handling of a sequence of empty, unquoted and quoted fields
     * @throws IllegalInputCharacterException
     * @throws PipelineException
     */
    public void testEmptyQuotedAndUnquotedFieldsParser() throws IllegalInputCharacterException, PipelineException {

        String words = " , \\\\John \"Fox , \"St. Moritz, 2\" , \\\\, \\\"Steve Wolf, \" \\\"Night & Day\\\", \\\"2nd\\\" edition \", , Again Here, \"and there, of\"";

        pipeline.putFront(SeparatorHandler.ENDPIECE());
        pipeline.putFront(EscapeHandler.ACCEPT());
        pipeline.putFront(IsAlnumHandler.QUOTE());
        pipeline.putFront(QuoteHandler.QUOTE());
        pipeline.putFront(EscapeHandler.ESCAPE());
        pipeline.putFront(WhitespacesHandler.IGNORE());
        pipeline.putFront(TransparentHandler.IGNORE());

        feed (pipeline, words);

        //dump(pipeline.getProducts());

        assertEquals(9, pipeline.getProducts().size());
        assertEquals("", pipeline.getProducts().get(0).toString());
        assertEquals("\\John \"Fox ", pipeline.getProducts().get(1).toString());
        assertEquals("St. Moritz, 2", pipeline.getProducts().get(2).toString());
        assertEquals("\\", pipeline.getProducts().get(3).toString());

        assertEquals("\"Steve Wolf", pipeline.getProducts().get(4).toString());
        assertEquals(" \"Night & Day\", \"2nd\" edition ", pipeline.getProducts().get(5).toString());
        assertEquals("", pipeline.getProducts().get(6).toString());
        assertEquals("Again Here", pipeline.getProducts().get(7).toString());
        assertEquals("and there, of", pipeline.getProducts().get(8).toString());
    }

    private void doNotAcceptHelper(String toAccept, Handler component) throws IllegalInputCharacterException, PipelineException {
        for (int i = 0; i < toAccept.length(); i++) {
            char c = toAccept.charAt(i);
            assertFalse(c + " should not be accepted", component.canHandle(c));
        }
    }

    public void testEscapeHandler () throws PipelineException, IllegalInputCharacterException {
        String accepted = "\\\"";

        EscapeHandler escapeHandler = (EscapeHandler) EscapeHandler.ESCAPE();
        pipeline.putFront(escapeHandler);
        acceptHelper(accepted, pipeline);
        assertEquals("\"", pipeline.getCurrentProduct().toString());
    }

    public void testWhitespaceHandler() throws Exception {

        String accepted = " \t";

        PipelineComponent acceptHandler = WhitespacesHandler.ACCEPT();
        pipeline.putFront(acceptHandler);
        acceptHelper(accepted, acceptHandler);
        acceptHelper(accepted, WhitespacesHandler.IGNORE());

        assertEquals(accepted, pipeline.getCurrentProduct().toString());

    }


    public void testUnquotedHandler() throws IllegalInputCharacterException, PipelineException {
        String accepted = "_1234567890abcdefghilmnopqrstuvzxywjABCDEFGHILMNOPQRSTUVZXYWJ()/&%$|-_.:;+*<>";
        String notAccepted = " \t\\";

        PipelineComponent acceptHandler = IsAlnumHandler.ACCEPT();
        pipeline.putFront(acceptHandler);
        acceptHelper(accepted, acceptHandler);
        
        PipelineComponent ignoreHandler = IsAlnumHandler.IGNORE();
        pipeline.putFront(ignoreHandler);
        acceptHelper(accepted, ignoreHandler);

        doNotAcceptHelper(notAccepted, acceptHandler);
        doNotAcceptHelper(notAccepted, ignoreHandler);

        assertEquals(accepted, pipeline.getCurrentProduct().toString());
    }


    protected void setUp() throws Exception {
        pipeline = new Pipeline();
    }

}
