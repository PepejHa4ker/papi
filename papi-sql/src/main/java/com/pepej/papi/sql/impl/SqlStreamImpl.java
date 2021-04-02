package com.pepej.papi.sql.impl;

import com.pepej.papi.sql.*;
import com.pepej.papi.sql.util.SqlBiFunction;
import com.pepej.papi.sql.util.SqlFunction;
import com.pepej.papi.sql.util.Wrap;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Objects;


public class SqlStreamImpl implements SqlStream {

    private final DataSource dataSource;
    final SqlBindings bindings;

    SqlStreamImpl(SqlBindings bindings) {
        this.dataSource = null;
        this.bindings = bindings;
    }

    public SqlStreamImpl(DataSource dataSource) {
        this.dataSource = Objects.requireNonNull(dataSource);
        this.bindings = new SqlBindings();
    }

    protected Connection getConnection() {
        return Wrap.get(dataSource::getConnection);
    }

    protected boolean closeConnectionAfterAction() {
        return true;
    }

    @Override
    public <T> SqlStreamImpl registerCustomBinding(Class<T> clazz,
                                                   PreparedStatementBinderByIndex<T> preparedStatementBinderByIndex) {
        bindings.addMapping(clazz, null, null, preparedStatementBinderByIndex);
        return this;
    }

    @Override
    public Transaction transaction() {
        return new TransactionImpl(this);
    }

    @Override
    public Transaction transaction(Transaction.IsolationLevel isolationLevel) {
        return new TransactionImpl(this, isolationLevel.isolationLevel);
    }

    @Override
    public Query query(SqlFunction<Connection, PreparedStatement> preparer) {
        return prepare(QueryImpl::new, preparer);
    }

    @Override
    public Update update(SqlFunction<Connection, PreparedStatement> preparer) {
        return prepare(UpdateImpl::new, preparer);
    }

    @Override
    public BatchUpdate batchUpdate(String sql) {
        return prepare(sql, BatchUpdateImpl::new, Connection::prepareStatement);
    }

    @Override
    public Execute<PreparedStatement> execute(String sql) {
        return prepare(sql, ExecuteImpl::new, Connection::prepareStatement);
    }

    @Override
    public Execute<CallableStatement> call(String sql) {
        return prepare(sql, ExecuteImpl::new, Connection::prepareCall);
    }

    @Override
    public void close() {
        if (dataSource instanceof AutoCloseable) {
            try {
                ((AutoCloseable) dataSource).close();
            } catch (SQLException e) {
                throw new UncheckedSqlException(e);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @FunctionalInterface
    private interface Creator<T, S extends Statement> {
        T create(SqlStreamImpl impl, Connection connection, S statement, boolean closeConnectionAfterAction);
    }

    private <T, S extends Statement> T prepare(SqlStreamImpl.Creator<T, S> creator,
                                               SqlFunction<Connection, S> statementCreator) {
        Connection connection = getConnection();
        return creator.create(
                this,
                connection,
                Wrap.get(() -> statementCreator.apply(connection)),
                closeConnectionAfterAction());
    }

    private <T, S extends Statement> T prepare(String sql,
                                               SqlStreamImpl.Creator<T, S> creator,
                                               SqlBiFunction<Connection, String, S> statementCreator) {
        Connection connection = getConnection();
        return creator.create(
                this,
                connection,
                Wrap.get(() -> statementCreator.apply(connection, sql)),
                closeConnectionAfterAction());
    }
}
