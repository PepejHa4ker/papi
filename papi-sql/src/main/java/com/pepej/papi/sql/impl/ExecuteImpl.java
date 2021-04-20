package com.pepej.papi.sql.impl;


import com.pepej.papi.sql.Execute;
import com.pepej.papi.sql.util.Wrap;

import java.sql.Connection;
import java.sql.PreparedStatement;

class ExecuteImpl<S extends PreparedStatement>
        extends ParameterProviderImpl<Execute<S>, S> implements Execute<S> {

    private final Connection connection;
    private final boolean closeConnection;

    ExecuteImpl(SqlStreamImpl sql, Connection connection, S statement, boolean closeConnection) {
        super(statement, sql.bindings);
        this.connection = connection;
        this.closeConnection = closeConnection;
    }

    @Override
    public boolean execute() {
        return super.execute();
    }

    @Override
    public void close() {
        super.close();
        if (closeConnection) {
            Wrap.execute(connection::close);
        }
    }
}
