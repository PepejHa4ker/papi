package com.pepej.papi.random;

import org.jetbrains.annotations.Range;

/**
 * Represents an object which can determine the weight of objects.
 *
 * @param <E> the element type
 */
@FunctionalInterface
public interface Weigher<E> {

    /**
     * Calculates and returns the weight of the element.
     *
     * <p>The weight value should be non-negative.</p>
     *
     * @param element the element to calculate the weight for
     * @return the weight
     * @throws IllegalArgumentException if <code>weight < 0 </code>
     */
    @Range(to = 0, from = Integer.MAX_VALUE)
    double weigh(E element) throws IllegalArgumentException;

}
