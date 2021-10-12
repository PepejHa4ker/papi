package com.pepej.papi.messaging;

import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;
import com.pepej.papi.gson.GsonProvider;
import com.pepej.papi.messaging.codec.Codec;
import com.pepej.papi.messaging.codec.GsonCodec;
import com.pepej.papi.messaging.codec.Message;
import com.pepej.papi.promise.Promise;
import com.pepej.papi.scheduler.Schedulers;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import javax.annotation.Nonnull;
import java.util.Base64;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * An abstract implementation of {@link Messenger}.
 *
 * <p>Outgoing messages are passed to a {@link BiConsumer} to be passed on.</p>
 * <p>Incoming messages can be distributed using {@link #registerIncomingMessage(String, byte[])}.</p>
 */
@SuppressWarnings("UnstableApiUsage")
public class AbstractMessenger implements Messenger {

    @SuppressWarnings("unchecked")
    private final LoadingCache<Map.Entry<String, TypeToken<?>>, AbstractChannel<?>> channels = CacheBuilder.newBuilder().build(new ChannelLoader());

    // consumer for outgoing messages. accepts in the format [channel name, message]
    private final BiConsumer<String, byte[]> outgoingMessages;
    // consumer for channel names which should be subscribed to.
    private final Consumer<String> notifySub;
    // consumer for channel names which should be unsubscribed from.
    private final Consumer<String> notifyUnsub;

    /**
     * Creates a new abstract messenger
     *
     * @param outgoingMessages the consumer to pass outgoing messages to
     * @param notifySub the consumer to pass the names of channels which should be subscribed to
     * @param notifyUnsub the consumer to pass the names of channels which should be unsubscribed from
     */
    public AbstractMessenger(BiConsumer<String, byte[]> outgoingMessages, Consumer<String> notifySub, Consumer<String> notifyUnsub) {
        this.outgoingMessages = Objects.requireNonNull(outgoingMessages, "outgoingMessages");
        this.notifySub = Objects.requireNonNull(notifySub, "notifySub");
        this.notifyUnsub = Objects.requireNonNull(notifyUnsub, "notifyUnsub");
    }

    /**
     * Distributes an oncoming message to the channels held in this messenger.
     *
     * @param channel the channel the message was received on
     * @param message the message
     */
    public void registerIncomingMessage(String channel, byte[] message) {
        Objects.requireNonNull(channel, "channel");
        Objects.requireNonNull(message, "message");

        for (Map.Entry<Map.Entry<String, TypeToken<?>>, AbstractChannel<?>> c : this.channels.asMap().entrySet()) {
            if (c.getKey().getKey().equals(channel)) {
                c.getValue().onIncomingMessage(message);
            }
        }
    }

    @Nonnull
    @SuppressWarnings("unchecked")
    @Override
    public <T> Channel<T> getChannel(@Nonnull String name, @Nonnull TypeToken<T> type) {
        Objects.requireNonNull(name, "name");
        Preconditions.checkArgument(!name.trim().isEmpty(), "name cannot be empty");
        Objects.requireNonNull(type, "type");

        return (Channel<T>) this.channels.getUnchecked(Maps.immutableEntry(name, type));
    }

    private static <T> Codec<T> getCodec(TypeToken<T> type) {
        Class<? super T> rawType = type.getRawType();
        do {
            Message message = rawType.getAnnotation(Message.class);
            if (message != null) {
                Class<? extends Codec<?>> codec = message.codec();
                try {
                    //noinspection unchecked
                    return (Codec<T>) codec.newInstance();
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        } while ((rawType = rawType.getSuperclass()) != null);

        return new GsonCodec<>(GsonProvider.standard(), type);
    }

    private static class AbstractChannel<T> implements Channel<T> {
        private final AbstractMessenger messenger;
        private final String name;
        private final TypeToken<T> type;
        private final Codec<T> codec;

        private final Set<AbstractChannelAgent<T>> agents = ConcurrentHashMap.newKeySet();
        private boolean subscribed = false;

        private AbstractChannel(AbstractMessenger messenger, String name, TypeToken<T> type) {
            this.messenger = messenger;
            this.name = name;
            this.type = type;
            this.codec = AbstractMessenger.getCodec(type);
        }

        private void onIncomingMessage(byte[] message) {
            try {
                T decoded = this.codec.decode(message);
                Objects.requireNonNull(decoded, "decoded");

                for (AbstractChannelAgent<T> agent : this.agents) {
                    try {
                        agent.onIncomingMessage(decoded);
                    } catch (Exception e) {
                        new RuntimeException("Unable to pass decoded message to agent: " + decoded, e).printStackTrace();
                    }
                }

            } catch (Exception e) {
                new RuntimeException("Unable to decode message: " + Base64.getEncoder().encodeToString(message), e).printStackTrace();
            }
        }

        private void checkSubscription() {
            boolean shouldSubscribe = this.agents.stream().anyMatch(AbstractChannelAgent::hasListeners);
            if (shouldSubscribe == this.subscribed) {
                return;
            }
            this.subscribed = shouldSubscribe;

            Schedulers.async().run(() -> {
                try {
                    if (shouldSubscribe) {
                        this.messenger.notifySub.accept(this.name);
                    } else {
                        this.messenger.notifyUnsub.accept(this.name);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        @Override
        @NonNull
        public String getName() {
            return this.name;
        }

        @Override
        @NonNull
        public TypeToken<T> getType() {
            return this.type;
        }

        @Nonnull
        @Override
        public Codec<T> getCodec() {
            return this.codec;
        }

        @Override
        @NonNull
        public ChannelAgent<T> newAgent() {
            AbstractChannelAgent<T> agent = new AbstractChannelAgent<>(this);
            this.agents.add(agent);
            return agent;
        }

        @Override
        @NonNull
        public Promise<Void> sendMessage(@NonNull T message) {
            Objects.requireNonNull(message, "message");
            return Schedulers.async().call(() -> {
                byte[] buf = this.codec.encode(message);
                this.messenger.outgoingMessages.accept(this.name, buf);
                return null;
            });
        }
    }

    private static class AbstractChannelAgent<T> implements ChannelAgent<T> {
        @Nullable
        private AbstractChannel<T> channel;
        private final Set<ChannelListener<T>> listeners = ConcurrentHashMap.newKeySet();

        AbstractChannelAgent(@Nullable AbstractChannel<T> channel) {
            this.channel = channel;
        }

        private void onIncomingMessage(T message) {
            for (ChannelListener<T> listener : this.listeners) {
                Schedulers.async().run(() -> {
                    try {
                        listener.onMessage(this, message);
                    } catch (Exception e) {
                        new RuntimeException("Unable to pass decoded message to listener: " + listener, e).printStackTrace();
                    }
                });
            }
        }

        @Override
        @NonNull
        public Channel<T> getChannel() {
            Preconditions.checkState(this.channel != null, "agent not active");
            return this.channel;
        }

        @Override
        @NonNull
        public Set<ChannelListener<T>> getListeners() {
            Preconditions.checkState(this.channel != null, "agent not active");
            return ImmutableSet.copyOf(this.listeners);
        }

        @Override
        public boolean hasListeners() {
            return !this.listeners.isEmpty();
        }

        @Override
        public boolean addListener(@NonNull ChannelListener<T> listener) {
            Preconditions.checkState(this.channel != null, "agent not active");
            try {
                return this.listeners.add(listener);
            } finally {
                this.channel.checkSubscription();
            }
        }

        @Override
        public boolean removeListener(@NonNull ChannelListener<T> listener) {
            Preconditions.checkState(this.channel != null, "agent not active");
            try {
                return this.listeners.remove(listener);
            } finally {
                this.channel.checkSubscription();
            }
        }

        @Override
        public void close() {
            if (this.channel == null) {
                return;
            }

            this.listeners.clear();
            this.channel.agents.remove(this);
            this.channel.checkSubscription();
            this.channel = null;
        }
    }

    private class ChannelLoader<T> extends CacheLoader<Map.Entry<String, TypeToken<T>>, Channel<T>> {
        @Override
        public Channel<T> load(Map.Entry<String, TypeToken<T>> spec) throws Exception {
            return new AbstractChannel<>(AbstractMessenger.this, spec.getKey(), spec.getValue());
        }
    }
}