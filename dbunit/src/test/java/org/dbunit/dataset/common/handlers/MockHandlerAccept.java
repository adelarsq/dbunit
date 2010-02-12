package org.dbunit.dataset.common.handlers;


/**
 * author: fede
 * 4-set-2003 11.04.01
 * $Revision$
 */
public class MockHandlerAccept extends MockHandler {
    public boolean canHandle(char c) throws IllegalInputCharacterException {
        return true;
    }
}
