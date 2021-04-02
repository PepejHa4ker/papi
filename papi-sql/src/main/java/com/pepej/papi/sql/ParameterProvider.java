package com.pepej.papi.sql;


import com.pepej.papi.sql.util.SqlConsumer;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.util.Calendar;

/**
 * Represents an object holding a {@link PreparedStatement} and providing methods
 * to set it up for a sql query.
 *
 * @param <P> the type of the parent to return from each method of this class
 * @param <S> the type of the statement
 */
public interface ParameterProvider<P extends ParameterProvider<P, S>, S extends PreparedStatement>
        extends StatementHolder<S> {

    /**
     * Prepares an instance of {@link PreparedStatement} to be executed.
     *
     * @param preparator an operation to execute on the statement
     * @return {@code this} for chaining
     */
    P prepare(SqlConsumer<S> preparator);

    /**
     * Sets multiple parameters using magic bindings.
     * <p>
     * See <a href="package-summary.html#magic.mapping">here</a> for the types
     * supported by this method.
     *
     * @param params parameters to set
     * @return {@code this} for chaining
     */
    P with(Object... params);

    /**
     * Sets the designated parameter based on the type of the parameter given.
     * <p>
     * See <a href="package-summary.html#magic.mapping">here</a> for the types
     * supported by this method.
     *
     * @param index the index of the parameter to set (starting from 1)
     * @param x the value to set
     * @return {@code this} for chaining
     * @throws NullPointerException if {@code x} is {@code null}
     */
    P set(int index, Object x);

    /**
     * Sets the designated parameter to the given {@link Array}.
     *
     * @param index the index of the parameter to set (starting from 1)
     * @param x the value to set
     * @return {@code this} for chaining
     * @see PreparedStatement#setArray(int, Array)
     */
    P setArray(int index, Array x);

    /**
     * Sets the designated parameter to the given {@link InputStream}.
     *
     * @param index the index of the parameter to set (starting from 1)
     * @param x the value to set
     * @return {@code this} for chaining
     * @see PreparedStatement#setAsciiStream(int, InputStream)
     */
    P setAsciiStream(int index, InputStream x);

    /**
     * Sets the designated parameter to the given {@link InputStream}.
     *
     * @param index the index of the parameter to set (starting from 1)
     * @param x the value to set
     * @param length the number of bytes in the stream
     * @return {@code this} for chaining
     * @see PreparedStatement#setAsciiStream(int, InputStream, int)
     */
    P setAsciiStream(int index, InputStream x, int length);

    /**
     * Sets the designated parameter to the given {@link InputStream}.
     *
     * @param index the index of the parameter to set (starting from 1)
     * @param x the value to set
     * @param length the number of bytes in the stream
     * @return {@code this} for chaining
     * @see PreparedStatement#setAsciiStream(int, InputStream, long)
     */
    P setAsciiStream(int index, InputStream x, long length);

    /**
     * Sets the designated parameter to the given {@link BigDecimal}.
     *
     * @param index the index of the parameter to set (starting from 1)
     * @param x the value to set
     * @return {@code this} for chaining
     * @see PreparedStatement#setBigDecimal(int, BigDecimal)
     */
    P setBigDecimal(int index, BigDecimal x);

    /**
     * Sets the designated parameter to the given {@link InputStream}.
     *
     * @param index the index of the parameter to set (starting from 1)
     * @param x the value to set
     * @return {@code this} for chaining
     * @see PreparedStatement#setBinaryStream(int, InputStream)
     */
    P setBinaryStream(int index, InputStream x);

    /**
     * Sets the designated parameter to the given {@link InputStream}.
     *
     * @param index the index of the parameter to set (starting from 1)
     * @param x the value to set
     * @param length the number of bytes in the stream
     * @return {@code this} for chaining
     * @see PreparedStatement#setBinaryStream(int, InputStream, int)
     */
    P setBinaryStream(int index, InputStream x, int length);

    /**
     * Sets the designated parameter to the given {@link InputStream}.
     *
     * @param index the index of the parameter to set (starting from 1)
     * @param x the value to set
     * @param length the number of bytes in the stream
     * @return {@code this} for chaining
     * @see PreparedStatement#setBinaryStream(int, InputStream, long)
     */
    P setBinaryStream(int index, InputStream x, long length);

    /**
     * Sets the designated parameter to the given {@link Blob}.
     *
     * @param index the index of the parameter to set (starting from 1)
     * @param x the value to set
     * @return {@code this} for chaining
     * @see PreparedStatement#setBlob(int, Blob)
     */
    P setBlob(int index, Blob x);

    /**
     * Sets the designated parameter to the given {@link InputStream}.
     *
     * @param index the index of the parameter to set (starting from 1)
     * @param x the value to set
     * @return {@code this} for chaining
     * @see PreparedStatement#setBlob(int, InputStream)
     */
    P setBlob(int index, InputStream x);

    /**
     * Sets the designated parameter to the given {@link InputStream}.
     *
     * @param index the index of the parameter to set (starting from 1)
     * @param x the value to set
     * @param length the number of bytes in the stream
     * @return {@code this} for chaining
     * @see PreparedStatement#setBlob(int, InputStream, long)
     */
    P setBlob(int index, InputStream x, long length);

    /**
     * Sets the designated parameter to the given {@code boolean}.
     *
     * @param index the index of the parameter to set (starting from 1)
     * @param x the value to set
     * @return {@code this} for chaining
     * @see PreparedStatement#setBoolean(int, boolean)
     */
    P setBoolean(int index, boolean x);

    /**
     * Sets the designated parameter to the given {@code byte}.
     *
     * @param index the index of the parameter to set (starting from 1)
     * @param x the value to set
     * @return {@code this} for chaining
     * @see PreparedStatement#setByte(int, byte)
     */
    P setByte(int index, byte x);

    /**
     * Sets the designated parameter to the given {@code byte[]}.
     *
     * @param index the index of the parameter to set (starting from 1)
     * @param x the value to set
     * @return {@code this} for chaining
     * @see PreparedStatement#setBytes(int, byte[])
     */
    P setBytes(int index, byte[] x);

    /**
     * Sets the designated parameter to the given {@link Reader}.
     *
     * @param index the index of the parameter to set (starting from 1)
     * @param x the value to set
     * @return {@code this} for chaining
     * @see PreparedStatement#setCharacterStream(int, Reader)
     */
    P setCharacterStream(int index, Reader x);

    /**
     * Sets the designated parameter to the given {@link Reader}.
     *
     * @param index the index of the parameter to set (starting from 1)
     * @param x the value to set
     * @param length the number of characters in the stream
     * @return {@code this} for chaining
     * @see PreparedStatement#setCharacterStream(int, Reader, int)
     */
    P setCharacterStream(int index, Reader x, int length);

    /**
     * Sets the designated parameter to the given {@link Reader}.
     *
     * @param index the index of the parameter to set (starting from 1)
     * @param x the value to set
     * @param length the number of characters in the stream
     * @return {@code this} for chaining
     * @see PreparedStatement#setCharacterStream(int, Reader, long)
     */
    P setCharacterStream(int index, Reader x, long length);

    /**
     * Sets the designated parameter to the given {@link Clob}.
     *
     * @param index the index of the parameter to set (starting from 1)
     * @param x the value to set
     * @return {@code this} for chaining
     * @see PreparedStatement#setClob(int, Clob)
     */
    P setClob(int index, Clob x);

    /**
     * Sets the designated parameter to the given {@link Reader}.
     *
     * @param index the index of the parameter to set (starting from 1)
     * @param x the value to set
     * @return {@code this} for chaining
     * @see PreparedStatement#setClob(int, Reader)
     */
    P setClob(int index, Reader x);

    /**
     * Sets the designated parameter to the given {@link Reader}.
     *
     * @param index the index of the parameter to set (starting from 1)
     * @param x the value to set
     * @param length the number of characters in the stream
     * @return {@code this} for chaining
     * @see PreparedStatement#setClob(int, Reader, long)
     */
    P setClob(int index, Reader x, long length);

    /**
     * Sets the designated parameter to the given {@link Date}.
     *
     * @param index the index of the parameter to set (starting from 1)
     * @param x the value to set
     * @return {@code this} for chaining
     * @see PreparedStatement#setDate(int, Date)
     */
    P setDate(int index, Date x);

    /**
     * Sets the designated parameter to the given {@link Date}.
     *
     * @param index the index of the parameter to set (starting from 1)
     * @param x the value to set
     * @param cal the {@link Calendar} object the driver will use to construct the date
     * @return {@code this} for chaining
     * @see PreparedStatement#setDate(int, Date, Calendar)
     */
    P setDate(int index, Date x, Calendar cal);

    /**
     * Sets the designated parameter to the given {@code double}.
     *
     * @param index the index of the parameter to set (starting from 1)
     * @param x the value to set
     * @return {@code this} for chaining
     * @see PreparedStatement#setDouble(int, double)
     */
    P setDouble(int index, double x);

    /**
     * Sets the designated parameter to the given {@code float}.
     *
     * @param index the index of the parameter to set (starting from 1)
     * @param x the value to set
     * @return {@code this} for chaining
     * @see PreparedStatement#setFloat(int, float)
     */
    P setFloat(int index, float x);

    /**
     * Sets the designated parameter to the given {@code int}.
     *
     * @param index the index of the parameter to set (starting from 1)
     * @param x the value to set
     * @return {@code this} for chaining
     * @see PreparedStatement#setInt(int, int)
     */
    P setInt(int index, int x);

    /**
     * Sets the designated parameter to the given {@code long}.
     *
     * @param index the index of the parameter to set (starting from 1)
     * @param x the value to set
     * @return {@code this} for chaining
     * @see PreparedStatement#setLong(int, long)
     */
    P setLong(int index, long x);

    /**
     * Sets the designated parameter to the given {@link Reader}.
     *
     * @param index the index of the parameter to set (starting from 1)
     * @param x the value to set
     * @return {@code this} for chaining
     * @see PreparedStatement#setNCharacterStream(int, Reader)
     */
    P setNCharacterStream(int index, Reader x);

    /**
     * Sets the designated parameter to the given {@link Reader}.
     *
     * @param index the index of the parameter to set (starting from 1)
     * @param x the value to set
     * @param length the number of characters in the stream
     * @return {@code this} for chaining
     * @see PreparedStatement#setNCharacterStream(int, Reader, long length)
     */
    P setNCharacterStream(int index, Reader x, long length);

    /**
     * Sets the designated parameter to the given {@link NClob}.
     *
     * @param index the index of the parameter to set (starting from 1)
     * @param x the value to set
     * @return {@code this} for chaining
     * @see PreparedStatement#setNClob(int, NClob)
     */
    P setNClob(int index, NClob x);

    /**
     * Sets the designated parameter to the given {@link Reader}.
     *
     * @param index the index of the parameter to set (starting from 1)
     * @param x the value to set
     * @return {@code this} for chaining
     * @see PreparedStatement#setNClob(int, Reader)
     */
    P setNClob(int index, Reader x);

    /**
     * Sets the designated parameter to the given {@link Reader}.
     *
     * @param index the index of the parameter to set (starting from 1)
     * @param x the value to set
     * @param length the number of characters in the stream
     * @return {@code this} for chaining
     * @see PreparedStatement#setNClob(int, Reader, long)
     */
    P setNClob(int index, Reader x, long length);

    /**
     * Sets the designated parameter to the given {@link String}.
     *
     * @param index the index of the parameter to set (starting from 1)
     * @param x the value to set
     * @return {@code this} for chaining
     * @see PreparedStatement#setNString(int, String)
     */
    P setNString(int index, String x);

    /**
     * Sets the designated parameter to the given {@code int}.
     *
     * @param index the index of the parameter to set (starting from 1)
     * @param sqlType the SQL type code defined in {@link Types}
     * @return {@code this} for chaining
     * @see PreparedStatement#setNull(int, int)
     */
    P setNull(int index, int sqlType);

    /**
     * Sets the designated parameter to the given {@code int}.
     *
     * @param index the index of the parameter to set (starting from 1)
     * @param sqlType the SQL type code defined in {@link Types}
     * @param typeName the fully-qualified name of an SQL user-defined type;
     *     ignored if the parameter is not a user-defined type or REF
     * @return {@code this} for chaining
     * @see PreparedStatement#setNull(int, int, String)
     */
    P setNull(int index, int sqlType, String typeName);

    /**
     * Sets the designated parameter to the given {@link Object}.
     *
     * @param index the index of the parameter to set (starting from 1)
     * @param x the value to set
     * @return {@code this} for chaining
     * @see PreparedStatement#setObject(int, Object)
     */
    P setObject(int index, Object x);

    /**
     * Sets the designated parameter to the given {@link Object}.
     *
     * @param index the index of the parameter to set (starting from 1)
     * @param x the value to set
     * @param targetSqlType the SQL type code defined in {@link Types}
     * @return {@code this} for chaining
     * @see PreparedStatement#setObject(int, Object, int)
     */
    P setObject(int index, Object x, int targetSqlType);

    /**
     * Sets the designated parameter to the given {@link Object}.
     *
     * @param index the index of the parameter to set (starting from 1)
     * @param x the value to set
     * @param targetSqlType the SQL type code defined in {@link Types}
     * @param scaleOrLength for {@code java.sql.Types.DECIMAL}
     *     or {@code java.sql.Types.NUMERIC}, this is the number of digits
     *     after the decimal point. For Java Object types {@link InputStream}
     *     and {@link Reader}, this is the length of the data in the stream
     *     or reader. For all other types, this value will be ignored.
     * @return {@code this} for chaining
     * @see PreparedStatement#setObject(int, Object, int, int)
     */
    P setObject(int index, Object x, int targetSqlType, int scaleOrLength);

    /**
     * Sets the designated parameter to the given {@link Object}.
     *
     * @param index the index of the parameter to set (starting from 1)
     * @param x the value to set
     * @param targetSqlType the SQL type code defined in {@link Types}
     * @return {@code this} for chaining
     * @see PreparedStatement#setObject(int, Object, SQLType)
     */
    P setObject(int index, Object x, SQLType targetSqlType);

    /**
     * Sets the designated parameter to the given {@link Object}.
     *
     * @param index the index of the parameter to set (starting from 1)
     * @param x the value to set
     * @param targetSqlType the SQL type code defined in {@link Types}
     * @param scaleOrLength for {@code java.sql.Types.DECIMAL}
     *     or {@code java.sql.Types.NUMERIC}, this is the number of digits
     *     after the decimal point. For Java Object types {@link InputStream}
     *     and {@link Reader}, this is the length of the data in the stream
     *     or reader. For all other types, this value will be ignored.
     * @return {@code this} for chaining
     * @see PreparedStatement#setObject(int, Object, SQLType, int)
     */
    P setObject(int index, Object x, SQLType targetSqlType, int scaleOrLength);

    /**
     * Sets the designated parameter to the given {@link Ref}.
     *
     * @param index the index of the parameter to set (starting from 1)
     * @param x the value to set
     * @return {@code this} for chaining
     * @see PreparedStatement#setRef(int, Ref)
     */
    P setRef(int index, Ref x);

    /**
     * Sets the designated parameter to the given {@link RowId}.
     *
     * @param index the index of the parameter to set (starting from 1)
     * @param x the value to set
     * @return {@code this} for chaining
     * @see PreparedStatement#setRowId(int, RowId)
     */
    P setRowId(int index, RowId x);

    /**
     * Sets the designated parameter to the given {@code short}.
     *
     * @param index the index of the parameter to set (starting from 1)
     * @param x the value to set
     * @return {@code this} for chaining
     * @see PreparedStatement#setShort(int, short)
     */
    P setShort(int index, short x);

    /**
     * Sets the designated parameter to the given {@link SQLXML}.
     *
     * @param index the index of the parameter to set (starting from 1)
     * @param x the value to set
     * @return {@code this} for chaining
     * @see PreparedStatement#setSQLXML(int, SQLXML)
     */
    P setSQLXML(int index, SQLXML x);

    /**
     * Sets the designated parameter to the given {@link String}.
     *
     * @param index the index of the parameter to set (starting from 1)
     * @param x the value to set
     * @return {@code this} for chaining
     * @see PreparedStatement#setString(int, String)
     */
    P setString(int index, String x);

    /**
     * Sets the designated parameter to the given {@link Time}.
     *
     * @param index the index of the parameter to set (starting from 1)
     * @param x the value to set
     * @return {@code this} for chaining
     * @see PreparedStatement#setTime(int, Time)
     */
    P setTime(int index, Time x);

    /**
     * Sets the designated parameter to the given {@link Time}.
     *
     * @param index the index of the parameter to set (starting from 1)
     * @param x the value to set
     * @param cal the {@link Calendar} object the driver will use to construct the date
     * @return {@code this} for chaining
     * @see PreparedStatement#setTime(int, Time, Calendar)
     */
    P setTime(int index, Time x, Calendar cal);

    /**
     * Sets the designated parameter to the given {@link Timestamp}.
     *
     * @param index the index of the parameter to set (starting from 1)
     * @param x the value to set
     * @return {@code this} for chaining
     * @see PreparedStatement#setTimestamp(int, Timestamp)
     */
    P setTimestamp(int index, Timestamp x);

    /**
     * Sets the designated parameter to the given {@link Timestamp}.
     *
     * @param index the index of the parameter to set (starting from 1)
     * @param x the value to set
     * @param cal the {@link Calendar} object the driver will use to construct the date
     * @return {@code this} for chaining
     * @see PreparedStatement#setTimestamp(int, Timestamp, Calendar)
     */
    P setTimestamp(int index, Timestamp x, Calendar cal);

    /**
     * Sets the designated parameter to the given {@link URL}.
     *
     * @param index the index of the parameter to set (starting from 1)
     * @param x the value to set
     * @return {@code this} for chaining
     * @see PreparedStatement#setURL(int, URL)
     */
    P setURL(int index, URL x);
}
