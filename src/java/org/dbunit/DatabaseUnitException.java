/*
 *
 * The dbUnit database testing framework.
 * Copyright (C) 2002   Manuel Laflamme
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

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * @author Manuel Laflamme
 * @version 1.0
 */
public class DatabaseUnitException extends Exception
{
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
        return _e;
    }

    //////////////////////////////////////////////////////////////////////
    // Exception class

    /**
     *
     */
    public void printStackTrace()
    {
        super.printStackTrace();
        if (_e != null)
            _e.printStackTrace();
    }

    /**
     *
     */
    public void printStackTrace(PrintStream s)
    {
        super.printStackTrace(s);
        if (_e != null)
            _e.printStackTrace(s);
    }

    /**
     *
     */
    public void printStackTrace(PrintWriter s)
    {
        super.printStackTrace(s);
        if (_e != null)
            _e.printStackTrace(s);
    }

}
