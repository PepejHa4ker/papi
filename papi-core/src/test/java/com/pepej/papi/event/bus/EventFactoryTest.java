package com.pepej.papi.event.bus;


import com.pepej.papi.event.bus.api.EventBus;
import com.pepej.papi.event.bus.api.SimpleEventBus;
import com.pepej.papi.event.bus.method.*;
import com.pepej.papi.event.bus.method.annotation.DefaultMethodScanner;
import com.pepej.papi.event.bus.method.annotation.Subscribe;
import org.junit.jupiter.api.Test;

public class EventFactoryTest {

    private final EventExecutor.Factory<Object, EventFactoryTest> eventExecutor = new MethodHandleEventExecutorFactory<>();
    private final EventBus<Object> eventBus = new SimpleEventBus<>(Object.class);
    private final MethodSubscriptionAdapter<EventFactoryTest> methodSubscriptionAdapter = new SimpleMethodSubscriptionAdapter<>(eventBus, eventExecutor);

    @Subscribe
    public void onEvent(Object event) {
        System.out.println("Received an event");
    }

    @Test
    public void testEvent() {
        try {
            eventBus.post(new Object());
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
