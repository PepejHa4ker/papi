package com.pepej.papi.cooldown;

import com.google.gson.JsonElement;
import com.pepej.papi.gson.JsonBuilder;

import javax.annotation.Nonnull;
import java.util.OptionalLong;
import java.util.concurrent.TimeUnit;

class CooldownImpl implements Cooldown {

    // when the last test occurred.
    private long lastTested;

    // the cooldown duration in millis
    private final long timeout;

    CooldownImpl(long amount, TimeUnit unit) {
        this.timeout = unit.toMillis(amount);
        this.lastTested = 0;
    }

    @Nonnull
    @Override
    public OptionalLong getLastTested() {
        return this.lastTested == 0 ? OptionalLong.empty() : OptionalLong.of(this.lastTested);
    }

    @Override
    public void setLastTested(long time) {
        if (time <= 0) {
            this.lastTested = 0;
        } else {
            this.lastTested = time;
        }
    }

    @Override
    public long getTimeout() {
        return this.timeout;
    }

    @Nonnull
    @Override
    public CooldownImpl copy() {
        return new CooldownImpl(this.timeout, TimeUnit.MILLISECONDS);
    }

    @Nonnull
    @Override
    public JsonElement serialize() {
        return JsonBuilder.object()
                          .add("lastTested", lastTested)
                          .add("timeout", timeout)
                          .build();
    }
}
