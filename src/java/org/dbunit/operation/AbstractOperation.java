/*
 *
 * The DbUnit Database Testing Framework
 * Copyright (C)2002, Manuel Laflamme
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
package org.dbunit.operation;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.dataset.DataSetUtils;

/**
 * @author Manuel Laflamme
 * @since Jan 17, 2004
 * @version $Revision$
 */
public abstract class AbstractOperation extends DatabaseOperation
{
    protected String getQualifiedName(String prefix, String name, IDatabaseConnection connection)
    {
        String escapePattern = (String)connection.getConfig().getProperty(
                DatabaseConfig.PROPERTY_ESCAPE_PATTERN);

        return DataSetUtils.getQualifiedName(prefix, name, escapePattern);
    }


}
