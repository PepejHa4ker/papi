package com.pepej.papi.profiles;

import com.mojang.authlib.GameProfile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

abstract class AbstractProfile implements Profile {

    @NotNull
    private final UUID uniqueId;

    @Nullable
    private final String name;

    private final long timestamp;


    AbstractProfile(@NotNull UUID uniqueId, @Nullable String name) {
        this.uniqueId = Objects.requireNonNull(uniqueId, "uniqueId");
        this.name = name;
        this.timestamp = System.currentTimeMillis();
    }

    @NotNull
    @Override
    public UUID getUniqueId() {
        return this.uniqueId;
    }

    @NotNull
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
        if (!(o instanceof AbstractProfile)) return false;
        final AbstractProfile other = (AbstractProfile) o;
        return this.getUniqueId().equals(other.getUniqueId()) && this.getName().equals(other.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.uniqueId, this.name);
    }

    public String toString() {
        return "AbstractProfile(uniqueId=" + this.uniqueId + ", name=" + this.name + ")";
    }
}
