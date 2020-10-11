package com.pepej.papi.internal;

import com.pepej.papi.logging.SystemLogger;

import java.util.function.Supplier;
import java.util.logging.Logger;

final class FallbackSystemLogger implements SystemLogger {
    public static final Supplier<SystemLogger> INSTANCE = FallbackSystemLogger::getInstance;

    private static SystemLogger instance = null;
    private static synchronized SystemLogger getInstance() {
        if (instance == null) {
            instance = new FallbackSystemLogger();
        }
        return instance;
    }

    private final Logger logger = Logger.getLogger(ScriptControllerImpl.class.getName());

    private FallbackSystemLogger() {

    }

    @Override
    public void info(String message) {
        this.logger.info(message);
    }

    @Override
    public void warning(String message) {
        this.logger.warning(message);
    }

    @Override
    public void severe(String message) {
        this.logger.severe(message);
    }
}
