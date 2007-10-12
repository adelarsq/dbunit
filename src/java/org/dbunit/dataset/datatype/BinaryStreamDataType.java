package org.dbunit.dataset.datatype;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BinaryStreamDataType extends BytesDataType
{

    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(BinaryStreamDataType.class);

    public BinaryStreamDataType(String name, int sqlType)
    {
        super(name, sqlType);
    }

    public Object getSqlValue(int column, ResultSet resultSet)
            throws SQLException, TypeCastException
    {
        logger.debug("getSqlValue(column=" + column + ", resultSet=" + resultSet + ") - start");

        InputStream in = resultSet.getBinaryStream(column);
        if (in == null || resultSet.wasNull())
        {
            return null;
        }

        try
        {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[32];
            int length = in.read(buffer);
            while (length != -1)
            {
                out.write(buffer, 0, length);
                length = in.read(buffer);
            }
            return out.toByteArray();
        }
        catch (IOException e)
        {
            logger.error("getSqlValue()", e);

            throw new TypeCastException(e);
        }
    }

    public void setSqlValue(Object value, int column, PreparedStatement statement)
            throws SQLException, TypeCastException
    {
        logger.debug("setSqlValue(value=" + value + ", column=" + column + ", statement=" + statement + ") - start");

        byte[] bytes = (byte[])typeCast(value);
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        statement.setBinaryStream(column, in, bytes.length);
    }

}