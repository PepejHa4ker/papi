package com.pepej.papi.messaging.conversation;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;
import com.google.common.reflect.TypeToken;
import com.pepej.papi.messaging.Channel;
import com.pepej.papi.messaging.ChannelAgent;
import com.pepej.papi.messaging.ChannelListener;
import com.pepej.papi.messaging.Messenger;
import com.pepej.papi.promise.Promise;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.*;

/**
 * Simple implementation of {@link ConversationChannel}.
 *
 * @param <T> the outgoing message type
 * @param <R> the reply message type
 */
public class SimpleConversationChannel<T extends ConversationMessage, R extends ConversationMessage> implements ConversationChannel<T, R> {
    private final String name;
    private final Channel<T> outgoingChannel;
    private final Channel<R> replyChannel;

    private final Set<Agent<T, R>> agents = ConcurrentHashMap.newKeySet();

    private final ScheduledExecutorService replyTimeoutExecutor = Executors.newSingleThreadScheduledExecutor();
    private final ChannelAgent<R> replyAgent;
    private final SetMultimap<UUID, ReplyListenerRegistration<R>> replyListeners = Multimaps.newSetMultimap(new ConcurrentHashMap<>(), ConcurrentHashMap::newKeySet);

    public SimpleConversationChannel(Messenger messenger, String name, TypeToken<T> outgoingType, TypeToken<R> replyType) {
        this.name = name;
        this.outgoingChannel = messenger.getChannel(name + "-o", outgoingType);
        this.replyChannel = messenger.getChannel(name + "-r", replyType);

        this.replyAgent = this.replyChannel.newAgent(new ReplyListener());
    }

    private final class ReplyListener implements ChannelListener<R> {
        @Override
        public void onMessage(@NonNull ChannelAgent<R> agent, @NonNull R message) {
            SimpleConversationChannel.this.replyListeners.get(message.getConversationId()).removeIf(l -> l.onReply(message));
        }
    }

    private static final class ReplyListenerRegistration<R extends ConversationMessage> {
        private final ConversationReplyListener<R> listener;
        private final List<R> replies = new ArrayList<>();
        private ScheduledFuture<?> timeoutFuture;

        private boolean active = true;

        private ReplyListenerRegistration(ConversationReplyListener<R> listener) {
            this.listener = listener;

        }

        /**
         * Passes the incoming reply to the listener, and returns true if the listener should be
         * unregistered
         *
         * @param message the message
         * @return if the listener should be unregistered
         */
        public boolean onReply(R message) {
            synchronized (this) {
                if (!this.active) {
                    return true;
                }

                this.replies.add(message);
                ConversationReplyListener.RegistrationAction action = this.listener.onReply(message);
                if (action == ConversationReplyListener.RegistrationAction.STOP_LISTENING) {

                    // unregister
                    this.active = false;
                    this.timeoutFuture.cancel(false);

                    return true;
                } else {
                    return false;
                }
            }
        }

        public void timeout() {
            synchronized (this) {
                if (!this.active) {
                    return;
                }

                this.listener.onTimeout(this.replies);
                this.active = false;
            }
        }
    }

    @NonNull
    @Override
    public String getName() {
        return this.name;
    }

    @NonNull
    @Override
    public Channel<T> getOutgoingChannel() {
        return this.outgoingChannel;
    }

    @NonNull
    @Override
    public Channel<R> getReplyChannel() {
        return this.replyChannel;
    }

    @NonNull
    @Override
    public ConversationChannelAgent<T, R> newAgent() {
        Agent<T, R> agent = new Agent<>(this);
        this.agents.add(agent);
        return agent;
    }

    @NonNull
    @Override
    public Promise<Void> sendMessage(@NonNull T message, @NonNull ConversationReplyListener<R> replyListener, long timeoutDuration, @NonNull TimeUnit unit) {
        // register the listener
        ReplyListenerRegistration<R> listenerRegistration = new ReplyListenerRegistration<>(replyListener);
        listenerRegistration.timeoutFuture = this.replyTimeoutExecutor.schedule(listenerRegistration::timeout, timeoutDuration, unit);
        this.replyListeners.put(message.getConversationId(), listenerRegistration);

        // send the outgoing message
        return this.outgoingChannel.sendMessage(message);
    }

    @Override
    public void close() {
        this.replyAgent.close();
        this.replyTimeoutExecutor.shutdown();
        this.agents.forEach(Agent::close);
    }

    private static final class Agent<T extends ConversationMessage, R extends ConversationMessage> implements ConversationChannelAgent<T, R> {
        private final SimpleConversationChannel<T, R> channel;
        private final ChannelAgent<T> delegateAgent;

        private Agent(@NonNull SimpleConversationChannel<T, R> channel) {
            this.channel = channel;
            this.delegateAgent = this.channel.getOutgoingChannel().newAgent();
        }

        @NonNull
        @Override
        public ConversationChannel<T, R> getChannel() {
            this.delegateAgent.getChannel(); // ensure this agent is still active
            return this.channel;
        }

        @NonNull
        @Override
        public Set<ConversationChannelListener<T, R>> getListeners() {
            Set<ChannelListener<T>> listeners = this.delegateAgent.getListeners();

            ImmutableSet.Builder<ConversationChannelListener<T, R>> ret = ImmutableSet.builder();
            for (ChannelListener<T> listener : listeners) {
                //noinspection unchecked
                ret.add(((WrappedListener) listener).delegate);
            }
            return ret.build();
        }

        @Override
        public boolean hasListeners() {
            return this.delegateAgent.hasListeners();
        }

        @Override
        public boolean addListener(@NonNull ConversationChannelListener<T, R> listener) {
            return this.delegateAgent.addListener(new WrappedListener(listener));
        }

        @Override
        public boolean removeListener(@NonNull ConversationChannelListener<T, R> listener) {
            Set<ChannelListener<T>> listeners = this.delegateAgent.getListeners();
            for (ChannelListener<T> other : listeners) {
                WrappedListener wrapped = (WrappedListener) other;
                if (wrapped.delegate == listener) {
                    return this.delegateAgent.removeListener(other);
                }
            }
            return false;
        }

        @Override
        public void close() {
            this.delegateAgent.close();
        }

        private final class WrappedListener implements ChannelListener<T> {
            private final ConversationChannelListener<T, R> delegate;

            private WrappedListener(ConversationChannelListener<T, R> delegate) {
                this.delegate = delegate;
            }

            @Override
            public void onMessage(@NonNull ChannelAgent<T> agent, @NonNull T message) {
                ConversationReply<R> reply = this.delegate.onMessage(Agent.this, message);
                if (reply.hasReply()) {
                    reply.getReply().thenAcceptAsync(m -> {
                        if (m != null) {
                            Agent.this.channel.replyChannel.sendMessage(m);
                        }
                    });
                }
            }
        }
    }
}
