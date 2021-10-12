package com.pepej.papi.network.redirect;

import com.google.common.collect.ImmutableMap;
import com.pepej.papi.event.SingleSubscription;
import com.pepej.papi.events.Events;
import com.pepej.papi.messaging.InstanceData;
import com.pepej.papi.messaging.Messenger;
import com.pepej.papi.messaging.conversation.*;
import com.pepej.papi.profiles.Profile;
import com.pepej.papi.promise.Promise;
import com.pepej.papi.text.Text;
import com.pepej.papi.utils.Log;
import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

public class AbstractRedirectSystem implements RedirectSystem {
    private final InstanceData instanceData;
    private final PlayerRedirector redirector;

    private final ConversationChannel<RequestMessage, ResponseMessage> channel;
    private final ConversationChannelAgent<RequestMessage, ResponseMessage> agent;

    private final ExpiringMap<UUID, Response> expectedPlayers = ExpiringMap.builder()
            .expiration(5, TimeUnit.SECONDS)
            .expirationPolicy(ExpirationPolicy.CREATED)
            .build();

    private final SingleSubscription<AsyncPlayerPreLoginEvent> loginEventListener;
    private boolean ensureJoinedViaQueue = true;

    private RequestHandler handler = new AllowAllHandler();
    private final List<RedirectParameterProvider> defaultParameters = new CopyOnWriteArrayList<>();

    public AbstractRedirectSystem(Messenger messenger, InstanceData instanceData, PlayerRedirector redirector) {
        this.instanceData = instanceData;
        this.redirector = redirector;

        this.channel = messenger.getConversationChannel("papi-redirect", RequestMessage.class, ResponseMessage.class);

        this.agent = this.channel.newAgent();
        this.agent.addListener((agent, message) -> {
            if (!this.instanceData.getId().equalsIgnoreCase(message.targetServer)) {
                return ConversationReply.noReply();
            }

            // call the handler
            Promise<Response> response = this.handler.handle(message);

            // process the redirect

            response.thenAcceptAsync(r -> {

                if (!r.isAllowed()) {
                    return;
                }

                // add player to the expected players queue
                this.expectedPlayers.put(message.uuid, r);

                // tell the connect server to move the player
                this.redirector.redirectPlayer(this.instanceData.getId(), Profile.create(message.uuid, message.username));
            });

            // send the response
            return ConversationReply.ofPromise(response.thenApplyAsync(r -> {
                ResponseMessage resp = new ResponseMessage();
                resp.convoId = message.convoId;
                resp.allowed = r.isAllowed();
                resp.reason = r.getReason();
                resp.params = new HashMap<>(r.getParams());
                return resp;
            }));
        });

        this.loginEventListener = Events.subscribe(AsyncPlayerPreLoginEvent.class)
                .filter(e -> this.ensureJoinedViaQueue)
                .handler(e -> {
                    Response response = this.expectedPlayers.remove(e.getUniqueId());
                    if (response == null || !response.isAllowed()) {
                        e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST, Text.colorize("&cSorry! The server is unable to process your login at this time. (queue error)"));
                    }
                });
    }

    @Override
    public Promise<ReceivedResponse> redirectPlayer(@NotNull String serverId, @NotNull Profile profile, @NotNull Map<String, String> params) {
        RequestMessage req = new RequestMessage();
        req.convoId = UUID.randomUUID();
        req.targetServer = serverId;
        req.uuid = profile.getUniqueId();
        req.username = profile.getName().orElse(null);
        req.params = new HashMap<>(params);

        // include default parameters
        for (RedirectParameterProvider defaultProvider : this.defaultParameters) {
            for (Map.Entry<String, String> ent : defaultProvider.provide(profile, serverId).entrySet()) {
                req.params.putIfAbsent(ent.getKey(), ent.getValue());
            }
        }

        Promise<ReceivedResponse> promise = Promise.empty();

        // send req and await reply.
        this.channel.sendMessage(req, new ConversationReplyListener<ResponseMessage>() {
            @NotNull
            @Override
            public RegistrationAction onReply(@NotNull ResponseMessage reply) {
                promise.supply(reply);
                return RegistrationAction.STOP_LISTENING;
            }

            @Override
            public void onTimeout(@NotNull List<ResponseMessage> replies) {
                promise.supply(MissingResponse.INSTANCE);
            }
        }, 5, TimeUnit.SECONDS);

        return promise;
    }

    @Override
    public void setHandler(@NotNull RequestHandler handler) {
        this.handler = Objects.requireNonNull(handler, "handler");
    }

    @Override
    public void addDefaultParameterProvider(@NotNull RedirectParameterProvider provider) {
        this.defaultParameters.add(provider);
    }

    @Override
    public void setEnsure(boolean ensureJoinedViaQueue) {
        this.ensureJoinedViaQueue = ensureJoinedViaQueue;
    }

    @Override
    public int getExpectedConnectionsCount() {
        return this.expectedPlayers.size();
    }

    @Override
    public void close() {
        this.agent.close();
        this.loginEventListener.close();
    }

    private static final class RequestMessage implements ConversationMessage, Request {
        private UUID convoId;
        private String targetServer;
        private UUID uuid;
        private String username;
        private Map<String, String> params;

        @NotNull
        @Override
        public UUID getConversationId() {
            return this.convoId;
        }

        @NotNull
        @Override
        public Profile getProfile() {
            return Profile.create(this.uuid, this.username);
        }

        @NotNull
        @Override
        public Map<String, String> getParams() {
            return ImmutableMap.copyOf(this.params);
        }
    }

    private static final class ResponseMessage implements ConversationMessage, ReceivedResponse {
        private UUID convoId;
        private boolean allowed;
        private String reason;
        private Map<String, String> params;

        @NotNull
        @Override
        public UUID getConversationId() {
            return this.convoId;
        }

        @NotNull
        @Override
        public Status getStatus() {
            return this.allowed ? Status.ALLOWED : Status.DENIED;
        }

        @NotNull
        @Override
        public Optional<String> getReason() {
            return Optional.ofNullable(this.reason);
        }

        @NotNull
        @Override
        public Map<String, String> getParams() {
            return ImmutableMap.copyOf(this.params);
        }
    }

    private static final class MissingResponse implements ReceivedResponse {
        private static final MissingResponse INSTANCE = new MissingResponse();

        @NotNull
        @Override
        public Status getStatus() {
            return Status.NO_REPLY;
        }

        @NotNull
        @Override
        public Optional<String> getReason() {
            return Optional.empty();
        }

        @NotNull
        @Override
        public Map<String, String> getParams() {
            return ImmutableMap.of();
        }
    }

    private static final class AllowAllHandler implements RequestHandler {

        @NotNull
        @Override
        public Promise<Response> handle(@NotNull Request request) {
            return Promise.completed(Response.allow());
        }
    }

}
