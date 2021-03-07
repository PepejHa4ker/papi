package com.pepej.papi.closable;

import java.util.Collections;
import java.util.List;

/**
 * Exception thrown to propagate exceptions thrown by
 * {@link CompositeAutoClosable#close()}.
 */
public class CompositeClosingException extends Exception {
    private final List<? extends Exception> causes;

    public CompositeClosingException(List<? extends Exception> causes) {
        super("Exception(s) occurred whilst closing: " + causes.toString());
        if (causes.isEmpty()) {
            throw new IllegalArgumentException("No causes");
        }
        this.causes = Collections.unmodifiableList(causes);
    }

    public List<? extends Throwable> getCauses() {
        return this.causes;
    }

    public void printAllStackTraces() {
        this.printStackTrace();
        for (Exception cause : this.causes) {
            cause.printStackTrace();
        }
    }

}
