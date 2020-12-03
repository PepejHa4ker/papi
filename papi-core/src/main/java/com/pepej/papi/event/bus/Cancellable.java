package com.pepej.papi.event.bus;


/**
 * A cancellable event.
 */
public interface Cancellable {
    /**
     * Tests if the event has been cancelled.
     *
     * @return {@code true} if the event has been cancelled, {@code false} otherwise
     */
    boolean cancelled();

    /**
     * Sets the cancelled state of the event.
     *
     * @param cancelled {@code true} if the event should be cancelled, {@code false} otherwise
     */
    void cancelled(final boolean cancelled);

    /**
     * An abstract implementation of a cancellable event.
     *
     * <p>This implementation is not always possible to use if {@link EventBus} requires events
     * to implement an {@code abstract} class.</p>
     */
    abstract class Impl implements Cancellable {
        // protected to allow children classes to access
        protected boolean cancelled;

        @Override
        public boolean cancelled() {
            return this.cancelled;
        }

        @Override
        public void cancelled(final boolean cancelled) {
            this.cancelled = cancelled;
        }
    }
}
