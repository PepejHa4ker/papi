package com.pepej.papi.sql.impl;


import com.pepej.papi.sql.Query;
import com.pepej.papi.sql.util.SqlFunction;
import com.pepej.papi.sql.util.Wrap;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.stream.Stream;

class QueryImpl extends ParameterProviderImpl<Query, PreparedStatement> implements Query {

    private final Connection connection;
    private final boolean closeConnection;

    QueryImpl(SqlStreamImpl sql, Connection connection, PreparedStatement statement, boolean closeConnection) {
        super(statement, sql.bindings);
        this.connection = connection;
        this.closeConnection = closeConnection;
    }

    @Override
    public <R> Stream<R> map(SqlFunction<ResultSet, R> mapping) {
        return ResultSetSpliterator.stream(mapping, Wrap.get(statement::executeQuery))
                                   .onClose(this::close);
    }

    @Override
    public void close() {
        super.close();
        if (closeConnection) {
            Wrap.execute(connection::close);
        }
    }
}

