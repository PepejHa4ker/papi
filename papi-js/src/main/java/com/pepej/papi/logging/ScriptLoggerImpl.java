package com.pepej.papi.logging;

import com.pepej.papi.environment.script.Script;

final class ScriptLoggerImpl implements ScriptLogger {
    private static final String FORMAT = "[%s]%s";
    private final SystemLogger logger;
    private final Script script;

    public ScriptLoggerImpl(SystemLogger logger, Script script) {
        this.logger = logger;
        this.script = script;
    }

    @Override
    public void info(Object... message) {
        this.logger.info(formatLog(message));
    }

    @Override
    public void warn(Object... message) {
        this.logger.warning(formatLog(message));
    }

    @Override
    public void error(Object... message) {
        this.logger.severe(formatLog(message));
    }

    private String formatLog(Object... message) {
        return String.format(FORMAT, this.script.getName(), format(message));
    }

    private static String format(Object[] message) {
        if (message == null || message.length == 0) {
            return " ";
        } else if (message.length == 1) {
            return " " + message[0];
        } else {
            StringBuilder sb = new StringBuilder();
            for (Object o : message) {
                sb.append(" ").append(o);
            }
            return sb.toString();
        }
    }
}
