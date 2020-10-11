package com.pepej.papi.cooldown;

import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

class CooldownMapImpl<T> implements CooldownMap<T> {

    private final Cooldown base;
    private final LoadingCache<T, Cooldown> cache;

    CooldownMapImpl(Cooldown base) {
        this.base = base;
        this.cache = CacheBuilder.newBuilder()
                                 // remove from the cache 10 seconds after the cooldown expires
                                 .expireAfterAccess(base.getTimeout() + 10000L, TimeUnit.MILLISECONDS)
                                 .build(new CacheLoader<T, Cooldown>() {
                                     @Override
                                     public Cooldown load(@Nonnull T key) {
                                         return base.copy();
                                     }
                                 });
    }

    @Nonnull
    @Override
    public Cooldown getBase() {
        return this.base;
    }

    @Nonnull
    public Cooldown get(@Nonnull T key) {
        Objects.requireNonNull(key, "key");
        return this.cache.getUnchecked(key);
    }

    @Override
    public void put(@Nonnull T key, @Nonnull Cooldown cooldown) {
        Objects.requireNonNull(key, "key");
        Preconditions.checkArgument(cooldown.getTimeout() == this.base.getTimeout(), "different timeout");
        this.cache.put(key, cooldown);
    }

    @Nonnull
    public Map<T, Cooldown> getAll() {
        return this.cache.asMap();
    }
}
