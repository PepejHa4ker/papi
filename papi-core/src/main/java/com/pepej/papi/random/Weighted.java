package com.pepej.papi.random;


import org.checkerframework.checker.index.qual.NonNegative;

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
     */
    @NonNegative
    double getWeight();


}