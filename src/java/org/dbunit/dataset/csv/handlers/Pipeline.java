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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Pipeline implements Handler {


    private LinkedList components;
    private List products;
    private StringBuffer currentProduct;
    private PipelineComponent noHandler;

    public Pipeline() {
        setComponents(new LinkedList());
        setProducts(new ArrayList());


        // add a no handler as the last handler
        setNoHandler(NoHandler.IGNORE());
        getNoHandler().setSuccessor(null);
        getComponents().addFirst(getNoHandler());

        // add a trasparent handler as placeholder
        //getComponents().addFirst(TransparentHandler.IGNORE);

        //prepareNewPiece();
        setCurrentProduct(new StringBuffer());
        putFront(TransparentHandler.IGNORE());
    }

    public StringBuffer getCurrentProduct() {
        return currentProduct;
    }

    public void setCurrentProduct(StringBuffer currentProduct) {
        this.currentProduct = currentProduct;
    }

    private void prepareNewPiece() {
        setCurrentProduct(new StringBuffer());

        // remove all the components down to a TrasparentHandler
        try {
            while (!(getComponents().getFirst() instanceof TransparentHandler)) {
                removeFront();
            }
        } catch (PipelineException e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    public void thePieceIsDone() {
        getProducts().add(getCurrentProduct().toString());
        prepareNewPiece();
    }

    public List getProducts() {
        return products;
    }

    protected void setProducts(List products) {
        this.products = products;
    }

    private LinkedList getComponents() {
        return components;
    }

    private void setComponents(LinkedList components) {
        this.components = components;
    }

    public void putFront(PipelineComponent component) {
        component.setSuccessor((PipelineComponent) getComponents().getFirst());
        component.setPipeline(this);
        getComponents().addFirst(component);
    }

    public PipelineComponent removeFront() throws PipelineException {
        PipelineComponent first = (PipelineComponent) getComponents().getFirst();
        remove(first);
        return first;
    }

    public void remove(PipelineComponent component) throws PipelineException {

        if (component == getNoHandler()) {
            throw new PipelineException("Cannot remove the last handler");
        }

        if (!getComponents().remove(component)) {
            throw new PipelineException("Cannot remove a non existent component from a pipeline");
        }
    }

    public boolean canHandle(char c) throws IllegalInputCharacterException {
        return true;
    }

    public void handle(char c) throws IllegalInputCharacterException, PipelineException {
        ((Handler) getComponents().getFirst()).handle(c);
    }

    public boolean allowForNoMoreInput() {
        throw new IllegalStateException("you cannot call Pipeline.allowForNoMoreInput");
    }

    private PipelineComponent getNoHandler() {
        return noHandler;
    }

    private void setNoHandler(PipelineComponent noHandler) {
        this.noHandler = noHandler;
    }

    public void resetProducts() {
        setProducts(new ArrayList());
    }

    public void noMoreInput() {
        ((Handler) getComponents().getFirst()).noMoreInput();
        //thePieceIsDone();
    }

}
