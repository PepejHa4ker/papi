package com.pepej.papi.sql.util;

import com.pepej.papi.sql.UncheckedSqlException;

import java.sql.SQLException;

/**
 * Represents a supplier of data.
 *
 * @param <T> the type of the supplied data
 */
@FunctionalInterface
public interface SqlSupplier<T> {

    /**
     * Gets a result.
     *
     * @return the result
     * @throws SQLException generally rethrown as {@link UncheckedSqlException}
     */
    T get() throws SQLException;
}