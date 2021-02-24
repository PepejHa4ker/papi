package com.pepej.papi.time.timer;

import com.pepej.papi.promise.ThreadContext;
import com.pepej.papi.scheduler.Ticks;
import com.pepej.papi.terminable.composite.CompositeTerminable;

import java.util.concurrent.TimeUnit;

final class TimerImpl implements Timer {

    private final Runnable task;
    private final ThreadContext threadContext;
    private final long delayTicks, intervalTicks, maxTimesRun;
    private final CompositeTerminable listener = CompositeTerminable.create();


    TimerImpl(final Runnable runnable, ThreadContext context, final long delay, final long period, final long maxTimesRun) {
       this.delayTicks = delay;
       this.intervalTicks = period;
        this.maxTimesRun = maxTimesRun;

        this.task = runnable;
       this.threadContext = context;
    }

    TimerImpl(final Runnable runnable, ThreadContext context, final long delay, final TimeUnit delayUnit, final long duration, final TimeUnit durationUnit, final long maxTimesRun) {
        this.delayTicks = Ticks.from(delay, delayUnit);
        this.intervalTicks =  Ticks.from(duration, durationUnit);
        this.maxTimesRun = maxTimesRun;
        this.task = runnable;
        this.threadContext = context;

    }


    @Override
    public void close() throws Exception {
        this.listener.close();
    }

    @Override
    public void onTick() throws Exception {
//        Task t = Schedulers.builder().on(threadContext)
//                             .after(delayTicks)
//                             .every(intervalTicks)
//                             .run(() -> {
//                                 task.run();
//                                 if (t.getTimesRan() > maxTimesRun) {
//                                     try {
//                                         close();
//                                     } catch (Exception exception) {
//                                         exception.printStackTrace();
//                                     }
//                                 }
//                             });
//
//        t.bindWith(listener);
//        out.println(t.getTimesRan());
//        out.println(listener);
//        if (t.getTimesRan() > maxTimesRun) {
//            close();
//        }


    }

}
