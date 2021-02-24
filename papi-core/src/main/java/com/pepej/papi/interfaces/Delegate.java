package com.pepej.papi.interfaces;

/**
 * Represents a class which delegates calls to a different object.
 *
 * @param <T> the delegate type
 */
public interface Delegate<T> {

    /**
     * Gets the delegate object
     *
     * @return the delegate object
     */
    T getDelegate();

}
