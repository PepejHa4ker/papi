package com.pepej.papi.network.redirect;

import com.google.common.collect.ImmutableMap;
import com.pepej.papi.messaging.InstanceData;
import com.pepej.papi.messaging.Messenger;
import com.pepej.papi.profiles.Profile;
import com.pepej.papi.promise.Promise;
import com.pepej.papi.services.Services;
import com.pepej.papi.terminable.Terminable;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Implements a system for controlled redirects between servers.
 */
public interface RedirectSystem extends Terminable {

    /**
     * Creates a new {@link RedirectSystem} instance. These should be shared if possible.
     *
     * @param messenger the messenger
     * @param instanceData the instance data
     * @param redirecter the redirecter
     * @return the new RedirectSystem
     */
    static RedirectSystem create(Messenger messenger, InstanceData instanceData, PlayerRedirector redirecter) {
        return new AbstractRedirectSystem(messenger, instanceData, redirecter);
    }

    /**
     * Creates a new {@link RedirectSystem} instance. These should be shared if possible.
     *
     * @param messenger the messenger
     * @return the new RedirectSystem
     */
    static <M extends Messenger & InstanceData & PlayerRedirector> RedirectSystem create(M messenger) {
        return new AbstractRedirectSystem(messenger, messenger, messenger);
    }

    /**
     * Tries to obtain an instance of RedirectSystem from the services manager, falling
     * back to given supplier if one is not already present.
     *
     * @param ifElse the supplier
     * @return the RedirectSystem instance
     */
    static RedirectSystem obtain(Supplier<RedirectSystem> ifElse) {
        RedirectSystem network = Services.getNullable(RedirectSystem.class);
        if (network == null) {
            network = ifElse.get();
            Services.provide(RedirectSystem.class, network);
        }
        return network;
    }

    /**
     * Makes a request to redirect the given player to the given server.
     *
     * @param serverId the server to redirect to
     * @param profile the player to be redirected
     * @param params the parameters for the request
     * @return a promise for the redirect response
     */
    Promise<ReceivedResponse> redirectPlayer(@NotNull String serverId, @NotNull Profile profile, @NotNull Map<String, String> params);

    /**
     * Makes a request to redirect the given player to the given server.
     *
     * @param serverId the server to redirect to
     * @param player the player to be redirected
     * @param params the parameters for the request
     * @return a promise for the redirect response
     */
    default Promise<ReceivedResponse> redirectPlayer(@NotNull String serverId, @NotNull Player player, @NotNull Map<String, String> params) {
        return redirectPlayer(serverId, Profile.create(player), params);
    }

    /**
     * Sets the {@link RequestHandler} for this instance.
     *
     * @param handler the handler
     */
    void setHandler(@NotNull RequestHandler handler);

    /**
     * Adds a default parameter provider.
     *
     * @param provider the provider
     */
    void addDefaultParameterProvider(@NotNull RedirectParameterProvider provider);

    /**
     * Sets if the system should ensure that incoming connections were made (and accepted) by the
     * redirect system.
     *
     * @param ensureJoinedViaQueue if the system should ensure all joins are in the redirect queue
     */
    void setEnsure(boolean ensureJoinedViaQueue);

    /**
     * Gets the number of connections which have been allowed, but not yet fully established.
     *
     * @return the number of expected connections
     */
    int getExpectedConnectionsCount();

    @Override
    void close();

    /**
     * Encapsulates a redirect request
     */
    interface Request {

        /**
         * Gets the profile sending the request
         *
         * @return the profile sending the request
         */
        @NotNull
        Profile getProfile();

        /**
         * Gets the parameters included with the request.
         *
         * @return the parameters
         */
        @NotNull
        Map<String, String> getParams();

    }

    /**
     * Encapsulates the response to a {@link Request}.
     */
    final class Response {
        private static final Response ALLOW = new Response(true, null, ImmutableMap.of());
        private static final Response DENY = new Response(false, null, ImmutableMap.of());

        public static Response allow() {
            return ALLOW;
        }

        public static Response allow(@NotNull Map<String, String> params) {
            return new Response(true, null, params);
        }

        public static Response deny() {
            return DENY;
        }

        public static Response deny(@NotNull String reason) {
            return new Response(false, reason, ImmutableMap.of());
        }

        public static Response deny(@NotNull Map<String, String> params) {
            return new Response(false, null, params);
        }

        public static Response deny(@NotNull String reason, @NotNull Map<String, String> params) {
            return new Response(false, reason, params);
        }

        private final boolean allowed;
        private final String reason;
        private final Map<String, String> params;

        public Response(boolean allowed, @Nullable String reason, @NotNull Map<String, String> params) {
            this.allowed = allowed;
            this.reason = reason;
            this.params = ImmutableMap.copyOf(params);
        }

        public boolean isAllowed() {
            return this.allowed;
        }

        @Nullable
        public String getReason() {
            return this.reason;
        }

        public Map<String, String> getParams() {
            return this.params;
        }
    }

    /**
     * Handles incoming redirect requests for this server
     */
    interface RequestHandler {

        /**
         * Handles the request and produces a result.
         *
         * @param request the request
         * @return the response
         */
        @NotNull
        Promise<Response> handle(@NotNull Request request);

    }

    /**
     * Represents the response to a redirect request.
     */
    interface ReceivedResponse {

        /**
         * Gets the status of the response
         *
         * @return the status
         */
        @NotNull
        Status getStatus();

        /**
         * Gets the reason for the response.
         *
         * @return the reason
         */
        @NotNull
        Optional<String> getReason();

        /**
         * Gets the parameters included with the response.
         *
         * @return the parameters
         */
        @NotNull
        Map<String, String> getParams();

        enum Status {
            ALLOWED,
            DENIED,
            NO_REPLY
        }
    }

}
