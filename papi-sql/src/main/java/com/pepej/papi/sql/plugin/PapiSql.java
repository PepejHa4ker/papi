package com.pepej.papi.sql.plugin;

import com.google.common.collect.ImmutableMap;
import com.pepej.papi.sql.DatabaseCredentials;
import com.pepej.papi.sql.Sql;
import com.pepej.papi.sql.SqlStream;
import com.pepej.papi.sql.batch.BatchBuilder;
import com.pepej.papi.sql.util.SqlConsumer;
import com.pepej.papi.sql.util.SqlFunction;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.intellij.lang.annotations.Language;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class PapiSql implements Sql {

    private static final AtomicInteger POOL_COUNTER = new AtomicInteger(0);

    // https://github.com/brettwooldridge/HikariCP/wiki/About-Pool-Sizing
    private static final int MAXIMUM_POOL_SIZE = (Runtime.getRuntime().availableProcessors() * 2) + 1;
    private static final int MINIMUM_IDLE = Math.min(MAXIMUM_POOL_SIZE, 10);

    private static final long MAX_LIFETIME = TimeUnit.MINUTES.toMillis(30);
    private static final long CONNECTION_TIMEOUT = TimeUnit.SECONDS.toMillis(10);
    private final HikariDataSource source;
    private final SqlStream stream;

    public PapiSql(@NonNull DatabaseCredentials credentials) {
        final HikariConfig hikari = new HikariConfig();

        hikari.setPoolName("papi-sql-" + POOL_COUNTER.getAndIncrement());

        hikari.setDriverClassName("com.mysql.cj.jdbc.Driver");
        hikari.setJdbcUrl("jdbc:mysql://" + credentials.getAddress() + ":" + credentials.getPort() + "/" + credentials.getDatabase());

        hikari.setUsername(credentials.getUsername());
        hikari.setPassword(credentials.getPassword());

        hikari.setMaximumPoolSize(MAXIMUM_POOL_SIZE);
        hikari.setMinimumIdle(MINIMUM_IDLE);

        hikari.setMaxLifetime(MAX_LIFETIME);
        hikari.setConnectionTimeout(CONNECTION_TIMEOUT);

        Map<String, String> properties = ImmutableMap.<String, String>builder()
                // Ensure we use utf8 encoding
                .put("useUnicode", "true")
                .put("characterEncoding", "utf8")

                // https://github.com/brettwooldridge/HikariCP/wiki/MySQL-Configuration
                .put("cachePrepStmts", "true")
                .put("prepStmtCacheSize", "250")
                .put("prepStmtCacheSqlLimit", "2048")
                .put("useServerPrepStmts", "true")
                .put("useLocalSessionState", "true")
                .put("rewriteBatchedStatements", "true")
                .put("cacheResultSetMetadata", "true")
                .put("cacheServerConfiguration", "true")
                .put("elideSetAutoCommits", "true")
                .put("maintainTimeStats", "false")
                .put("alwaysSendSetIsolation", "false")
                .put("cacheCallableStmts", "true")

                // Set the driver level TCP socket timeout
                // See: https://github.com/brettwooldridge/HikariCP/wiki/Rapid-Recovery
                .put("socketTimeout", String.valueOf(TimeUnit.SECONDS.toMillis(30)))
                .build();

        for (Map.Entry<String, String> property : properties.entrySet()) {
            hikari.addDataSourceProperty(property.getKey(), property.getValue());
        }

        this.source = new HikariDataSource(hikari);
        this.stream = SqlStream.connect(this.source);
    }

    @NonNull
    @Override
    public HikariDataSource getHikari() {
        return this.source;
    }

    @NonNull
    @Override
    public Connection getConnection() throws SQLException {
        return Objects.requireNonNull(this.source.getConnection(), "connection is null");
    }

    @NonNull
    @Override
    public SqlStream stream() {
        return this.stream;
    }

    @Override
    public void execute(@Language("MySQL") @NonNull String statement, @NonNull SqlConsumer<PreparedStatement> preparer) {
        try (Connection c = this.getConnection(); PreparedStatement s = c.prepareStatement(statement)) {
            preparer.accept(s);
            s.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public <R> Optional<R> query(@Language("MySQL") @NonNull String query, @NonNull SqlConsumer<PreparedStatement> preparer, @NonNull SqlFunction<ResultSet, R> handler) {
        try (Connection c = this.getConnection(); PreparedStatement s = c.prepareStatement(query)) {
            preparer.accept(s);
            try (ResultSet r = s.executeQuery()) {
                return Optional.ofNullable(handler.apply(r));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public void executeBatch(@NonNull BatchBuilder builder) {
        if (builder.getHandlers().isEmpty()) {
            return;
        }

        if (builder.getHandlers().size() == 1) {
            this.execute(builder.getStatement(), builder.getHandlers().iterator().next());
            return;
        }

        try (Connection c = this.getConnection(); PreparedStatement s = c.prepareStatement(builder.getStatement())) {
            for (SqlConsumer<PreparedStatement> handlers : builder.getHandlers()) {
                handlers.accept(s);
                s.addBatch();
            }
            s.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public BatchBuilder batch(@Language("MySQL") @NonNull String statement) {
        return new PapiSqlBatchBuilder(this, statement);
    }

    @Override
    public void close() {
        this.source.close();
    }
}
