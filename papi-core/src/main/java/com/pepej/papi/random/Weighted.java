package com.pepej.papi.random;


import org.jetbrains.annotations.Range;

/**
 * Represents an object which has a weight
 */
@FunctionalInterface
public interface Weighted {

    /**
     * An instance of {@link Weigher} which uses the {@link #getWeight()} method
     * to determine weight.
     */
    Weigher<? super Weighted> WEIGHER = Weighted::getWeight;

    /**
     * Gets the weight of this entry.
     *
     * @return The weight
     * @throws IllegalArgumentException if <code>return value < 0</code>
     */
    @Range(to = 0, from = Integer.MAX_VALUE)
    double getWeight() throws IllegalArgumentException;


}