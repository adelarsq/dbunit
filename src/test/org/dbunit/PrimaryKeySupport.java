/*
 * PrimaryKeySupport.java   Feb 20, 2002
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

public class PrimaryKeySupport
{
    public static final PrimaryKeySupport SINGLE = new PrimaryKeySupport("single");
    public static final PrimaryKeySupport MULTIPLE = new PrimaryKeySupport("multiple");
    public static final PrimaryKeySupport NONE = new PrimaryKeySupport("none");

    private final String _name;

    private PrimaryKeySupport(String name)
    {
        _name = name;
    }

    public String toString()
    {
        return _name;
    }

    public static PrimaryKeySupport forName(String name)
    {
        if (SINGLE.toString().equalsIgnoreCase(name))
        {
            return SINGLE;
        }
        else if (MULTIPLE.toString().equalsIgnoreCase(name))
        {
            return MULTIPLE;
        }
        else
        {
            return NONE;
        }
    }
}
