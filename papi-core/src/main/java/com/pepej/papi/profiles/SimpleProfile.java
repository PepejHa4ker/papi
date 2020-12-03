package com.pepej.papi.profiles;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

final class SimpleProfile implements Profile {

    @NonNull
    private final UUID uniqueId;

    @Nullable
    private final String name;

    private final long timestamp;

    SimpleProfile(@NonNull UUID uniqueId, @Nullable String name) {
        this.uniqueId = Objects.requireNonNull(uniqueId, "uniqueId");
        this.name = name;
        this.timestamp = System.currentTimeMillis();
    }

    @NonNull
    @Override
    public UUID getUniqueId() {
        return this.uniqueId;
    }

    @NonNull
    @Override
    public Optional<String> getName() {
        return Optional.ofNullable(this.name);
    }

    @Override
    public long getTimestamp() {
        return this.timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Profile)) return false;
        final Profile other = (Profile) o;
        return this.getUniqueId().equals(other.getUniqueId()) && this.getName().equals(other.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.uniqueId, this.name);
    }

    public String toString() {
        return "Profile(uniqueId=" + this.uniqueId + ", name=" + this.name + ")";
    }
}
