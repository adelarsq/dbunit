package org.dbunit.dataset.csv.handlers;

import org.dbunit.dataset.csv.IllegalInputCharacterException;

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
