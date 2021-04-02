package com.pepej.papi.sql;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Provides {@link Sql} instances.
 */
public interface SqlProvider {

    /**
     * Gets the global datasource.
     *
     * @return the global datasource.
     */
    @NonNull
    Sql getSql();

    /**
     * Constructs a new datasource using the given credentials.
     *
     * <p>These instances are not cached, and a new datasource is created each
     * time this method is called.</p>
     *
     * @param credentials the credentials for the database
     * @return a new datasource
     */
    @NonNull
    Sql getSql(@NonNull DatabaseCredentials credentials);

    /**
     * Gets the global database credentials being used for the global datasource.
     *
     * @return the global credentials
     */
    @NonNull
    DatabaseCredentials getGlobalCredentials();

}
