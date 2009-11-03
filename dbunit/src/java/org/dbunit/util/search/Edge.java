/*
 *
 * The DbUnit Database Testing Framework
 * Copyright (C)2005, DbUnit.org
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
package org.dbunit.util.search;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Basic implementation of the {@link IEdge} interface.
 * 
 * @author Felipe Leme (dbunit@felipeal.net)
 * @author Last changed by: $Author$
 * @version $Revision$ $Date$
 * @since 2.2.0 (Aug 25, 2005)
 */
public class Edge implements IEdge {

    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(Edge.class);

    private final Comparable nodeFrom;
    private final Comparable nodeTo;

    /**
     * @param nodeFrom
     * @param nodeTo
     */
    public Edge(Comparable nodeFrom, Comparable nodeTo) {
        if (nodeFrom == null) {
            throw new IllegalArgumentException("node from cannot be null");
        }
        if (nodeTo == null) {
            throw new IllegalArgumentException("node to cannot be null");
        }
        this.nodeFrom = nodeFrom;
        this.nodeTo = nodeTo;
    }

    public Object getFrom() {
        return this.nodeFrom;
    }

    public Object getTo() {
        return this.nodeTo;
    }

    public String toString() {
        return this.nodeFrom + "->" + this.nodeTo;
    }

    /**
     * Compares this edge to the given one using 
     * the <code>{@link #getFrom()}</code> nodes first. 
     * If those are equal the <code>{@link #getTo()}}</code>
     * is used for comparison.
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Object o) {
        logger.debug("compareTo(o={}) - start", o);

        Edge otherEdge = (Edge) o;
        int result = this.nodeFrom.compareTo(otherEdge.getFrom());
        if ( result == 0 ) {
            result = this.nodeTo.compareTo(otherEdge.getTo());
        }
        return result;
    }

    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((nodeFrom == null) ? 0 : nodeFrom.hashCode());
        result = prime * result + ((nodeTo == null) ? 0 : nodeTo.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Edge other = (Edge) obj;
        if (nodeFrom == null) {
            if (other.nodeFrom != null)
                return false;
        } else if (!nodeFrom.equals(other.nodeFrom))
            return false;
        if (nodeTo == null) {
            if (other.nodeTo != null)
                return false;
        } else if (!nodeTo.equals(other.nodeTo))
            return false;
        return true;
    }

}
