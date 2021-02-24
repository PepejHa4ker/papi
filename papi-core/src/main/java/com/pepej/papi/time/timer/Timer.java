package com.pepej.papi.time.timer;

import com.pepej.papi.promise.ThreadContext;
import com.pepej.papi.terminable.Terminable;

import java.util.concurrent.TimeUnit;

public interface Timer extends Terminable, Runnable {

    static Timer create(Runnable runnable, ThreadContext context, long delay, long period, long maxTimesRun) {
        return new TimerImpl(runnable, context, delay, period, maxTimesRun);
    }

    static Timer create(Runnable runnable, ThreadContext context, long delay, TimeUnit delayUnit, long duration, TimeUnit durationUnit, long maxTimesRun) {
        return new TimerImpl(runnable, context, delay, delayUnit, duration, durationUnit, maxTimesRun);
    }

    void onTick() throws Exception;

    @Override
    default void run() {
        try {
            onTick();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
