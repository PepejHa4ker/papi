package com.pepej.papi.sql.impl;


import com.pepej.papi.sql.ParameterProvider;
import com.pepej.papi.sql.util.SqlConsumer;
import com.pepej.papi.sql.util.Wrap;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.util.Calendar;
import java.util.Objects;

@SuppressWarnings("unchecked")
class ParameterProviderImpl<P extends ParameterProvider<P, S>, S extends PreparedStatement>
        implements ParameterProvider<P, S> {

    protected final S statement;
    private final SqlBindings bindings;

    ParameterProviderImpl(S statement, SqlBindings bindings) {
        this.statement = statement;
        this.bindings = bindings;
    }

    @Override
    public S getStatement() {
        return statement;
    }

    @Override
    public P prepare(SqlConsumer<S> preparator) {
        Wrap.execute(() -> preparator.accept(statement));
        return (P) this;
    }

    @Override
    public P with(Object... params) {
        return prepare(statement -> bindings.bind(statement, params, 0));
    }

    @Override
    public P set(int index, Object x) {
        return prepare(statement -> bindings.bind(statement, index, Objects.requireNonNull(x)));
    }

    @Override
    public P setArray(int index, Array x) {
        return prepare(statement -> statement.setArray(index, x));
    }

    @Override
    public P setAsciiStream(int index, InputStream x) {
        return prepare(statement -> statement.setAsciiStream(index, x));
    }

    @Override
    public P setAsciiStream(int index, InputStream x, int length) {
        return prepare(statement -> statement.setAsciiStream(index, x, length));
    }

    @Override
    public P setAsciiStream(int index, InputStream x, long length) {
        return prepare(statement -> statement.setAsciiStream(index, x, length));
    }

    @Override
    public P setBigDecimal(int index, BigDecimal x) {
        return prepare(statement -> statement.setBigDecimal(index, x));
    }

    @Override
    public P setBinaryStream(int index, InputStream x) {
        return prepare(statement -> statement.setBinaryStream(index, x));
    }

    @Override
    public P setBinaryStream(int index, InputStream x, int length) {
        return prepare(statement -> statement.setBinaryStream(index, x, length));
    }

    @Override
    public P setBinaryStream(int index, InputStream x, long length) {
        return prepare(statement -> statement.setBinaryStream(index, x, length));
    }

    @Override
    public P setBlob(int index, Blob x) {
        return prepare(statement -> statement.setBlob(index, x));
    }

    @Override
    public P setBlob(int index, InputStream inputStream) {
        return prepare(statement -> statement.setBlob(index, inputStream));
    }

    @Override
    public P setBlob(int index, InputStream inputStream, long length) {
        return prepare(statement -> statement.setBlob(index, inputStream, length));
    }

    @Override
    public P setBoolean(int index, boolean x) {
        return prepare(statement -> statement.setBoolean(index, x));
    }

    @Override
    public P setByte(int index, byte x) {
        return prepare(statement -> statement.setByte(index, x));
    }

    @Override
    public P setBytes(int index, byte[] x) {
        return prepare(statement -> statement.setBytes(index, x));
    }

    @Override
    public P setCharacterStream(int index, Reader reader) {
        return prepare(statement -> statement.setCharacterStream(index, reader));
    }

    @Override
    public P setCharacterStream(int index, Reader reader, int length) {
        return prepare(statement -> statement.setCharacterStream(index, reader, length));
    }

    @Override
    public P setCharacterStream(int index, Reader reader, long length) {
        return prepare(statement -> statement.setCharacterStream(index, reader, length));
    }

    @Override
    public P setClob(int index, Clob x) {
        return prepare(statement -> statement.setClob(index, x));
    }

    @Override
    public P setClob(int index, Reader reader) {
        return prepare(statement -> statement.setClob(index, reader));
    }

    @Override
    public P setClob(int index, Reader reader, long length) {
        return prepare(statement -> statement.setClob(index, reader, length));
    }

    @Override
    public P setDate(int index, Date x) {
        return prepare(statement -> statement.setDate(index, x));
    }

    @Override
    public P setDate(int index, Date x, Calendar cal) {
        return prepare(statement -> statement.setDate(index, x, cal));
    }

    @Override
    public P setDouble(int index, double x) {
        return prepare(statement -> statement.setDouble(index, x));
    }

    @Override
    public P setFloat(int index, float x) {
        return prepare(statement -> statement.setFloat(index, x));
    }

    @Override
    public P setInt(int index, int x) {
        return prepare(statement -> statement.setInt(index, x));
    }

    @Override
    public P setLong(int index, long x) {
        return prepare(statement -> statement.setLong(index, x));
    }

    @Override
    public P setNCharacterStream(int index, Reader value) {
        return prepare(statement -> statement.setNCharacterStream(index, value));
    }

    @Override
    public P setNCharacterStream(int index, Reader value, long length) {
        return prepare(statement -> statement.setNCharacterStream(index, value, length));
    }

    @Override
    public P setNClob(int index, NClob value) {
        return prepare(statement -> statement.setNClob(index, value));
    }

    @Override
    public P setNClob(int index, Reader reader) {
        return prepare(statement -> statement.setNClob(index, reader));
    }

    @Override
    public P setNClob(int index, Reader reader, long length) {
        return prepare(statement -> statement.setNClob(index, reader, length));
    }

    @Override
    public P setNString(int index, String value) {
        return prepare(statement -> statement.setNString(index, value));
    }

    @Override
    public P setNull(int index, int sqlType) {
        return prepare(statement -> statement.setNull(index, sqlType));
    }

    @Override
    public P setNull(int index, int sqlType, String typeName) {
        return prepare(statement -> statement.setNull(index, sqlType, typeName));
    }

    @Override
    public P setObject(int index, Object x) {
        return prepare(statement -> statement.setObject(index, x));
    }

    @Override
    public P setObject(int index, Object x, int targetSqlType) {
        return prepare(statement -> statement.setObject(index, x, targetSqlType));
    }

    @Override
    public P setObject(int index, Object x, int targetSqlType, int scaleOrLength) {
        return prepare(statement -> statement.setObject(index, x, targetSqlType, scaleOrLength));
    }

    @Override
    public P setObject(int index, Object x, SQLType targetSqlType) {
        return prepare(statement -> statement.setObject(index, x, targetSqlType));
    }

    @Override
    public P setObject(int index, Object x, SQLType targetSqlType, int scaleOrLength) {
        return prepare(statement -> statement.setObject(index, x, targetSqlType, scaleOrLength));
    }

    @Override
    public P setRef(int index, Ref x) {
        return prepare(statement -> statement.setRef(index, x));
    }

    @Override
    public P setRowId(int index, RowId x) {
        return prepare(statement -> statement.setRowId(index, x));
    }

    @Override
    public P setShort(int index, short x) {
        return prepare(statement -> statement.setShort(index, x));
    }

    @Override
    public P setSQLXML(int index, SQLXML xmlObject) {
        return prepare(statement -> statement.setSQLXML(index, xmlObject));
    }

    @Override
    public P setString(int index, String x) {
        return prepare(statement -> statement.setString(index, x));
    }

    @Override
    public P setTime(int index, Time x) {
        return prepare(statement -> statement.setTime(index, x));
    }

    @Override
    public P setTime(int index, Time x, Calendar cal) {
        return prepare(statement -> statement.setTime(index, x, cal));
    }

    @Override
    public P setTimestamp(int index, Timestamp x) {
        return prepare(statement -> statement.setTimestamp(index, x));
    }

    @Override
    public P setTimestamp(int index, Timestamp x, Calendar cal) {
        return prepare(statement -> statement.setTimestamp(index, x, cal));
    }

    @Override
    public P setURL(int index, URL x) {
        return prepare(statement -> statement.setURL(index, x));
    }
}
