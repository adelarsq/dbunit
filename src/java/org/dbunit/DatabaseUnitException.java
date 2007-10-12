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

package org.dbunit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * @author Manuel Laflamme
 * @version $Revision$
 */
public class DatabaseUnitException extends Exception
{

    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(DatabaseUnitException.class);

    private final Throwable _e;

    /**
     * Constructs an <code>DatabaseUnitException</code> with no detail
     * message and no encapsulated exception.
     */
    public DatabaseUnitException()
    {
        super();
        _e = null;
    }

    /**
     * Constructs an <code>DatabaseUnitException</code> with the specified detail
     * message and no encapsulated exception.
     */
    public DatabaseUnitException(String msg)
    {
        super(msg);
        _e = null;
    }

    /**
     * Constructs an <code>DatabaseUnitException</code> with the specified detail
     * message and encapsulated exception.
     */
    public DatabaseUnitException(String msg, Throwable e)
    {
        super(msg);
        _e = e;
    }

    /**
     * Constructs an <code>DatabaseUnitException</code> with the encapsulated
     * exception and use string representation as detail message.
     */
    public DatabaseUnitException(Throwable e)
    {
        super(e.toString());
        _e = e;
    }

    /**
     * Returns the encapsuled exception or <code>null</code> if none.
     */
    public Throwable getException()
    {
        logger.debug("getException() - start");

        return _e;
    }

    //////////////////////////////////////////////////////////////////////
    // Exception class

    /**
     *
     */
    public void printStackTrace()
    {
        logger.debug("printStackTrace() - start");

        super.printStackTrace();
        if (_e != null)
            _e.printStackTrace();
    }

    /**
     *
     */
    public void printStackTrace(PrintStream s)
    {
        logger.debug("printStackTrace(s=" + s + ") - start");

        super.printStackTrace(s);
        if (_e != null)
            _e.printStackTrace(s);
    }

    /**
     *
     */
    public void printStackTrace(PrintWriter s)
    {
        logger.debug("printStackTrace(s=" + s + ") - start");

        super.printStackTrace(s);
        if (_e != null)
            _e.printStackTrace(s);
    }

}




