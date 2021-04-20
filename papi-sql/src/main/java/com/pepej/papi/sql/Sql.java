package com.pepej.papi.sql;

import com.pepej.papi.promise.Promise;
import com.pepej.papi.scheduler.Schedulers;
import com.pepej.papi.sql.batch.BatchBuilder;
import com.pepej.papi.sql.util.SqlConsumer;
import com.pepej.papi.sql.util.SqlFunction;
import com.pepej.papi.terminable.Terminable;
import com.zaxxer.hikari.HikariDataSource;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.intellij.lang.annotations.Language;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.sql.PreparedStatement;

/**
 * Represents an individual SQL datasource, created by the library.
 */
public interface Sql extends Terminable {

    /**
     * Gets the Hikari instance backing the datasource
     *
     * @return the hikari instance
     */
    @NonNull
    HikariDataSource getHikari();

    /**
     * Gets a connection from the datasource.
     *
     * <p>The connection should be returned once it has been used.</p>
     *
     * @return a connection
     */
    @NonNull
    Connection getConnection() throws SQLException;

    /**
     * Gets a {@link SqlStream} instance for this {@link Sql}.
     *
     * @return a instance of the stream library for this connection.
     */
    @NonNull
    SqlStream stream();

    /**
     * Executes a database statement with no preparation.
     *
     * <p>This will be executed on an asynchronous thread.</p>
     *
     * @param statement the statement to be executed
     * @return a Promise of an asynchronous database execution
     * @see #execute(String) to perform this action synchronously
     */
    @NonNull
    default Promise<Void> executeAsync(@Language("MySQL") @NonNull String statement) {
        return Schedulers.async().run(() -> this.execute(statement));
    }

    /**
     * Executes a database statement with no preparation.
     *
     * <p>This will be executed on whichever thread it's called from.</p>
     *
     * @param statement the statement to be executed
     * @see #executeAsync(String) to perform the same action asynchronously
     */
    default void execute(@Language("MySQL") @NonNull String statement) {
        this.execute(statement, stmt -> {});
    }

    /**
     * Executes a database statement with preparation.
     *
     * <p>This will be executed on an asynchronous thread.</p>
     *
     * @param statement the statement to be executed
     * @param preparer the preparation used for this statement
     * @return a Promise of an asynchronous database execution
     * @see #executeAsync(String, SqlConsumer) to perform this action synchronously
     */
    @NonNull
    default Promise<Void> executeAsync(@Language("MySQL") @NonNull String statement, @NonNull SqlConsumer<PreparedStatement> preparer) {
        return Schedulers.async().run(() -> this.execute(statement, preparer));
    }

    /**
     * Executes a database statement with preparation.
     *
     * <p>This will be executed on whichever thread it's called from.</p>
     *
     * @param statement the statement to be executed
     * @param preparer the preparation used for this statement
     * @see #executeAsync(String, SqlConsumer) to perform this action asynchronously
     */
    void execute(@Language("MySQL") @NonNull String statement, @NonNull SqlConsumer<PreparedStatement> preparer);

    /**
     * Executes a database query with no preparation.
     *
     * <p>This will be executed on an asynchronous thread.</p>
     *
     * <p>In the case of a {@link SQLException} or in the case of
     * no data being returned, or the handler evaluating to null,
     * this method will return an {@link Optional#empty()} object.</p>
     *
     * @param query the query to be executed
     * @param handler the handler for the data returned by the query
     * @param <R> the returned type
     * @return a Promise of an asynchronous database query
     * @see #query(String, SqlFunction) to perform this query synchronously
     */
    default <R> Promise<Optional<R>> queryAsync(@Language("MySQL") @NonNull String query, @NonNull SqlFunction<ResultSet, R> handler) {
        return Schedulers.async().supply(() -> query(query, handler));
    }

    /**
     * Executes a database query with no preparation.
     *
     * <p>This will be executed on whichever thread it's called from.</p>
     *
     * <p>In the case of a {@link SQLException} or in the case of
     * no data being returned, or the handler evaluating to null,
     * this method will return an {@link Optional#empty()} object.</p>
     *
     * @param query the query to be executed
     * @param handler the handler for the data returned by the query
     * @param <R> the returned type
     * @return the results of the database query
     * @see #queryAsync(String, SqlFunction) to perform this query asynchronously
     */
    default <R> Optional<R> query(@Language("MySQL") @NonNull String query, @NonNull SqlFunction<ResultSet, R> handler) {
        return query(query, stmt -> {}, handler);
    }

    /**
     * Executes a database query with preparation.
     *
     * <p>This will be executed on an asynchronous thread.</p>
     *
     * <p>In the case of a {@link SQLException} or in the case of
     * no data being returned, or the handler evaluating to null,
     * this method will return an {@link Optional#empty()} object.</p>
     *
     * @param query the query to be executed
     * @param preparer the preparation used for this statement
     * @param handler the handler for the data returned by the query
     * @param <R> the returned type
     * @return a Promise of an asynchronous database query
     * @see #query(String, SqlFunction) to perform this query synchronously
     */
    default <R> Promise<Optional<R>> queryAsync(@Language("MySQL") @NonNull String query, @NonNull SqlConsumer<PreparedStatement> preparer, @NonNull SqlFunction<ResultSet, R> handler) {
        return Schedulers.async().supply(() -> query(query, preparer, handler));
    }
    /**
     * Executes a database query with preparation.
     *
     * <p>This will be executed on whichever thread it's called from.</p>
     *
     * <p>In the case of a {@link SQLException} or in the case of
     * no data being returned, or the handler evaluating to null,
     * this method will return an {@link Optional#empty()} object.</p>
     *
     * @param query the query to be executed
     * @param preparer the preparation used for this statement
     * @param handler the handler for the data returned by the query
     * @param <R> the returned type
     * @return the results of the database query
     * @see #queryAsync(String, SqlFunction) to perform this query asynchronously
     */
    <R> Optional<R> query(@Language("MySQL") @NonNull String query, @NonNull SqlConsumer<PreparedStatement> preparer, @NonNull SqlFunction<ResultSet, R> handler);

    /**
     * Executes a batched database execution.
     *
     * <p>This will be executed on an asynchronous thread.</p>
     *
     * <p>Note that proper implementations of this method should determine
     * if the provided {@link BatchBuilder} is actually worth of being a
     * batched statement. For instance, a BatchBuilder with only one
     * handler can safely be referred to {@link #executeAsync(String, SqlConsumer)}</p>
     *
     * @param builder the builder to be used.
     * @return a Promise of an asynchronous batched database execution
     * @see #executeBatch(BatchBuilder) to perform this action synchronously
     */
    default Promise<Void> executeBatchAsync(@NonNull BatchBuilder builder) {
        return Schedulers.async().run(() -> this.executeBatch(builder));
    }

    /**
     * Executes a batched database execution.
     *
     * <p>This will be executed on whichever thread it's called from.</p>
     *
     * <p>Note that proper implementations of this method should determine
     * if the provided {@link BatchBuilder} is actually worth of being a
     * batched statement. For instance, a BatchBuilder with only one
     * handler can safely be referred to {@link #execute(String, SqlConsumer)}</p>
     *
     * @param builder the builder to be used.
     * @see #executeBatchAsync(BatchBuilder) to perform this action asynchronously
     */
    void executeBatch(@NonNull BatchBuilder builder);

    /**
     * Gets a {@link BatchBuilder} for the provided statement.
     *
     * @param statement the statement to prepare for batching.
     * @return a BatchBuilder
     */
    BatchBuilder batch(@Language("MySQL") @NonNull String statement);
}
