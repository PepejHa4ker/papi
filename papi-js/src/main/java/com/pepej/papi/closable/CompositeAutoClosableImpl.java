package com.pepej.papi.closable;

import com.pepej.papi.terminable.composite.CompositeClosingException;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * A simple implementation of {@link CompositeAutoClosable} using a
 * {@link ConcurrentLinkedDeque}.
 */
class CompositeAutoClosableImpl implements CompositeAutoClosable {
    private final Deque<AutoCloseable> closeables = new ConcurrentLinkedDeque<>();

    @Override
    public CompositeAutoClosable bind(AutoCloseable autoCloseable) {
        Objects.requireNonNull(autoCloseable, "autoCloseable");
        this.closeables.push(autoCloseable);
        return this;
    }

    @Override
    public void close() throws CompositeClosingException {
        List<Exception> caught = new ArrayList<>();
        for (AutoCloseable ac; (ac = this.closeables.poll()) != null; ) {
            try {
                ac.close();
            } catch (Exception e) {
                caught.add(e);
            }
        }

        if (!caught.isEmpty()) {
            throw new CompositeClosingException(caught);
        }
    }
}
