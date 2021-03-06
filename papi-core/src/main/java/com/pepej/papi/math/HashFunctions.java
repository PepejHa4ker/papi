package com.pepej.papi.math;

public final class HashFunctions {
    /**
     * Returns a hashcode for the specified value.
     * @param value the value to hash
     * @return a hash code value for the specified value.
     */
    public static int hash(double value) {
        assert !Double.isNaN(value) : "Values of NaN are not supported.";

        long bits = Double.doubleToLongBits(value);
        return (int) (bits ^ (bits >>> 32));
        //return (int) Double.doubleToLongBits(value*663608941.737);
        // This avoids excessive hashCollisions in the case values are of the form (1.0, 2.0, 3.0, ...)
    }

    /**
     * Returns a hashcode for the specified value.
     * @param value the value to hash
     * @return a hash code value for the specified value.
     */
    public static int hash(float value) {
        assert !Float.isNaN(value) : "Values of NaN are not supported.";

        return Float.floatToIntBits(value * 663608941.737f);
        // This avoids excessive hashCollisions in the case values are of the form (1.0, 2.0, 3.0, ...)
    }

    /**
     * Returns a hashcode for the specified value.
     * @param value the value to hash
     * @return a hash code value for the specified value.
     */
    public static int hash(int value) {
        return value;
    }

    /**
     * Returns a hashcode for the specified value.
     * @param value the value to hash
     * @return a hash code value for the specified value.
     */
    public static int hash(long value) {
        return ((int) (value ^ (value >>> 32)));
    }

    /**
     * Returns a hashcode for the specified object.
     * @param object the object to hash
     * @return a hash code value for the specified object.
     */
    public static int hash(Object object) {
        return object == null ? 0 : object.hashCode();
    }

    /**
     * In profiling, it has been found to be faster to have our own local implementation of "ceil" rather than to call to {@link Math#ceil(double)}.
     * @param v the value to calc ceil
     * @return the ceil value of {@code v}
     */
    public static int fastCeil(float v) {
        int possible_result = (int) v;
        if (v - possible_result > 0) {
            possible_result += 1;
        }
        return possible_result;
    }

    private HashFunctions() {
        throw new UnsupportedOperationException("This class cannot be initialized");

    }
}