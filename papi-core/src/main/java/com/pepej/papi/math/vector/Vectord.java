package com.pepej.papi.math.vector;

public interface Vectord {

    /**
     * Multiplies the current vector by a
     *
     * @param a The number by which the vector will be multiplied
     * @return the vector multiplied by a
     */
    Vectord mul(double a);

    /**
     * Divides the current vector by a
     *
     * @param a The number by which the vector will be divided
     * @return the vector divided by a
     */
    Vectord div(double a);

    /**
     * Raises the current vector to the power pow
     *
     * @param pow The number by which the vector will be raised
     * @return the vector raised to the power pow
     */
    Vectord pow(double pow);

    Vectord ceil();

    Vectord floor();

    Vectord round();

    /**
     * Takes an absolute value from the current vector
     *
     * @return the vector's absolute value
     */
    Vectord abs();

    /**
     * Takes an negative value from the current vector
     *
     * @return the vector's negation value
     */
    Vectord negate();

    /**
     * Returns the magnitude of a vector
     *
     * @return the vector's magnitude
     */
    double length();

    /**
     * Returns the squared length of a vector
     *
     * @return the vector's squared length
     */
    double lengthSquared();

    /**
     * Normalizes the current vector
     *
     * @return the normalized vector
     */
    Vectord normalize();

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

    double[] toArray();

    Vectori toInt();

    Vectorl toLong();

    Vectorf toFloat();
}
