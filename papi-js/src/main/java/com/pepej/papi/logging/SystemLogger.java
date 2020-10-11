package com.pepej.papi.logging;

import java.util.Objects;
import java.util.logging.Logger;

/**
 * Represents a bridge between the platforms logger and a ScriptController.
 */
public interface SystemLogger {

    /**
     * Creates a {@link SystemLogger} using a java logger
     *
     * @param logger the logger
     * @return a new system logger
     */
    static SystemLogger usingJavaLogger(Logger logger) {
        Objects.requireNonNull(logger, "logger");
        return new SystemLogger() {
            @Override
            public void info(String message) {
                logger.info(message);
            }

            @Override
            public void warning(String message) {
                logger.warning(message);
            }

            @Override
            public void severe(String message) {
                logger.severe(message);
            }
        };
    }

    void info(String message);

    void warning(String message);

    void severe(String message);

}
