package org.dbunit.ext.oracle;

import org.dbunit.dataset.datatype.DataType;
import org.dbunit.dataset.datatype.DataTypeException;
import org.dbunit.dataset.datatype.DefaultDataTypeFactory;

import java.sql.Types;

/**
 * Specialized factory that recognizes Oracle data types.
 *
 * <p> Copyright (c) 2002 OZ.COM.  All Rights Reserved. </p>
 * @author manuel.laflamme
 * @since Jul 17, 2003
 */
public class OracleDataTypeFactory extends DefaultDataTypeFactory
{
    public DataType createDataType(int sqlType, String sqlTypeName) throws DataTypeException
    {
        if (sqlType == Types.OTHER)
        {
            // BLOB
            if ("BLOB".equals(sqlTypeName))
            {
                return DataType.BLOB;
            }

            // CLOB
            if ("CLOB".equals(sqlTypeName))
            {
                return DataType.CLOB;
            }

            // TIMESTAMP
            if (sqlTypeName.startsWith("TIMESTAMP"))
            {
                return DataType.TIMESTAMP;
            }
        }

        return super.createDataType(sqlType, sqlTypeName);
    }
}
