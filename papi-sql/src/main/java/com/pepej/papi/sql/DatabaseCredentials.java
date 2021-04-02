package com.pepej.papi.sql;

import org.bukkit.configuration.ConfigurationSection;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Objects;

/**
 * Represents the credentials for a remote database.
 */
public final class DatabaseCredentials {

    @NonNull
    public static DatabaseCredentials of(@NonNull String address, int port, @NonNull String database, @NonNull String username, @NonNull String password) {
        return new DatabaseCredentials(address, port, database, username, password);
    }

    @NonNull
    public static DatabaseCredentials fromConfig(@NonNull ConfigurationSection config) {
        return of(
                config.getString("address", "localhost"),
                config.getInt("port", 3306),
                config.getString("database", "minecraft"),
                config.getString("username", "root"),
                config.getString("password", "")
        );
    }

    private final String address;
    private final int port;
    private final String database;
    private final String username;
    private final String password;

    private DatabaseCredentials(@NonNull String address, int port, @NonNull  String database, @NonNull String username, @NonNull String password) {
        this.address = Objects.requireNonNull(address);
        this.port = port;
        this.database = Objects.requireNonNull(database);
        this.username = Objects.requireNonNull(username);
        this.password = Objects.requireNonNull(password);
    }

    @NonNull
    public String getAddress() {
        return this.address;
    }

    public int getPort() {
        return this.port;
    }

    @NonNull
    public String getDatabase() {
        return this.database;
    }

    @NonNull
    public String getUsername() {
        return this.username;
    }

    @NonNull
    public String getPassword() {
        return this.password;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof DatabaseCredentials)) return false;
        final DatabaseCredentials other = (DatabaseCredentials) o;

        return this.getAddress().equals(other.getAddress()) &&
                this.getPort() == other.getPort() &&
                this.getDatabase().equals(other.getDatabase()) &&
                this.getUsername().equals(other.getUsername()) &&
                this.getPassword().equals(other.getPassword());
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        result = result * PRIME + this.getPort();
        result = result * PRIME + this.getAddress().hashCode();
        result = result * PRIME + this.getDatabase().hashCode();
        result = result * PRIME + this.getUsername().hashCode();
        result = result * PRIME + this.getPassword().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "DatabaseCredentials(" +
                "address=" + this.getAddress() + ", " +
                "port=" + this.getPort() + ", " +
                "database=" + this.getDatabase() + ", " +
                "username=" + this.getUsername() + ", " +
                "password=" + this.getPassword() + ")";
    }
}
