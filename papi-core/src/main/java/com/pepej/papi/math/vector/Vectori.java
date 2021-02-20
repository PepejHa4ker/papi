package com.pepej.papi.math.vector;

public interface Vectori {

    /**
     * Multiplies the current vector by a
     *
     * @param a The number by which the vector will be multiplied
     * @return the vector multiplied by a
     */
    Vectori mul(int a);

    /**
     * Divides the current vector by a
     *
     * @param a The number by which the vector will be divided
     * @return the vector divided by a
     */
    Vectori div(int a);

    /**
     * Raises the current vector to the power pow
     *
     * @param pow The number by which the vector will be raised
     * @return the vector raised to the power pow
     */
    Vectori pow(int pow);

    /**
     * Takes an absolute value from the current vector
     *
     * @return the vector's absolute value
     */
    Vectori abs();

    /**
     * Takes an negative value from the current vector
     *
     * @return the vector's negation value
     */
    Vectori negate();


    /**
     * Returns the magnitude of a vector
     *
     * @return the vector's magnitude
     */
    int length();

    /**
     * Returns the squared length of a vector
     *
     * @return the vector's squared length
     */
    int lengthSquared();

    /**
     * Returns the minimum vector's axis
     *
     * @return the minimum vector's axis
     */
    int getMinAxis();

    /**
     * Returns the maximum vector's axis
     *
     * @return the maximum vector's axis
     */
    int getMaxAxis();

    int[] toArray();

    Vectorl toLong();

    Vectorf toFloat();

    Vectord toDouble();
}
