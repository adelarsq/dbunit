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

package org.dbunit.dataset;


/**
 * @author Manuel Laflamme
 * @version $Revision$
 * @since Feb 13, 2003
 */
public class NoColumnsFoundException extends DataSetException
{
    public NoColumnsFoundException()
    {
    }

    public NoColumnsFoundException(String msg)
    {
        super(msg);
    }

    public NoColumnsFoundException(String msg, Throwable e)
    {
        super(msg, e);
    }

    public NoColumnsFoundException(Throwable e)
    {
        super(e);
    }
}





