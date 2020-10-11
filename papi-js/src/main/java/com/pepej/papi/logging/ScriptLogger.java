package com.pepej.papi.logging;

import com.pepej.papi.environment.script.Script;

/**
 * A scripts logger instance
 */
public interface ScriptLogger {

    static ScriptLogger create(SystemLogger logger, Script script) {
        return new ScriptLoggerImpl(logger, script);
    }

    void info(Object... message);

    default void log(Object... message) {
        info(message);
    }

    void warn(Object... message);

    default void warning(Object... message) {
        warn(message);
    }

    void error(Object... message);

    default void severe(Object... message) {
        error(message);
    }

}
