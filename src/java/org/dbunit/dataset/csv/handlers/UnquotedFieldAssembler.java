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

import java.util.LinkedList;

public class UnquotedFieldAssembler extends AbstractPipelineComponent {

    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(UnquotedFieldAssembler.class);

    LinkedList addedComponents;

    public UnquotedFieldAssembler() {
        setAddedComponents(new LinkedList());
        getPipeline().putFront(SeparatorHandler.ENDPIECE());
        getPipeline().putFront(IsAlnumHandler.QUOTE());
        getPipeline().putFront(WhitespacesHandler.IGNORE());
    }

    private LinkedList getAddedComponents() {
        logger.debug("getAddedComponents() - start");

        return addedComponents;
    }

    private void setAddedComponents(LinkedList addedComponents) {
        logger.debug("setAddedComponents(addedComponents=" + addedComponents + ") - start");

        this.addedComponents = addedComponents;
    }

    public boolean canHandle(char c) throws IllegalInputCharacterException {
        logger.debug("canHandle(c=" + c + ") - start");

        return true;
    }

    static protected class ASSEMBLE extends Helper {

        /**
         * Logger for this class
         */
        private static final Logger logger = LoggerFactory.getLogger(ASSEMBLE.class);

        void helpWith(char c) {
            logger.debug("helpWith(c=" + c + ") - start");

            getHandler().getPipeline().thePieceIsDone();
        }
    }

}
