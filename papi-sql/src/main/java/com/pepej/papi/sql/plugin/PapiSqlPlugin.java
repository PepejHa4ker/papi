package com.pepej.papi.sql.plugin;

import com.pepej.papi.ap.Plugin;
import com.pepej.papi.ap.PluginDependency;
import com.pepej.papi.dependency.Dependency;
import com.pepej.papi.internal.PapiImplementationPlugin;
import com.pepej.papi.plugin.PapiJavaPlugin;
import com.pepej.papi.sql.DatabaseCredentials;
import com.pepej.papi.sql.Sql;
import com.pepej.papi.sql.SqlProvider;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Optional;


@PapiImplementationPlugin(moduleName = PapiSqlPlugin.MODULE_ID)
@Plugin(name = "papi-sql", version = "1.0", description = "papi sql module",  depends = @PluginDependency("papi"))
@Dependency("org.slf4j:slf4j-api:1.7.30")
@Dependency("com.zaxxer:HikariCP:4.0.3")
@Dependency("mysql:mysql-connector-java:8.0.23")
public final class PapiSqlPlugin extends PapiJavaPlugin implements SqlProvider {
    public static final String MODULE_ID = "Papi Sql";

    private DatabaseCredentials globalCredentials;
    private Sql globalDataSource;

    @Override
    public void onPluginEnable() {
        this.globalCredentials = DatabaseCredentials.fromConfig(loadConfig("config.yml"));
        this.globalDataSource = getSql(globalCredentials);
        this.globalDataSource.bindWith(this);

        // expose all instances as services.
        provideService(SqlProvider.class, this);
        provideService(DatabaseCredentials.class, globalCredentials);
        provideService(Sql.class, globalDataSource);
    }

    private static class Player {

        public void setUsername(final String username) {
            this.username = username;
        }

        private String username;

        private Player(final String username) {
            this.username = username;
        }

        public String getUsername() {
            return username;
        }
    }


    @NonNull
    @Override
    public Sql getSql() {
        return globalDataSource;
    }

    @NonNull
    @Override
    public Sql getSql(@NonNull DatabaseCredentials credentials) {
        return new PapiSql(credentials);
    }

    @NonNull
    @Override
    public DatabaseCredentials getGlobalCredentials() {
        return globalCredentials;
    }

}
