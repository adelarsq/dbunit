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
 * 4-set-2003 11.42.06
 * $Revision$
 */
public class EnforceHandlerTest extends TestCase {
    Pipeline pipeline;

    public void testOwnAnEnforcedHandler () {
        PipelineComponent enforced = AllHandler.ACCEPT();
        EnforceHandler enforceHandler = (EnforceHandler)EnforceHandler.ENFORCE(enforced);
        pipeline.putFront(enforceHandler);

        assertTrue(true);
        assertSame(enforced, enforceHandler.getEnforcedComponents()[0]);
        assertSame("enforced pipeline should be the same of the enforcing one", enforceHandler.getPipeline(), enforced.getPipeline());
    }

    public void testThrowExceptionWhenEnforcedDoesNotHandle () throws PipelineException, IllegalInputCharacterException {
        PipelineComponent enforceHandler = EnforceHandler.ENFORCE(new MockHandler());
        pipeline.putFront(enforceHandler);
        try {
            enforceHandler.handle('x');
            fail ("Enforce handler should have thrown an exception");
        } catch (IllegalInputCharacterException illEx) {}

    }

    public void testDontRemoveItselfOnException () throws PipelineException, IllegalInputCharacterException {
        PipelineComponent enforceHandler = EnforceHandler.ENFORCE(new MockHandler());
        pipeline.putFront(enforceHandler);
        try {
            pipeline.handle('x');
            fail ("Enforce handler should have thrown an exception");
        } catch (IllegalInputCharacterException illEx) {}

        assertSame(pipeline.removeFront(), enforceHandler);
    }

    public void testRemoveItselfAfterEnforcing () throws PipelineException, IllegalInputCharacterException {
        PipelineComponent enforceHandler = EnforceHandler.ENFORCE(AllHandler.ACCEPT());
        pipeline.putFront(enforceHandler);
        pipeline.handle('\"');
        pipeline.thePieceIsDone();
        assertNotSame(pipeline.removeFront(), enforceHandler);
        assertEquals(1, pipeline.getProducts().size());
        assertEquals("\"", pipeline.getProducts().get(0));
    }

    public void testEnforceOneBetweenMany () throws PipelineException, IllegalInputCharacterException {
        PipelineComponent pass = SeparatorHandler.ACCEPT();
        PipelineComponent accept = AllHandler.ACCEPT();
        EnforceHandler enforceHandler = (EnforceHandler)EnforceHandler.ENFORCE(new PipelineComponent [] {pass, accept});
        pipeline.putFront(enforceHandler);

        pipeline.handle('\"');
        pipeline.thePieceIsDone();

        assertNotSame(pipeline.removeFront(), enforceHandler);
        assertEquals(1, pipeline.getProducts().size());
        assertEquals("\"", pipeline.getProducts().get(0));
    }

    protected void setUp() throws Exception {
        pipeline = new Pipeline();
    }

}
