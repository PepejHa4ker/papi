package com.pepej.papi.cooldown;

import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

class ComposedCooldownMapImpl<I, O> implements ComposedCooldownMap<I, O> {

    private final Cooldown base;
    private final LoadingCache<O, Cooldown> cache;
    private final Function<I, O> composeFunction;

    ComposedCooldownMapImpl(Cooldown base, Function<I, O> composeFunction) {
        this.base = base;
        this.composeFunction = composeFunction;
        this.cache = CacheBuilder.newBuilder()
                                 // remove from the cache 10 seconds after the cooldown expires
                                 .expireAfterAccess(base.getTimeout() + 10000L, TimeUnit.MILLISECONDS)
                                 .build(new CacheLoader<O, Cooldown>() {
                                     @Override
                                     public Cooldown load(@Nonnull O key) {
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
    public Cooldown get(@Nonnull I key) {
        Objects.requireNonNull(key, "key");
        return this.cache.getUnchecked(this.composeFunction.apply(key));
    }

    @Override
    public void put(@Nonnull O key, @Nonnull Cooldown cooldown) {
        Objects.requireNonNull(key, "key");
        Preconditions.checkArgument(cooldown.getTimeout() == this.base.getTimeout(), "different timeout");
        this.cache.put(key, cooldown);
    }

    @Nonnull
    public Map<O, Cooldown> getAll() {
        return this.cache.asMap();
    }
}
