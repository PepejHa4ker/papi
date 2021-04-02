package com.pepej.papi.sql;


import com.pepej.papi.sql.util.Wrap;
import com.pepej.papi.terminable.Terminable;

import java.sql.PreparedStatement;

/**
 * Represents an object holding an instance of {@link PreparedStatement}.
 *
 * @param <S> the type of the statement
 */
public interface StatementHolder<S extends PreparedStatement> extends Terminable {

    /**
     * Returns the underlying statement handled by this object.
     *
     * @return the statement
     */
    S getStatement();

    /**
     * Executes the statement held by this object.
     *
     * @return {@code true} if the first result is a {@link java.sql.ResultSet}
     *         object; {@code false} if the first result is an update count or
     *         there is no result
     * @see PreparedStatement#execute()
     * @see StatementHolder#getStatement()
     */
    default boolean execute() {
        return Wrap.get(getStatement()::execute);
    }

    /**
     * Closes the statement held by this object.
     */
    default void close() {
        Wrap.execute(getStatement()::close);
    }
}
