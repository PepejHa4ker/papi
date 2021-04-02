package com.pepej.papi.sql.plugin;

import com.pepej.papi.promise.Promise;
import com.pepej.papi.sql.Sql;
import com.pepej.papi.sql.batch.BatchBuilder;
import com.pepej.papi.sql.util.SqlConsumer;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.sql.PreparedStatement;
import java.util.LinkedList;
import java.util.List;

public class PapiSqlBatchBuilder implements BatchBuilder {
    @NonNull
    private final Sql owner;
    @NonNull
    private final String statement;
    @NonNull
    private final List<SqlConsumer<PreparedStatement>> handlers;

    public PapiSqlBatchBuilder(@NonNull Sql owner, @NonNull String statement) {
        this.owner = owner;
        this.statement = statement;
        this.handlers = new LinkedList<>();
    }

    @NonNull
    @Override
    public String getStatement() {
        return this.statement;
    }

    @NonNull
    @Override
    public List<SqlConsumer<PreparedStatement>> getHandlers() {
        return this.handlers;
    }

    @Override
    public BatchBuilder reset() {
        this.handlers.clear();
        return this;
    }

    @Override
    public BatchBuilder batch(@NonNull SqlConsumer<PreparedStatement> handler) {
        this.handlers.add(handler);
        return this;
    }

    @Override
    public void execute() {
        this.owner.executeBatch(this);
    }

    @NonNull
    @Override
    public Promise<Void> executeAsync() {
        return this.owner.executeBatchAsync(this);
    }
}
