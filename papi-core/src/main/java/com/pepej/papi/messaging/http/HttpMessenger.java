package com.pepej.papi.messaging.http;

import com.google.common.io.ByteStreams;
import com.google.common.reflect.TypeToken;
import com.pepej.papi.messaging.AbstractMessenger;
import com.pepej.papi.messaging.Channel;
import com.pepej.papi.messaging.Messenger;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.function.Function;

/**
 * Proof of concept {@link Messenger} implementation using the HTTP protocol.
 *
 * <p>The messenger works by sending/receiving HTTP POST requests. It is a simple (not necessarily
 * the most efficient) implementation, which could be quite easily optimised if necessary - pooling
 * connections would be a start!</p>
 *
 * <p>Of course, a messenger using plain TCP sockets (optionally with a library like netty)
 * could work just as well.</p>
 */
public class HttpMessenger implements Messenger {

    // The abstract messenger implementation used as the basis for Channel construction and handling
    private final AbstractMessenger messenger;

    // The http server used to handle incoming messages
    private final HttpServer httpServer;
    // A factory which creates remote URLs for outgoing messages
    private final Function<String, URL> remoteUrl;

    public HttpMessenger(String host, int port, String remoteHost, int remotePort) {
        this.messenger = new AbstractMessenger(this::handleOutgoing, this::subscribe, this::unsubscribe);
        try {
            this.httpServer = HttpServer.create(new InetSocketAddress(host, port), 0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.remoteUrl = channel -> {
            try {
                return new URL("http", remoteHost, remotePort, channel);
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        };
    }

    private void handleOutgoing(String channel, byte[] message) {
        // Handle outgoing messages: just send a HTTP POST request to the remote server.

        try {
            HttpURLConnection connection = (HttpURLConnection) this.remoteUrl.apply(encodeChannel(channel)).openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setDoInput(false);
            connection.setUseCaches(false);
            connection.connect();
            try (OutputStream outputStream = connection.getOutputStream()) {
                outputStream.write(message);
            }

            if (connection.getResponseCode() >= 400) {
                throw new IOException("Response code: " + connection.getResponseCode() + " - " + connection.getResponseMessage());
            }

            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Subscribe and unsubscribe from channels by creating the corresponding handler context on the server.
    private void subscribe(String channel) {
        this.httpServer.createContext(encodeChannel(channel), new Handler(channel));
    }

    private void unsubscribe(String channel) {
        this.httpServer.removeContext(encodeChannel(channel));
    }

    /**
     * Incoming handler for POST requests
     */
    private final class Handler implements HttpHandler {
        private final String channelName;

        private Handler(String channelName) {
            this.channelName = channelName;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            if (!method.equals("POST")) {
                throw new UnsupportedEncodingException("Unsupported request: " + exchange.getRequestMethod());
            }

            byte[] message;
            try (InputStream in = exchange.getRequestBody()) {
                message = ByteStreams.toByteArray(in);
            }

            // forward to the abstract Messenger impl
            HttpMessenger.this.messenger.registerIncomingMessage(this.channelName, message);

            exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
            exchange.getResponseBody().close();
        }
    }

    // Encode channel ids using the URL encoder
    private static String encodeChannel(String channel) {
        try {
            return '/' + URLEncoder.encode(channel, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            throw new AssertionError(e);
        }
    }

    @Nonnull
    @Override
    public <T> Channel<T> getChannel(@Nonnull String name, @Nonnull TypeToken<T> type) {
        return this.messenger.getChannel(name, type);
    }

}