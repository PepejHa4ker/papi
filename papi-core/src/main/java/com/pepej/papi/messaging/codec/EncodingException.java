package com.pepej.papi.messaging.codec;

/**
 * Exception thrown if an error occurs whilst encoding/decoding a message.
 */
public class EncodingException extends RuntimeException {

    public EncodingException() {
    }

    public EncodingException(String message) {
        super(message);
    }

    public EncodingException(String message, Exception cause) {
        super(message, cause);
    }

    public EncodingException(Exception cause) {
        super(cause);
    }
}