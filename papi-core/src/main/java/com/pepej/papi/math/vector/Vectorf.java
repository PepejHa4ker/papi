package com.pepej.papi.math.vector;

public interface Vectorf {

    /**
     * Multiplies the current vector by a
     *
     * @param a The number by which the vector will be multiplied
     * @return the vector multiplied by a
     */
    Vectorf mul(float a);

    /**
     * Divides the current vector by a
     *
     * @param a The number by which the vector will be divided
     * @return the vector divided by a
     */
    Vectorf div(float a);

    /**
     * Raises the current vector to the power pow
     *
     * @param pow The number by which the vector will be raised
     * @return the vector raised to the power pow
     */
    Vectorf pow(float pow);

    Vectorf ceil();

    Vectorf floor();

    Vectorf round();

    /**
     * Takes an absolute value from the current vector
     *
     * @return the vector's absolute value
     */
    Vectorf abs();

    /**
     * Takes an negative value from the current vector
     *
     * @return the vector's negation value
     */
    Vectorf negate();

    /**
     * Returns the magnitude of a vector
     *
     * @return the vector's magnitude
     */
    float length();

    /**
     * Returns the squared length of a vector
     *
     * @return the vector's squared length
     */
    float lengthSquared();

    /**
     * Normalizes the current vector
     *
     * @return the normalized vector
     */
    Vectorf normalize();

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

    float[] toArray();

    Vectori toInt();

    Vectorl toLong();

    Vectord toDouble();
}
