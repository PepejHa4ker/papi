package com.pepej.papi.event.functional.protocol;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketEvent;
import com.google.common.collect.ImmutableSet;
import com.pepej.papi.event.ProtocolSubscription;
import com.pepej.papi.event.functional.ExpiryTestStage;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

class ProtocolSubscriptionBuilderImpl implements ProtocolSubscriptionBuilder {
    final Set<PacketType> types;
    final ListenerPriority priority;

    BiConsumer<? super PacketEvent, Throwable> exceptionConsumer = DEFAULT_EXCEPTION_CONSUMER;

    final List<Predicate<PacketEvent>> filters = new ArrayList<>(3);
    final List<BiPredicate<ProtocolSubscription, PacketEvent>> preExpiryTests = new ArrayList<>(0);
    final List<BiPredicate<ProtocolSubscription, PacketEvent>> midExpiryTests = new ArrayList<>(0);
    final List<BiPredicate<ProtocolSubscription, PacketEvent>> postExpiryTests = new ArrayList<>(0);

    ProtocolSubscriptionBuilderImpl(Set<PacketType> types, ListenerPriority priority) {
        this.types = ImmutableSet.copyOf(types);
        this.priority = priority;
    }

    @Nonnull
    @Override
    public ProtocolSubscriptionBuilder expireIf(@Nonnull BiPredicate<ProtocolSubscription, PacketEvent> predicate, @Nonnull ExpiryTestStage... testPoints) {
        Objects.requireNonNull(testPoints, "testPoints");
        Objects.requireNonNull(predicate, "predicate");
        for (ExpiryTestStage testPoint : testPoints) {
            switch (testPoint) {
                case PRE:
                    this.preExpiryTests.add(predicate);
                    break;
                case POST_FILTER:
                    this.midExpiryTests.add(predicate);
                    break;
                case POST_HANDLE:
                    this.postExpiryTests.add(predicate);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown ExpiryTestPoint: " + testPoint);
            }
        }
        return this;
    }

    @Nonnull
    @Override
    public ProtocolSubscriptionBuilder filter(@Nonnull Predicate<PacketEvent> predicate) {
        Objects.requireNonNull(predicate, "predicate");
        this.filters.add(predicate);
        return this;
    }

    @Nonnull
    @Override
    public ProtocolSubscriptionBuilder exceptionConsumer(@Nonnull BiConsumer<? super PacketEvent, Throwable> exceptionConsumer) {
        Objects.requireNonNull(exceptionConsumer, "exceptionConsumer");
        this.exceptionConsumer = exceptionConsumer;
        return this;
    }

    @Nonnull
    @Override
    public ProtocolHandlerList handlers() {
        return new ProtocolHandlerListImpl(this);
    }
}
