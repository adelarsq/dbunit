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

import junit.framework.TestCase;

/**
 * author: fede
 * 29-lug-2003 16.14.18
 * $Revision$
 */
public class PipelineTest extends TestCase {
    Pipeline line;

    public void testRemovingTheLastHandlerThrowsException () {
        try {
            line.removeFront();
            line.removeFront();
            fail ("Removing from an ampty pipeline should throw an exception");
        } catch (PipelineException e) {}
    }

    public void testAnHandlerCanBeAddedInFront () throws PipelineException {
        PipelineComponent handler = SeparatorHandler.ACCEPT();
        line.putFront(handler);
        assertSame(handler, line.removeFront());
        assertSame(line, handler.getPipeline());
    }

    public void testTheFrontHandlerIsThereAfterAddingAndRemovingAnother () throws PipelineException {
        PipelineComponent handler = SeparatorHandler.ACCEPT();
        PipelineComponent handler2 = SeparatorHandler.ACCEPT();
        line.putFront(handler);
        line.putFront(handler2);
        assertSame(handler2, line.removeFront());
        assertSame(handler, line.removeFront());
    }

    public void testEachHandlerIsCalled () throws IllegalInputCharacterException, PipelineException {
        MockHandler component = new MockHandler();
        MockHandler component2 = new MockHandler();
        component.setExpectedHandleCalls(1);
        component2.setExpectedHandleCalls(1);
        line.putFront(component);
        line.putFront(component2);

        // the last handler will throw an exception
        try {
            line.handle('x');
            fail("Exception expected");
        } catch (IllegalInputCharacterException seen) {}

        component.verify();
        component2.verify();
    }

    public void testWhenAPieceIsDoneIsAddedToProducts () throws IllegalInputCharacterException, PipelineException {
        PipelineComponent c = AllHandler.ACCEPT();
        line.putFront(c);
        line.handle('x');
        line.thePieceIsDone();
        assertEquals(1, line.getProducts().size());
        assertEquals("x", line.getProducts().get(0));
    }

    public void testWhetAPieceIsDoneANewOneIsCreated () throws IllegalInputCharacterException, PipelineException {
        PipelineComponent c = AllHandler.ACCEPT();
        line.putFront(c);
        line.handle('x');
        line.thePieceIsDone();
        assertEquals("", line.getCurrentProduct().toString());
    }


    protected void setUp() throws Exception {
        line = new Pipeline();
    }
}
