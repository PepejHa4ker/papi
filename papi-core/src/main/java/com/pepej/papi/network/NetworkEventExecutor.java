package com.pepej.papi.network;

import com.pepej.papi.event.bus.api.EventBus;
import com.pepej.papi.event.bus.method.EventExecutor;
import com.pepej.papi.event.bus.method.MethodHandleEventExecutorFactory;
import com.pepej.papi.event.bus.method.MethodSubscriptionAdapter;
import com.pepej.papi.event.bus.method.SimpleMethodSubscriptionAdapter;
import com.pepej.papi.network.event.NetworkEvent;
import org.jetbrains.annotations.NotNull;

public class NetworkEventExecutor<L> {

    private final EventBus<NetworkEvent> eventBus;
    private final EventExecutor.Factory<NetworkEvent, L> eventFactory = new MethodHandleEventExecutorFactory<>();
    private final MethodSubscriptionAdapter<L> methodSubscriptionAdapter;

    public NetworkEventExecutor(@NotNull EventBus<NetworkEvent> eventBus) {
        this.eventBus = eventBus;
        this.methodSubscriptionAdapter = new SimpleMethodSubscriptionAdapter<>(eventBus, eventFactory);
    }

    private final EventExecutor.Factory<NetworkEvent, L> eventExecutor = new MethodHandleEventExecutorFactory<>();

    public EventBus<NetworkEvent> getEventBus() {
        return eventBus;
    }

    public MethodSubscriptionAdapter<L> getMethodSubscriptionAdapter() {
        return methodSubscriptionAdapter;
    }
}
