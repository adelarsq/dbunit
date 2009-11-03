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
package org.dbunit.database;

import com.mockobjects.sql.MockSingleRowResultSet;

import java.sql.SQLException;

/**
 * @author Manuel Laflamme
 * @since Aug 11, 2003
 * @version $Revision$
 */
public class ExtendedMockSingleRowResultSet extends MockSingleRowResultSet
{
    private Object _lastValue = null;

    public Object getObject(String s) throws SQLException
    {
        Object object = super.getObject(s);
        _lastValue = object;
        return object;
    }

    public Object getObject(int i) throws SQLException
    {
        Object object = super.getObject(i);
        _lastValue = object;
        return object;
    }

    public boolean getBoolean(int i) throws SQLException
    {
        Object object = getObject(i);
        if (object == null)
        {
            return false;
        }
        return super.getBoolean(i);
    }

    public boolean getBoolean(String s) throws SQLException
    {
        Object object = getObject(s);
        if (object == null)
        {
            return false;
        }
        return super.getBoolean(s);
    }

    public byte getByte(int i) throws SQLException
    {
        Object object = getObject(i);
        if (object == null)
        {
            return 0;
        }
        return super.getByte(i);
    }

    public byte getByte(String s) throws SQLException
    {
        Object object = getObject(s);
        if (object == null)
        {
            return 0;
        }
        return super.getByte(s);
    }

    public double getDouble(int i) throws SQLException
    {
        Object object = getObject(i);
        if (object == null)
        {
            return 0;
        }
        return super.getDouble(i);
    }

    public double getDouble(String s) throws SQLException
    {
        Object object = getObject(s);
        if (object == null)
        {
            return 0;
        }
        return super.getDouble(s);
    }

    public float getFloat(int i) throws SQLException
    {
        Object object = getObject(i);
        if (object == null)
        {
            return 0;
        }
        return super.getFloat(i);
    }

    public float getFloat(String s) throws SQLException
    {
        Object object = getObject(s);
        if (object == null)
        {
            return 0;
        }
        return super.getFloat(s);
    }

    public int getInt(int i) throws SQLException
    {
        Object object = getObject(i);
        if (object == null)
        {
            return 0;
        }
        return super.getInt(i);
    }

    public int getInt(String s) throws SQLException
    {
        Object object = getObject(s);
        if (object == null)
        {
            return 0;
        }
        return super.getInt(s);
    }

    public long getLong(int i) throws SQLException
    {
        Object object = getObject(i);
        if (object == null)
        {
            return 0;
        }
        return super.getLong(i);
    }

    public long getLong(String s) throws SQLException
    {
        Object object = getObject(s);
        if (object == null)
        {
            return 0;
        }
        return super.getLong(s);
    }

    public short getShort(String s) throws SQLException
    {
        Object object = getObject(s);
        if (object == null)
        {
            return 0;
        }
        return super.getShort(s);
    }

    public short getShort(int i) throws SQLException
    {
        Object object = getObject(i);
        if (object == null)
        {
            return 0;
        }
        return super.getShort(i);
    }

    public boolean wasNull() throws SQLException
    {
        return _lastValue == null;
    }
}
