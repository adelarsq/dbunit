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
package org.dbunit.dataset.csv.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.dbunit.dataset.csv.IllegalInputCharacterException;

public abstract class AbstractPipelineComponent implements PipelineComponent {

    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(AbstractPipelineComponent.class);

    private PipelineComponent successor;
    private Pipeline pipeline;

    private Helper helper;

    protected PipelineComponent getSuccessor() {
        return successor;
    }

    public Pipeline getPipeline() {
        return pipeline;
    }

    public void setPipeline(Pipeline pipeline) {
        logger.debug("setPipeline(pipeline={}) - start", pipeline);
        this.pipeline = pipeline;
    }

    public void setSuccessor(PipelineComponent successor) {
        logger.debug("setSuccessor(successor={}) - start", successor);
        this.successor = successor;
    }


    private StringBuffer getThePiece() {
        return getPipeline().getCurrentProduct();
    }

    public void handle(char c) throws IllegalInputCharacterException, PipelineException {
        logger.debug("handle(c=" + c + ") - start");

        if (!canHandle(c)) {
            getSuccessor().handle(c);
        } else {
            getHelper().helpWith(c);
        }
    }

    public void noMoreInput() {
        logger.debug("noMoreInput() - start");

        if (allowForNoMoreInput()) {
            if (getSuccessor()!= null)
                getSuccessor().noMoreInput();
        }
    }

    public boolean allowForNoMoreInput() {
        logger.debug("allowForNoMoreInput() - start");

        return getHelper().allowForNoMoreInput();
    }

    protected static PipelineComponent createPipelineComponent(AbstractPipelineComponent handler, Helper helper) {
        logger.debug("createPipelineComponent(handler={}, helper={}) - start", handler, helper);
        helper.setHandler(handler);
        handler.setHelper(helper);
        return handler;
    }

    /**
     * Method invoked when the character should be accepted
     * @param c
     */
    public void accept(char c) {
        getThePiece().append(c);
    }

    protected Helper getHelper() {
        return helper;
    }

    private void setHelper(Helper helper) {
        logger.debug("setHelper(helper={}) - start", helper);
        this.helper = helper;
    }

    static protected class IGNORE extends Helper {
        public void helpWith(char c) {
            // IGNORE
        }
    }

    static protected class ACCEPT extends Helper {
        public void helpWith(char c) {
            logger.debug("helpWith(c={}) - start", String.valueOf(c));
            getHandler().accept(c);
        }
    }
}
