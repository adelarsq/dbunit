/*
 * Composite.java    Mar 24, 2002
 *
 * The DbUnit Database Testing Framework
 * Copyright (C)2002, Timothy Ruppert && Ben Cox
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

package org.dbunit.ant;

import org.dbunit.DatabaseUnitException;
import org.dbunit.database.IDatabaseConnection;
import java.util.*;

/**
 * The <code>Composite</code> class executes nested <code>Operation</code> steps.
 *
 * @author Timothy Ruppert && Ben Cox
 * @version $Revision$
 * @see org.dbunit.ant.Operation
 */
public class Composite extends Operation
{

    private List operations = new ArrayList();

    public Composite()
    {
        type = "COMPOSITE";
    }

    /**
     * Throw an <code>IllegalStateException</code> if the <code>type</code> is
     * attempted to be changed.
     *
     * @param type a <code>String</code> value
     */
    public void setType(String type)
    {
        throw new IllegalStateException("Cannot set type of a <composite>!");
    }

    /**
     * Gets the Operations.
     */
    public List getOperations()
    {
        return operations;
    }

    /**
     * Adds an Operation.
     */
    public void addOperation(Operation operation)
    {
        operations.add(operation);
    }

    /**
     * Loops over the operations making up the composition, sets
     * their src and flat value, and calls execute on each one.
     *
     * @param conn a <code>IDatabaseConnection</code> value
     * @exception DatabaseUnitException if an error occurs
     */
    public void execute(IDatabaseConnection connection) throws DatabaseUnitException
    {
        Iterator operIter = operations.listIterator();
        while (operIter.hasNext())
        {
            Operation operation = (Operation)operIter.next();
            if (operation.getSrc() != null)
            {
                throw new DatabaseUnitException("Cannot set 'src' attribute "
                        + "in a <composite>'s sub-<operation>");
            }
            operation.setSrc(getSrc());
            if (operation.getRawFormat() != null 
		&& !operation.getFormat().equalsIgnoreCase(getFormat()))
            {
                throw new DatabaseUnitException("Cannot override 'format' attribute "
                        + "in a <composite>'s sub-<operation>");
            }
            operation.setFormat(getFormat());
            operation.execute(connection);
        }

    }

    /**
     * Prints out the log Message for the composite.
     *
     * @return a <code>String</code> value
     */
    public String getLogMessage()
    {
        StringBuffer result = new StringBuffer();
        result.append("Executing composite: ");
        result.append("\n          on   file: " + getSrc().getAbsolutePath());
        Iterator operIter = operations.listIterator();
        while (operIter.hasNext())
        {
            Operation operation = (Operation)operIter.next();
            result.append("\n    operation: " + type);
        }
        return result.toString();
    }

    public String toString()
    {
        StringBuffer result = new StringBuffer();
        result.append("Composite: ");
        result.append("  src=" + getSrc().getAbsolutePath());
	result.append(", format=" + getFormat());
        result.append(", operations=" + operations);

        return result.toString();
    }
}













