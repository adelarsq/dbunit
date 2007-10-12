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

public class SeparatorHandler extends AbstractPipelineComponent {

    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(SeparatorHandler.class);

    private SeparatorHandler() {}

    public static final PipelineComponent ACCEPT () {
        logger.debug("ACCEPT() - start");

        return createPipelineComponent(new SeparatorHandler(), new ACCEPT());
    }

    public static final PipelineComponent IGNORE () {
        logger.debug("IGNORE() - start");

        return createPipelineComponent(new SeparatorHandler(), new IGNORE());
    }

    public static final PipelineComponent ENDPIECE () {
        logger.debug("ENDPIECE() - start");

        return createPipelineComponent(new SeparatorHandler(), new ENDPIECE());
    }

    public static final char SEPARATOR_CHAR = ',';

    public boolean canHandle(char c) throws IllegalInputCharacterException {
        logger.debug("canHandle(c=" + c + ") - start");

        if (c == SEPARATOR_CHAR) {
            return true;
        }
        return false; //throw new IllegalInputCharacterException("Cannot handle character '" + c + "'");
    }

    static protected class ENDPIECE extends Helper {

        /**
         * Logger for this class
         */
        private static final Logger logger = LoggerFactory.getLogger(ENDPIECE.class);

        void helpWith(char c) throws PipelineException {
            logger.debug("helpWith(c=" + c + ") - start");

            // we are done with the piece
            getHandler().getPipeline().thePieceIsDone();
        }
    }

}
