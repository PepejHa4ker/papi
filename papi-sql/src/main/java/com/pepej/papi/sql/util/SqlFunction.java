package com.pepej.papi.sql.util;

import com.pepej.papi.sql.UncheckedSqlException;

import java.sql.SQLException;

@FunctionalInterface
public interface SqlFunction<T, R> {

    /**
     * Applies this function to the given argument.
     *
     * @param t the function argument
     * @return the function result
     * @throws SQLException generally rethrown as {@link UncheckedSqlException}
     */
    R apply(T t) throws SQLException;
}
