package com.pepej.papi.sql.util;

import com.pepej.papi.sql.UncheckedSqlException;

import java.sql.SQLException;

/**
 * Represents an operation that accepts a single input argument and returns no result.
 *
 * @param <T> the type of the input to the operation
 *
 */
@FunctionalInterface
public interface SqlConsumer<T> {

    /**
     * Performs this operation on the given argument.
     *
     * @param t the input argument
     * @throws SQLException generally rethrown as {@link UncheckedSqlException}
     */
    void accept(T t) throws SQLException;
}
