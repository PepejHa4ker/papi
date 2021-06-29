package com.pepej.papi.terminable.composite;

import com.pepej.papi.terminable.Terminable;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedDeque;

public class AbstractCompositeTerminable implements CompositeTerminable {
    private final Deque<AutoCloseable> closeables = new ConcurrentLinkedDeque<>(); // closeables stack

    protected AbstractCompositeTerminable() {

    }

    @Override
    public CompositeTerminable with(@NonNull AutoCloseable autoCloseable) {
        Objects.requireNonNull(autoCloseable, "autoCloseable");
        this.closeables.push(autoCloseable);
        return this;
    }

    @Override
    public void close() throws CompositeClosingException {
        List<Exception> caught = new ArrayList<>();
        AutoCloseable ac;
        while ((ac = this.closeables.poll()) != null) {
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

    @Override
    public void cleanup() {
        this.closeables.removeIf(ac -> {
            if (!(ac instanceof Terminable)) {
                return false;
            }
            if (ac instanceof CompositeTerminable) {
                ((CompositeTerminable) ac).cleanup();
            }
            return ((Terminable) ac).isClosed();
        });
    }
}
