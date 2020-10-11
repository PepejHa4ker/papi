package com.pepej.papi.internal;

import java.util.concurrent.TimeUnit;

class Duration {
    private final long duration;
    private final TimeUnit unit;

    Duration(long duration, TimeUnit unit) {
        this.duration = duration;
        this.unit = unit;
    }

    public long getDuration() {
        return this.duration;
    }

    public TimeUnit getUnit() {
        return this.unit;
    }
}