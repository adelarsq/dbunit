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

/**
 * author: fede
 * 4-set-2003 10.20.45
 * $Revision$
 */
public class EnforceHandler extends AbstractPipelineComponent {

    private PipelineComponent [] enforcedComponents;
    private PipelineComponent theHandlerComponent;

    private EnforceHandler(PipelineComponent [] components) {
        setEnforcedComponents(components);
    }


    public static final PipelineComponent ENFORCE(PipelineComponent component) {
        return EnforceHandler.ENFORCE(new PipelineComponent [] {component});
    }

    public static final PipelineComponent ENFORCE(PipelineComponent [] components) {
        return createPipelineComponent(new EnforceHandler(components), new ENFORCE());
    }

    public boolean canHandle(char c) throws IllegalInputCharacterException {
        for (int i = 0; i < getEnforcedComponents().length; i++) {
            if (getEnforcedComponents()[i].canHandle(c)) {
                setTheHandlerComponent(getEnforcedComponents()[i]);
                return true;
            }
        }
        throw new IllegalInputCharacterException("(working on piece #" + getPipeline().getProducts().size() + ")"
                + getPipeline().getCurrentProduct().toString() + ": " + "Character '" + c + "' cannot be handled");
    }

    public void setPipeline(Pipeline pipeline) {
        for (int i = 0; i < getEnforcedComponents().length; i++) {
            getEnforcedComponents()[i].setPipeline(pipeline);
        }
        super.setPipeline(pipeline);
    }

    protected PipelineComponent[] getEnforcedComponents() {
        return enforcedComponents;
    }

    protected void setEnforcedComponents(PipelineComponent[] enforcedComponents) {
        this.enforcedComponents = enforcedComponents;
    }

    PipelineComponent getTheHandlerComponent() {
        return theHandlerComponent;
    }

    void setTheHandlerComponent(PipelineComponent theHandlerComponent) {
        this.theHandlerComponent = theHandlerComponent;
    }

    static private class ENFORCE extends Helper {

        public void helpWith(char c) {
            try {
                EnforceHandler handler = (EnforceHandler) getHandler();
                handler.getTheHandlerComponent().handle(c);
                getHandler().getPipeline().removeFront();
            } catch (PipelineException e) {
                throw new RuntimeException(e.getMessage());
            } catch (IllegalInputCharacterException e) {
                throw new RuntimeException(e.getMessage());
            }
            // ignore the char
        }
    }
}
