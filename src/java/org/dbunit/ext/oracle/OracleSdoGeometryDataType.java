/*
 *
 * The DbUnit Database Testing Framework
 * Copyright (C)2002-2008, DbUnit.org
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

package org.dbunit.ext.oracle;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.ParseException;

import oracle.sql.STRUCT;
import oracle.sql.StructDescriptor;

import org.dbunit.dataset.datatype.AbstractDataType;
import org.dbunit.dataset.datatype.TypeCastException;
import org.dbunit.dataset.ITable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class implements DataType for Oracle SDO_GEOMETRY type used in Oracle Spatial. 
 * See the Oracle Spatial Developer's Guide for details on SDO_GEOMETRY.  This class
 * handles values similar to:
 * <ul>
 * <li>SDO_GEOMETRY(NULL, NULL, NULL, NULL, NULL)</li>
 * <li>NULL</li>
 * <li>SDO_GEOMETRY(2001, 8307, SDO_POINT_TYPE(71.2988, 42.8052, NULL), NULL, NULL)</li>
 * <li>SDO_GEOMETRY(3302, NULL, SDO_POINT_TYPE(96.8233, 32.5261, NULL), SDO_ELEM_INFO_ARRAY(1, 2, 1), SDO_ORDINATE_ARRAY(2, 2, 0, 2, 4, 2, 8, 4, 8, 12, 4, 12, 12, 10, NULL, 8, 10, 22, 5, 14, 27))</li>
 * </ul>
 *
 * <p>
 * For more information on oracle spatial support go to http://tahiti.oracle.com
 * and search for &quot;spatial&quot;.  The developers guide is available at
 * http://download.oracle.com/docs/cd/B28359_01/appdev.111/b28400/toc.htm
 * </p>
 *
 * <p>
 * example table:
 * <code>
 *   CREATE TABLE cola_markets (
 *     mkt_id NUMBER PRIMARY KEY,
 *     name VARCHAR2(32),
 *     shape SDO_GEOMETRY);
 * </code>
 * </p>
 *
 * <p>
 * example insert:
 * <code>
 *   INSERT INTO cola_markets VALUES(
 *     2,
 *     'cola_b',
 *     SDO_GEOMETRY(
 *       2003,  -- two-dimensional polygon
 *       NULL,
 *       NULL,
 *       SDO_ELEM_INFO_ARRAY(1,1003,1), -- one polygon (exterior polygon ring)
 *       SDO_ORDINATE_ARRAY(5,1, 8,1, 8,6, 5,7, 5,1)
 *     )
 *    );
 * </code>
 * </p>
 *
 * @author clucas@e-miles.com
 * @author Last changed by: $Author$
 * @version $Revision$ $Date$
 * @since <dbunit-version>
 */
public class OracleSdoGeometryDataType extends AbstractDataType
{
    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(OracleSdoGeometryDataType.class);

    private static final String NULL = "NULL";
    private static final String SDO_GEOMETRY = "SDO_GEOMETRY";
    private static final String SDO_POINT_TYPE = "SDO_POINT_TYPE";
    private static final String SDO_ELEM_INFO_ARRAY = "SDO_ELEM_INFO_ARRAY";
    private static final String SDO_ORDINATE_ARRAY = "SDO_ORDINATE_ARRAY";

    OracleSdoGeometryDataType ()
    {
        super(SDO_GEOMETRY, Types.STRUCT, java.sql.Struct.class, false);
    }

    public Object typeCast(Object value) throws TypeCastException
    {
        return typeCast(value, null);
    }

    /**
     * This method parses out a list of numbers similar to:
     * <ul>
     * <li>
     *   SDO_ORDINATE_ARRAY(2, 2, 0, 2, 4, 2, 8, 4, 8, 12, 4, 12, 12, 10, NULL, 8)
     * </li>
     * <li>
     *   SDO_POINT_TYPE(96.8233, 32.5261, NULL)
     * </li>
     * </ul>
     * 
     * @param input input string to parse (may be null)
     * @param name name of array (ex: SDO_ORDINATE_ARRAY)
     * @return array of parsed numbers some of which may be null
     */
    private BigDecimal [] parseNumbers(String input, String name) throws ParseException
    {
        logger.debug("parseNumbers(input={}, name={}) - start", input, name);
        if (input == null)
        {
            return null;
        }

        int workIndex = 0;
        if (name != null)
        {
            if (! input.startsWith(name))
            {
                throw new ParseException("missing " + name, workIndex);
            }
            workIndex += name.length();
        }

        if (workIndex+1 >= input.length() || input.charAt(workIndex) != '(')
        {
            throw new ParseException("missing (", workIndex);
        }
        if (input.charAt(input.length() - 1) != ')')
        {
            throw new ParseException("missing )", input.length() - 1);
        }
        String [] numberStrings = input.substring(workIndex + 1, input.length() - 1).split(",");
        if (numberStrings == null)
        {
            return null;
        }
        BigDecimal returnVal [] = new BigDecimal[numberStrings.length];
        for (int index=0; index<numberStrings.length; index++)
        {
            String valToParse = numberStrings[index].trim();
            logger.debug("parsing {} as BigDecimal", valToParse);
            if (NULL.equals(valToParse))
            {
                returnVal[index] = null;
            }
            else
            {
                returnVal[index] = new BigDecimal(valToParse);
            }
        }

        return returnVal;
    }

    /**
     * This method performs a typeCast using a jdbc connection.  The connection is required
     * for working with oracle STRUCTs.
     *
     */
    public Object typeCast(Object value, Connection connection) throws TypeCastException
    {
        logger.debug("typeCast(value={}) - start", value);

        if (value == null || value == ITable.NO_VALUE)
        {
            return null;
        }

        if (value instanceof java.sql.Struct)
        {
            return value;
        }

        if (value instanceof String)
        {
            // attempt to parse the SDO_GEOMETRY
            if (connection == null)
            {
                throw new TypeCastException(value, this);
            }

            try
            {
                String upperVal = ((String) value).toUpperCase();
                if (NULL.equals(upperVal))
                {
                    return null;
                }

                if (! (upperVal.startsWith(SDO_GEOMETRY + "(") && upperVal.endsWith(")")))
                {
                    throw new TypeCastException(value, this);
                }

                String workingVal = upperVal.substring(
                    (SDO_GEOMETRY + "(").length(), upperVal.length() - 1);
                int workingIndex = 0;

                // parse out SDO_GTYPE
                int commaIndex = workingVal.indexOf(",", workingIndex);
                if (commaIndex == -1 || commaIndex == workingIndex ||
                    commaIndex+1 == workingVal.length())
                {
                    throw new TypeCastException(value, this);
                }
                String gtypeString = workingVal.substring(workingIndex, commaIndex).trim();
                Integer gtype = NULL.equals(gtypeString) ? null : new Integer(gtypeString);
                workingIndex = commaIndex + 1;

                // parse out SDO_SRID
                commaIndex = workingVal.indexOf(",", workingIndex);
                if (commaIndex == -1 || commaIndex == workingIndex ||
                    commaIndex+1 == workingVal.length())
                {
                    throw new TypeCastException(value, this);
                }
                String sridString = workingVal.substring(workingIndex, commaIndex).trim();
                Integer srid = NULL.equals(sridString) ? null : new Integer(sridString);
                workingIndex = commaIndex + 1;

                // parse out SDO_POINT
                workingVal = workingVal.substring(workingIndex).trim();
                workingIndex = 0;

                BigDecimal pointValues [];
                if (workingVal.startsWith(NULL))
                {
                    pointValues = null;
                }
                else
                {
                    int closingParenIndex = workingVal.indexOf(")", workingIndex);
                    if (closingParenIndex == -1)
                    {
                        throw new TypeCastException(value, this);
                    }
                    pointValues = parseNumbers(workingVal.substring(
                        workingIndex, closingParenIndex + 1), SDO_POINT_TYPE);
                    workingIndex = closingParenIndex + 1;
                }


                // eat the comma
                commaIndex = workingVal.indexOf(",", workingIndex);
                if (commaIndex == -1 || commaIndex+1 == workingVal.length())
                {
                    throw new TypeCastException(value, this);
                }
                workingVal = workingVal.substring(commaIndex + 1).trim();
                workingIndex = 0;
                

                BigDecimal elemInfos [];
                if (workingVal.startsWith(NULL))
                {
                    elemInfos = null;
                }
                else
                {
                    int closingParenIndex = workingVal.indexOf(")", workingIndex);
                    if (closingParenIndex == -1)
                    {
                        throw new TypeCastException(value, this);
                    }
                    elemInfos = parseNumbers(workingVal.substring(
                        workingIndex, closingParenIndex + 1), SDO_ELEM_INFO_ARRAY);
                    workingIndex = closingParenIndex + 1;
                }

                // eat the comma
                commaIndex = workingVal.indexOf(",", workingIndex);
                if (commaIndex == -1 || commaIndex+1 == workingVal.length())
                {
                    throw new TypeCastException(value, this);
                }
                workingVal = workingVal.substring(commaIndex + 1).trim();
                workingIndex = 0;
                
                BigDecimal ordinates [];
                if (workingVal.startsWith(NULL))
                {
                    ordinates = null;
                }
                else
                {
                    int closingParenIndex = workingVal.indexOf(")", workingIndex);
                    if (closingParenIndex == -1)
                    {
                        throw new TypeCastException(value, this);
                    }
                    ordinates = parseNumbers(workingVal.substring(
                        workingIndex, closingParenIndex + 1), SDO_ORDINATE_ARRAY);
                    workingIndex = closingParenIndex + 1;
                }


                // Now package it all up in an oracle STRUCT object

                // SDO_POINT_TYPE
                STRUCT pointStruct;
                if (pointValues == null)
                {
                    pointStruct = null;
                }
                else
                {
                    // SDO_POINT_TYPE has x,y,z coordinates
                    if (pointValues.length != 3)
                    {
                        throw new TypeCastException(value, this);
                    }
                    StructDescriptor pointDescriptor = StructDescriptor.createDescriptor(SDO_POINT_TYPE, connection);
                    Object [] pointAttributes = new Object []
                        { pointValues[0], pointValues[1], pointValues[2] };
                    pointStruct = new STRUCT(pointDescriptor, connection, pointAttributes);
                }

                // SDO_GEOMETRY
                StructDescriptor geometryDescriptor = StructDescriptor.createDescriptor(SDO_GEOMETRY, connection);
                Object [] geometryAttributes = new Object []
                    { gtype, srid, pointStruct, elemInfos, ordinates };
                STRUCT geometryStruct = new STRUCT(geometryDescriptor, connection, geometryAttributes);

                return geometryStruct;
            }
            catch (ParseException e)
            {
                throw new TypeCastException(value, this, e);
            }
            catch (SQLException e)
            {
                throw new TypeCastException(value, this, e);
            }
        }

        throw new TypeCastException(value, this);
    }


    protected void appendNamedArray(StringBuffer buf, String name,
        java.sql.Array array) throws SQLException
    {
        if (array == null)
        {
            buf.append(NULL);
        }
        else
        {
            buf.append(name);
            buf.append("(");
            Object elements [] = (Object[]) array.getArray();
            for (int index=0; index<elements.length; index++)
            {
                buf.append(elements[index] == null ? NULL : elements[index]);
                if (index+1 != elements.length)
                {
                    buf.append(", ");
                }
            }
            buf.append(")");
        }
    }

    public Object getSqlValue(int column, ResultSet resultSet)
        throws SQLException, TypeCastException
    {
        if(logger.isDebugEnabled())
            logger.debug("getSqlValue(column={}, resultSet={}) - start",
            new Integer(column), resultSet);

        Object data = null;
        try
        {
            data = resultSet.getObject(column);

            if (data != null)
            {
                if (! (data instanceof STRUCT))
                {
                    throw new TypeCastException(data, this);
                }

                Object attributes [] = ((STRUCT) data).getAttributes();

                // build out a string representing the SDO_GEOMETRY
                StringBuffer buf = new StringBuffer();

                Object attribute;
                buf.append(SDO_GEOMETRY);
                buf.append("(");
                attribute = attributes[0];
                buf.append(attribute == null ? NULL : attribute);
                buf.append(", ");
                attribute = attributes[1];
                buf.append(attribute == null ? NULL : attribute);
                buf.append(", ");
                attribute = attributes[2];
                if (attribute == null)
                {
                    buf.append(NULL);
                }
                else
                {
                    buf.append(SDO_POINT_TYPE);
                    buf.append("(");
                    Object [] pointAttributes = ((STRUCT) attribute).getAttributes();
                    buf.append(pointAttributes[0] == null ? NULL : pointAttributes[0]);
                    buf.append(", ");
                    buf.append(pointAttributes[1] == null ? NULL : pointAttributes[1]);
                    buf.append(", ");
                    buf.append(pointAttributes[2] == null ? NULL : pointAttributes[2]);
                    buf.append(")");
                }
                buf.append(", ");

                appendNamedArray(buf, SDO_ELEM_INFO_ARRAY, (java.sql.Array) attributes[3]);
                buf.append(", ");
                appendNamedArray(buf, SDO_ORDINATE_ARRAY, (java.sql.Array) attributes[4]);

                buf.append(")");

                return buf.toString();
            }
            else
            {
                // return a string instead of null so that it can be interpreted
                // in typeCast.  DBUnit does not handle PreparedStatement.setNull
                // for user defined types.
                return NULL;
            }

        }
        catch (SQLException e)
        {
            throw new TypeCastException(data, this, e);
        }
    }

    public void setSqlValue(Object value, int column, PreparedStatement statement) throws SQLException, TypeCastException
    {
        Object castValue = typeCast(value, statement.getConnection());
        if (castValue == null)
        {
            statement.setNull(column, Types.STRUCT, SDO_GEOMETRY);
        }
        else
        {
            statement.setObject(column, castValue, Types.STRUCT);
        }
    }
}
