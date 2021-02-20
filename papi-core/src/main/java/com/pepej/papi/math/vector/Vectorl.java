package com.pepej.papi.math.vector;

public interface Vectorl {

    /**
     * Multiplies the current vector by a
     *
     * @param a The number by which the vector will be multiplied
     * @return the vector multiplied by a
     */
    Vectorl mul(long a);

    /**
     * Divides the current vector by a
     *
     * @param a The number by which the vector will be divided
     * @return the vector divided by a
     */
    Vectorl div(long a);

    /**
     * Raises the current vector to the power pow
     *
     * @param pow The number by which the vector will be raised
     * @return the vector raised to the power pow
     */
    Vectorl pow(long pow);

    /**
     * Takes an absolute value from the current vector
     *
     * @return the vector's absolute value
     */
    Vectorl abs();

    /**
     * Takes an negative value from the current vector
     *
     * @return the vector's negation value
     */
    Vectorl negate();

    /**
     * Returns the magnitude of a vector
     *
     * @return the vector's magnitude
     */
    long length();

    /**
     * Returns the squared length of a vector
     *
     * @return the vector's squared length
     */
    long lengthSquared();

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

    long[] toArray();

    Vectori toInt();

    Vectorf toFloat();

    Vectord toDouble();
}
