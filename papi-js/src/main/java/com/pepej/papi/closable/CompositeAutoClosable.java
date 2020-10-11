package com.pepej.papi.closable;

import com.pepej.papi.terminable.composite.CompositeClosingException;

/**
 * Represents an {@link AutoCloseable} made up of several other
 * {@link AutoCloseable}s.
 *
 * <p>The {@link #close()} method closes in LIFO (Last-In-First-Out) order.</p>
 *
 * <p>{@link CompositeAutoClosable}s can be reused. The instance is effectively
 * cleared on each invocation of {@link #close()}.</p>
 */
public interface CompositeAutoClosable extends AutoCloseable {

    /**
     * Creates a new standalone {@link CompositeAutoClosable}.
     *
     * @return a new {@link CompositeAutoClosable}.
     */
    static CompositeAutoClosable create() {
        return new CompositeAutoClosableImpl();
    }

    /**
     * Binds an {@link AutoCloseable} with this composite closable.
     *
     * <p>Note that implementations do not keep track of duplicate contains
     * closables. If a single {@link AutoCloseable} is added twice, it will be
     * {@link #close() closed} twice.</p>
     *
     * @param autoCloseable the closable to bind
     * @throws NullPointerException if the closable is null
     * @return this (for chaining)
     */
    CompositeAutoClosable bind(AutoCloseable autoCloseable);

    /**
     * Binds all given {@link AutoCloseable} with this composite closable.
     *
     * <p>Note that implementations do not keep track of duplicate contains
     * closables. If a single {@link AutoCloseable} is added twice, it will be
     * {@link #close() closed} twice.</p>
     *
     * <p>Ignores null values.</p>
     *
     * @param autoCloseables the closables to bind
     * @return this (for chaining)
     */
    default CompositeAutoClosable bindAll(AutoCloseable... autoCloseables) {
        for (AutoCloseable autoCloseable : autoCloseables) {
            if (autoCloseable == null) {
                continue;
            }
            bind(autoCloseable);
        }
        return this;
    }

    /**
     * Binds all given {@link AutoCloseable} with this composite closable.
     *
     * <p>Note that implementations do not keep track of duplicate contains
     * closables. If a single {@link AutoCloseable} is added twice, it will be
     * {@link #close() closed} twice.</p>
     *
     * <p>Ignores null values.</p>
     *
     * @param autoCloseables the closables to bind
     * @return this (for chaining)
     */
    default CompositeAutoClosable bindAll(Iterable<? extends AutoCloseable> autoCloseables) {
        for (AutoCloseable autoCloseable : autoCloseables) {
            if (autoCloseable == null) {
                continue;
            }
            bind(autoCloseable);
        }
        return this;
    }

    /**
     * Closes this composite resource.
     *
     * @throws CompositeClosingException if any of the sub instances throw an
     *                                   exception whilst closing
     */
    @Override
    void close() throws CompositeClosingException;

    /**
     * Closes this composite resource, but doesn't rethrow or print any
     * exceptions.
     *
     * @see #close()
     */
    default void closeSilently() {
        try {
            close();
        } catch (CompositeClosingException e) {
            // ignore
        }
    }

    /**
     * Closes this composite resource, but simply prints any resultant
     * exceptions instead of rethrowing them.
     *
     * @see #close()
     * @see CompositeClosingException#printAllStackTraces()
     */
    default void closeAndReportExceptions() {
        try {
            close();
        } catch (CompositeClosingException e) {
            e.printAllStackTraces();
        }
    }

}
