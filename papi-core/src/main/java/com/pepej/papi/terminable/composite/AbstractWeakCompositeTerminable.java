package com.pepej.papi.terminable.composite;

import com.pepej.papi.terminable.Terminable;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedDeque;

public class AbstractWeakCompositeTerminable implements CompositeTerminable {
    private final Deque<WeakReference<AutoCloseable>> closeables = new ConcurrentLinkedDeque<>();

    protected AbstractWeakCompositeTerminable() {}

    @Override
    public CompositeTerminable with(@NonNull AutoCloseable autoCloseable) {
        Objects.requireNonNull(autoCloseable, "autoCloseable");
        this.closeables.push(new WeakReference<>(autoCloseable));
        return this;
    }

    @Override
    public void close() throws CompositeClosingException {
        List<Exception> caught = new ArrayList<>();
        WeakReference<AutoCloseable> ref;
        while ((ref = this.closeables.poll()) != null) {
            AutoCloseable ac = ref.get();
            if (ac == null) {
                continue;
            }

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
        this.closeables.removeIf(ref -> {
            AutoCloseable ac = ref.get();
            return ac == null || (ac instanceof Terminable && ((Terminable) ac).isClosed());
        });
    }
}
