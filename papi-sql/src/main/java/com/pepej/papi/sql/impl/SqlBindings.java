package com.pepej.papi.sql.impl;

import com.pepej.papi.sql.PreparedStatementBinderByIndex;
import com.pepej.papi.sql.ResultSetRetrieverByIndex;
import com.pepej.papi.sql.ResultSetRetrieverByName;

import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Provides type based bindings for {@link ResultSet} and {@link PreparedStatement}
 * parameters.
 * <p>
 * Note that this class is not part of the public API of this library and as such, is
 * not required to stay compatible between versions.
 *
 * @see ParameterProviderImpl#with(Object...)
 * @see ParameterProviderImpl#set(int, Object)
 */
public final class SqlBindings {

    public static class Bindings<T> {
        private final ResultSetRetrieverByIndex<T> retrieverByIndex;
        private final ResultSetRetrieverByName<T> retrieverByName;

        public Bindings(ResultSetRetrieverByIndex<T> retrieverByIndex,
                        ResultSetRetrieverByName<T> retrieverByName) {
            this.retrieverByIndex = retrieverByIndex;
            this.retrieverByName = retrieverByName;
        }
    }

    private final Map<Class<?>, PreparedStatementBinderByIndex<?>> binders;
    private final Map<Class<?>, SqlBindings.Bindings<?>> retrievers;

    SqlBindings() {
        this.binders = new HashMap<>();
        this.retrievers = new HashMap<>();

        addMapping(Date.class, ResultSet::getDate, ResultSet::getDate, PreparedStatement::setDate);
        addMapping(Time.class, ResultSet::getTime, ResultSet::getTime, PreparedStatement::setTime);
        addMapping(Timestamp.class, ResultSet::getTimestamp, ResultSet::getTimestamp, PreparedStatement::setTimestamp);

        addMapping(LocalDate.class, (rs, i) -> rs.getDate(i).toLocalDate(), (rs, name) -> rs.getDate(name).toLocalDate(), (statement, index, value) -> statement.setDate(index, Date.valueOf(value)));
        addMapping(LocalDateTime.class, (rs, i) -> rs.getTimestamp(i).toLocalDateTime(), (rs, name) -> rs.getTimestamp(name).toLocalDateTime(), (statement, index, value) -> statement.setTimestamp(index, Timestamp.valueOf(value)));
        addMapping(LocalTime.class, (rs, i) -> rs.getTime(i).toLocalTime(), (rs, name) -> rs.getTime(name).toLocalTime(), (statement, index, value) -> statement.setTime(index, Time.valueOf(value)));
        addMapping(UUID.class, (rs, i) -> UUID.fromString(rs.getString(i)), (rs, name) -> UUID.fromString(rs.getString(name)), (statement, index, value) -> statement.setString(index, value.toString()));
        addMapping(String.class, ResultSet::getString, ResultSet::getString, PreparedStatement::setString);

        addMapping(Long.class, ResultSet::getLong, ResultSet::getLong, PreparedStatement::setLong);
        addMapping(long.class, ResultSet::getLong, ResultSet::getLong, PreparedStatement::setLong);
        addMapping(Integer.class, ResultSet::getInt, ResultSet::getInt, PreparedStatement::setInt);
        addMapping(int.class, ResultSet::getInt, ResultSet::getInt, PreparedStatement::setInt);
        addMapping(Short.class, ResultSet::getShort, ResultSet::getShort, PreparedStatement::setShort);
        addMapping(short.class, ResultSet::getShort, ResultSet::getShort, PreparedStatement::setShort);
        addMapping(Byte.class, ResultSet::getByte, ResultSet::getByte, PreparedStatement::setByte);
        addMapping(byte.class, ResultSet::getByte, ResultSet::getByte, PreparedStatement::setByte);
        addMapping(Boolean.class, ResultSet::getBoolean, ResultSet::getBoolean, PreparedStatement::setBoolean);
        addMapping(boolean.class, ResultSet::getBoolean, ResultSet::getBoolean, PreparedStatement::setBoolean);
        addMapping(URL.class, ResultSet::getURL, ResultSet::getURL, PreparedStatement::setURL);
        addMapping(byte[].class, ResultSet::getBytes, ResultSet::getBytes, PreparedStatement::setBytes);
    }

    <T> void addMapping(Class<T> clazz,
                        ResultSetRetrieverByIndex<T> fromBindingWithIndex,
                        ResultSetRetrieverByName<T> fromBindingWithName,
                        PreparedStatementBinderByIndex<T> preparedStatementBinderByIndex) {
        binders.put(clazz, preparedStatementBinderByIndex);
        retrievers.put(clazz, new Bindings<>(fromBindingWithIndex, fromBindingWithName));
    }

    public <S extends PreparedStatement> S bind(S stmt, Object[] params, int offset) throws SQLException {
        for (int i = 0; i < params.length; ++i) {
            bind(stmt, i + offset + 1, params[i]);
        }
        return stmt;
    }

    @SuppressWarnings("unchecked")
    public <T> void bind(PreparedStatement stmt, int index, T value) throws SQLException {
        PreparedStatementBinderByIndex<T> toSqlBinding;

        if (value.getClass().isEnum()) {
            toSqlBinding = (s, i, v) -> s.setInt(i, ((Enum<?>) v).ordinal());
        }
        else {
            toSqlBinding = (PreparedStatementBinderByIndex<T>) this.binders.get(value.getClass());
            if (toSqlBinding == null) {
                throw new IllegalArgumentException("No binding for " + value.getClass());
            }
        }

        toSqlBinding.bind(stmt, index, value);
    }

    public <T> T retrieve(ResultSet resultSet, int index, Class<T> clazz) throws SQLException {
        ResultSetRetrieverByIndex<T> fromSqlBinding;

        if (clazz.isEnum()) {
            fromSqlBinding = (rs, i) -> clazz.getEnumConstants()[rs.getInt(i)];
        }
        else {
            @SuppressWarnings("unchecked")
            SqlBindings.Bindings<T> bindings = (SqlBindings.Bindings<T>) this.retrievers.get(clazz);
            if (bindings == null) {
                throw new IllegalArgumentException("No binding for " + clazz);
            }

            fromSqlBinding = bindings.retrieverByIndex;
        }

        T retrieved = fromSqlBinding.retrieve(resultSet, index);
        return resultSet.wasNull() ? null : retrieved;
    }

    public <T> T retrieve(ResultSet resultSet, String name, Class<T> clazz) throws SQLException {
        ResultSetRetrieverByName<T> fromSqlBinding;

        if (clazz.isEnum()) {
            fromSqlBinding = (rs, i) -> clazz.getEnumConstants()[rs.getInt(i)];
        }
        else {
            @SuppressWarnings("unchecked")
            SqlBindings.Bindings<T> bindings = (SqlBindings.Bindings<T>) this.retrievers.get(clazz);
            if (bindings == null) {
                throw new IllegalArgumentException("No binding for " + clazz);
            }

            fromSqlBinding = bindings.retrieverByName;
        }

        T retrieved = fromSqlBinding.retrieve(resultSet, name);
        return resultSet.wasNull() ? null : retrieved;
    }

    public <T> boolean hasBinder(Class<T> clazz) {
        return binders.containsKey(clazz);
    }
}