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

import org.dbunit.dataset.csv.IllegalInputCharacterException;

public class SeparatorHandler extends AbstractPipelineComponent {

    private SeparatorHandler() {}

    public static final PipelineComponent ACCEPT () {
        return createPipelineComponent(new SeparatorHandler(), new ACCEPT());
    }

    public static final PipelineComponent IGNORE () {
        return createPipelineComponent(new SeparatorHandler(), new IGNORE());
    }

    public static final PipelineComponent ENDPIECE () {
        return createPipelineComponent(new SeparatorHandler(), new ENDPIECE());
    }

    public static final char SEPARATOR_CHAR = ',';

    public boolean canHandle(char c) throws IllegalInputCharacterException {
        if (c == SEPARATOR_CHAR) {
            return true;
        }
        return false; //throw new IllegalInputCharacterException("Cannot handle character '" + c + "'");
    }

    static protected class ENDPIECE extends Helper {
        void helpWith(char c) throws PipelineException {
            // we are done with the piece
            getHandler().getPipeline().thePieceIsDone();
        }
    }

}
