package com.pepej.papi.sql.util;

import com.pepej.papi.sql.UncheckedSqlException;

import java.sql.SQLException;

/**
 * Represents an action.
 */
@FunctionalInterface
public interface SqlAction {

    /**
     * Performs this action.
     *
     * @throws SQLException generally rethrown as {@link UncheckedSqlException}
     */
    void execute() throws SQLException;
}

