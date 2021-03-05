package com.pepej.papi.signprompt;

import com.pepej.papi.services.Implementor;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;

/**
 * Represents an object which can accept input from players using signs.
 */
@Implementor(PacketSignPromptFactory.class)
public interface SignPromptFactory {

    /**
     * Opens a sign prompt.
     *
     * @param player the player to open the prompt for
     * @param lines the lines to fill the sign with initially
     * @param responseHandler the response handler.
     */
    void openPrompt(@NonNull Player player, @NonNull List<String> lines, @NonNull ResponseHandler responseHandler);

    /**
     * Functional interface for handling responses to an active sign prompt.
     */
    @FunctionalInterface
    interface ResponseHandler {

        /**
         * Handles the response
         *
         * @param lines the response content
         * @return the response
         */
        @NonNull
        Response handleResponse(@NonNull List<String> lines);

    }

    /**
     * Encapsulates a response to the players input.
     */
    enum Response {

        /**
         * Marks that the response was accepted
         */
        ACCEPTED,

        /**
         * Marks that the response was not accepted, the player will be prompted
         * for another input.
         */
        TRY_AGAIN

    }

}